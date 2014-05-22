package com.openxc.openxcdiagnostic.diagnostic;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.openxc.VehicleManager;
import com.openxc.measurements.DiagnosticMeasurement;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.menu.MenuActivity;

public class DiagnosticActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private Button mTestButton;


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
    
    public void initButton() {
        mTestButton = (Button) findViewById(
                R.id.hardButton);
        mTestButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		try {
					mVehicleManager.send(new DiagnosticMeasurement(1));
				} catch (UnrecognizedMeasurementTypeException e) {
					e.printStackTrace();
				}
        	}
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic);
        Log.i(TAG, "Vehicle dashboard created");

        initButton();
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

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Diagnostic) {
               //do nothing
               startActivity(new Intent(this, DiagnosticActivity.class));
        } else if (item.getItemId() == R.id.Menu) {
             startActivity(new Intent(this, MenuActivity.class));
             return true;
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
