package com.openxc.openxcdiagnostic.util;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

/**
 * 
 * Simple class for displaying an Android Toast.
 * 
 */
public class Toaster {

    public static Toast sToast;

    /**
     * Displays a toast with the given <code>message</code> for a time indicated
     * by <code>Toast.LENGTH_LONG</code>
     * 
     * @param context
     * @param message
     */
    public static void showToast(Activity context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    /**
     * Displays a toast with the given <code>message</code> for a time indicated
     * by <code>length/code>
     * 
     * @param context
     * @param message
     * @param length
     */
    public static void showToast(Activity context, String message, int length) {

        if (sToast != null && toastIsDisplaying()) {
            sToast.cancel();
        }

        sToast = Toast.makeText(context, message, length);
        sToast.show();
    }

    private static boolean toastIsDisplaying() {
        return sToast.getView().getWindowVisibility() == View.VISIBLE;
    }

}
