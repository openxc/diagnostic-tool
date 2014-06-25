package com.openxc.openxcdiagnostic.diagnostic;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.openxc.VehicleManager;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.KeyMatcher;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.menu.MenuActivity;
import com.openxc.openxcdiagnostic.util.Utilities;

public class DiagnosticActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private DiagnosticSettingsManager mSettingsManager;
    private InputManager mInputManager;
    private DiagnosticFavoritesAlertManager mFavoritesAlertManager;
    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private Button mFrequencyInfoButton;
    private Map<Button, String> buttonInfo = new HashMap<>();
    private DiagnosticOutputTable mOutputTable;

    DiagnosticResponse.Listener mResponseListener = new DiagnosticResponse.Listener() {
        @Override
        public void receive(final DiagnosticRequest request,
                final DiagnosticResponse response) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOutputTable.addRow(request, response);
                    scrollOutputToTop();
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

            if (mSettingsManager.shouldSniff()) {
                registerForAllResponses();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };

    private void scrollOutputToTop() {
        ((ScrollView) findViewById(R.id.responseOutputScroll)).fullScroll(View.FOCUS_UP);
    }

    private void initButtons() {

        Button sendRequestButton = (Button) findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                getCurrentFocus().clearFocus();
                DiagnosticRequest request = mInputManager.generateDiagnosticRequestFromInput();
                if (request != null) {
                    sendRequest(request);
                }
            }
        });

        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputManager.clearFields();
                hideKeyboard();
                getCurrentFocus().clearFocus();
            }
        });

        Button favoritesButton = (Button) findViewById(R.id.favoritesButton);
        favoritesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoritesAlertManager.showAlert();
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingsManager.showAlert();
            }
        });

        initInfoButtons();
    }

    public void deleteAllOutputResponses() {
        mOutputTable.deleteAllRows();
    }

    public void sendRequest(DiagnosticRequest request) {
        // TODO JUST FOR TESTING! should be
        // registerForResponse(request);
        // mVehicleManager.request(request);
        mResponseListener.receive(request, Utilities.generateRandomFakeResponse(request));
    }

    public void registerForResponse(DiagnosticRequest request) {
        mVehicleManager.addListener(KeyMatcher.buildExactMatcher(request), mResponseListener);
    }

    // TODO i'm thinking responses registered for individually will be received
    // by the listener
    // twice because they will match the wildcard KeyMatcher and their
    // exactMatcher...both
    // will be pairs in the MessageListenerSink map
    public void registerForAllResponses() {
        mVehicleManager.addListener(KeyMatcher.getWildcardMatcher(), mResponseListener);
    }

    /**
     * Stops the activity listening for all responses via the wildcard
     * KeyMatcher. It DOES NOT stop the activity from listening for responses
     * registered individually via KeyMatcher.buildExactMatcher(KeyedMessage)
     */
    public void stopListeningForAllResponses() {
        mVehicleManager.removeListener(KeyMatcher.getWildcardMatcher(), mResponseListener);
    }

    public void populateFields(DiagnosticRequest req) {
        mInputManager.populateFields(req);
    }

    private void initInfoButtons() {

        Resources res = getResources();
        final BiMap<String, String> infoMap = HashBiMap.create();

        mFrequencyInfoButton = (Button) findViewById(R.id.frequencyQuestionButton);
        buttonInfo.put(mFrequencyInfoButton, res.getString(R.string.frequency_info));
        infoMap.put(res.getString(R.string.frequency_label), res.getString(R.string.frequency_info));

        Button mBusInfoButton = (Button) findViewById(R.id.busQuestionButton);
        buttonInfo.put(mBusInfoButton, res.getString(R.string.bus_info));
        infoMap.put(res.getString(R.string.bus_label), res.getString(R.string.bus_info));

        Button mIdInfoButton = (Button) findViewById(R.id.idQuestionButton);
        buttonInfo.put(mIdInfoButton, res.getString(R.string.id_info));
        infoMap.put(res.getString(R.string.id_label), res.getString(R.string.id_info));

        Button mModeInfoButton = (Button) findViewById(R.id.modeQuestionButton);
        buttonInfo.put(mModeInfoButton, res.getString(R.string.mode_info));
        infoMap.put(res.getString(R.string.mode_label), res.getString(R.string.mode_info));

        Button mPidInfoButton = (Button) findViewById(R.id.pidQuestionButton);
        buttonInfo.put(mPidInfoButton, res.getString(R.string.pid_info));
        infoMap.put(res.getString(R.string.pid_label), res.getString(R.string.pid_info));

        Button mPayloadInfoButton = (Button) findViewById(R.id.payloadQuestionButton);
        buttonInfo.put(mPayloadInfoButton, res.getString(R.string.payload_info));
        infoMap.put(res.getString(R.string.payload_label), res.getString(R.string.payload_info));

        Button mNameInfoButton = (Button) findViewById(R.id.nameQuestionButton);
        buttonInfo.put(mNameInfoButton, res.getString(R.string.name_info));
        infoMap.put(res.getString(R.string.name_label), res.getString(R.string.name_info));

        for (final Button button : buttonInfo.keySet()) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DiagnosticActivity.this);
                    String info = buttonInfo.get(button);
                    builder.setMessage(info).setTitle(infoMap.inverse().get(info));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.create().show();
                }
            });
        }
    }

    public void hideKeyboard() {

        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager.isAcceptingText()) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic);
        Log.i(TAG, "Vehicle diagnostic created");

        // TODO ick
        DiagnosticFavoritesManager.init(this);
        mInputManager = new InputManager(this);
        mSettingsManager = new DiagnosticSettingsManager(this);
        mFavoritesAlertManager = new DiagnosticFavoritesAlertManager(this);

        initButtons();

        mOutputTable = new DiagnosticOutputTable(this);
        mOutputTable.load();
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
        if (item.getItemId() == R.id.Diagnostic) {
            // do nothing
            startActivity(new Intent(this, DiagnosticActivity.class));
        } else if (item.getItemId() == R.id.Menu) {
            startActivity(new Intent(this, MenuActivity.class));
            return true;
        } else if (item.getItemId() == R.id.Dashboard) {
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
