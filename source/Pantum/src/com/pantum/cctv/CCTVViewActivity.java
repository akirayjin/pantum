package com.pantum.cctv;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.pantum.R;
import com.pantum.model.CCTVPlaceModelData;
import com.pantum.utility.ConstantVariable;
import com.pantum.utility.Utility;

public class CCTVViewActivity extends Activity {

	private boolean isVideo = false;
	private ImageView image;
	private VideoView video;
	private String urlImage;
	private String urlVideo;
	private boolean isFinished = false;
	private ProgressBar mProgressBar;
	private ActionBar actionBar;
	private boolean isAutoRefresh = false;
	private CountDownTimer refreshTimer;
	private boolean isOnPause = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cctvview);
		isAutoRefresh = Utility.loadBooleanPreferences(ConstantVariable.TRAIN_AUTO_REFRESH_KEY, this.getApplicationContext());
		setRefreshTimer();
		image = (ImageView)findViewById(R.id.cctv_image);
		video = (VideoView)findViewById(R.id.cctv_video);
		mProgressBar = (ProgressBar)findViewById(R.id.generic_progress_bar);
		CCTVPlaceModelData place = Utility.getTempPlaceData();
		setVideoPlayer();
		if(place!=null){
			urlImage = ConstantVariable.CCTV_IMAGE_URL_BASE+place.getCamId();
			urlVideo = ConstantVariable.CCTV_VIDEO_URL_BASE+place.getCamId();
			startRequest(isVideo);
		}
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setRefreshTimer(){
		int savedRefreshTimeout = Utility.loadIntegerPreferences(ConstantVariable.CCTV_REFRESH_TIMEOUT_VALUE_KEY, this.getApplicationContext());
		if (savedRefreshTimeout < 1){
			savedRefreshTimeout = ConstantVariable.TEN_THOUSAND_MILLIS;
		}
		refreshTimer = new CountDownTimer(savedRefreshTimeout, ConstantVariable.INTERVAL_ONE_THOUSAND_MILLIS) {

			@Override
			public void onTick(long millisUntilFinished) {}

			@Override
			public void onFinish() {
				startRequest(isVideo);
			}
		};
	}

	private class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected void onPreExecute() {
			showProgressBar(true);
		}

		protected void onPostExecute(Bitmap result) {
			image.setImageBitmap(result);
			image.setVisibility(View.VISIBLE);
			isFinished = true;
			showProgressBar(false);
			startRefreshTimer();
		}

		@Override
		protected Bitmap doInBackground(String... urls){
			isFinished = false;
			Bitmap bitmap = null;
			HttpClient Client = new DefaultHttpClient();
			try
			{
				HttpGet httpget = new HttpGet(urls[0]);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseString = Client.execute(httpget, responseHandler);
				InputStream in = (InputStream) new URL(responseString).getContent();
				bitmap = BitmapFactory.decodeStream(in);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return bitmap;
		}
	}

	private class DownloadVideoTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			showProgressBar(true);
		}

		protected void onPostExecute(String result) {
			playVideo(result);
			isFinished = true;
			showProgressBar(false);
			startRefreshTimer();
		}

		@Override
		protected String doInBackground(String... urls){
			isFinished = false;
			String responseString = null;
			HttpClient Client = new DefaultHttpClient();
			try
			{
				HttpGet httpget = new HttpGet(urls[0]);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				responseString = Client.execute(httpget, responseHandler);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return responseString;
		}
	}

	private void setVideoPlayer(){
		video.setMediaController(null);
		video.requestFocus();
		video.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if(!mp.isLooping()){
					mp.setLooping(true);
					video.start();
				}
			}
		});
	}

	private void playVideo(String url){
		video.setVisibility(View.VISIBLE);
		Uri videoLink = Uri.parse(url);
		video.setVideoURI(videoLink);
		video.start();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(isVideo){
			menu.findItem(R.id.action_picture).setVisible(true);
			menu.findItem(R.id.action_video).setVisible(false);
		}else{
			menu.findItem(R.id.action_picture).setVisible(false);
			menu.findItem(R.id.action_video).setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.cctv_view_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_picture:
			isVideo = false;
			if(video.isShown()){
				video.stopPlayback();
				video.setVisibility(View.GONE);
			}
			if(isFinished){
				startRequest(isVideo);
			}
			invalidateOptionsMenu();
			return true;
		case R.id.action_video:
			isVideo = true;
			if(image.isShown()){
				image.setVisibility(View.GONE);
			}
			if(isFinished){
				startRequest(isVideo);
			}
			invalidateOptionsMenu();
			return true;
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_refresh:
			startRequest(isVideo);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startRequest(boolean isVideo){
		if(isVideo){
			new DownloadVideoTask().execute(urlVideo);
		}else{
			new DownloadImageTask().execute(urlImage);
		}
	}

	private void showProgressBar(boolean state){
		if(state && !mProgressBar.isShown()){
			mProgressBar.setVisibility(View.VISIBLE);
		}else if(mProgressBar.isShown()){
			mProgressBar.setVisibility(View.GONE);
		}
	}
	
	public void stopRefreshTimer(){
		refreshTimer.cancel();
	}

	public void startRefreshTimer(){
		refreshTimer.cancel();
		refreshTimer.start();
	}
	
	@Override
	public void onPause() {
		isOnPause = true;
		stopRefreshTimer();
		super.onPause();
	}

	@Override
	public void onResume() {
		if(isOnPause && isAutoRefresh){
			isOnPause = false;
			startRequest(isVideo);
		}
		super.onResume();
	}
}
