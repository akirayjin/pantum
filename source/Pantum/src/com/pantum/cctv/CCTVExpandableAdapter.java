package com.pantum.cctv;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pantum.R;
import com.pantum.model.CCTVPlaceModelData;
import com.pantum.model.CCTVRegionModelData;

public class CCTVExpandableAdapter extends BaseExpandableListAdapter{
	
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<CCTVRegionModelData> groupArray;
	
	public CCTVExpandableAdapter(Context c, ArrayList<CCTVRegionModelData> data){
		mContext = c;
		mInflater = LayoutInflater.from(mContext);
		groupArray = data;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groupArray.get(groupPosition).getPlacesArray().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder;
		CCTVPlaceModelData currentPlace = (CCTVPlaceModelData) getChild(groupPosition, childPosition);
		if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.cctv_child_list, null);
            holder.placeName = (TextView)convertView.findViewById(R.id.cctv_child_list_place);
            convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.placeName.setText(currentPlace.getPlaceName());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groupArray.get(groupPosition).getPlacesArray().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupArray.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupArray.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder holder;
		CCTVRegionModelData currentRegion = (CCTVRegionModelData) getGroup(groupPosition);
		if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.cctv_group_list, null);
            holder.placeName = (TextView)convertView.findViewById(R.id.cctv_group_list_region);
            convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.placeName.setText(currentRegion.getRegionName());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	static class ViewHolder {
		TextView regionName;
		TextView placeName;
	}

}
