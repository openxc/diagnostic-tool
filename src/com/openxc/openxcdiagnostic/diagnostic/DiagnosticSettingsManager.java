package com.openxc.openxcdiagnostic.diagnostic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TableLayout;

import com.openxc.openxcdiagnostic.R;

/**
 * 
 * Singleton class for managing Diagnostic Settings
 *
 */
public class DiagnosticSettingsManager {

    private SharedPreferences mPreferences;
    private DiagnosticActivity mContext;
    
    public DiagnosticSettingsManager(DiagnosticActivity context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }
        
    public void showAlert() {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        TableLayout settingsLayout = (TableLayout) mContext.getLayoutInflater().inflate(R.layout.diagsettingsalert, null);
                        
        builder.setView(settingsLayout);

        builder.setTitle(mContext.getResources().getString(R.string.settings_alert_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
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
                    mContext.registerForAllResponses();
                } else {
                    mContext.stopListeningForAllResponses();
                }
            }
        });
        
        final Button deleteAllResponsesButton = (Button) layout.findViewById(R.id.deleteAllResponsesButton);
        deleteAllResponsesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete all responses?");
                builder.setTitle("Delete Responses");
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mContext.deleteAllOutputResponses();
                    }
                });
                
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
            }
        });
    }
    
    public boolean shouldSniff() {
        return mPreferences.getBoolean(mContext.getResources().getString(R.string.sniffing_checkbox_key), false);
    }
    
    public void setShouldSniff(boolean shouldSniff) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(mContext.getResources().getString(R.string.sniffing_checkbox_key), 
                shouldSniff);
        editor.commit();
    }

}
