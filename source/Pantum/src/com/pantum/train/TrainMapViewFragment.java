package com.pantum.train;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pantum.R;
import com.pantum.utility.ConstantVariable;

public class TrainMapViewFragment extends Activity{

	private String stationName;
	private LatLng coordinate;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.train_map_fragment);
		setUpCoordinate();
		setMap(coordinate, stationName);
		actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void setUpCoordinate(){
		String[] position = getIntent().getExtras().getStringArray(ConstantVariable.TRAIN_INTENT_EXTRA_MAP);
		double latitude = Double.parseDouble(position[ConstantVariable.TRAIN_INDEX_LATITUDE]);
		double longitude = Double.parseDouble(position[ConstantVariable.TRAIN_INDEX_LONGITUDE]);
		stationName = getIntent().getExtras().getString(ConstantVariable.TRAIN_INTENT_EXTRA_STATION_NAME);
		coordinate = new LatLng(latitude, longitude);
	}
	
	private void setMap(LatLng coordinate, String stationName){
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.addMarker(getMarkerOption(coordinate, stationName));
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setTiltGesturesEnabled(true);
		map.getUiSettings().setRotateGesturesEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, ConstantVariable.TRAIN_MAP_ZOOM));
	}
	
	private MarkerOptions getMarkerOption(LatLng coordinate, String stationName){
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(coordinate);
		markerOptions.title(stationName);
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.train));
		return markerOptions;
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
