package com.pantum.model;

import android.content.Context;

import com.pantum.R;
import com.pantum.utility.ConstantVariable;

public class TrainModelData {
	public Context mContext;
	public String trainNumber;
	public String destination;
	public String scheduleArrive;
	public String currentPosition;
	public String positionStatus;
	public int trainLine;
	public String backgroundColor;
	public String noTrainMessage;
	public int size;
	
	public Context getmContext() {
		return mContext;
	}
	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}
	public String getTrainNumber() {
		return trainNumber;
	}
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		String[] destinationArray = destination.split(ConstantVariable.DASH_SPLITTER);
		this.destination = destinationArray[1];
	}
	public String getScheduleArrive() {
		return scheduleArrive;
	}
	public void setScheduleArrive(String scheduleArrive) {
		this.scheduleArrive = scheduleArrive;
	}
	public String getCurrentPosition() {
		return currentPosition;
	}
	public void setCurrentPosition(String currentPosition) {
		String[] positionArray = currentPosition.split(ConstantVariable.SPACE_SPLITTER);
		this.currentPosition = positionArray[1];
		setPositionStatus(positionArray[0]);
	}
	public String getPositionStatus() {
		return positionStatus;
	}
	public void setPositionStatus(String positionStatus) {
		if(positionStatus.equalsIgnoreCase("di")){
			positionStatus = mContext.getResources().getString(R.string.train_position_at);
		}else{
			positionStatus = mContext.getResources().getString(R.string.train_position_depart);
		}
		this.positionStatus = positionStatus;
	}
	public int getTrainLine() {
		return trainLine;
	}
	public void setTrainLine(int trainLine) {
		this.trainLine = trainLine;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public String getNoTrainMessage() {
		return noTrainMessage;
	}
	public void setNoTrainMessage(String noTrainMessage) {
		this.noTrainMessage = noTrainMessage;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}
