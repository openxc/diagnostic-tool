package com.openxc.openxcdiagnostic.diagnostic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.openxc.openxcdiagnostic.R;

public class SettingsManager {

    private SharedPreferences mPreferences;
    private DiagnosticActivity mContext;
    //this value is requested a lot, so more efficient to store and save when needed than to read 
    //from preferences every time
    private boolean mDisplayCommands;
    
    public SettingsManager(DiagnosticActivity context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        
        if (!mPreferences.contains(getSniffingCheckboxKey())) {
            setShouldSniff(false);
        }
        if (!mPreferences.contains(getScrollingCheckboxKey())) {
            setShouldScroll(true);
        }
        mDisplayCommands = mPreferences.getBoolean(getDisplayCommandsKey(), false);
    }
        
    public void showAlert() {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LinearLayout settingsLayout = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagsettingsalert, null);
                        
        builder.setView(settingsLayout);
        builder.setTitle(mContext.getResources().getString(R.string.settings_alert_label));
        builder.setPositiveButton("Done", null);
        builder.create().show();
        initButtons(settingsLayout);    
    }
    
    private void initButtons(View layout) {
        
        final CheckedTextView sniffingCheckBox = (CheckedTextView) layout.findViewById(R.id.sniffingCheckBox);
        sniffingCheckBox.setChecked(shouldSniff());
        sniffingCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sniffingCheckBox.setChecked(!sniffingCheckBox.isChecked());
                setShouldSniff(sniffingCheckBox.isChecked());
                if (sniffingCheckBox.isChecked()) {
                    mContext.startSniffing();
                } else {
                    mContext.stopSniffing();
                }
            }
        });
        
        final CheckedTextView scrollingCheckBox = (CheckedTextView) layout.findViewById(R.id.responseScrollCheckBox);
        scrollingCheckBox.setChecked(shouldScroll());
        scrollingCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollingCheckBox.setChecked(!scrollingCheckBox.isChecked());
                setShouldScroll(scrollingCheckBox.isChecked());
            }
        });
        
        ((Button) layout.findViewById(R.id.deleteDiagnosticResponsesButton))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getResources().getString(R.string.delete_diagnostic_responses_verification));
                builder.setTitle("Delete Diagnostic Responses");
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mContext.clearDiagnosticTable();
                    }
                });
                
                builder.setPositiveButton("Cancel", null);
                builder.create().show();
            }
        });
        
        ((Button) layout.findViewById(R.id.deleteCommandResponsesButton))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getResources().getString(R.string.delete_command_responses_verification));
                builder.setTitle("Delete Command Responses");
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mContext.clearCommandTable();
                    }
                });
                
                builder.setPositiveButton("Cancel", null);
                builder.create().show();
            }
        });        
        
        final Button responseCommandToggle = (Button) layout.findViewById(R.id.responseCommandToggleButton);
        configureToggleButton(responseCommandToggle);
        responseCommandToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                setShouldDisplayCommands(!shouldDisplayCommands());
                configureToggleButton(responseCommandToggle);
            }
        });
    }
    
    private void configureToggleButton(Button toggleButton) {
        String buttonText;
        int backgroundSelector;

        if (shouldDisplayCommands()) {
            buttonText = "Send Requests";
            backgroundSelector = R.drawable.send_request_button_selector;
        }
        else {
            buttonText = "Send Commands";
            backgroundSelector = R.drawable.send_command_button_selector;
        }
        toggleButton.setText(buttonText);
        toggleButton.setBackground(mContext.getResources()
                .getDrawable(backgroundSelector));
    }
    
    public boolean shouldDisplayCommands() {
        return mDisplayCommands;
    }
    
    private void setShouldDisplayCommands(boolean shouldDisplay) {
        save(getDisplayCommandsKey(), shouldDisplay);
        mDisplayCommands = shouldDisplay;
        mContext.setRequestCommandState(shouldDisplayCommands());
    }
    
    public boolean shouldSniff() {
        return mPreferences.getBoolean(getSniffingCheckboxKey(), false);
    }
    
    public boolean shouldScroll() {
        return mPreferences.getBoolean(getScrollingCheckboxKey(), true);
    }
    
    private void setShouldSniff(boolean shouldSniff) {
        save(getSniffingCheckboxKey(), shouldSniff);
    }
    
    private void setShouldScroll(boolean shouldScroll) {
        save(getScrollingCheckboxKey(), shouldScroll);
    }
    
    private void save(String key, boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    private String getDisplayCommandsKey() {
        return "display_commands_key";
    }
    
    private String getSniffingCheckboxKey() {
        return "sniffing_checkbox_key";
    }
    
    private String getScrollingCheckboxKey() {
        return "scrolling_checkbox_key";
    }

}
