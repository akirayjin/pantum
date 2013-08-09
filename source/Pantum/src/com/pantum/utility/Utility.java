package com.pantum.utility;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pantum.R;

public class Utility {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static void savePreference(String key, boolean value, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void savePreference(String key, int value, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void savePreference(String key, Long value, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static void savePreference(String key, float value, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public static void savePreference(String key, String value, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static boolean loadBooleanPreferences(String key, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}

	public static String loadStringPreferences(String key, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}

	public static int loadIntegerPreferences(String key, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, 0);
	}

	public static float loadFloadPreferences(String key, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		return sharedPreferences.getFloat(key, 0);
	}

	public static Long loadLongPreferences(String key, Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences("com.pantum", Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, 0);
	}

	public static ArrayList<String> getFavoriteArray(Context context){
		String[] stationArray = context.getResources().getStringArray(R.array.station_arrays);
		//ArrayList<String> stationArray = new ArrayList<String>(R.array.station_arrays);
		ArrayList<String> favoritedArray = new ArrayList<String>();
		for (int i = 0; i < stationArray.length; i++) {
			String currentStation = stationArray[i];
			boolean isFavorite = loadBooleanPreferences(currentStation, context);
			if(isFavorite){
				favoritedArray.add(currentStation);	
			}
		}
		return favoritedArray;
	}
}
