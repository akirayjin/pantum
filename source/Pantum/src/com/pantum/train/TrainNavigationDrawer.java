package com.pantum.train;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pantum.R;
import com.pantum.utility.ConstantVariable;
import com.pantum.utility.Utility;

public class TrainNavigationDrawer extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerListMenu;
	private FragmentManager fragmentManager;
	private static TrainNavigationDrawer mInstance;
	private int currentFragment = 0;
	private int oldFragment = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mInstance = this;
		mTitle = mDrawerTitle = getTitle();
		mDrawerListMenu = getResources().getStringArray(R.array.drawer_menu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		fragmentManager = getFragmentManager();
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerListMenu));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			if(Utility.getFavoriteArray(getApplicationContext()).size() > 0){
				selectItem(ConstantVariable.FAVORITE_LIST_FRAGMENT, null);
			}else{
				selectItem(ConstantVariable.STATION_LIST_FRAGMENT, null);
			}

		}
	}

	public TrainNavigationDrawer getInstance(){
		return mInstance;
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position, null);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void selectItem(int position,  Bundle args) {
		Fragment fragment = null;
		switch(position){
		case ConstantVariable.FAVORITE_LIST_FRAGMENT:
			fragment = new TrainFavoriteFragment();
			currentFragment = ConstantVariable.FAVORITE_LIST_FRAGMENT;
			break;
		case ConstantVariable.STATION_LIST_FRAGMENT:
			fragment = new TrainStationListFragment();
			currentFragment = ConstantVariable.STATION_LIST_FRAGMENT;
			break;
		case ConstantVariable.POSITION_FRAGMENT:
			fragment = new TrainPositionFragment();
			currentFragment = ConstantVariable.POSITION_FRAGMENT;
			break;
		case ConstantVariable.CREDIT_FRAGMENT:
			fragment = new TrainCreditFragment();
			currentFragment = ConstantVariable.CREDIT_FRAGMENT;
			break;
		case ConstantVariable.TRAIN_SETTING_FRAGMENT:
			fragment = new TrainSettingFragment();
			currentFragment = ConstantVariable.TRAIN_SETTING_FRAGMENT;
			break;
		}
		if(args != null){
			fragment.setArguments(args);
		}
		if(currentFragment == oldFragment){
			mDrawerLayout.closeDrawer(mDrawerList);
		}else{
			oldFragment = currentFragment;
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.content_frame, fragment);
			transaction.commit();

			mDrawerList.setItemChecked(position, true);
			setTitle(mDrawerListMenu[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	
	public boolean isDrawerOpen(){
		return mDrawerLayout.isDrawerOpen(mDrawerList);
	}
}
