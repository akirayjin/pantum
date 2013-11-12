package com.pantum.train;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pantum.R;
import com.pantum.model.TrainModelData;
import com.pantum.utility.ConstantVariable;
import com.pantum.utility.PantumDatabase;

public class TrainPositionListAdapter extends BaseAdapter {
	public ArrayList<TrainModelData> rowArray;
	public Context context;
	private LayoutInflater mInflater;
	private PantumDatabase pd;

	public TrainPositionListAdapter(Context context, ArrayList<TrainModelData> rowArray, PantumDatabase pd){
		this.context = context;
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
		final TrainModelData currentRow = rowArray.get(position);
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
				setNoScheduleLayout(holder, convertView);
			}else{
				setScheduleLayout(holder, convertView);
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
			holder.trainNo.setText(currentRow.getTrainNumber());
			holder.trainDestination.setText(currentRow.getDestination());
			holder.trainSchedule.setText(currentRow.getScheduleArrive());
			holder.trainPosition.setText(currentRow.getCurrentPosition());
			holder.trainPositionStatus.setText(currentRow.getPositionStatus());
			if(currentRow.getSize() > 5){
				holder.trainLine.setText(String.valueOf(currentRow.getTrainLine()));
			}
			holder.optionButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PopupMenu popUpMenu = new PopupMenu(context, v);
					popUpMenu.inflate(R.menu.train_schedule_popup_menu);
					popUpMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

						@Override
						public boolean onMenuItemClick(MenuItem item) {
							String stationName = currentRow.getCurrentPosition().toLowerCase(Locale.getDefault());
							switch(item.getItemId()){
							case R.id.action_schedule_map_view:
								Intent intent = new Intent(context, TrainMapViewActivity.class);
								String[] position = {pd.getLatitude(stationName),pd.getLongitude(stationName)};
								intent.putExtra(ConstantVariable.TRAIN_INTENT_EXTRA_MAP, position);
								intent.putExtra(ConstantVariable.TRAIN_INTENT_EXTRA_STATION_NAME, stationName);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intent);
								break;
							case R.id.action_schedule_report:
								Toast.makeText(context, "Not Available yet", Toast.LENGTH_SHORT).show();
								break;
							case R.id.action_schedule_share:
								Toast.makeText(context, "Not Available yet", Toast.LENGTH_SHORT).show();
								break;
							default:
								Toast.makeText(context, "Not Available yet", Toast.LENGTH_SHORT).show();
							}
							return true;
						}
					});
					popUpMenu.show();
				}
			});
			backgroundColor = pd.getClassBackgroundColor(currentRow.getBackgroundColor());
			textColor = pd.getClassTextColor(currentRow.getBackgroundColor());
			setColorUI(backgroundColor, textColor, holder);
		}
		return convertView;
	}

	private void setNoScheduleLayout(ViewHolder holder, View convertView){
		holder.noSchedule = (TextView)convertView.findViewById(R.id.empty_schedule);
		holder.noSchedule.setBackgroundColor(Color.BLACK);
		holder.noSchedule.setTextColor(Color.RED);
	}

	private void setScheduleLayout(ViewHolder holder, View convertView){
		holder.trainNo = (TextView)convertView.findViewById(R.id.train_schedule_no);
		holder.trainDestination = (TextView)convertView.findViewById(R.id.train_schedule_destination);
		holder.trainSchedule = (TextView)convertView.findViewById(R.id.train_schedule_time);
		holder.trainPosition = (TextView)convertView.findViewById(R.id.train_schedule_position);
		holder.trainLine = (TextView)convertView.findViewById(R.id.train_schedule_line);
		holder.trainPositionStatus = (TextView)convertView.findViewById(R.id.train_schedule_position_atdep);
		holder.trainPositionBackground = (LinearLayout)convertView.findViewById(R.id.train_schedule_destination_background);
		holder.optionButton = (ImageView)convertView.findViewById(R.id.train_schedule_option);
	}

	static class ViewHolder {
		TextView trainNo;
		TextView trainDestination;
		TextView trainSchedule;
		TextView trainPosition;
		TextView trainPositionStatus;
		TextView trainLine;
		TextView noSchedule;
		ImageView optionButton;
		LinearLayout trainPositionBackground;
	}

	public void refreshList(ArrayList<TrainModelData> rowArray){
		this.rowArray = rowArray;
		notifyDataSetChanged();
	}

	private void setColorUI(String backgroundColor, String textColor, ViewHolder holder){
		holder.trainPositionBackground.setBackgroundColor(Color.parseColor(backgroundColor));
		holder.trainDestination.setTextColor(Color.parseColor(textColor));
	}

}
