package com.pantum.cctv;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cctvview);
		image = (ImageView)findViewById(R.id.cctv_image);
		video = (VideoView)findViewById(R.id.cctv_video);
		CCTVPlaceModelData place = Utility.getTempPlaceData();
		if(place!=null){
			urlImage = ConstantVariable.CCTV_IMAGE_URL_BASE+place.getCamId();
			urlVideo = ConstantVariable.CCTV_VIDEO_URL_BASE+place.getCamId();
			new DownloadImageTask().execute(urlImage);
		}
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		protected void onPostExecute(Bitmap result) {
			image.setImageBitmap(result);
			image.setVisibility(View.VISIBLE);
			isFinished = true;
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
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(responseString).getContent());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return bitmap;
		}
	}

	private class DownloadVideoTask extends AsyncTask<String, Void, String> {
		protected void onPostExecute(String result) {
			playVideo(result);
			isFinished = true;
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

	private void playVideo(String url){
		video.setVisibility(View.VISIBLE);
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(video);
		Uri videoLink = Uri.parse(url);
		video.setMediaController(mediaController);
		video.setVideoURI(videoLink);
		video.requestFocus();
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
				video.setVisibility(View.GONE);
			}
			if(isFinished){
				new DownloadImageTask().execute(urlImage);
			}
			invalidateOptionsMenu();
			return true;
		case R.id.action_video:
			isVideo = true;
			if(image.isShown()){
				image.setVisibility(View.GONE);
			}
			if(isFinished){
				new DownloadVideoTask().execute(urlVideo);
			}
			invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
