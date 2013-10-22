package com.pantum;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CreditFragment extends Fragment {
	public ListView list;
	public CreditListAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.credit_layout, container, false);
		list = (ListView)rootView.findViewById(R.id.credit_list);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ArrayList<String> name = new ArrayList<String>();
		name.add("Eka Cakra A W");
		name.add("Guntur Suparno Putro");
		name.add("Budiharja Kusma");
		name.add("Muhammad Ikhwan");
		
		adapter = new CreditListAdapter(this.getActivity().getApplicationContext(), name);
		list.setAdapter(adapter);
	}

}
