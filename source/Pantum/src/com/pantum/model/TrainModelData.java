package com.pantum.model;

public class TrainModelData {
	public String trainNumber;
	public String destination;
	public String scheduleArrive;
	public String currentPosition;
	public int trainLine;
	public String backgroundColor;
	public String noTrainMessage;
	public int size;
	
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
		this.destination = destination;
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
		this.currentPosition = currentPosition;
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
