package com.openxc.openxcdiagnostic.diagnostic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.messages.DiagnosticMessage;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.util.Toaster;

public class DiagnosticInputManager {

    private EditText mFrequencyInputText;
    private EditText mBusInputText;
    private EditText mIdInputText;
    private EditText mModeInputText;
    private EditText mPidInputText;
    private EditText mPayloadInputText;
    private EditText mNameInputText;
    private List<EditText> textFields = new ArrayList<>();
    private static final int MAX_PAYLOAD_LENGTH_IN_CHARS = DiagnosticRequest.MAX_PAYLOAD_LENGTH_IN_BYTES * 2;
    private SharedPreferences mPreferences;
    private DiagnosticActivity mContext;

    private class InputHolder {
        private String frequencyInput = getFrequencyInput();
        private String busInput = getBusInput();
        private String idInput = getIdInput();
        private String modeInput = getModeInput();
        private String pidInput = getPidInput();
        private String payloadInput = getPayloadInput();
        private String nameInput = getNameInput();

        private InputHolder() {
        }
    }

    public DiagnosticInputManager(DiagnosticActivity context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        initTextFields();
        restoreFields();
    }
    
    public void toggleRequestCommand() {
        
    }

    public void populateFields(DiagnosticRequest req) {
        mFrequencyInputText.setText(selfOrEmptyIfNull(String.valueOf(req.getFrequency())));
        mBusInputText.setText(selfOrEmptyIfNull(String.valueOf(req.getBusId())));
        mIdInputText.setText(selfOrEmptyIfNull(String.valueOf(req.getId())));
        mModeInputText.setText(selfOrEmptyIfNull(Integer.toHexString(req.getMode()).toUpperCase(Locale.US)));
        mPidInputText.setText(selfOrEmptyIfNull(String.valueOf(req.getPid())));
        if (req.getPayload() != null) {
            mPayloadInputText.setText(selfOrEmptyIfNull(new String(req.getPayload())));
        } else {
            mPayloadInputText.setText("");
        }
        mNameInputText.setText(selfOrEmptyIfNull(String.valueOf(req.getName())));
    }

    public void populateFields(InputHolder holder) {
        mFrequencyInputText.setText(holder.frequencyInput);
        mBusInputText.setText(holder.busInput);
        mIdInputText.setText(holder.idInput);
        mModeInputText.setText(holder.modeInput);
        mPidInputText.setText(holder.pidInput);
        mPayloadInputText.setText(holder.payloadInput);
        mNameInputText.setText(holder.nameInput);
    }

    private String selfOrEmptyIfNull(String st) {
        if (st == null || st.toLowerCase(Locale.US).equals("null")) {
            return "";
        }
        return st;
    }

    public void clearFields() {
        for (int i = 0; i < textFields.size(); i++) {
            textFields.get(i).setText("");
        }
    }

