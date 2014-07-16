package com.openxc.openxcdiagnostic.dump;

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

import com.openxc.VehicleManager;
import com.openxc.messages.KeyMatcher;
import com.openxc.messages.VehicleMessage;
import com.openxc.messages.formatters.JsonFormatter;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.util.ActivityLauncher;

public class DumpActivity extends Activity {

    private static String TAG = "DumpActivity";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private LinearLayout mDumpLayout;
    
    VehicleMessage.Listener mDumpListener = new VehicleMessage.Listener() {
        @Override
        public void receive(final VehicleMessage response) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TextView newRow = new TextView(DumpActivity.this);
                    newRow.setText(JsonFormatter.serialize(response));
                    newRow.setTextColor(DumpActivity.this.getResources().getColor(R.color.lightBlue));
                    newRow.setTextSize(18f);
                    mDumpLayout.addView(newRow, 0);
                }
            });
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void
                onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder) service).getService();
            mIsBound = true;
            mVehicleManager.addListener(KeyMatcher.getWildcardMatcher(), mDumpListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dump);
        mDumpLayout = (LinearLayout) findViewById(R.id.dumpOutput);
        Log.i(TAG, "Vehicle Dump Created");
    }
    

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVehicleManager != null) {
            mVehicleManager.removeListener(KeyMatcher.getWildcardMatcher(), mDumpListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService(new Intent(this, VehicleManager.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIsBound) {
            Log.i(TAG, "Unbinding from vehicle service");
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActivityLauncher.launchActivity(this, item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }
}
