package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;

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
import com.openxc.messages.DiagnosticMessage;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.ExactKeyMatcher;
import com.openxc.messages.KeyMatcher;
import com.openxc.messages.KeyedMessage;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.diagnostic.output.OutputTableManager;
import com.openxc.openxcdiagnostic.util.ActivityLauncher;
import com.openxc.openxcdiagnostic.util.DialogLauncher;
import com.openxc.openxcdiagnostic.util.MessageAnalyzer;
import com.openxc.openxcdiagnostic.util.ResponseEmulator;

public class DiagnosticActivity extends Activity {

    private static String TAG = "DiagnosticActivity";
    private static int FUNCTIONAL_BROADCAST_ID = 0x7DF;

    private SettingsManager mSettingsManager;
    private InputManager mInputManager;
    private ButtonManager mButtonsManager;
    private FavoritesAlertManager mFavoritesAlertManager;
    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private OutputTableManager mOutputTableManager;
    private ArrayList<DiagnosticManager> mManagers = new ArrayList<>();

    private ArrayList<DiagnosticRequest> outstandingRequests = new ArrayList<>();
    private ArrayList<Command> outstandingCommands = new ArrayList<>();

    boolean emulate = true;

    private VehicleMessage.Listener mResponseListener = new VehicleMessage.Listener() {
        @Override
        public void receive(final VehicleMessage response) {
            // prevent trying to add, for example, SimpleVehicleMessages
            // received due to sniffing
            if (shouldBeReceived(response)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        VehicleMessage request = findRequestThatMatchesResponse(response);
                        // update response if old one is in table, otherwise
                        // just add it
                        if (!mOutputTableManager.replaceIfMatchesExisting(request, response)) {
                            mOutputTableManager.add(request, response);
                        }

                        removeIfDone(request);
                    }
                });
            }
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

    private VehicleMessage findRequestThatMatchesResponse(
            VehicleMessage response) {

        if (MessageAnalyzer.isDiagnosticResponse(response)) {
            return findMatchingRequest((DiagnosticResponse) response);
        } else if (MessageAnalyzer.isCommandResponse(response)) {
            return findMatchingCommand((CommandResponse) response);
        } else {
            Log.e(TAG, "Attempted to find matching request/command for response of type: "
                    + response.getClass());
        }
        return null;
    }

    private Command findMatchingCommand(VehicleMessage msg) {

        ExactKeyMatcher matcher = null;
        if (MessageAnalyzer.isCommand(msg)) {
            matcher = ExactKeyMatcher.buildExactMatcher((Command) msg);
        } else if (MessageAnalyzer.isCommandResponse(msg)) {
            matcher = ExactKeyMatcher.buildExactMatcher((CommandResponse) msg);
        } else {
            Log.e(TAG, "Attempted to find matching command for object of type: "
                    + msg.getClass());
        }

        return (Command) MessageAnalyzer.findMatching(matcher, outstandingCommands);
    }

    private DiagnosticRequest findMatchingRequest(DiagnosticMessage msg) {

        ExactKeyMatcher matcher = null;
        if (MessageAnalyzer.isDiagnosticRequest(msg)) {
            matcher = ExactKeyMatcher.buildExactMatcher((DiagnosticRequest) msg);
        } else if (MessageAnalyzer.isDiagnosticResponse(msg)) {
            matcher = ExactKeyMatcher.buildExactMatcher((DiagnosticResponse) msg);
        } else {
            Log.e(TAG, "Attempted to find matching diagnostic request for object of type: "
                    + msg.getClass());
        }

        if (matcher == null) {
            return findMatchingFunctionalBroadcastRequest(msg);
        }

        return (DiagnosticRequest) MessageAnalyzer.findMatching(matcher, outstandingRequests);
    }

    private DiagnosticRequest findMatchingFunctionalBroadcastRequest(
            DiagnosticMessage msg) {

        for (int i = 0; i < outstandingRequests.size(); i++) {
            DiagnosticRequest request = outstandingRequests.get(i);
            if (request.getId() == FUNCTIONAL_BROADCAST_ID) {
                if (MessageAnalyzer.exactMatchExceptId(request, msg)) {
                    return request;
                }
            }
        }

        return null;
    }

    private boolean shouldBeReceived(VehicleMessage msg) {
        return MessageAnalyzer.isDiagnosticResponse(msg)
                || MessageAnalyzer.isCommandResponse(msg);
    }

    public void send(VehicleMessage request) {

        if (!MessageAnalyzer.canBeSent(request)) {
            Log.w(TAG, "Request must be of type DiagnosticRequest or Command...not sending.");
            return;
        }

        if (MessageAnalyzer.isDiagnosticRequest(request)) {
            // remove an outstanding request that matches if a new one is sent
            // because the new one will overwrite the old request in the VI
            DiagnosticRequest oldReq = findMatchingRequest((DiagnosticRequest) request);
            if (oldReq != null) {
                outstandingRequests.remove(oldReq);
            }
            outstandingRequests.add((DiagnosticRequest) request);
        } else if (MessageAnalyzer.isCommand(request)) {
            outstandingCommands.add((Command) request);
        }

        if (!emulate) {
            registerForResponse(request);
            if (!mVehicleManager.send(request)) {
                DialogLauncher.launchAlert(this, "Unable to Send", "The request or command could"
                        + " not be sent.  Ensure that the VI is on and connected.");
                return;
            }
        } else {
            ResponseEmulator.emulate(request, mResponseListener);
        }

    }

    private void registerForResponse(VehicleMessage request) {
        if (MessageAnalyzer.isDiagnosticRequest(request)) {
            mVehicleManager.addListener((DiagnosticRequest) request, mResponseListener);
        } else if (MessageAnalyzer.isCommand(request)) {
            mVehicleManager.addListener((Command) request, mResponseListener);
        } else {
            Log.w(TAG, "Unable to register for response of type: "
                    + request.getClass());
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

    public boolean multipleResponsesEnabled() {
        return mSettingsManager.multipleResponsesEnabled();
    }

    public void clearDiagnosticTable() {
        mOutputTableManager.deleteAllDiagnosticResponses();
    }

    public void clearCommandTable() {
        mOutputTableManager.deleteAllCommandResponses();
    }

    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (manager.isAcceptingText()) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            getCurrentFocus().clearFocus();
        }
    }

    public void setRequestCommandState(boolean displayCommands) {
        for (int i = 0; i < mManagers.size(); i++) {
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
        // order matters here
        mFavoritesAlertManager = new FavoritesAlertManager(this, displayCommands);
        mManagers.add(mFavoritesAlertManager);
        mInputManager = new InputManager(this, displayCommands);
        mManagers.add(mInputManager);
        mButtonsManager = new ButtonManager(this, displayCommands);
        mManagers.add(mButtonsManager);
        mOutputTableManager = new OutputTableManager(this, displayCommands);
        mManagers.add(mOutputTableManager);
    }

    public boolean isDisplayingCommands() {
        return mSettingsManager.shouldDisplayCommands();
    }

    public void cancelRecurringRequests() {
        for (int i = 0; i < outstandingRequests.size(); i++) {
            DiagnosticRequest req = outstandingRequests.get(i);
            if (req.getFrequency() != null && req.getFrequency() > 0) {
                req.setFrequency(null);
                send(req);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOutputTableManager.save();

        // TODO do we actually want to do this?
        removeListener(outstandingCommands, mResponseListener);
        removeListener(outstandingRequests, mResponseListener);
    }

    private void removeListener(ArrayList<? extends KeyedMessage> commands,
            VehicleMessage.Listener listener) {
        for (int i = 0; i < commands.size(); i++) {
            mVehicleManager.removeListener(commands.get(i), listener);
        }
    }

    private void removeIfDone(VehicleMessage request) {
        // unregister listener if frequency of request is 0 or null b/c no more
        // should come
        if (MessageAnalyzer.isDiagnosticRequest(request)) {
            DiagnosticRequest diagReq = (DiagnosticRequest) request;
            if (diagReq.getFrequency() == null || diagReq.getFrequency() == 0) {
                mVehicleManager.removeListener(diagReq, DiagnosticActivity.this.mResponseListener);
            }
        } else if (MessageAnalyzer.isCommand(request)) {
            mVehicleManager.removeListener((Command) request, DiagnosticActivity.this.mResponseListener);
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
        mOutputTableManager.save();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActivityLauncher.launchActivity(this, item.getItemId());
        mOutputTableManager.save();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }
}
