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
import android.content.res.Configuration;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.openxc.VehicleManager;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.menu.MenuActivity;
import com.openxc.openxcdiagnostic.resources.Utilities;

public class DiagnosticActivity extends Activity {

    private static String TAG = "VehicleDashboard";

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
    private Button mFactorInfoButton;
    private Button mOffsetInfoButton;
    private Button mNameInfoButton;
    private Map<Button, String> buttonInfo = new HashMap<>();
    private EditText mFrequencyInputText;
    private EditText mBusInputText;
    private EditText mIdInputText;
    private EditText mModeInputText;
    private EditText mPidInputText;
    private EditText mPayloadInputText;
    private EditText mFactorInputText;
    private EditText mOffsetInputText;
    private EditText mNameInputText;
    private List<View> responseRows = new ArrayList<>();
    private List<EditText> textFields = new ArrayList<>();

    DiagnosticResponse.Listener mResponseListener = new DiagnosticResponse.Listener() {
        public void receive(DiagnosticResponse response) {
            mHandler.post(new Runnable() {
                public void run() {

                }
            });
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Programmatically added views in the output disappear on orientation
        // change. This is a workaround to add them back
        LinearLayout outputRows = (LinearLayout) findViewById(R.id.outputRows);
        outputRows.removeAllViews();
        for (int i = 0; i < responseRows.size(); i++) {
            View row = responseRows.get(i);
            outputRows.addView(row);
        }
    }

    private void outputResponse(DiagnosticResponse response) {

        final LinearLayout outputRows = (LinearLayout) findViewById(R.id.outputRows);
        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.createsingleoutputrow, null);
        TextView output = (TextView) row.getChildAt(0);

        final Button deleteButton = (Button) row.getChildAt(1);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View row = (View) deleteButton.getParent();
                outputRows.removeView(row);
                responseRows.remove(row);
            }
        });

        Utilities.writeLine(output, "bus : "
                + String.valueOf(response.getCanBus()));
        Utilities.writeLine(output, "id : " + String.valueOf(response.getId()));
        Utilities.writeLine(output, "mode: "
                + String.valueOf(response.getMode()));
        boolean success = response.getSuccess();
        Utilities.writeLine(output, "success : " + String.valueOf(success));
        if (success) {
            Utilities.writeLine(output, "payload : "
                    + String.valueOf(response.getPayload()));
            output.append("value : " + String.valueOf(response.getValue()));
        } else {
            output.append("negative_response_code"
                    + response.getNegativeResponseCode().toString());
        }

        outputRows.addView(row, 0);
        responseRows.add(0, row);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void
                onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder) service).getService();
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return null;
    }

    private DiagnosticRequest generateDiagnosticRequestFromInputFields() {

        Map<String, Object> map = new HashMap<>();

        try {
            int freq = Integer.parseInt(mFrequencyInputText.getText().toString());
            if (freq > 0) {
                map.put(DiagnosticRequest.FREQUENCY_KEY, freq);
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Enter frequency does not appear to be a number.");
        }
        try {
            int bus = Integer.parseInt(mBusInputText.getText().toString());
            if (bus <= (int) DiagnosticRequest.BUS_RANGE.getMax()
                    && bus >= (int) DiagnosticRequest.BUS_RANGE.getMin()) {
                map.put(DiagnosticRequest.BUS_KEY, bus);
            } else {
                return failAndToastError("Invalid Bus entry. Did you mean 1 or 2?");
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered Bus does not appear to be an integer.");
        }
        try {
            int id = Integer.parseInt(mIdInputText.getText().toString());
            map.put(DiagnosticRequest.ID_KEY, id);
        } catch (NumberFormatException e) {
            return failAndToastError("Entered ID does not appear to be an integer.");
        }
        try {
            int mode = Integer.parseInt(mModeInputText.getText().toString());
            if (mode <= (int) DiagnosticRequest.MODE_RANGE.getMax()
                    && mode >= (int) DiagnosticRequest.MODE_RANGE.getMin()) {
                map.put(DiagnosticRequest.MODE_KEY, mode);
            } else {
                return failAndToastError("Invalid mode entry.  Mode must be 0 < Mode < 16");
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered Mode does not appear to be an integer.");
        }
        try {
            String pidInput = mPidInputText.getText().toString();
            // pid is optional, ok if empty
            if (!pidInput.equals("")) {
                int pid = Integer.parseInt(pidInput);
                map.put(DiagnosticRequest.PID_KEY, pid);
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered PID does not appear to be an integer.");
        }

        String payloadString = mPayloadInputText.getText().toString();
        if (!payloadString.equals("")) {
            if (payloadString.length() <= DiagnosticRequest.MAX_PAYLOAD_LENGTH_IN_CHARS) {
                if (payloadString.length() % 2 == 0) {
                    map.put(DiagnosticRequest.PAYLOAD_KEY, payloadString);
                } else {
                    return failAndToastError("Payload must have an even number of digits.");
                }
            } else {
                return failAndToastError("Payload can only be up to 7 bytes, i.e. 14 digits");
            }
        }

        try {
            String factorInput = mFactorInputText.getText().toString();
            // factor is optional, ok if empty
            if (!factorInput.equals("")) {
                float factor = Float.parseFloat(mFactorInputText.getText().toString());
                map.put(DiagnosticRequest.FACTOR_KEY, factor);
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered Factor does not appear to be a decimal number.");
        }
        try {
            String offsetInput = mOffsetInputText.getText().toString();
            // factor is optional, ok if empty
            if (!offsetInput.equals("")) {
                float offset = Float.parseFloat(offsetInput);
                map.put(DiagnosticRequest.OFFSET_KEY, offset);
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered Offset does not appear to be a decimal number.");
        }

        String name = mNameInputText.getText().toString();
        if (!name.equals("")) {
            map.put(DiagnosticRequest.NAME_KEY, name);
        }

        return new DiagnosticRequest(map);
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
                    DiagnosticResponse response = mVehicleManager.request(request);
                    outputResponse(response);
                }
            }
        });

        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < textFields.size(); i++) {
                    textFields.get(i).setText("");
                }
            }
        });

        initInfoButtons();
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

        mFactorInfoButton = (Button) findViewById(R.id.factorQuestionButton);
        buttonInfo.put(mFactorInfoButton, res.getString(R.string.factorInfo));
        infoMap.put(res.getString(R.string.factor_label), res.getString(R.string.factorInfo));

        mOffsetInfoButton = (Button) findViewById(R.id.offsetQuestionButton);
        buttonInfo.put(mOffsetInfoButton, res.getString(R.string.offsetInfo));
        infoMap.put(res.getString(R.string.offset_label), res.getString(R.string.offsetInfo));

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
        mBusInputText.setHint("Likely 1 or 2");
        textFields.add(mBusInputText);

        mIdInputText = (EditText) findViewById(R.id.idInput);
        mIdInputText.setHint("#");
        textFields.add(mIdInputText);

        mModeInputText = (EditText) findViewById(R.id.modeInput);
        mModeInputText.setHint(DiagnosticRequest.MODE_RANGE.getMin() + " - "
                + DiagnosticRequest.MODE_RANGE.getMax());
        textFields.add(mModeInputText);

        mPidInputText = (EditText) findViewById(R.id.pidInput);
        mPidInputText.setHint("#");
        textFields.add(mPidInputText);

        mPayloadInputText = (EditText) findViewById(R.id.payloadInput);
        mPayloadInputText.setHint("e.g. 0x1234");
        textFields.add(mPayloadInputText);

        mFactorInputText = (EditText) findViewById(R.id.factorInput);
        mFactorInputText.setHint("1.0");
        textFields.add(mFactorInputText);

        mOffsetInputText = (EditText) findViewById(R.id.offsetInput);
        mOffsetInputText.setHint("0");
        textFields.add(mOffsetInputText);

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
