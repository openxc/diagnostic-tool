package com.openxc.openxcdiagnostic;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.openxc.NoValueException;
import com.openxc.VehicleManager;
import com.openxc.measurements.IgnitionStatus;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.SteeringWheelAngle;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.VehicleDoorStatus;
import com.openxc.openxcdiagnostic.resources.GridManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MenuActivity extends Activity {

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private static String TAG = "Menu";

    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder)service
                    ).getService();

            mIsBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new GridImageAdapter(this, GridManager.MenuThumbIDs, GridManager.MenuButtonUnpressedImgID));
        gridview.setBackgroundColor(Color.BLACK);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	flipButtonBackground(v);
                GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
                        new GraphViewData(1, 2.0d)
                        , new GraphViewData(2, 1.5d)
                        , new GraphViewData(3, 2.5d)
                        , new GraphViewData(4, 1.0d)
                    });
                     
                    /*GraphView graphView = new LineGraphView(
                        MenuActivity.this // context
                        , "GraphViewDemo" // heading
                    );
                    graphView.addSeries(exampleSeries); // data
                     
                    LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
                    layout.addView(graphView);
                    MenuActivity.this.setContentView(R.layout.graph);*/
				
                try {
                	Class<? extends Measurement> measurementType = GridManager.getClass(position);
					Measurement measurement = MenuActivity.this.mVehicleManager.get(measurementType);
	            	Toast.makeText(MenuActivity.this, measurementType.toString() + " : "+ measurement.toString(), Toast.LENGTH_SHORT).show();
				} catch (UnrecognizedMeasurementTypeException e) {
		            Log.w(TAG, "Unrecognized Measurement Type");
					e.printStackTrace();
				} catch (NoValueException e) {
		            Log.w(TAG, "No Value Available");
					e.printStackTrace();
				}
            }
        });
        
    }

    
    private void flipButtonBackground(View v) {
    	Drawable pressed = getResources().getDrawable(GridManager.MenuButtonPressedImgID);
    	Drawable unpressed = getResources().getDrawable(GridManager.MenuButtonUnpressedImgID);
    	if (GridManager.drawablesAreEqual(v.getBackground(), pressed)) {
    		v.setBackground(unpressed);
    	} else if (GridManager.drawablesAreEqual(v.getBackground(), unpressed)) {
    		v.setBackground(pressed);
    	}    	
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
    		//do nothing
    	}
    	else if (item.getItemId() == R.id.Dashboard) {
    	    startActivity(new Intent(this, DashboardActivity.class));
            return true;
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
