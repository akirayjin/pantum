package com.pantum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.pantum.utility.PantumDatabase;
import com.pantum.utility.Utility;

public class MainActivity extends MapActivity {
	private WebView wv;
	private Spinner stationSpinner;
	private HashMap<String, String> map;
	private LinearLayout favoriteView;
	private TextView stationName;
	private MapView mapView;
	private TextView locationText;
	private PantumDatabase pd;
	private ScrollView scroller;
	private ListView favoriteList;
	private ImageView favoriteButton;
	private String currentStationName = "";
	private ArrayList<String> favoriteArray;
	private boolean isFavorited = false;

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

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				ArrayAdapter myAdap = (ArrayAdapter) stationSpinner.getAdapter(); //cast to an ArrayAdapter
				int spinnerPosition = myAdap.getPosition((String)adapter.getItemAtPosition(position));
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
				}else{
					if(!currentStationName.equalsIgnoreCase("")){
						Drawable starImage = getResources().getDrawable(R.drawable.star_orange_50_50);
						favoriteButton.setImageDrawable(starImage);
						Utility.savePreference(currentStationName, true, MainActivity.this);
					}
				}
			}
		});
	}

	private void findLayout(){
		stationName = (TextView)findViewById(R.id.text);
		locationText = (TextView)findViewById(R.id.location_text);
		favoriteView = (LinearLayout)findViewById(R.id.favorite_view);
		scroller = (ScrollView)findViewById(R.id.scroll_view);
		favoriteList = (ListView)findViewById(R.id.favorite_list);
		favoriteButton = (ImageView)findViewById(R.id.favourite_button);
		setWebViewLayout();
		setMapViewLayout();
		addListenerOnSpinnerItemSelection();
	}

	private void setMapZoomPoint(GeoPoint geoPoint, int zoomLevel) {
		mapView.getController().setCenter(geoPoint);
		mapView.getController().setZoom(zoomLevel);
		mapView.postInvalidate();
	}

	private void setMapViewLayout(){
		mapView = (MapView)findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
		mapView.postInvalidate();
	}

	public void showStationLocationOnMap(String stationName){
		mapView.getOverlays().clear();
		Drawable drawable = this.getResources().getDrawable(R.drawable.train);
		MapItemizedOverlay itemizedoverlay = new MapItemizedOverlay(drawable, this);
		GeoPoint gp = pd.getStationGeoPoint(stationName);
		OverlayItem overlayitem = new OverlayItem(gp, "", "");
		itemizedoverlay.addOverlay(overlayitem);
		List<Overlay> mapOverlays = mapView.getOverlays();
		mapOverlays.add(itemizedoverlay);
		setMapZoomPoint(gp, 17);
	}


	@SuppressLint("SetJavaScriptEnabled")
	private void setWebViewLayout(){
		if(wv == null)
			wv = (WebView)findViewById(R.id.webview);
		wv.setWebViewClient(new WebViewClient(){
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
		});
		WebSettings wvSetting = wv.getSettings();
		wvSetting.setJavaScriptEnabled(true);
		wvSetting.setLoadWithOverviewMode(true);
		wvSetting.setUseWideViewPort(true);
		wvSetting.setBuiltInZoomControls(true);
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
				MainActivity.this.onItemSelected(adapter, position);
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
				String currentKey = map.get(currentStation);
				if(currentKey != null){
					setDataVisibility(true);
					wv.loadUrl("http://infoka.krl.co.id/to/"+currentKey);
					stationName.setText("Stasiun "+currentStation);
					locationText.setText("Lokasi Stasiun "+currentStation);
					showStationLocationOnMap(currentStation);
					isFound = true;
				}else{
					setDataVisibility(false);
					stationName.setText(R.string.no_station_selected);
					locationText.setText("Lokasi Stasiun");
				}
			}
		}else{
			Toast.makeText(MainActivity.this, "Anda tidak terhubung dengan Internet. Aktifkan Internet anda dan coba kembali", Toast.LENGTH_LONG).show();
		}
	}

	public void setDataVisibility(boolean state){
		if(state){
			scroller.setVisibility(View.VISIBLE);
			favoriteView.setVisibility(View.GONE);
			favoriteButton.setVisibility(View.VISIBLE);
		}else{
			scroller.setVisibility(View.GONE);
			favoriteView.setVisibility(View.VISIBLE);
			favoriteButton.setVisibility(View.GONE);
		}
	}
	
	public void onCreditClick(View v){
		Intent intent = new Intent(MainActivity.this, CreditActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
