package com.pantum.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.pantum.R;
import com.pantum.utility.ConstantVariable;
import com.pantum.utility.Utility;

public class SettingActivity extends Activity {
	private CheckBox autoRefreshCheckBox;
	private Spinner refreshTimeoutSpinner;
	private boolean isFirstTime = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.train_setting_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        
		autoRefreshCheckBox = (CheckBox)findViewById(R.id.train_setting_auto_refresh_check);
		refreshTimeoutSpinner = (Spinner)findViewById(R.id.train_setting_refresh_timeout_spinner);
		isFirstTime = true;
		
		int savedPosition = Utility.loadIntegerPreferences(ConstantVariable.TRAIN_REFRESH_TIMEOUT_KEY, this.getApplicationContext());
		boolean savedAutoRefresh = Utility.loadBooleanPreferences(ConstantVariable.TRAIN_AUTO_REFRESH_KEY, this.getApplicationContext());
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getApplicationContext(),
		        R.array.refresh_interval, R.layout.setting_train_refresh_interval_spinner);
		adapter.setDropDownViewResource(R.layout.setting_train_refresh_interval_spinner_list);
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
				Utility.savePreference(ConstantVariable.TRAIN_REFRESH_TIMEOUT_KEY, position, SettingActivity.this.getApplicationContext());
				Utility.savePreference(ConstantVariable.TRAIN_REFRESH_TIMEOUT_VALUE_KEY, time, SettingActivity.this.getApplicationContext());
				String stringPrefix = getResources().getString(R.string.train_setting_auto_refresh_interval);
				String completeMessage = String.format(stringPrefix, currentPosition);
				if(!isFirstTime){
					Toast.makeText(SettingActivity.this.getApplicationContext(), completeMessage, Toast.LENGTH_SHORT).show();
				}else{
					isFirstTime = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		autoRefreshCheckBox.setChecked(savedAutoRefresh);
		autoRefreshCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Utility.savePreference(ConstantVariable.TRAIN_AUTO_REFRESH_KEY, isChecked, SettingActivity.this.getApplicationContext());
				if(isChecked){
					Toast.makeText(SettingActivity.this.getApplicationContext(), getResources().getString(R.string.train_setting_auto_refresh_enabled), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(SettingActivity.this.getApplicationContext(), getResources().getString(R.string.train_setting_auto_refresh_disabled), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
