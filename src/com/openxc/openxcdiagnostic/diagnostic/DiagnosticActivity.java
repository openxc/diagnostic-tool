package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.openxc.VehicleManager;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.menu.MenuActivity;

public class DiagnosticActivity extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();
    private Button sendRequestButton;
    private Button clearButton;
    private EditText mBusInputText;
    private EditText mIdInputText;
    private EditText mModeInputText;
    private EditText mPidInputText;
    private EditText mPayloadInputText;
    private EditText mFactorInputText;
    private EditText mOffsetInputText;
    private EditText mNameInputText;
    private List<EditText> textFields = new ArrayList<EditText>();

    DiagnosticResponse.Listener mResponseListener = new DiagnosticResponse.Listener() {
        public void receive(DiagnosticResponse response) {
            mHandler.post(new Runnable() {
                public void run() {

                }
            });
        }
    };

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

    private DiagnosticRequest generateDiagnosticRequestFromInputFields() {

        int bus = Integer.parseInt(mBusInputText.getText().toString());
        int id = Integer.parseInt(mIdInputText.getText().toString());
        int mode = Integer.parseInt(mModeInputText.getText().toString());
        int pid = Integer.parseInt(mPidInputText.getText().toString());
        byte[] payload = mPayloadInputText.getText().toString().getBytes(); // TODO
                                                                            // ?
        float factor = Float.parseFloat(mFactorInputText.getText().toString());
        float offset = Float.parseFloat(mOffsetInputText.getText().toString());
        String name = mNameInputText.getText().toString();

        return new DiagnosticRequest(bus, id, mode, pid, payload, factor, false, offset, 0f, name);
    }

    private boolean inputFieldsAreValid() {

        try {
            int bus = Integer.parseInt(mBusInputText.getText().toString());
            if (bus < 1) {
                Toast.makeText(this, "Invalid Bus entry. Did you mean 1 or 2?", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered Bus does not appear to be an integer.", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            Integer.parseInt(mIdInputText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered ID does not appear to be an integer.", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            int mode = Integer.parseInt(mModeInputText.getText().toString());
            if (mode < 1 || mode > 15) {
                Toast.makeText(this, "Invalid mode entry.  Mode must be 0 < Mode < 16", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered Mode does not appear to be an integer.", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            String pidInput = mPidInputText.getText().toString();
            if (!pidInput.equals("")) {
                Integer.parseInt(pidInput);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered PID does not appear to be an integer.", Toast.LENGTH_LONG).show();
            return false;
        }

        // TODO check if this is valid
        mPayloadInputText.getText().toString();

        try {
            Float.parseFloat(mFactorInputText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered Factor does not appear to be a decimal number.", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            Float.parseFloat(mOffsetInputText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Entered Offset does not appear to be a decimal number.", Toast.LENGTH_LONG).show();
            return false;
        }

        // TODO check if this is valid
        mNameInputText.getText().toString();

        return true;
    }

    private void initButtons() {

        sendRequestButton = (Button) findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                getCurrentFocus().clearFocus();
                if (inputFieldsAreValid()) {
                    mVehicleManager.request(generateDiagnosticRequestFromInputFields());
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
    }

    private void hideKeyboard() {

        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager.isAcceptingText()) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void initTextFields() {

        mBusInputText = (EditText) findViewById(R.id.busInput);
        mBusInputText.setHint("Likely 1 or 2");
        textFields.add(mBusInputText);

        mIdInputText = (EditText) findViewById(R.id.idInput);
        textFields.add(mIdInputText);

        mModeInputText = (EditText) findViewById(R.id.modeInput);
        textFields.add(mModeInputText);

        mPidInputText = (EditText) findViewById(R.id.pidInput);
        textFields.add(mPidInputText);

        mPayloadInputText = (EditText) findViewById(R.id.payloadInput);
        mPayloadInputText.setHint("e.g. 0x1234");
        textFields.add(mPayloadInputText);

        mFactorInputText = (EditText) findViewById(R.id.factorInput);
        textFields.add(mFactorInputText);

        mOffsetInputText = (EditText) findViewById(R.id.offsetInput);
        textFields.add(mOffsetInputText);

        mNameInputText = (EditText) findViewById(R.id.nameInput);
        mNameInputText.setHint("Lorem Ipsum");
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
