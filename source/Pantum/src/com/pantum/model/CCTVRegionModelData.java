package com.pantum.model;

import java.util.ArrayList;

public class CCTVRegionModelData {
	public String regionName;
	public ArrayList<CCTVPlaceModelData> placesArray;
	
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public ArrayList<CCTVPlaceModelData> getPlacesArray() {
		return placesArray;
	}
	public void setPlacesArray(ArrayList<CCTVPlaceModelData> placesArray) {
		this.placesArray = placesArray;
	}
}
