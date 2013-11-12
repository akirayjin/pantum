package com.pantum.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pantum.R;

public class MenuMainPageAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;

	public MenuMainPageAdapter(Context c) {
		mContext = c;
		this.mInflater = LayoutInflater.from(mContext);
	}

	public int getCount() {
		return mThumbIds.length;
	}

	public Object getItem(int position) {
		return mThumbIds[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.mainpage_menu_list, null);
			holder.itemImage = (ImageView)convertView.findViewById(R.id.mainpage_image);
			holder.itemTitle = (TextView)convertView.findViewById(R.id.mainpage_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.itemImage.setImageResource(mThumbIds[position]);
		holder.itemTitle.setText(mTitle[position]);
		return convertView;
	}

	private Integer[] mThumbIds = {
			R.drawable.krl_jabotabek_logo, R.drawable.cctv_logo, R.drawable.ic_setting
	};
	
	private String[] mTitle = {"Train Position","View CCTV","Setting"};
	
	static class ViewHolder {
		ImageView itemImage;
		TextView itemTitle;
	}

}
