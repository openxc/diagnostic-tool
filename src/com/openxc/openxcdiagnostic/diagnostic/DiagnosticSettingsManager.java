package com.openxc.openxcdiagnostic.diagnostic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TableLayout;

import com.openxc.openxcdiagnostic.R;

public class DiagnosticSettingsManager {

    private DiagnosticSettingsManager() { }
    
    public static void showAlert(Activity context) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TableLayout settingsLayout = (TableLayout) context.getLayoutInflater().inflate(R.layout.diagsettingsalert, null);
                        
        builder.setView(settingsLayout);

        builder.setTitle(context.getResources().getString(R.string.settings_alert_label));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }
    
}