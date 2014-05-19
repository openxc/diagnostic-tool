package com.openxc.openxcdiagnostic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.openxc.VehicleManager;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.SteeringWheelAngle;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.WindshieldWiperStatus;
import com.openxc.remote.VehicleServiceException;

public class GrapherActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private TextView mWiperStatusView;
    

    /*WindshieldWiperStatus.Listener mWiperListener =
            new WindshieldWiperStatus.Listener() {
        public void receive(Measurement measurement) {
            final WindshieldWiperStatus wiperStatus =
                (WindshieldWiperStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    //mWiperStatusView.setText(wiperStatus.toString());
                }
            });
        }
    };*/

    /*private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder)service
                    ).getService();

            try {
                mVehicleManager.addListener(SteeringWheelAngle.class,
                        mWiperListener);
            } catch(VehicleServiceException e) {
                Log.w(TAG, "Couldn't add listeners for measurements", e);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Couldn't add listeners for measurements", e);
            }
            mIsBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        Log.i(TAG, "Vehicle dashboard created");


        //mWiperStatusView = (TextView) findViewById(
                //R.id.wiper_status);
        
        // init example series data
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
            new GraphViewData(1, 2.0d)
            , new GraphViewData(2, 1.5d)
            , new GraphViewData(3, 2.5d)
            , new GraphViewData(4, 1.0d)
        });
         
        GraphView graphView = new LineGraphView(
            this // context
            , "GraphViewDemo" // heading
        );
        graphView.addSeries(exampleSeries); // data
        setContentView(R.layout.graphlayout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.graphlayout);
        layout.addView(graphView);
        //setContentView(layout);
    }
    
    /*try {
	Class<? extends Measurement> measurementType = GridManager.getClass(position);
	Measurement measurement = MenuActivity.this.mVehicleManager.get(measurementType);
	Toast.makeText(MenuActivity.this, measurementType.toString() + " : "+ measurement.toString(), Toast.LENGTH_SHORT).show();
} catch (UnrecognizedMeasurementTypeException e) {
    Log.w(TAG, "Unrecognized Measurement Type");
	e.printStackTrace();
} catch (NoValueException e) {
    Log.w(TAG, "No Value Available");
	e.printStackTrace();
}*/

    @Override
    public void onResume() {
        super.onResume();
        //bindService(new Intent(this, VehicleManager.class),
        //        mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if(mIsBound) {
            Log.i(TAG, "Unbinding from vehicle service");
            unbindService(mConnection);
            mIsBound = false;
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.Diagnostic) {
    		startActivity(new Intent(this, DiagnosticActivity.class));
    	} else if (item.getItemId() == R.id.Menu) {
    		startActivity(new Intent(this, MenuActivity.class));
            return true;
    	}
    	else if (item.getItemId() == R.id.Dashboard) {
    	    //do nothing
    	}
    	return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }
}
