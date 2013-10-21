package com.pantum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pantum.utility.ConstantVariable;
import com.pantum.utility.PantumDatabase;
import com.pantum.utility.Utility;

public class PositionFragment extends Fragment {
	private WebView wv;
	private HashMap<String, String> map;
	private TextView stationName;
	private PantumDatabase pd;
	private ListView scheduleList;
	private ImageView favoriteButton;
	private LinearLayout dataLayout;
	private LinearLayout noDataLayout;
	private String currentStationName = "";
	private boolean isFavorited = false;
	private ArrayList<ArrayList<String>> rows;
	private String currentKey = "";
	final Timer myTimer = new Timer();
	private boolean isOnPause = false;
	private ProgressBar progressBar;
	private SchedulesListAdapter adapter;
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.position_layout, container, false);
		findLayout();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		pd = new PantumDatabase(this.getActivity().getApplicationContext());
		map = pd.getStationsCodeMap();
		Bundle argument = getArguments();
		if(argument != null){
			String currentStation = argument.getString(ConstantVariable.CURRENT_STATION_KEY);
			onItemSelected(currentStation);
			setDataVisibility(true);
		}
		favoriteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isFavorited){
					Drawable starImage = getResources().getDrawable(R.drawable.star_blank_50_50);
					favoriteButton.setImageDrawable(starImage);
					Utility.savePreference(currentStationName, false, PositionFragment.this.getActivity());
					isFavorited = Utility.loadBooleanPreferences(currentStationName, PositionFragment.this.getActivity());
				}else{
					if(!currentStationName.equalsIgnoreCase("")){
						Drawable starImage = getResources().getDrawable(R.drawable.star_orange_50_50);
						favoriteButton.setImageDrawable(starImage);
						Utility.savePreference(currentStationName, true, PositionFragment.this.getActivity());
						isFavorited = Utility.loadBooleanPreferences(currentStationName, PositionFragment.this.getActivity());
					}
				}
			}
		});
	}

	private void findLayout(){
		stationName = (TextView)rootView.findViewById(R.id.text);
		scheduleList = (ListView)rootView.findViewById(R.id.schedule_list);
		favoriteButton = (ImageView)rootView.findViewById(R.id.favourite_button);
		progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
		dataLayout = (LinearLayout)rootView.findViewById(R.id.data_layout);
		noDataLayout = (LinearLayout)rootView.findViewById(R.id.no_data_layout);
		setWebView();
	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private void setWebView(){
		wv = new WebView(this.getActivity().getApplicationContext());
		wv.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		wv.setWebViewClient(new WebViewClient(){
			boolean isfinish = false;

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				//Log.i("onpagestarted","start new url");
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
				Toast.makeText(PositionFragment.this.getActivity(), "Ada masalah dengan koneksi Internet anda. Cobalah kembali beberapa saat lagi.", Toast.LENGTH_LONG).show();
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
			//Log.i("Row table", html);
			String[] tables = html.split("</tr>");
			rows = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < tables.length; i++) {
				String[] coloumn = tables[i].split("</td>");
				ArrayList<String> coloumnArray = new ArrayList<String>();
				for (int j = 0; j < coloumn.length; j++) {
					String text = Html.fromHtml(coloumn[j]).toString();
					//Log.i("Row table", text);
					coloumnArray.add(text);
				}
				if(tables.length >= 1 && tables[i].contains("cls")){
					String currentTablesText = tables[i];
					String classTrain = currentTablesText.substring(11, 17);
					if(classTrain.contains("\"")){
						classTrain = classTrain.substring(0, classTrain.length()-1);
					}
					//Log.i("Row table", classTrain);
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
			if(adapter == null){
				adapter = new SchedulesListAdapter(PositionFragment.this.getActivity().getApplicationContext(), rows, pd);
				scheduleList.setAdapter(adapter);
			}else{
				adapter.refreshList(rows);
			}
		}
	}

	public void onItemSelected(String currentStation){
		if(Utility.isNetworkAvailable(PositionFragment.this.getActivity())){
			currentStationName = currentStation;
			isFavorited = Utility.loadBooleanPreferences(currentStationName, this.getActivity());
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
				}
			}
		}else{
			Toast.makeText(PositionFragment.this.getActivity(), "Anda tidak terhubung dengan Internet. Aktifkan Internet anda dan coba kembali", Toast.LENGTH_LONG).show();
		}
	}

	public void setDataVisibility(boolean state){
		if(state){
			dataLayout.setVisibility(View.VISIBLE);
			noDataLayout.setVisibility(View.GONE);
		}else{
			dataLayout.setVisibility(View.GONE);
			noDataLayout.setVisibility(View.VISIBLE);
		}
	}

	public void onCreditClick(){
		Intent intent = new Intent(PositionFragment.this.getActivity(), CreditFragment.class);
		startActivity(intent);
	}

	private void startLoadUrl(){
		showProgressBar(true);
		wv.loadUrl("http://infoka.krl.co.id/to/"+currentKey);
	}

	@Override
	public void onPause() {
		isOnPause = true;
		super.onPause();
	}

	@Override
	public void onResume() {
		if(isOnPause){
			isOnPause = false;
			startLoadUrl();
		}
		super.onResume();
	}

	private void showProgressBar(boolean state){
		if(state){
			progressBar.setVisibility(View.VISIBLE);
		}else{
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
}
