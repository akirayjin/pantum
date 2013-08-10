package com.pantum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pantum.utility.PantumDatabase;
import com.pantum.utility.Utility;

public class MainActivity extends Activity {
	private WebView wv;
	private Spinner stationSpinner;
	private HashMap<String, String> map;
	private TextView stationName;
	private PantumDatabase pd;
	private ListView favoriteList;
	private ListView scheduleList;
	private ImageView favoriteButton;
	private RelativeLayout scheduleListContainer;
	private String currentStationName = "";
	private ArrayList<String> favoriteArray;
	private boolean isFavorited = false;
	private ArrayList<ArrayList<String>> rows;
	private String currentKey = "";
	final Timer myTimer = new Timer();
	private boolean isFromFavorite = false;
	private boolean isOnPause = false;
	private boolean isBackToMain = false;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findLayout();
		pd = new PantumDatabase(this.getApplicationContext());
		map = pd.getStationsCodeMap();
		favoriteArray = Utility.getFavoriteArray(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, favoriteArray);
		favoriteList.setAdapter(adapter);
		favoriteList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				ArrayAdapter myAdap = (ArrayAdapter) stationSpinner.getAdapter(); //cast to an ArrayAdapter
				int spinnerPosition = myAdap.getPosition((String)adapter.getItemAtPosition(position));
				isFromFavorite = true;
				stationSpinner.setSelection(spinnerPosition);
				MainActivity.this.onItemSelected(adapter, position);
			}
		});

		favoriteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isFavorited){
					Drawable starImage = getResources().getDrawable(R.drawable.star_blank_50_50);
					favoriteButton.setImageDrawable(starImage);
					Utility.savePreference(currentStationName, false, MainActivity.this);
					isFavorited = Utility.loadBooleanPreferences(currentStationName, MainActivity.this);
				}else{
					if(!currentStationName.equalsIgnoreCase("")){
						Drawable starImage = getResources().getDrawable(R.drawable.star_orange_50_50);
						favoriteButton.setImageDrawable(starImage);
						Utility.savePreference(currentStationName, true, MainActivity.this);
						isFavorited = Utility.loadBooleanPreferences(currentStationName, MainActivity.this);
					}
				}
			}
		});
	}

	private void findLayout(){
		stationName = (TextView)findViewById(R.id.text);
		favoriteList = (ListView)findViewById(R.id.favorite_list);
		scheduleList = (ListView)findViewById(R.id.schedule_list);
		favoriteButton = (ImageView)findViewById(R.id.favourite_button);
		scheduleListContainer = (RelativeLayout)findViewById(R.id.containerList);
		progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		setWebView();
		addListenerOnSpinnerItemSelection();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView(){
		wv = new WebView(this.getApplicationContext());
		wv.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		wv.setWebViewClient(new WebViewClient(){
			boolean isfinish = false;

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.i("onpagestarted","start new url");
				isfinish = false;
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(MainActivity.this, "Ada masalah dengan koneksi Internet anda. Cobalah kembali beberapa saat lagi.", Toast.LENGTH_LONG).show();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('tbody')[0].innerHTML);");
				isfinish = true;
				showProgressBar(false);
				super.onPageFinished(view, url);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				if(isfinish && !isOnPause){
					showProgressBar(true);
					view.loadUrl("http://infoka.krl.co.id/to/"+currentKey);
				}
				super.onLoadResource(view, url);
			}

		});
		WebSettings wvSetting = wv.getSettings();
		wvSetting.setJavaScriptEnabled(true);
		wvSetting.setLoadWithOverviewMode(true);
		wvSetting.setUseWideViewPort(true);
		wvSetting.setBuiltInZoomControls(true);
	}

	public class MyJavaScriptInterface
	{
		public void processHTML(String html)
		{
			Log.i("Row table", html);
			String[] tables = html.split("</tr>");
			rows = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < tables.length; i++) {
				String[] coloumn = tables[i].split("</td>");
				ArrayList<String> coloumnArray = new ArrayList<String>();
				for (int j = 0; j < coloumn.length; j++) {
					String text = Html.fromHtml(coloumn[j]).toString();
					Log.i("Row table", text);
					coloumnArray.add(text);
				}
				if(tables.length >= 1 && tables[i].contains("cls")){
					String currentTablesText = tables[i];
					String classTrain = currentTablesText.substring(11, 17);
					if(classTrain.contains("\"")){
						classTrain = classTrain.substring(0, classTrain.length()-1);
					}
					Log.i("Row table", classTrain);
					coloumnArray.add(classTrain);
				}
				rows.add(coloumnArray);
			}
			handler.post(startUpdateList);
		}

		final Handler handler = new Handler();

		final Runnable startUpdateList = new Runnable() {
			public void run() {
				startUpdateListView();
			}
		};

		protected void startUpdateListView() {
			SchedulesListAdapter adapter = new SchedulesListAdapter(MainActivity.this.getApplicationContext(), rows, pd);
			scheduleList.setAdapter(adapter);
		}
	}

	public void addListenerOnSpinnerItemSelection() {
		stationSpinner = (Spinner) findViewById(R.id.station_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.station_arrays, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stationSpinner.setAdapter(adapter);
		stationSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> adapter, View view, int position,long id) {
			if(position > 0){
				if(!isFromFavorite){
					MainActivity.this.onItemSelected(adapter, position);
				}else{
					isFromFavorite = false;
				}
			}else{
				favoriteArray = Utility.getFavoriteArray(MainActivity.this);
				ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this,
						android.R.layout.simple_list_item_1, android.R.id.text1, favoriteArray);
				favoriteList.setAdapter(spinnerAdapter);
				setDataVisibility(false);
				stationName.setText(R.string.favorite_text);
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {}

	}

	public void onItemSelected(AdapterView<?> adapter, int position){
		if(Utility.isNetworkAvailable(MainActivity.this)){
			String currentStation = adapter.getItemAtPosition(position).toString();
			currentStationName = currentStation;
			isFavorited = Utility.loadBooleanPreferences(currentStationName, this);
			if(isFavorited){
				Drawable starImage = getResources().getDrawable(R.drawable.star_orange_50_50);
				favoriteButton.setImageDrawable(starImage);
			}else{
				Drawable starImage = getResources().getDrawable(R.drawable.star_blank_50_50);
				favoriteButton.setImageDrawable(starImage);
			}
			boolean isFound = false;
			for (int i = 0; i < map.size() && !isFound; i++) {
				currentKey = map.get(currentStation);
				if(currentKey != null && !currentKey.equalsIgnoreCase("")){
					setDataVisibility(true);
					startLoadUrl();
					stationName.setText("Stasiun "+currentStation);
					isFound = true;
					isBackToMain = true;
				}else{
					setDataVisibility(false);
					stationName.setText(R.string.no_station_selected);
				}
			}
		}else{
			Toast.makeText(MainActivity.this, "Anda tidak terhubung dengan Internet. Aktifkan Internet anda dan coba kembali", Toast.LENGTH_LONG).show();
		}
	}

	public void setDataVisibility(boolean state){
		if(state){
			scheduleListContainer.setVisibility(View.VISIBLE);
			favoriteList.setVisibility(View.GONE);
			favoriteButton.setVisibility(View.VISIBLE);
		}else{
			scheduleListContainer.setVisibility(View.GONE);
			favoriteList.setVisibility(View.VISIBLE);
			favoriteButton.setVisibility(View.GONE);
		}
	}

	public void onCreditClick(){
		Intent intent = new Intent(MainActivity.this, CreditActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_credit:
			onCreditClick();
			return true;
		case R.id.menu_direction:
			Uri uri = Uri.parse("geo:" + pd.getLatitude(currentStationName)  + "," + pd.getLongitude(currentStationName) +"?z=16");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startLoadUrl(){
		showProgressBar(true);
		wv.loadUrl("http://infoka.krl.co.id/to/"+currentKey);
	}

	@Override
	protected void onPause() {
		isOnPause = true;
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(isOnPause){
			isOnPause = false;
			startLoadUrl();
		}
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if(!isBackToMain){
			super.onBackPressed();
		}else{
			stationSpinner.setSelection(0);
			setDataVisibility(false);
			stationName.setText(R.string.no_station_selected);
			isBackToMain = false;
			isOnPause = true;
			scheduleList.setAdapter(null);
		}
	}

	private void showProgressBar(boolean state){
		if(state){
			progressBar.setVisibility(View.VISIBLE);
		}else{
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
}
