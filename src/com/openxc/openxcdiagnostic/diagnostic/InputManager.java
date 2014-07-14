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
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.messages.Command;
import com.openxc.messages.DiagnosticMessage;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.util.Toaster;
import com.openxc.openxcdiagnostic.util.Utilities;

public class InputManager implements DiagnosticManager {

    private static String TAG = "DiagnosticInputManager";
    private EditText mFrequencyInputText;
    private EditText mBusInputText;
    private EditText mIdInputText;
    private EditText mModeInputText;
    private EditText mPidInputText;
    private EditText mPayloadInputText;
    private EditText mNameInputText;
    private EditText mCommandInputText;
    private List<EditText> textFields;
    private static final int MAX_PAYLOAD_LENGTH_IN_CHARS = DiagnosticRequest.MAX_PAYLOAD_LENGTH_IN_BYTES * 2;
    private SharedPreferences mPreferences;
    private DiagnosticActivity mContext;
    private boolean mDisplayCommands;

    private class InputHolder {
        private String frequencyInput = getFrequencyInput();    
    }
    
    private class RequestInputHolder extends InputHolder {
        private String busInput = getBusInput();
        private String idInput = getIdInput();
        private String modeInput = getModeInput();
        private String pidInput = getPidInput();
        private String payloadInput = getPayloadInput();
        private String nameInput = getNameInput();
    }
    
    private class CommandInputHolder extends InputHolder {
        private String commandInput = getCommandInput();
    }

    public InputManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        setRequestCommandState(displayCommands);
    }
    
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
        initTextFields();
    }
    
    public void populateFields(VehicleMessage message) {
        if (Utilities.isDiagnosticRequest(message)) {
            populateFields((DiagnosticRequest) message);
        } else if (Utilities.isCommand(message)){ 
            populateFields((Command) message);
        } else {
            Log.w(TAG, "Unable to populate fields from message from favorites of type " + message.getClass().toString());
        }
    }
    
    private void populateFields(Command command) {
        mCommandInputText.setText(selfOrEmptyIfNull(String.valueOf(command.getCommand())));
    }

    private void populateFields(DiagnosticRequest req) {
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

        if (holder instanceof RequestInputHolder) {
            populateFields((RequestInputHolder) holder);
        } else {
            populateFields((CommandInputHolder) holder);
        }
    }
    
    private void populateFields(CommandInputHolder holder) {
        mCommandInputText.setText(holder.commandInput);
    }

    private void populateFields(RequestInputHolder holder) {
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
        
        textFields = new ArrayList<>();
        
        int inputTableId = R.id.inputTable;
        FrameLayout mainLayout = (FrameLayout) mContext.findViewById(android.R.id.content);
        LinearLayout diagnosticLayout = (LinearLayout) mainLayout.findViewById(R.id.diagnostic);
        LinearLayout oldView = (LinearLayout) diagnosticLayout.findViewById(inputTableId);
        LinearLayout newView;
        if (mDisplayCommands) {
            newView = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagcommandinput, null);
            initCommandTextFields(newView);
            restoreCommandTextFields();
        } else {
            newView = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagrequestinput, null);
            initRequestTextFields(newView);
            restoreRequestTextFields();
        }
        newView.setId(inputTableId);
        Utilities.replaceView(diagnosticLayout, oldView, newView);
        
        mFrequencyInputText = (EditText) mContext.findViewById(R.id.frequencyInput);
        mFrequencyInputText.setHint("0");
        textFields.add(mFrequencyInputText);
                
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
                    //Do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                        int before, int count) {
                    //Do nothing
                }

                @Override
                public void afterTextChanged(Editable s) {
                    saveFields();
                }
            });
        }
    }
    
    private void initCommandTextFields(LinearLayout parent) {
        mCommandInputText = (EditText) parent.findViewById(R.id.commandInput);
        textFields.add(mCommandInputText);
    }

    private void initRequestTextFields(LinearLayout parent) {

        mBusInputText = (EditText) parent.findViewById(R.id.busInput);
        mBusInputText.setHint("1 or 2");
        textFields.add(mBusInputText);

        mIdInputText = (EditText) parent.findViewById(R.id.idInput);
        mIdInputText.setHint("0x");
        textFields.add(mIdInputText);

        mModeInputText = (EditText) parent.findViewById(R.id.modeInput);
        mModeInputText.setHint("0x"
                + Integer.toHexString(DiagnosticMessage.MODE_RANGE.getMin())
                + " - " + "0x"
                + Integer.toHexString(DiagnosticMessage.MODE_RANGE.getMax()));
        textFields.add(mModeInputText);

        mPidInputText = (EditText) parent.findViewById(R.id.pidInput);
        mPidInputText.setHint("#");
        textFields.add(mPidInputText);

        mPayloadInputText = (EditText) parent.findViewById(R.id.payloadInput);
        mPayloadInputText.setHint("e.g. 0x1234");
        textFields.add(mPayloadInputText);

        mNameInputText = (EditText) parent.findViewById(R.id.nameInput);
        textFields.add(mNameInputText);
    }

    private void saveFields() {
        
        InputHolder inputHolder;
        String inputKey;
        if (mDisplayCommands) {
            inputHolder = new CommandInputHolder();
            inputKey = getCommandInputKey();
        } else {
            inputHolder = new RequestInputHolder();
            inputKey = getRequestInputKey();
        }
        Editor prefsEditor = mPreferences.edit();
        String json = (new Gson()).toJson(inputHolder);
        prefsEditor.putString(inputKey, json);
        prefsEditor.commit();
    }

    private void restoreRequestTextFields() {

        @SuppressWarnings("serial")
        Type type = new TypeToken<RequestInputHolder>() {
        }.getType();
        String json = mPreferences.getString(getRequestInputKey(), "");
        RequestInputHolder inputHolder = (new Gson()).fromJson(json, type);
        if (inputHolder != null) {
            populateFields(inputHolder);
        }
    }
    
    private void restoreCommandTextFields() {

        @SuppressWarnings("serial")
        Type type = new TypeToken<CommandInputHolder>() {
        }.getType();
        String json = mPreferences.getString(getCommandInputKey(), "");
        CommandInputHolder inputHolder = (new Gson()).fromJson(json, type);
        if (inputHolder != null) {
            populateFields(inputHolder);
        }
    }

    private String getRequestInputKey() {
        return "request_input_key";
    }
    
    private String getCommandInputKey() {
        return "command_input_key";
    }

    /**
     * Method to ensure that null is returned by
     * methods that call it when it should be. There are
     * so many fail points in those methods that it's safer to always return a
     * call to this method than to match up a "return null" statement everywhere
     * there should be a fail and a Toast. If the Toast happens when it should,
     * the fail (return of null) must too.
     */
    private DiagnosticRequest failAndToastError(String message) {
        Toaster.showToast(mContext, message);
        return null;
    }
    
    private Command failAndToastCommandError(String message) {
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
            id = Integer.parseInt(getIdInput(), 16);
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
                int pid = Integer.parseInt(pidInput, 16);
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
    
    public Command generateCommandFromInput() {
        String commandInput = getCommandInput();
        if (commandInput.equals("")) {
            return failAndToastCommandError("Command cannot be empty.");
        } 
        
        Command.CommandType[] values = Command.CommandType.values();
        for (int i=0; i < values.length; i++) {
            Command.CommandType type = values[i];
            if (type.toString().equals(commandInput)) {
                return new Command(type);
            }
        }
       return null;
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
    
    private String getCommandInput() {
        return mCommandInputText.getText().toString();
    }

}
