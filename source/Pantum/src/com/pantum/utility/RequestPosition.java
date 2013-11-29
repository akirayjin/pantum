package com.pantum.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.pantum.R;
import com.pantum.model.TrainModelData;

public class RequestPosition {

	private Context mContext;
	private WebView wv;
	private boolean isStopped = false;
	private boolean isRefreshing = false;
	private boolean isAutoRefresh = false;
	private CountDownTimer refreshTimer;
	private ArrayList<TrainModelData> rows;
	private OnRequestListener onRequestListener;
	private String currentKey;
	private WebSettings wvSetting;

	public RequestPosition(Context context){
		mContext = context;
		isAutoRefresh = Utility.loadBooleanPreferences(ConstantVariable.TRAIN_AUTO_REFRESH_KEY, mContext);
		setRefreshTimer();
		setWebView();
	}

	private void setRefreshTimer(){
		int savedRefreshTimeout = Utility.loadIntegerPreferences(ConstantVariable.TRAIN_REFRESH_TIMEOUT_VALUE_KEY, mContext);
		if (savedRefreshTimeout < 1){
			savedRefreshTimeout = ConstantVariable.TEN_THOUSAND_MILLIS;
		}
		refreshTimer = new CountDownTimer(savedRefreshTimeout, ConstantVariable.INTERVAL_ONE_THOUSAND_MILLIS) {

			@Override
			public void onTick(long millisUntilFinished) {}

			@Override
			public void onFinish() {
				onRequestListener.onAutoRefresh();
				startRequest(currentKey);
			}
		};
	}

	public void startRequest(String stationKey){
		currentKey = stationKey;
		if(Utility.isNetworkAvailable(mContext)){
			String url = String.format(ConstantVariable.TRAIN_URL_FORMAT, stationKey);
			Map<String, String> noCacheHeaders = new HashMap<String, String>(2);
			noCacheHeaders.put("Pragma", "no-cache");
			noCacheHeaders.put("Cache-Control", "no-cache");
			wv.loadUrl(url, noCacheHeaders);
		}else{
			Toast.makeText(mContext, mContext.getResources().getString(R.string.train_no_internet_connection), Toast.LENGTH_LONG).show();
		}
	}

	public OnRequestListener getOnFinishListener() {
		return onRequestListener;
	}

	public void setOnFinishListener(OnRequestListener onRequestListener) {
		this.onRequestListener = onRequestListener;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public void setWebView(){
		wv = new WebView(mContext);

		wvSetting = wv.getSettings();
		wvSetting.setJavaScriptEnabled(true);
		wvSetting.setAppCacheEnabled(false);
		wvSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		wvSetting.setDatabaseEnabled(false);
		wvSetting.setBlockNetworkImage(true);

		wv.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		wv.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				wvSetting.setJavaScriptEnabled(true);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.i(this.toString(), "ERROR CODE: "+errorCode+", DESCRIPTION: "+description+", URL: "+failingUrl);
				Toast.makeText(mContext, mContext.getResources().getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if(!isStopped){
					view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('tbody')[0].innerHTML);");
					isAutoRefresh = Utility.loadBooleanPreferences(ConstantVariable.TRAIN_AUTO_REFRESH_KEY, mContext);
					if(isAutoRefresh){
						startRefreshTimer();
					}
					if(isRefreshing){
						Toast.makeText(mContext, mContext.getResources().getString(R.string.train_position_refresh_end), Toast.LENGTH_SHORT).show();
						isRefreshing = false;
						onRequestListener.onRefresh();
					}
					wvSetting.setJavaScriptEnabled(false);
				}
				clearWebViewCache();
				super.onPageFinished(view, url);
			}
		});
	}
	
	public class MyJavaScriptInterface{
		final Handler handler = new Handler();
		
		@JavascriptInterface
		public void processHTML(String html){
			String[] tables = html.split(ConstantVariable.TABLE_SPLITTER);
			rows = new ArrayList<TrainModelData>();

			for (int i = 0; i < tables.length; i++) {
				String[] coloumn = tables[i].split(ConstantVariable.COLOUMN_SPLITTER);
				ArrayList<String> coloumnArray = new ArrayList<String>();
				for (int j = 0; j < coloumn.length; j++) {
					String text = Html.fromHtml(coloumn[j]).toString();
					//Log.i("Row table: ", text);
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
					//Log.i("Row table Class: ", classTrain);
					coloumnArray.add(classTrain);
				}
				TrainModelData modelData = new TrainModelData();
				modelData.setmContext(mContext);
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
	}

	final Runnable startUpdateList = new Runnable() {
		public void run() {
			if(onRequestListener!=null){
				onRequestListener.onFinish(rows);
			}
		}
	};

	public void stopRefreshTimer(){
		refreshTimer.cancel();
	}

	public void startRefreshTimer(){
		refreshTimer.cancel();
		refreshTimer.start();
	}

	private boolean isBelowKitKat(){
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
	}

	@SuppressWarnings("deprecation")
	public void clearWebViewCache(){
		if(isBelowKitKat()){
			wv.freeMemory();
		}
		wv.clearCache(true);
		wv.clearHistory();
		wv.clearFormData();
	}
	
	public void reloadRequest(){
		stopRefreshTimer();
		wv.reload();
		isRefreshing = true;
	}
	
	public boolean isAutoRefresh(){
		return isAutoRefresh;
	}
	
	public void setIsStopped(boolean state){
		isStopped = state;
	}
}
