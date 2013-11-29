package com.pantum.utility;

import java.util.ArrayList;

import com.pantum.model.TrainModelData;

public interface OnRequestListener {
	void onFinish(ArrayList<TrainModelData> response);
	void onRefresh();
	void onAutoRefresh();
}
