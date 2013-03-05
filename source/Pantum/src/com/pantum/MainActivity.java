package com.pantum;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {
	WebView wv;
	Spinner stationSpinner;
	HashMap<String, String> map;
	LinearLayout opening;
	TextView stationName;
	Context activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		opening = (LinearLayout)findViewById(R.id.opening);
		stationName = (TextView)findViewById(R.id.text);
		createMapForStationCode();
		activity = this.getApplicationContext();
		setWebViewLayout();
		addListenerOnSpinnerItemSelection();
		ImageView starIcon = (ImageView)findViewById(R.id.star_icon);
		starIcon.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent i = new Intent(activity, MapViewActivity.class);
//				startActivity(i); 
			}
		});
	}
	
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public class CustomOnItemSelectedListener implements OnItemSelectedListener {
		
		public CustomOnItemSelectedListener(){}
		
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			String currentStation = parent.getItemAtPosition(pos).toString();

			boolean isFound = false;
			for (int i = 0; i < map.size() && !isFound; i++) {
				String currentKey = map.get(currentStation);
				if(currentKey != null){
					wv.setVisibility(View.VISIBLE);
					opening.setVisibility(View.GONE);
					wv.loadUrl("http://infoka.krl.co.id/to/"+currentKey);
					stationName.setText(currentStation);
					isFound = true;
				}else{
					wv.setVisibility(View.GONE);
					opening.setVisibility(View.VISIBLE);
					stationName.setText(R.string.no_station_selected);
				}
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {}

	}

}
