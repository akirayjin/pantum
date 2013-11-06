package com.pantum.cctv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.pantum.R;
import com.pantum.model.CCTVPlaceModelData;
import com.pantum.utility.PantumDatabase;
import com.pantum.utility.Utility;

public class CCTVMainPageActivity extends Activity {
	PantumDatabase pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cctvmain_page);
		pd = new PantumDatabase(getApplicationContext());
		ExpandableListView expListView = (ExpandableListView)findViewById(R.id.cctv_expandable_list_view);
		expListView.setAdapter(new CCTVExpandableAdapter(getApplicationContext(), pd.getCCTVRegionArray()));
		expListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				CCTVPlaceModelData currentPlace = (CCTVPlaceModelData) pd.getCCTVRegionArray().get(groupPosition).getPlacesArray().get(childPosition);
				Utility.setTempPlaceData(currentPlace);
				Intent intent = new Intent(CCTVMainPageActivity.this, CCTVViewActivity.class);
				startActivity(intent);
				return false;
			}
		});
	}
}
