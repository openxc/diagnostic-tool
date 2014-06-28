package com.openxc.openxcdiagnostic.diagnostic;

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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

import com.openxc.VehicleManager;
import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.KeyMatcher;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.util.ActivityLauncher;
import com.openxc.openxcdiagnostic.util.Utilities;

public class DiagnosticActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private DiagnosticSettingsManager mSettingsManager;
    private DiagnosticInputManager mInputManager;
    private DiagnosticButtonsManager mButtonsManager;
    private DiagnosticFavoritesAlertManager mFavoritesAlertManager;
    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private DiagnosticOutputTable mOutputTable;

    DiagnosticResponse.Listener mResponseListener = new DiagnosticResponse.Listener() {
        @Override
        public void receive(final DiagnosticRequest request,
                final DiagnosticResponse response) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOutputTable.addRow(request, response);
                    if (request.getFrequency() == null
                            || request.getFrequency() == 0) {
                        mVehicleManager.removeListener(KeyMatcher.buildExactMatcher(request), 
                                DiagnosticActivity.this.mResponseListener);
                    }
                    scrollOutputToTop();
                }
            });
        }
    };
    
    CommandResponse.Listener mCommandResponseListener = new CommandResponse.Listener() {
        @Override
        public void receive(final Command command, final CommandResponse response) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mOutputTable.addRow(command, response);
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

    public void deleteAllOutputResponses() {
        mOutputTable.deleteAllRows();
    }

    public void send(VehicleMessage request) {
        
        if (request instanceof DiagnosticRequest) {
            // TODO JUST FOR TESTING! should be
            // registerForResponse(request);
            // mVehicleManager.request(request);
            DiagnosticRequest diagRequest = (DiagnosticRequest) request;
            mResponseListener.receive(diagRequest, Utilities.generateRandomFakeResponse(diagRequest));
        } else if (request instanceof Command) {
    
            // TODO JUST FOR TESTING! should be
            //...something else
            Command command = (Command)request;
            mCommandResponseListener.receive(command, Utilities.generateRandomFakeCommandResponse(command));
        } else {
            Log.w(TAG, "Unable to send unrecognized request type");
        }
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

    public void populateFields(VehicleMessage req) {
        mInputManager.populateFields(req);
    }

    public void takeSendButtonPush() {
        hideKeyboard();
        getCurrentFocus().clearFocus();
        
        VehicleMessage request;
        if (mSettingsManager.shouldDisplayCommands()) {
            request = mInputManager.generateCommandFromInput();
            
        } else {
             request = mInputManager.generateDiagnosticRequestFromInput();
        }
        if (request != null) {
            send(request);
        }
    }

    public void takeClearButtonPush() {
        mInputManager.clearFields();
        hideKeyboard();
        getCurrentFocus().clearFocus();
    }

    public void takeFavoritesButtonPush() {
        mFavoritesAlertManager.showAlert(mSettingsManager.shouldDisplayCommands());
    }

    public void takeSettingsButtonPush() {
        mSettingsManager.showAlert();
    }

    public void hideKeyboard() {

        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager.isAcceptingText()) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    
    public void toggleRequestCommand(boolean displayCommands) {
        //order matters here
        mInputManager.toggleRequestCommand(displayCommands);
        mButtonsManager.toggleRequestCommand(displayCommands);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic);
        Log.i(TAG, "Vehicle diagnostic created");

        DiagnosticFavoritesManager.init(this);
        mFavoritesAlertManager = new DiagnosticFavoritesAlertManager(this);
        mSettingsManager = new DiagnosticSettingsManager(this);
        boolean displayCommands = mSettingsManager.shouldDisplayCommands();
        //order matters here
        mInputManager = new DiagnosticInputManager(this, displayCommands);
        mButtonsManager = new DiagnosticButtonsManager(this, displayCommands);

        mOutputTable = new DiagnosticOutputTable(this);
        mOutputTable.load();
    }

    @Override
    public void onDestroy() {
        mVehicleManager.removeListener(mResponseListener);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }
}
