package com.openxc.openxcdiagnostic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class SimpleDialogLauncher {

    public static void launchAlert(Activity context, String title, String message) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }
    
}
