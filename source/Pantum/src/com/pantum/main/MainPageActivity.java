package com.pantum.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.pantum.R;
import com.pantum.cctv.CCTVMainPageActivity;
import com.pantum.train.TrainNavigationDrawer;

public class MainPageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new MenuMainPageAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent intent = null;
				switch(position){
				case 0:
					intent = new Intent(MainPageActivity.this, TrainNavigationDrawer.class);
					break;
				case 1:
					intent = new Intent(MainPageActivity.this, CCTVMainPageActivity.class);
					break;
				}
				startActivity(intent);
			}
		});
	}
}
