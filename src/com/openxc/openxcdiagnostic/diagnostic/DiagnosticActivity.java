package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.Timer;

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
import android.view.inputmethod.InputMethodManager;
import com.openxc.VehicleManager;
import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.ExactKeyMatcher;
import com.openxc.messages.KeyMatcher;
import com.openxc.messages.KeyedMessage;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.diagnostic.output.OutputTableManager;
import com.openxc.openxcdiagnostic.util.ActivityLauncher;
import com.openxc.openxcdiagnostic.util.RecurringResponseGenerator;
import com.openxc.openxcdiagnostic.util.Utilities;

public class DiagnosticActivity extends Activity {

    private static String TAG = "DiagnosticActivity";

    private SettingsManager mSettingsManager;
    private InputManager mInputManager;
    private ButtonsManager mButtonsManager;
    private FavoritesAlertManager mFavoritesAlertManager;
    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private OutputTableManager mOutputTableManager;
    private ArrayList<DiagnosticManager> mManagers = new ArrayList<>();
    
    private ArrayList<DiagnosticRequest> outstandingRequests = new ArrayList<>();
    private ArrayList<Command> outstandingCommands = new ArrayList<>();
    
    boolean emulate = true;
    private RecurringResponseGenerator mGenerator;
    private Timer mTimer;

    VehicleMessage.Listener mResponseListener = new VehicleMessage.Listener() {
        @Override
        public void receive(final VehicleMessage response) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //prevent trying to add, for example, SimpleVehicleMessages received
                    //due to sniffing
                    if (Utilities.isDiagnosticResponse(response) 
                            || Utilities.isCommandResponse(response)) {
                        
                        //update response if old one is in table
                        if (!mOutputTableManager.replaceIfMatchesExisting(findRequest(response), response)) {;
                            mOutputTableManager.add(findRequest(response), response);
                        }
                    }
                    /*if (message.getFrequency() == null
                            || request.getFrequency() == 0) {
                        mVehicleManager.removeListener(KeyMatcher.buildExactMatcher(request), 
                                DiagnosticActivity.this.mResponseListener);
                    }*/
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
                startSniffing();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };
    
    private VehicleMessage findRequest(VehicleMessage message) {
        
        if (Utilities.isDiagnosticResponse(message)) {
            ExactKeyMatcher matcher = ExactKeyMatcher.buildExactMatcher((DiagnosticResponse) message);
            for (int i=0; i < outstandingRequests.size(); i++) {
                DiagnosticRequest request = outstandingRequests.get(i);
                if (matcher.matches(request)) {
                    outstandingRequests.remove(request);
                    return request;
                }
            }
        } else if (Utilities.isCommandResponse(message)) {
            ExactKeyMatcher matcher = ExactKeyMatcher.buildExactMatcher((CommandResponse) message);
            for (int i=0; i < outstandingCommands.size(); i++) {
                Command command = outstandingCommands.get(i);
                if (matcher.matches(command)) {
                    outstandingCommands.remove(command);
                    return command;
                }
            }
        }
        return null;
    }

    public void send(VehicleMessage request) {
                
        if (Utilities.isDiagnosticRequest(request)) {
            outstandingRequests.add((DiagnosticRequest) request);
            if (emulate) {
                mResponseListener.receive(Utilities.generateRandomFakeResponse((DiagnosticRequest) request));
            }
        } else if (Utilities.isCommand(request)) {
            outstandingCommands.add((Command) request);
            if (emulate) {
                mResponseListener.receive(Utilities.generateRandomFakeCommandResponse((Command) request));
            }
        } else {
            Log.w(TAG, "Request must be of type DiagnosticRequest or Command...not sending.");
            return;
        }
        
        if (!emulate) {
            registerForResponse(request);
            mVehicleManager.send(request);
        } else {
            if (Utilities.isDiagnosticRequest(request)) {
                DiagnosticRequest diagReq = (DiagnosticRequest) request;
                if (diagReq.getFrequency() != null && diagReq.getFrequency() > 0) {
                    mGenerator = new RecurringResponseGenerator(diagReq, mResponseListener);
                    mTimer = new Timer();
                    mTimer.schedule(mGenerator, 100, 1000);
                }
            }
        }
    }
    
    private void registerForResponse(VehicleMessage request) {
        if (Utilities.isDiagnosticRequest(request)) {
            mVehicleManager.addListener((DiagnosticRequest) request, mResponseListener);
        } else if (Utilities.isCommand(request)) {
            mVehicleManager.addListener((Command) request, mResponseListener);
        } else {
            Log.w(TAG, "Unable to register for response of type: " + request.getClass());
        }
    }
    
    public void startSniffing() {
        mVehicleManager.addListener(KeyMatcher.getWildcardMatcher(), mResponseListener);
    }

    /**
     * Stops the activity listening for all responses via the wildcard
     * KeyMatcher. It DOES NOT stop the activity from listening for responses
     * which were registered for individually.
     */
    public void stopSniffing() {
        mVehicleManager.removeListener(KeyMatcher.getWildcardMatcher(), mResponseListener);
    }

    public void populateFields(VehicleMessage req) {
        mInputManager.populateFields(req);
    }

    public Command generateCommandFromInput() {
        return mInputManager.generateCommandFromInput();
    }
    
    public DiagnosticRequest generateDiagnosticRequestFromInput() {
        return mInputManager.generateDiagnosticRequestFromInput();
    }

    public void clearFields() {
        mInputManager.clearFields();
    }

    public void launchFavorites() {
        mFavoritesAlertManager.showAlert();
    }

    public void launchSettings() {
        mSettingsManager.showAlert();
    }
    
    public boolean shouldScroll() {
        return mSettingsManager.shouldScroll();
    }

    public void clearDiagnosticTable() {
        mOutputTableManager.deleteAllDiagnosticResponses();
    }
    
    public void clearCommandTable() {
        mOutputTableManager.deleteAllCommandResponses();
    }
    
    public void hideKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (manager.isAcceptingText()) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            getCurrentFocus().clearFocus();
        }
    }
    
    public void setRequestCommandState(boolean displayCommands) {        
        for (int i=0; i < mManagers.size(); i++) {
            mManagers.get(i).setRequestCommandState(displayCommands);
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic);
        Log.i(TAG, "Vehicle Diagnostic created");

        mSettingsManager = new SettingsManager(this);
        FavoritesManager.init(this);
        boolean displayCommands = isDisplayingCommands();
        //order matters here
        mFavoritesAlertManager = new FavoritesAlertManager(this, displayCommands);
        mManagers.add(mFavoritesAlertManager);
        mInputManager = new InputManager(this, displayCommands);
        mManagers.add(mInputManager);
        mButtonsManager = new ButtonsManager(this, displayCommands);
        mManagers.add(mButtonsManager);
        mOutputTableManager = new OutputTableManager(this, displayCommands);
        mManagers.add(mOutputTableManager);
       
    }
    
    public boolean isDisplayingCommands() {
        return mSettingsManager.shouldDisplayCommands();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListener(outstandingCommands, mResponseListener);
        removeListener(outstandingRequests, mResponseListener);
    }
    
    private void removeListener(ArrayList<? extends KeyedMessage> commands, VehicleMessage.Listener listener) {
        for (int i=0; i < commands.size(); i++) {
            mVehicleManager.removeListener(commands.get(i), listener);
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
