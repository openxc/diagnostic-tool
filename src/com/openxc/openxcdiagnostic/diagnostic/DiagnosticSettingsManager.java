package com.openxc.openxcdiagnostic.diagnostic;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private CheckedTextView sniffingCheckBox;
    private Button deleteAllResponsesButton;
    
    private static DiagnosticSettingsManager instance = null; 
    private DiagnosticSettingsManager() { }
    
    public static DiagnosticSettingsManager getInstance() {
        if (instance == null) {
            instance = new DiagnosticSettingsManager();
        }
        return instance;
    }
        
    public void showAlert(DiagnosticActivity context) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TableLayout settingsLayout = (TableLayout) context.getLayoutInflater().inflate(R.layout.diagsettingsalert, null);
                        
        builder.setView(settingsLayout);

        builder.setTitle(context.getResources().getString(R.string.settings_alert_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
        
        initButtons(context, settingsLayout);    
    }
    
    private void initButtons(final DiagnosticActivity context, View layout) {
        
        sniffingCheckBox = (CheckedTextView) layout.findViewById(R.id.sniffingCheckBox);
        sniffingCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sniffingCheckBox.setChecked(!sniffingCheckBox.isChecked());
                if (sniffingCheckBox.isChecked()) {
                    context.registerForAllResponses();
                } else {
                    context.stopListeningForAllResponses();
                }
            }
        });
        
        deleteAllResponsesButton = (Button) layout.findViewById(R.id.deleteAllResponsesButton);
        deleteAllResponsesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete all responses?");
                builder.setTitle("Delete Responses");
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.deleteAllOutputResponses();
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
    
}
