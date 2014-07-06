package com.openxc.openxcdiagnostic.util;

import java.util.HashMap;
import java.util.Map;

import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.dash.DashboardActivity;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.menu.MenuActivity;

import android.app.Activity;
import android.content.Intent;

public class ActivityLauncher {
    
    public static Map<Integer, Class<?>> sActivityMap = new HashMap<>(); 
    
    static {
        sActivityMap.put(R.id.Diagnostic, DiagnosticActivity.class);
        sActivityMap.put(R.id.Menu, MenuActivity.class);
        sActivityMap.put(R.id.Dashboard, DashboardActivity.class);
    }

    public static void launchActivity (Activity activity, int itemId) {
        
        Class<?> newActivityClass = sActivityMap.get(Integer.valueOf(itemId));
        if (!activity.getClass().equals(newActivityClass)) {
            activity.startActivity(new Intent(activity, newActivityClass));
        }
    }
    
}
