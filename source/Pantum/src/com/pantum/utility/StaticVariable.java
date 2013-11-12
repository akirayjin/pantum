package com.pantum.utility;

public class StaticVariable {
	public static String lastStationName;

	public static String getLastStationName() {
		return lastStationName;
	}

	public static void setLastStationName(String lastStationName) {
		StaticVariable.lastStationName = lastStationName;
	}

}
