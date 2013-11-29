package com.pantum.train;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pantum.R;
import com.pantum.model.TrainModelData;
import com.pantum.utility.ConstantVariable;
import com.pantum.utility.OnRequestListener;
import com.pantum.utility.PantumDatabase;
import com.pantum.utility.RequestPosition;
import com.pantum.utility.StaticVariable;
import com.pantum.utility.Utility;

public class TrainPositionFragment extends Fragment {
	private HashMap<String, String> map;
	private PantumDatabase pd;
	private ListView scheduleList;
	private LinearLayout dataLayout;
	private LinearLayout noDataLayout;
	private String currentStationName = "";
	private boolean isFavorited = false;
	private ArrayList<TrainModelData> rows;
	private String currentKey = "";
	private boolean isOnPause = false;
	private ProgressBar progressBar;
	private TrainPositionListAdapter adapter;
	private View rootView;
	private boolean isEmpty = false;
	private RequestPosition request;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle argument = getArguments();
		String lastStationName = StaticVariable.getLastStationName();
		request = new RequestPosition(getActivity());
		request.setOnFinishListener(onRequest);
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
		pd = new PantumDatabase(this.getActivity().getApplicationContext());
		map = pd.getStationsCodeMap();
		if(!isEmpty){
			onItemSelected(currentStationName);
			setDataVisibility(true);
		}
	}

	private void findLayout(){
		progressBar = (ProgressBar)rootView.findViewById(R.id.generic_progress_bar);
		progressBar.setIndeterminate(true);
		scheduleList = (ListView)rootView.findViewById(R.id.schedule_list);
		dataLayout = (LinearLayout)rootView.findViewById(R.id.data_layout);
		noDataLayout = (LinearLayout)rootView.findViewById(R.id.no_data_layout);
	}
	
	private OnRequestListener onRequest = new OnRequestListener() {
		
		@Override
		public void onFinish(ArrayList<TrainModelData> response) {
			rows = response;
			startUpdateListView();
		}

		@Override
		public void onRefresh() {
			showProgressBar(false);
		}

		@Override
		public void onAutoRefresh() {
			showProgressBar(true);
		}
	};
	
	public void startUpdateListView() {
		request.clearWebViewCache();
		showProgressBar(false);
		if(adapter == null){
			adapter = new TrainPositionListAdapter(TrainPositionFragment.this.getActivity().getApplicationContext(), rows, pd);
			scheduleList.setAdapter(adapter);
		}else{
			adapter.refreshList(rows);
		}
	}

	public void onItemSelected(String currentStation){
		if(Utility.isNetworkAvailable(TrainPositionFragment.this.getActivity())){
			boolean isFound = false;
			for (int i = 0; i < map.size() && !isFound; i++) {
				currentKey = map.get(currentStation);
				if(currentKey != null && !currentKey.equalsIgnoreCase("")){
					setDataVisibility(true);
					startRequest();
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

	@Override
	public void onPause() {
		isOnPause = true;
		request.stopRefreshTimer();
		super.onPause();
	}

	@Override
	public void onResume() {
		if(isOnPause && request.isAutoRefresh()){
			isOnPause = false;
			startRequest();
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
			reloadRequest();
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
	
	private void startRequest(){
		showProgressBar(true);
		request.startRequest(currentKey);
	}
	
	private void reloadRequest(){
		showProgressBar(true);
		request.reloadRequest();
	}

	@Override
	public void onStop() {
		super.onStop();
		request.setIsStopped(true);
	}
	
	@Override
	public void onDestroy() {
		request.clearWebViewCache();
		super.onDestroy();
	}
}
