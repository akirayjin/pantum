package com.pantum;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pantum.utility.ConstantVariable;
import com.pantum.utility.Utility;

public class FavoriteFragment extends Fragment {

	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.favorite_layout, container, false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView list = (ListView)rootView.findViewById(R.id.favorite_listview);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
		        android.R.layout.simple_list_item_1, Utility.getFavoriteArray(getActivity()));
		list.setOnItemClickListener(new  OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				String currentStation = adapter.getItemAtPosition(position).toString();
				Bundle args = new Bundle();
		        args.putString(ConstantVariable.CURRENT_STATION_KEY, currentStation);
				NavigationDrawer drawer = new NavigationDrawer().getInstance();
				drawer.selectItem(ConstantVariable.POSITION_FRAGMENT, args);
			}
		});
		list.setAdapter(adapter);
	}

}
