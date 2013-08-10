package com.pantum;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pantum.utility.PantumDatabase;

public class SchedulesListAdapter extends BaseAdapter {
	public ArrayList<ArrayList<String>> rowArray;
	public Context context;
	private LayoutInflater mInflater;
	private PantumDatabase pd;

	public SchedulesListAdapter(Context context, ArrayList<ArrayList<String>> rowArray, PantumDatabase pd){
		this.rowArray = rowArray;
		this.mInflater = LayoutInflater.from(context);
		this.pd = pd;
	}

	@Override
	public int getCount() {
		return rowArray.size();
	}

	@Override
	public Object getItem(int position) {
		return rowArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ArrayList<String> currentRow = rowArray.get(position);
		View currentView = null;

		if(currentRow.size() == 1){
			currentView = mInflater.inflate(R.layout.schedule_row_empty, null);
		}else{
			currentView = mInflater.inflate(R.layout.schedule_rows, null);
		}

		ViewHolder holder;
		if (convertView == null && currentRow != null) {
			holder = new ViewHolder();
			convertView = currentView;
			if(currentRow.size() == 1){
				holder.noSchedule = (TextView)convertView.findViewById(R.id.empty_schedule);
				holder.noSchedule.setBackgroundColor(Color.BLACK);
				holder.noSchedule.setTextColor(Color.RED);
			}else{
				holder.noKa = (TextView)convertView.findViewById(R.id.no_ka);
				holder.tujuanKa = (TextView)convertView.findViewById(R.id.tujuan_ka);
				holder.jadwalKa = (TextView)convertView.findViewById(R.id.jadwal_ka);
				holder.posisiKa = (TextView)convertView.findViewById(R.id.posisi_ka);
				holder.jalurKa = (TextView)convertView.findViewById(R.id.jalur_ka);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		if(currentRow.size() == 1){
			holder.noSchedule.setText(currentRow.get(0));
		}else{
			holder.noKa.setText(currentRow.get(0));
			holder.tujuanKa.setText(currentRow.get(1));
			holder.jadwalKa.setText(currentRow.get(2));
			holder.posisiKa.setText(currentRow.get(3));
			holder.jalurKa.setText(currentRow.get(4));
			String backgroundColor = pd.getClassBackgroundColor(currentRow.get(5));
			String textColor = pd.getClassTextColor(currentRow.get(5));
			setColorUI(backgroundColor, textColor, holder);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView noKa;
		TextView tujuanKa;
		TextView jadwalKa;
		TextView posisiKa;
		TextView jalurKa;
		TextView noSchedule;
	}

	public void refreshList(){
		notifyDataSetChanged();
	}

	private void setColorUI(String backgroundColor, String textColor, ViewHolder holder){
		holder.tujuanKa.setBackgroundColor(Color.parseColor(backgroundColor));
		holder.tujuanKa.setTextColor(Color.parseColor(textColor));
		holder.noKa.setBackgroundColor(Color.parseColor(backgroundColor));
		holder.noKa.setTextColor(Color.parseColor(textColor));
		holder.jadwalKa.setBackgroundColor(Color.parseColor(backgroundColor));
		holder.jadwalKa.setTextColor(Color.parseColor(textColor));
		holder.posisiKa.setBackgroundColor(Color.parseColor(backgroundColor));
		holder.posisiKa.setTextColor(Color.parseColor(textColor));
		holder.jalurKa.setBackgroundColor(Color.parseColor(backgroundColor));
		holder.jalurKa.setTextColor(Color.parseColor(textColor));
	}

}
