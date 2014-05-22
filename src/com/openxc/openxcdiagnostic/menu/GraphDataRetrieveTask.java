package com.openxc.openxcdiagnostic.menu;

import java.util.TimerTask;

import com.jjoe64.graphview.GraphViewSeries;
import com.openxc.NoValueException;
import com.openxc.VehicleManager;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.SteeringWheelAngle;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;

import android.app.Activity;
import android.util.Log;
import com.jjoe64.graphview.GraphView.GraphViewData;


public class GraphDataRetrieveTask extends TimerTask {
    private VehicleManager mVehicleManager;
    private GraphViewSeries mGraphSeries;
    private Class <? extends Measurement> mMeasurementType;
    private static String TAG = "GraphDataRetrieveTask";
    private int time = 0;
    private Activity mActivity;


    public GraphDataRetrieveTask(VehicleManager vehicleService, Activity act, GraphViewSeries series, Class <? extends Measurement> measurementType) {
        mVehicleManager = vehicleService;
        mGraphSeries = series;
        mMeasurementType = measurementType;
        mActivity = act;
    }

    public void run() {
        
        	if (mVehicleManager != null) {
        		mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                    	GraphDataRetrieveTask.this.time++;
		                try {
		                	Measurement measurement = mVehicleManager.get(mMeasurementType);
		                	int value = ((SteeringWheelAngle)measurement).getValue().intValue();
		                	mGraphSeries.resetData(new GraphViewData[] {});
		                	mGraphSeries.appendData(new GraphViewData(time, value), true, 10);
		                }
		                catch (UnrecognizedMeasurementTypeException e) {
		                	Log.w(TAG, "Unrecognized Measurement Type");
		                	e.printStackTrace();
		                } catch (NoValueException e) {
		                	Log.w(TAG, "No Value Available");
		                	e.printStackTrace();
		                } 
                    }
        		});
        	}
    }
        
}
