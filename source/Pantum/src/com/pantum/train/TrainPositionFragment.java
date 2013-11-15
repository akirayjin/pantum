package com.pantum.train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pantum.R;
import com.pantum.model.TrainModelData;
import com.pantum.utility.ConstantVariable;
import com.pantum.utility.PantumDatabase;
import com.pantum.utility.StaticVariable;
import com.pantum.utility.Utility;

public class TrainPositionFragment extends Fragment {
	private WebView wv;
	private HashMap<String, String> map;
	private PantumDatabase pd;
	private ListView scheduleList;
	private LinearLayout dataLayout;
	private LinearLayout noDataLayout;
	private String currentStationName = "";
	private boolean isFavorited = false;
	private ArrayList<TrainModelData> rows;
	private String currentKey = "";
	final Timer myTimer = new Timer();
	private boolean isOnPause = false;
	private ProgressBar progressBar;
	private TrainPositionListAdapter adapter;
	private View rootView;
	private CountDownTimer refreshTimer;
	private boolean isEmpty = false;
	private boolean isStopped = false;
	private boolean isRefreshing = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle argument = getArguments();
		String lastStationName = StaticVariable.getLastStationName();
		if(argument != null){
			String currentStation = argument.getString(ConstantVariable.CURRENT_STATION_KEY);
			currentStationName = currentStation;
			StaticVariable.setLastStationName(currentStation);
			isFavorited = Utility.loadBooleanPreferences(currentStationName, getActivity());
		}else if(lastStationName != null && !lastStationName.equalsIgnoreCase("")){
			currentStationName = lastStationName;
			isFavorited = Utility.loadBooleanPreferences(currentStationName, getActivity());
		}else{
			isEmpty = true;
		}
		setHasOptionsMenu(true);
	}

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
		int savedRefreshTimeout = Utility.loadIntegerPreferences(ConstantVariable.TRAIN_REFRESH_TIMEOUT_VALUE_KEY, getActivity());
		if (savedRefreshTimeout < 1){
			savedRefreshTimeout = ConstantVariable.TEN_THOUSAND_MILLIS;
		}
		refreshTimer = new CountDownTimer(savedRefreshTimeout, ConstantVariable.INTERVAL_ONE_THOUSAND_MILLIS) {

			@Override
			public void onTick(long millisUntilFinished) {
				//Log.i(this.toString(), ">>>>>>>>>>>>>>>>>>>>>>>> timeout: "+millisUntilFinished/1000);
			}

			@Override
			public void onFinish() {
				startLoadUrl();
			}
		};
		pd = new PantumDatabase(this.getActivity().getApplicationContext());
		map = pd.getStationsCodeMap();
		if(!isEmpty){
			onItemSelected(currentStationName);
			setDataVisibility(true);
		}
	}


	private void findLayout(){
		progressBar = (ProgressBar)rootView.findViewById(R.id.train_progress_bar);
		progressBar.setIndeterminate(true);
		scheduleList = (ListView)rootView.findViewById(R.id.schedule_list);
		dataLayout = (LinearLayout)rootView.findViewById(R.id.data_layout);
		noDataLayout = (LinearLayout)rootView.findViewById(R.id.no_data_layout);
		setWebView();
	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private void setWebView(){
		wv = new WebView(this.getActivity().getApplicationContext());
		
		WebSettings wvSetting = wv.getSettings();
		wvSetting.setJavaScriptEnabled(true);
		wvSetting.setAppCacheEnabled(false);
		wvSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		wvSetting.setDatabaseEnabled(false);
		wvSetting.setBlockNetworkImage(true);
		
		wv.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		wv.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				showProgressBar(true);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(TrainPositionFragment.this.getActivity(), getResources().getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if(!isStopped){
					view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('tbody')[0].innerHTML);");
					boolean isAutoRefresh = Utility.loadBooleanPreferences(ConstantVariable.TRAIN_AUTO_REFRESH_KEY, getActivity());
					if(isAutoRefresh){
						refreshTimer.cancel();
						refreshTimer.start();
					}
					if(isRefreshing){
						Toast.makeText(getActivity(), getResources().getString(R.string.train_position_refresh_end), Toast.LENGTH_SHORT).show();
						isRefreshing = false;
					}
				}
				view.clearCache(true);
				super.onPageFinished(view, url);
			}
		});
		
	}

	public class MyJavaScriptInterface
	{
		public void processHTML(String html)
		{
			//Log.i("Row table", html);
			String[] tables = html.split(ConstantVariable.TABLE_SPLITTER);
			rows = new ArrayList<TrainModelData>();
			for (int i = 0; i < tables.length; i++) {
				String[] coloumn = tables[i].split(ConstantVariable.COLOUMN_SPLITTER);
				ArrayList<String> coloumnArray = new ArrayList<String>();
				for (int j = 0; j < coloumn.length; j++) {
					String text = Html.fromHtml(coloumn[j]).toString();
					//Log.i("Row table", text);
					if(!text.equalsIgnoreCase(ConstantVariable.EMPTY_STRING)){
						coloumnArray.add(text);
					}
				}
				if(tables.length >= 1 && tables[i].contains(ConstantVariable.TRAIN_CLASS_STRING)){
					String currentTablesText = tables[i];
					String classTrain = currentTablesText.substring(11, 17);
					if(classTrain.contains(ConstantVariable.QUOTE_STRING)){
						classTrain = classTrain.substring(0, classTrain.length()-1);
					}
					//Log.i("Row table", classTrain);
					coloumnArray.add(classTrain);
				}
				TrainModelData modelData = new TrainModelData();
				modelData.setmContext(getActivity().getApplicationContext());
				if(coloumnArray.size() > 1){
					modelData.setTrainNumber(coloumnArray.get(0));
					modelData.setDestination(coloumnArray.get(1));
					modelData.setScheduleArrive(coloumnArray.get(2));
					modelData.setCurrentPosition(coloumnArray.get(3));
					if(coloumnArray.size() < 6){
						modelData.setBackgroundColor(coloumnArray.get(4));
					}else{
						modelData.setTrainLine(Integer.parseInt(coloumnArray.get(4)));
						modelData.setBackgroundColor(coloumnArray.get(5));
					}
					modelData.setSize(coloumnArray.size());
				}else{
					modelData.setNoTrainMessage(coloumnArray.get(0));
					modelData.setSize(1);
				}
				rows.add(modelData);
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
			clearWebViewCache();
			showProgressBar(false);
			if(adapter == null){
				adapter = new TrainPositionListAdapter(TrainPositionFragment.this.getActivity().getApplicationContext(), rows, pd);
				scheduleList.setAdapter(adapter);
			}else{
				adapter.refreshList(rows);
			}
		}
	}

	public void onItemSelected(String currentStation){
		if(Utility.isNetworkAvailable(TrainPositionFragment.this.getActivity())){
			boolean isFound = false;
			for (int i = 0; i < map.size() && !isFound; i++) {
				currentKey = map.get(currentStation);
				if(currentKey != null && !currentKey.equalsIgnoreCase("")){
					setDataVisibility(true);
					startLoadUrl();
					String stringFormat = getResources().getString(R.string.train_station_string_format);
					String completeString = String.format(stringFormat, currentStation);
					getActivity().setTitle(completeString);
					isFound = true;
				}
			}
		}else{
			Toast.makeText(TrainPositionFragment.this.getActivity(), getResources().getString(R.string.train_no_internet_connection), Toast.LENGTH_LONG).show();
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
		Intent intent = new Intent(TrainPositionFragment.this.getActivity(), TrainCreditFragment.class);
		startActivity(intent);
	}

	private void startLoadUrl(){
		if(Utility.isNetworkAvailable(getActivity())){
			String url = String.format(ConstantVariable.TRAIN_URL_FORMAT, currentKey);
			Map<String, String> noCacheHeaders = new HashMap<String, String>(2);
		    noCacheHeaders.put("Pragma", "no-cache");
		    noCacheHeaders.put("Cache-Control", "no-cache");
			wv.loadUrl(url, noCacheHeaders);
		}else{
			Toast.makeText(TrainPositionFragment.this.getActivity(), getResources().getString(R.string.train_no_internet_connection), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onPause() {
		isOnPause = true;
		refreshTimer.cancel();
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
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		TrainNavigationDrawer drawer = new TrainNavigationDrawer().getInstance();
		
		if(isFavorited){
			menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_action_important);
		}else{
			menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_action_not_important);
		}
		menu.findItem(R.id.action_refresh).setVisible(!drawer.isDrawerOpen() && !isEmpty);
		menu.findItem(R.id.action_map).setVisible(!drawer.isDrawerOpen() && !isEmpty);
		menu.findItem(R.id.action_favorite).setVisible(!drawer.isDrawerOpen() && !isEmpty);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.train_position_fragment_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			refreshTimer.cancel();
			wv.reload();
			isRefreshing = true;
			Toast.makeText(getActivity(), getResources().getString(R.string.train_position_refresh_start), Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_map:
			Intent intent = new Intent(getActivity(), TrainMapViewActivity.class);
			String[] position = {pd.getLatitude(currentStationName),pd.getLongitude(currentStationName)};
			intent.putExtra(ConstantVariable.TRAIN_INTENT_EXTRA_MAP, position);
			intent.putExtra(ConstantVariable.TRAIN_INTENT_EXTRA_STATION_NAME, currentStationName);
			startActivity(intent);
			return true;
		case R.id.action_favorite:
			if(isFavorited){
				Utility.savePreference(currentStationName, false, getActivity());
				isFavorited = Utility.loadBooleanPreferences(currentStationName, getActivity());
				item.setIcon(R.drawable.ic_action_not_important);
			}else{
				if(!currentStationName.equalsIgnoreCase(ConstantVariable.EMPTY_STRING)){
					Utility.savePreference(currentStationName, true, getActivity());
					isFavorited = Utility.loadBooleanPreferences(currentStationName, getActivity());
					item.setIcon(R.drawable.ic_action_important);
				}
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	private void clearWebViewCache(){
		wv.clearCache(true);
		wv.clearHistory();
		wv.clearFormData();
	}

	@Override
	public void onStop() {
		super.onStop();
		isStopped = true;
	}
	
	@Override
	public void onDestroy() {
		clearWebViewCache();
		super.onDestroy();
	}
}
