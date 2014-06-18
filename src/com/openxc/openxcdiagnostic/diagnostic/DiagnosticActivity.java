package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.openxc.VehicleManager;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.KeyMatcher;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.menu.MenuActivity;
import com.openxc.openxcdiagnostic.resources.Utilities;

public class DiagnosticActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private Toast mToast;
    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private Button sendRequestButton;
    private Button clearButton;
    private Button mFrequencyInfoButton;
    private Button mBusInfoButton;
    private Button mIdInfoButton;
    private Button mModeInfoButton;
    private Button mPidInfoButton;
    private Button mPayloadInfoButton;
    private Button mNameInfoButton;
    private Map<Button, String> buttonInfo = new HashMap<>();
    private EditText mFrequencyInputText;
    private EditText mBusInputText;
    private EditText mIdInputText;
    private EditText mModeInputText;
    private EditText mPidInputText;
    private EditText mPayloadInputText;
    private EditText mNameInputText;
    private List<EditText> textFields = new ArrayList<>();
    private DiagnosticOutputTable mOutputTable;
    private static final int MAX_PAYLOAD_LENGTH_IN_CHARS = DiagnosticRequest.MAX_PAYLOAD_LENGTH_IN_BYTES * 2;

    DiagnosticResponse.Listener mResponseListener = new DiagnosticResponse.Listener() {
        public void receive(final DiagnosticRequest request,
                final DiagnosticResponse response) {
            mHandler.post(new Runnable() {
                public void run() {
                    mOutputTable.addRow(request, response);
                    scrollOutputToTop();
                }
            });
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        public void
                onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder) service).getService();
            mVehicleManager.addListener(KeyMatcher.getWildcardMatcher(), mResponseListener);
            mIsBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };

    /**
     * Method to ensure that null is returned by
     * generateDiagnosticRequestFromInputFields() when it should be. There are
     * so many fail points in that method that it's safer to always return a
     * call to this method than to match up a "return null" statement everywhere
     * there should be a fail and a Toast. If the Toast happens when it should,
     * the fail must too.
     */
    private DiagnosticRequest failAndToastError(String message) {
        showToast(message, Toast.LENGTH_LONG);
        return null;
    }
    
    private void showToast(String message, int length) {
        
        if (mToast != null && toastIsDisplaying()) {
            mToast.cancel();
        }
            
        mToast = Toast.makeText(this, message, length);
        mToast.show(); 
    }
    
    private boolean toastIsDisplaying() {
        return mToast.getView().getWindowVisibility() == View.VISIBLE;
    }
    
    private DiagnosticRequest generateRequestFromRequiredInputFields() {
        
        Integer busId, id, mode;

        try {
            busId = Integer.parseInt(mBusInputText.getText().toString());
            if (busId < (int) DiagnosticRequest.BUS_RANGE.getMin()
                    || busId > (int) DiagnosticRequest.BUS_RANGE.getMax()) {
                return failAndToastError("Invalid Bus entry. Did you mean 1 or 2?");
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered Bus does not appear to be an integer.");
        }
        try {
            id = Integer.parseInt(mIdInputText.getText().toString());
            if (id < 0) {
                return failAndToastError("Id cannot be negative.");
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered ID does not appear to be an integer.");
        }
        try {
            mode = Integer.parseInt(mModeInputText.getText().toString(), 16);
            if (mode < (int) DiagnosticRequest.MODE_RANGE.getMin()
                    || mode > (int) DiagnosticRequest.MODE_RANGE.getMax()) {
                return failAndToastError("Invalid mode entry.  Mode must be " + 
                        "0x" + Integer.toHexString(DiagnosticRequest.MODE_RANGE.getMin()) 
                        + " <= Mode <= " + "0x" + Integer.toHexString(DiagnosticRequest.MODE_RANGE.getMax()));
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered Mode does not appear to be an integer.");
        }
        
        return new DiagnosticRequest(busId, id, mode);
    }

    private DiagnosticRequest generateDiagnosticRequestFromInputFields() {
        
        DiagnosticRequest request = generateRequestFromRequiredInputFields();
        if (request == null) {
            return null;
        }
        
        try {
            String freqInput = mFrequencyInputText.getText().toString();
            // frequency is optional, ok if empty
            if (!freqInput.equals("")) {
                double frequency = Double.parseDouble(freqInput);
                if (frequency > 0) {
                    request.setFrequency(frequency);
                } else {
                    return failAndToastError("Frequency cannot be negative.");
                }
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Enter frequency does not appear to be a number.");
        }
        
        try {
            String pidInput = mPidInputText.getText().toString();
            // pid is optional, ok if empty
            if (!pidInput.equals("")) {
                int pid = Integer.parseInt(pidInput);
                if (pid > 0) {
                    request.setPid(pid);
                }
                else {
                    return failAndToastError("Pid cannot be negative.");
                }
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered PID does not appear to be an integer.");
        }

        String payloadString = mPayloadInputText.getText().toString();
        if (!payloadString.equals("")) {
            if (payloadString.length() <= MAX_PAYLOAD_LENGTH_IN_CHARS) {
                if (payloadString.length() % 2 == 0) {
                    //TODO these can't be the right bytes but idk
                    request.setPayload(payloadString.getBytes());
                } else {
                    return failAndToastError("Payload must have an even number of digits.");
                }
            } else {
                return failAndToastError("Payload can only be up to 7 bytes, i.e. 14 digits");
            }
        }

        String name = mNameInputText.getText().toString();
        if (!name.trim().equals("")) {
            request.setName(name);
        }
        
        //TODO not retrieving this value from UI yet
        request.setMultipleResponses(false);
 
        return request;
    }
    
    private void scrollOutputToTop() {
        ((ScrollView) findViewById(R.id.responseOutputScroll)).fullScroll(ScrollView.FOCUS_UP);
    }

    private void initButtons() {

        sendRequestButton = (Button) findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                getCurrentFocus().clearFocus();
                DiagnosticRequest request = generateDiagnosticRequestFromInputFields();
                if (request != null) {
                    sendRequest(request);
                }
            }
        });

        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < textFields.size(); i++) {
                    textFields.get(i).setText("");
                    hideKeyboard();
                    getCurrentFocus().clearFocus();
                }
            }
        });

        initInfoButtons();
    }
    
    public void sendRequest(DiagnosticRequest request) {
        //TODO JUST FOR TESTING! should be 
        //mVehicleManager.request(request);
        mResponseListener.receive(request, Utilities.generateRandomFakeResponse(request));
    }

    private void initInfoButtons() {

        Resources res = getResources();
        final BiMap<String, String> infoMap = HashBiMap.create();

        mFrequencyInfoButton = (Button) findViewById(R.id.frequencyQuestionButton);
        buttonInfo.put(mFrequencyInfoButton, res.getString(R.string.frequencyInfo));
        infoMap.put(res.getString(R.string.frequency_label), res.getString(R.string.frequencyInfo));

        mBusInfoButton = (Button) findViewById(R.id.busQuestionButton);
        buttonInfo.put(mBusInfoButton, res.getString(R.string.busInfo));
        infoMap.put(res.getString(R.string.bus_label), res.getString(R.string.busInfo));

        mIdInfoButton = (Button) findViewById(R.id.idQuestionButton);
        buttonInfo.put(mIdInfoButton, res.getString(R.string.idInfo));
        infoMap.put(res.getString(R.string.id_label), res.getString(R.string.idInfo));

        mModeInfoButton = (Button) findViewById(R.id.modeQuestionButton);
        buttonInfo.put(mModeInfoButton, res.getString(R.string.modeInfo));
        infoMap.put(res.getString(R.string.mode_label), res.getString(R.string.modeInfo));

        mPidInfoButton = (Button) findViewById(R.id.pidQuestionButton);
        buttonInfo.put(mPidInfoButton, res.getString(R.string.pidInfo));
        infoMap.put(res.getString(R.string.pid_label), res.getString(R.string.pidInfo));

        mPayloadInfoButton = (Button) findViewById(R.id.payloadQuestionButton);
        buttonInfo.put(mPayloadInfoButton, res.getString(R.string.payloadInfo));
        infoMap.put(res.getString(R.string.payload_label), res.getString(R.string.payloadInfo));

        mNameInfoButton = (Button) findViewById(R.id.nameQuestionButton);
        buttonInfo.put(mNameInfoButton, res.getString(R.string.nameInfo));
        infoMap.put(res.getString(R.string.name_label), res.getString(R.string.nameInfo));

        for (final Button button : buttonInfo.keySet()) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DiagnosticActivity.this);
                    String info = buttonInfo.get(button);
                    builder.setMessage(info).setTitle(infoMap.inverse().get(info));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.create().show();
                }
            });
        }
    }

    private void hideKeyboard() {

        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager.isAcceptingText()) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void initTextFields() {

        mFrequencyInputText = (EditText) findViewById(R.id.frequencyInput);
        mFrequencyInputText.setHint("0");
        textFields.add(mFrequencyInputText);

        mBusInputText = (EditText) findViewById(R.id.busInput);
        mBusInputText.setHint("1 or 2");
        textFields.add(mBusInputText);

        mIdInputText = (EditText) findViewById(R.id.idInput);
        mIdInputText.setHint("#");
        textFields.add(mIdInputText);

        mModeInputText = (EditText) findViewById(R.id.modeInput);
        mModeInputText.setHint("0x" + Integer.toHexString(DiagnosticRequest.MODE_RANGE.getMin()) + " - "
                + "0x" + Integer.toHexString(DiagnosticRequest.MODE_RANGE.getMax()));
        textFields.add(mModeInputText);

        mPidInputText = (EditText) findViewById(R.id.pidInput);
        mPidInputText.setHint("#");
        textFields.add(mPidInputText);

        mPayloadInputText = (EditText) findViewById(R.id.payloadInput);
        mPayloadInputText.setHint("e.g. 0x1234");
        textFields.add(mPayloadInputText);

        mNameInputText = (EditText) findViewById(R.id.nameInput);
        textFields.add(mNameInputText);

        for (int i = 0; i < textFields.size(); i++) {
            final EditText textField = textFields.get(i);
            textField.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId,
                        KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard();
                        getCurrentFocus().clearFocus();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnostic);
        Log.i(TAG, "Vehicle diagnostic created");

        initButtons();
        initTextFields();
        mOutputTable = new DiagnosticOutputTable(this);
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
