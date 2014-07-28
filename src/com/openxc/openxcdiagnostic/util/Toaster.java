package com.openxc.openxcdiagnostic.util;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

public class Toaster {

    public static Toast sToast;

    public static void showToast(Activity context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

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
