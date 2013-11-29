package com.pantum.train;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.pantum.R;
import com.pantum.utility.Utility;

public class TrainPositionMainFragment extends Fragment{
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	private PagerSlidingTabStrip tabs;
	private ViewPager mViewPager;
	private View rootView;
	private int currentColor = 0xFF96AA39;
	private String currentStationKey;

	public TrainPositionMainFragment(){
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getChildFragmentManager());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.train_station_list, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		ArrayList<String> favoriteArray = Utility.getFavoriteArray(getActivity());
		if(favoriteArray.isEmpty()){
			mViewPager.setCurrentItem(1);
		}
		tabs.setViewPager(mViewPager);
		changeColor(currentColor);
	}

	private void changeColor(int newColor) {
		tabs.setIndicatorColor(newColor);
		currentColor = newColor;
	}

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}


		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String title = "";
			switch(position){
			case 0:
				title = "POSITION";
				break;
			case 1:
				title = "SCHEDULE";
				break;
			case 2:
				title = "NEWS";
				break;
			}
			return title;
		}


		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch(position){
			case 0:
				fragment = new TrainPositionFragment();
				break;
			case 1:
				fragment = new TrainScheduleFragment();
				break;
			case 2:
				fragment = new TrainNewsFragment();
				break;
			}
			return fragment;
		}
	}
}
