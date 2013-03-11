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

import com.pantum.R;

import android.content.Context;
import android.util.Log;

public class PantumDatabase {
	private Context context;
	private JSONObject pantumJSON;
	private JSONArray stationsArray;

	public PantumDatabase(Context context){
		this.context = context;
		getDatabseFromFile();
	}

	private void getDatabseFromFile(){
		InputStream is = context.getResources().openRawResource(R.raw.pantum_database);
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

		String jsonString = writer.toString();
		
		try {
			pantumJSON = new JSONObject(jsonString);
			stationsArray = pantumJSON.getJSONArray("stations");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(this.toString(), getStationCode("Bogor"));
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

}
