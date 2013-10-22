package com.pantum;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.pantum.utility.ConstantVariable;
import com.pantum.utility.Utility;

public class FavoriteFragment extends Fragment {

	View rootView;
	LinearLayout noDataLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.favorite_layout, container, false);
		noDataLayout = (LinearLayout)rootView.findViewById(R.id.train_no_favorite_layout);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView list = (ListView)rootView.findViewById(R.id.favorite_listview);
		ArrayList<String> favoriteArray = Utility.getFavoriteArray(getActivity());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
		        android.R.layout.simple_list_item_1, favoriteArray);
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
		if(favoriteArray.size() > 0){
			noDataLayout.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
		}
	}
}
