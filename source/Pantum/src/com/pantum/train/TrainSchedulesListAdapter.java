package com.pantum.train;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pantum.R;
import com.pantum.model.TrainModelData;
import com.pantum.utility.PantumDatabase;

public class TrainSchedulesListAdapter extends BaseAdapter {
	public ArrayList<TrainModelData> rowArray;
	public Context context;
	private LayoutInflater mInflater;
	private PantumDatabase pd;

	public TrainSchedulesListAdapter(Context context, ArrayList<TrainModelData> rowArray, PantumDatabase pd){
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
		TrainModelData currentRow = rowArray.get(position);
		View currentView = null;

		if(currentRow.getSize() == 1){
			currentView = mInflater.inflate(R.layout.schedule_row_empty, null);
		}else{
			currentView = mInflater.inflate(R.layout.schedule_rows, null);
		}

		ViewHolder holder;
		if (convertView == null && currentRow != null) {
			holder = new ViewHolder();
			convertView = currentView;
			if(currentRow.getSize() == 1){
				holder.noSchedule = (TextView)convertView.findViewById(R.id.empty_schedule);
				holder.noSchedule.setBackgroundColor(Color.BLACK);
				holder.noSchedule.setTextColor(Color.RED);
			}else{
				holder.noKa = (TextView)convertView.findViewById(R.id.no_ka);
				holder.tujuanKa = (TextView)convertView.findViewById(R.id.tujuan_ka);
				holder.jadwalKa = (TextView)convertView.findViewById(R.id.jadwal_ka);
				holder.posisiKa = (TextView)convertView.findViewById(R.id.posisi_ka);
				holder.jalurKa = (TextView)convertView.findViewById(R.id.jalur_ka);
				holder.wrapperLayout = (LinearLayout)convertView.findViewById(R.id.schedule_row_layout);
				holder.noKaText = (TextView)convertView.findViewById(R.id.no_ka_text);
				holder.tujuanKaText = (TextView)convertView.findViewById(R.id.tujuan_ka_text);
				holder.jadwalKaText = (TextView)convertView.findViewById(R.id.jadwal_ka_text);
				holder.posisiKaText = (TextView)convertView.findViewById(R.id.posisi_ka_text);
				holder.jalurKaText = (TextView)convertView.findViewById(R.id.jalur_ka_text);
			}
			convertView.setTag(holder);
		} else if(currentRow.getSize() == 1){
			currentView = mInflater.inflate(R.layout.schedule_row_empty, null);
			convertView = currentView;
			holder = new ViewHolder();
			holder.noSchedule = (TextView)convertView.findViewById(R.id.empty_schedule);
			holder.noSchedule.setBackgroundColor(Color.BLACK);
			holder.noSchedule.setTextColor(Color.RED);
		} else{
			holder = (ViewHolder)convertView.getTag();
		}

		if(currentRow.getSize() == 1){
			holder.noSchedule.setText(currentRow.getNoTrainMessage());
		}else{
			String backgroundColor;
			String textColor;
			holder.noKa.setText(currentRow.getTrainNumber());
			holder.tujuanKa.setText(currentRow.getDestination());
			holder.jadwalKa.setText(currentRow.getScheduleArrive());
			holder.posisiKa.setText(currentRow.getCurrentPosition());
			if(currentRow.getSize() < 6){
				backgroundColor = pd.getClassBackgroundColor(currentRow.getBackgroundColor());
				textColor = pd.getClassTextColor(currentRow.getBackgroundColor());
			}else{
				holder.jalurKa.setText(String.valueOf(currentRow.getTrainLine()));
				backgroundColor = pd.getClassBackgroundColor(currentRow.getBackgroundColor());
				textColor = pd.getClassTextColor(currentRow.getBackgroundColor());
			}
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
		TextView noKaText;
		TextView tujuanKaText;
		TextView jadwalKaText;
		TextView posisiKaText;
		TextView jalurKaText;
		LinearLayout wrapperLayout;
	}

	public void refreshList(ArrayList<TrainModelData> rowArray){
		this.rowArray = rowArray;
		notifyDataSetChanged();
	}

	private void setColorUI(String backgroundColor, String textColor, ViewHolder holder){
		holder.wrapperLayout.setBackgroundColor(Color.parseColor(backgroundColor));
		holder.tujuanKa.setTextColor(Color.parseColor(textColor));
		holder.tujuanKaText.setTextColor(Color.parseColor(textColor));
		holder.noKa.setTextColor(Color.parseColor(textColor));
		holder.noKaText.setTextColor(Color.parseColor(textColor));
		holder.jadwalKa.setTextColor(Color.parseColor(textColor));
		holder.jadwalKaText.setTextColor(Color.parseColor(textColor));
		holder.posisiKa.setTextColor(Color.parseColor(textColor));
		holder.posisiKaText.setTextColor(Color.parseColor(textColor));
		holder.jalurKa.setTextColor(Color.parseColor(textColor));
		holder.jalurKaText.setTextColor(Color.parseColor(textColor));
	}

}