    private void initTextFields() {
        mFrequencyInputText = (EditText) mContext.findViewById(R.id.frequencyInput);
        mFrequencyInputText.setHint("0");
        textFields.add(mFrequencyInputText);

        mBusInputText = (EditText) mContext.findViewById(R.id.busInput);
        mBusInputText.setHint("1 or 2");
        textFields.add(mBusInputText);

        mIdInputText = (EditText) mContext.findViewById(R.id.idInput);
        mIdInputText.setHint("#");
        textFields.add(mIdInputText);

        mModeInputText = (EditText) mContext.findViewById(R.id.modeInput);
        mModeInputText.setHint("0x"
                + Integer.toHexString(DiagnosticMessage.MODE_RANGE.getMin())
                + " - " + "0x"
                + Integer.toHexString(DiagnosticMessage.MODE_RANGE.getMax()));
        textFields.add(mModeInputText);

        mPidInputText = (EditText) mContext.findViewById(R.id.pidInput);
        mPidInputText.setHint("#");
        textFields.add(mPidInputText);

        mPayloadInputText = (EditText) mContext.findViewById(R.id.payloadInput);
        mPayloadInputText.setHint("e.g. 0x1234");
        textFields.add(mPayloadInputText);

        mNameInputText = (EditText) mContext.findViewById(R.id.nameInput);
        textFields.add(mNameInputText);

        for (int i = 0; i < textFields.size(); i++) {
            final EditText textField = textFields.get(i);
            textField.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId,
                        KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        mContext.hideKeyboard();
                        mContext.getCurrentFocus().clearFocus();
                    }
                    return false;
                }
            });
            textField.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                        int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                        int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    saveFields();
                }
            });
        }
    }

    private void saveFields() {
        InputHolder inputHolder = new InputHolder();
        Editor prefsEditor = mPreferences.edit();
        String json = (new Gson()).toJson(inputHolder);
        prefsEditor.putString(getInputKey(), json);
        prefsEditor.commit();
    }

    private void restoreFields() {

        @SuppressWarnings("serial")
        Type type = new TypeToken<InputHolder>() {
        }.getType();
        String json = mPreferences.getString(getInputKey(), "");
        InputHolder inputHolder = (new Gson()).fromJson(json, type);
        if (inputHolder != null) {
            populateFields(inputHolder);
        }
    }

    private String getInputKey() {
        return "input_key";
    }

    /**
     * Method to ensure that null is returned by
     * generateDiagnosticRequestFromInputFields() when it should be. There are
     * so many fail points in that method that it's safer to always return a
     * call to this method than to match up a "return null" statement everywhere
     * there should be a fail and a Toast. If the Toast happens when it should,
     * the fail must too.
     */
    private DiagnosticRequest failAndToastError(String message) {
        Toaster.showToast(mContext, message);
        return null;
    }

    private DiagnosticRequest generateRequestFromRequiredInputFields() {

        Integer busId, id, mode;

        try {
            busId = Integer.parseInt(getBusInput());
            if (busId < DiagnosticMessage.BUS_RANGE.getMin()
                    || busId > DiagnosticMessage.BUS_RANGE.getMax()) {
                return failAndToastError("Invalid Bus entry. Did you mean 1 or 2?");
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered Bus does not appear to be an integer.");
        }
        try {
            id = Integer.parseInt(getIdInput());
            if (id < 0) {
                return failAndToastError("Id cannot be negative.");
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered ID does not appear to be an integer.");
        }
        try {
            mode = Integer.parseInt(getModeInput(), 16);
            if (mode < DiagnosticMessage.MODE_RANGE.getMin()
                    || mode > DiagnosticMessage.MODE_RANGE.getMax()) {
                return failAndToastError("Invalid mode entry.  Mode must be "
                        + "0x"
                        + Integer.toHexString(DiagnosticMessage.MODE_RANGE.getMin())
                        + " <= Mode <= "
                        + "0x"
                        + Integer.toHexString(DiagnosticMessage.MODE_RANGE.getMax()));
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered Mode does not appear to be an integer.");
        }

        return new DiagnosticRequest(busId, id, mode);
    }

    public DiagnosticRequest generateDiagnosticRequestFromInput() {

        DiagnosticRequest request = generateRequestFromRequiredInputFields();
        if (request == null) {
            return null;
        }

        try {
            String freqInput = getFrequencyInput();
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
            String pidInput = getPidInput();
            // pid is optional, ok if empty
            if (!pidInput.equals("")) {
                int pid = Integer.parseInt(pidInput);
                if (pid > 0) {
                    request.setPid(pid);
                } else {
                    return failAndToastError("Pid cannot be negative.");
                }
            }
        } catch (NumberFormatException e) {
            return failAndToastError("Entered PID does not appear to be an integer.");
        }

        String payloadString = getPayloadInput();
        if (!payloadString.equals("")) {
            if (payloadString.length() <= MAX_PAYLOAD_LENGTH_IN_CHARS) {
                if (payloadString.length() % 2 == 0) {
                    // TODO these can't be the right bytes but idk
                    request.setPayload(payloadString.getBytes());
                } else {
                    return failAndToastError("Payload must have an even number of digits.");
                }
            } else {
                return failAndToastError("Payload can only be up to 7 bytes, i.e. 14 digits");
            }
        }

        String name = getNameInput();
        if (!name.trim().equals("")) {
            request.setName(name);
        }

        // TODO not retrieving this value from UI yet
        request.setMultipleResponses(false);

        return request;
    }

    private String getFrequencyInput() {
        return mFrequencyInputText.getText().toString();
    }

    private String getBusInput() {
        return mBusInputText.getText().toString();
    }

    private String getIdInput() {
        return mIdInputText.getText().toString();
    }

    private String getModeInput() {
        return mModeInputText.getText().toString();
    }

    private String getPidInput() {
        return mPidInputText.getText().toString();
    }

    private String getPayloadInput() {
        return mPayloadInputText.getText().toString();
    }

    private String getNameInput() {
        return mNameInputText.getText().toString();
    }

}
