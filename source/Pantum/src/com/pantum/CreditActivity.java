package com.pantum;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class CreditActivity extends Activity {
	public ListView list;
	public CreditListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit);
		list = (ListView)findViewById(R.id.credit_list);
		ArrayList<String> name = new ArrayList<String>();
		name.add("Eka Cakra A W");
		name.add("Guntur Suparno Putro");
		name.add("kedua");
		name.add("Muhammad Ikhwan");
		
		adapter = new CreditListAdapter(this.getApplicationContext(), name);
		list.setAdapter(adapter);
	}

}
