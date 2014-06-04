package com.openxc.openxcdiagnostic.resources;

import android.widget.TextView;

public class Utilities {

    private Utilities() {
    };

    public static void writeLine(TextView tv, String text) {
        tv.append(text);
        tv.append("\n");
    }
}
