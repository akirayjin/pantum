package com.pantum;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.pantum.utility.ConstantVariable;
import com.pantum.utility.Utility;

public class TrainSettingFragment extends Fragment {
	private View rootView;
	private CheckBox autoRefreshCheckBox;
	private Spinner refreshTimeoutSpinner;
	
	public TrainSettingFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.train_setting_layout, container, false);
		autoRefreshCheckBox = (CheckBox)rootView.findViewById(R.id.train_setting_auto_refresh_check);
		refreshTimeoutSpinner = (Spinner)rootView.findViewById(R.id.train_setting_refresh_timeout_spinner);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int savedPosition = Utility.loadIntegerPreferences(ConstantVariable.TRAIN_REFRESH_TIMEOUT_KEY, getActivity());
		boolean savedAutoRefresh = Utility.loadBooleanPreferences(ConstantVariable.TRAIN_AUTO_REFRESH_KEY, getActivity());
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.refresh_interval, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		refreshTimeoutSpinner.setAdapter(adapter);
		refreshTimeoutSpinner.setSelection(savedPosition);
		refreshTimeoutSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int position, long id) {
				String currentPosition = (String) adapter.getItemAtPosition(position);
				String[] splitStringArray = currentPosition.split(ConstantVariable.SPACE_SPLITTER);
				int time = Integer.parseInt(splitStringArray[0]);
				if(position == 0 || position == 1){
					time = time*1000;
				}else{
					time = time*60000;
				}
				Utility.savePreference(ConstantVariable.TRAIN_REFRESH_TIMEOUT_KEY, position, getActivity());
				Utility.savePreference(ConstantVariable.TRAIN_REFRESH_TIMEOUT_VALUE_KEY, time, getActivity());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		autoRefreshCheckBox.setChecked(savedAutoRefresh);
		autoRefreshCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Utility.savePreference(ConstantVariable.TRAIN_AUTO_REFRESH_KEY, isChecked, getActivity());
			}
		});
	}
}
