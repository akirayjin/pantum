package com.pantum.train;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.pantum.R;

public class TrainStationsFragment extends Fragment{
	private final Handler handler = new Handler();
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	private PagerSlidingTabStrip tabs;
	private ViewPager mViewPager;
	private View rootView;
	private int currentColor = 0xFF96AA39;

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
		tabs.setViewPager(mViewPager);
		changeColor(currentColor);
	}

	private void changeColor(int newColor) {

		tabs.setIndicatorColor(newColor);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			Drawable colorDrawable = new ColorDrawable(newColor);
			Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
			LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });


			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
				ld.setCallback(drawableCallback);
			} else {
				getActivity().getActionBar().setBackgroundDrawable(ld);
			}

			getActivity().getActionBar().setDisplayShowTitleEnabled(false);
			getActivity().getActionBar().setDisplayShowTitleEnabled(true);

		}

		currentColor = newColor;

	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			getActivity().getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}


		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String title = "";
			switch(position){
			case 0:
				title = "FAVORITE";
				break;
			case 1:
				title = "ALL STATION";
				break;
			case 2:
				title = "ROUTE";
				break;
			case 3:
				title = "NEARBY";
				break;
			}
			return title;
		}


		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch(position){
			case 0:
				fragment = new TrainFavoriteFragment();
				break;
			case 1:
				fragment = new TrainAllStationsFragment();
				break;
			case 2:
				fragment = new TrainRouteFragment();
				break;
			case 3:
				fragment = new TrainNearbyFragment();
				break;
			}
			return fragment;
		}
	}
}
