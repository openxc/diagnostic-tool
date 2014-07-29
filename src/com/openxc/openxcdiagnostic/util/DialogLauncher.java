package com.openxc.openxcdiagnostic.util;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * 
 * Simple launcher for launching a dialog
 * 
 */
public class DialogLauncher {

    /**
     * Launch an alert with the given <code>title</code> and
     * <code>message</code>. The alert will have one button that says "OK" to
     * dismiss it.
     * 
     * @param context
     * @param title
     * @param message
     */
    public static void launchAlert(Activity context, String title,
            String message) {
        launchAlert(context, title, message, "OK");
    }

    /**
     * Launch an alert with the given <code>title</code> and
     * <code>message</code>. The alert will have one button that has the value
     * of <code>doneButton</code> to dismiss it.
     * 
     * @param context
     * @param title
     * @param message
     */
    public static void launchAlert(Activity context, String title,
            String message, String doneButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(doneButton, null);
        builder.create().show();
    }

}
