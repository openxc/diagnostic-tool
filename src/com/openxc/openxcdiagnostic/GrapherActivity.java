package com.openxc.openxcdiagnostic;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.openxc.VehicleManager;
import com.openxc.measurements.Measurement;
import com.openxc.openxcdiagnostic.resources.GridManager;

public class GrapherActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private TimerTask mUpdateGraphTask;
    private Timer mTimer;
    private GraphViewSeries dataSeries;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder)service
                    ).getService();
            
            mIsBound = true;
            
            Integer gridPosition = getIntent().getExtras().getInt("pos");
            Class <? extends Measurement> measurementType = GridManager.getClass(gridPosition);
            mUpdateGraphTask = new GraphDataRetrieveTask(mVehicleManager, GrapherActivity.this, dataSeries, measurementType);
            mTimer = new Timer();
            mTimer.schedule(mUpdateGraphTask, 100, 1000);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        Log.i(TAG, "Vehicle dashboard created");
        
        // init example series data
        dataSeries = new GraphViewSeries(new GraphViewData[] {});
         
        GraphView graphView = new LineGraphView(
            this // context
            , "GraphViewDemo" // heading
        );
        graphView.addSeries(dataSeries); // data
        graphView.setScrollable(true);
        graphView.setScalable(true);
        graphView.setManualYAxisBounds(1000, 0);
        setContentView(R.layout.graphlayout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.graphlayout);
        layout.addView(graphView);

    }

    @Override
    public void onResume() {
        super.onResume();
        bindService(new Intent(this, VehicleManager.class),
                mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mIsBound) {
            Log.i(TAG, "Unbinding from vehicle service");
            unbindService(mConnection);
            mIsBound = false;
        }
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
