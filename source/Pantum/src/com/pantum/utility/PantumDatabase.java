package com.pantum.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.pantum.R;

public class PantumDatabase {
	private Context context;
	private JSONObject pantumJSON;
	private JSONArray stationsArray;
	private JSONArray colorArray;

	public PantumDatabase(Context context){
		this.context = context;
		getDatabseFromFile();
	}

	private void getDatabseFromFile(){
		String jsonString = getResourceRaw(R.raw.pantum_database).toString();
		String jsonStringColor = getResourceRaw(R.raw.background_colors).toString();
		Log.i("Pantum Database", jsonStringColor);
		
		try {
			JSONObject colorJSON = new JSONObject(jsonStringColor);
			pantumJSON = new JSONObject(jsonString);
			stationsArray = pantumJSON.getJSONArray("stations");
			colorArray = colorJSON.getJSONArray("colors");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private Writer getResourceRaw(int resourceFile){
		InputStream is = context.getResources().openRawResource(resourceFile);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader;
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return writer;
	}

	public String getStationCode(String stationName){
		String response = null;
		try {
			for (int i = 0; i < stationsArray.length(); i++) {
				String currentStationName = stationsArray.getJSONObject(i).getString("name");
				if(stationName.equalsIgnoreCase(currentStationName)){
					response = stationsArray.getJSONObject(i).getString("code");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	public String getClassBackgroundColor(String className){
		String response = null;
		try {
			for (int i = 0; i < colorArray.length(); i++) {
				String currentClassName = colorArray.getJSONObject(i).getString("name");
				if(className.equalsIgnoreCase(currentClassName)){
					response = colorArray.getJSONObject(i).getString("background");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	public String getClassTextColor(String className){
		String response = null;
		try {
			for (int i = 0; i < colorArray.length(); i++) {
				String currentClassName = colorArray.getJSONObject(i).getString("name");
				if(className.equalsIgnoreCase(currentClassName)){
					response = colorArray.getJSONObject(i).getString("text");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	public HashMap<String, String> getStationsCodeMap(){
		HashMap<String, String> response = new HashMap<String, String>();
		try {
			for (int i = 0; i < stationsArray.length(); i++) {
				String currentStationName = stationsArray.getJSONObject(i).getString("name");
				String currentStationCode = stationsArray.getJSONObject(i).getString("code");
				response.put(currentStationName, currentStationCode);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public GeoPoint getStationGeoPoint(String stationName){
		GeoPoint response = null;
		try {
			for (int i = 0; i < stationsArray.length(); i++) {
				String currentStationName = stationsArray.getJSONObject(i).getString("name");
				if(stationName.equalsIgnoreCase(currentStationName)){
					float latitude = Float.valueOf((String) stationsArray.getJSONObject(i).get("lat"));
					float longitude = Float.valueOf((String) stationsArray.getJSONObject(i).get("long"));
					response = new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	public String getLatitude(String stationName){
		String response = null;
		try {
			for (int i = 0; i < stationsArray.length(); i++) {
				String currentStationName = stationsArray.getJSONObject(i).getString("name");
				if(stationName.equalsIgnoreCase(currentStationName)){
					response = (String) stationsArray.getJSONObject(i).get("lat");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	public String getLongitude(String stationName){
		String response = null;
		try {
			for (int i = 0; i < stationsArray.length(); i++) {
				String currentStationName = stationsArray.getJSONObject(i).getString("name");
				if(stationName.equalsIgnoreCase(currentStationName)){
					response = (String) stationsArray.getJSONObject(i).get("long");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return response;
	}

}
