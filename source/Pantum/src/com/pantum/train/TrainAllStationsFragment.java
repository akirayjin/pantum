package com.pantum.train;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pantum.R;
import com.pantum.utility.ConstantVariable;

public class TrainAllStationsFragment extends Fragment {
	
	private View rootView;
	public TrainAllStationsFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.station_list_layout, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView list = (ListView)rootView.findViewById(R.id.station_listview);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.station_arrays, android.R.layout.simple_list_item_1);
		list.setOnItemClickListener(new  OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				String currentStation = adapter.getItemAtPosition(position).toString();
				Bundle args = new Bundle();
		        args.putString(ConstantVariable.CURRENT_STATION_KEY, currentStation);
				TrainNavigationDrawer drawer = new TrainNavigationDrawer().getInstance();
				drawer.selectItem(ConstantVariable.POSITION_FRAGMENT, args);
			}
		});
		list.setAdapter(adapter);
	}
}
