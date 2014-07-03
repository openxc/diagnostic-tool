package com.openxc.openxcdiagnostic.util;

import android.app.Activity;
import android.app.AlertDialog;

public class DialogLauncher {

    public static void launchAlert(Activity context, String title, String message) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("Done", null);
        builder.create().show();
    }
    
    public static void launchAlert(Activity context, String title, String message, String doneButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(doneButton, null);
        builder.create().show();
    }
    
    
}
