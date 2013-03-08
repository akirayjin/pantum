package com.pantum;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity {
	public WebView wv;
	public Spinner stationSpinner;
	public HashMap<String, String> map;
	public LinearLayout opening, openingMap;
	public TextView stationName;
	public Context activity;
	public MapView mapView;
	public TextView locationText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		opening = (LinearLayout)findViewById(R.id.opening);
		openingMap = (LinearLayout)findViewById(R.id.opening_map);
		stationName = (TextView)findViewById(R.id.text);
		locationText = (TextView)findViewById(R.id.location_text);
		createMapForStationCode();
		activity = this.getApplicationContext();
		setWebViewLayout();
		addListenerOnSpinnerItemSelection();
		setMapViewLayout();

		//setMapZoomPoint(new GeoPoint((int)(37.441*1E6), (int)(-122.1419*1E6)), 15);
	}

//	private void setMapZoomPoint(GeoPoint geoPoint, int zoomLevel) {
//		mapView.getController().setCenter(geoPoint);
//		mapView.getController().setZoom(zoomLevel);
//		mapView.postInvalidate();
//	}
	
	private void setMapViewLayout(){
		mapView = (MapView)findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
		mapView.postInvalidate();
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

	public void createMapForStationCode(){
		map = new HashMap<String, String>();
		map.put("Jakartakota", "JAK");
		map.put("Jayakarta", "JYK");
		map.put("Manggabesar", "MGB");
		map.put("Sawahbesar", "SW");
		map.put("Juanda", "JUA");
		map.put("Gambir", "GMR");
		map.put("Gondangdia", "GDD");
		map.put("Cikini", "CKI");
		map.put("Manggarai", "MRI");
		map.put("Tebet", "TEB");
		map.put("Cawang", "CW");
		map.put("Durenkalibata", "DRN");
		map.put("Pasarminggubaru", "PSMB");
		map.put("Pasarminggu", "PSM");
		map.put("Tanjungbarat", "TNT");
		map.put("Lentengagung", "LNA");
		map.put("Universitaspancasila", "UP");
		map.put("Universitasindonesia", "UI");
		map.put("Pondokcina", "POC");
		map.put("Depokbaru", "DPB");
		map.put("Depok", "DP");
		map.put("Citayam", "CTA");
		map.put("Bojonggede", "BJD");
		map.put("Cilebut", "CLT");
		map.put("Bogor", "BOO");
		map.put("Bekasi", "BKS");
		map.put("Kranji", "KRI");
		map.put("Cakung", "CUK");
		map.put("Klenderbaru", "KLDB");
		map.put("Buaran", "BUA");
		map.put("Klender", "KLD");
		map.put("Jatinegara", "JNG");
		map.put("Pondokjati", "POK");
		map.put("Kramat", "KMT");
		map.put("Gangsentiong", "GST");
		map.put("Pasarsenen", "PSE");
		map.put("Kemayoran", "KMO");
		map.put("Rajawali", "RJW");
		map.put("Kampungbandan", "KPB");
		map.put("Ancol", "AC");
		map.put("Tanjungpriok", "TPK");
		map.put("Tanggerang", "TNG");
		map.put("Batuceper", "BPR");
		map.put("Poris", "PI");
		map.put("Kalideres", "KDS");
		map.put("Rawabuaya", "RW");
		map.put("Bojongindah", "BOI");
		map.put("Pesing", "PSG");
		map.put("Duri", "DU");
		map.put("Tanahabang", "THB");
		map.put("Angke", "AK");
		map.put("Karet", "KRT");
		map.put("Sudirman", "SUD");
		map.put("Serpong", "SRP");
		map.put("Rawabuntu", "RU");
		map.put("Sudimara", "SDM");
		map.put("Jurangmangu", "JRU");
		map.put("Podokranji", "PDJ");
		map.put("Kebayoran", "KBY");
		map.put("Palmerah", "PLM");
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	public class CustomOnItemSelectedListener implements OnItemSelectedListener {

		public CustomOnItemSelectedListener(){}

		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			String currentStation = parent.getItemAtPosition(pos).toString();

			boolean isFound = false;
			for (int i = 0; i < map.size() && !isFound; i++) {
				String currentKey = map.get(currentStation);
				if(currentKey != null){
					setDataVisibility(true);
					wv.loadUrl("http://infoka.krl.co.id/to/"+currentKey);
					stationName.setText("Stasiun "+currentStation);
					locationText.setText("Lokasi Stasiun "+currentStation);
					isFound = true;
				}else{
					setDataVisibility(false);
					stationName.setText(R.string.no_station_selected);
					locationText.setText("Lokasi Stasiun");
				}
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {}

	}
	
	public void setDataVisibility(boolean state){
		if(state){
			wv.setVisibility(View.VISIBLE);
			mapView.setVisibility(View.VISIBLE);
			opening.setVisibility(View.GONE);
			openingMap.setVisibility(View.GONE);
		}else{
			wv.setVisibility(View.GONE);
			mapView.setVisibility(View.GONE);
			opening.setVisibility(View.VISIBLE);
			openingMap.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
