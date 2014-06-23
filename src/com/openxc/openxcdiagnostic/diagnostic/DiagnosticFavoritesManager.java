package com.openxc.openxcdiagnostic.diagnostic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.openxcdiagnostic.R;

/**
 * 
 * Manager for storing favorite requests.  Must call initialize(DiagnosticActivity)
 * before using.
 * 
 */
public class DiagnosticFavoritesManager {

    private static DiagnosticActivity sContext;
    private static SharedPreferences sPreferences;
    private static ArrayList<DiagnosticRequest> sFavorites;
    
    private DiagnosticFavoritesManager() {}
    
    /**
     * This must be called before the DiagnosticFavoritesManager can be used.
     */
    public static void initialize(DiagnosticActivity context) {
        sContext = context;
        sPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        sFavorites = loadFavorites();
    }
        
    public static void showAlert() {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(sContext);
        LinearLayout favoritesLayout = (LinearLayout) sContext.getLayoutInflater().inflate(R.layout.diagfavoritesalert, null);
        
        fill(favoritesLayout);
        builder.setView(favoritesLayout);

        builder.setTitle(sContext.getResources().getString(R.string.favorites_alert_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
        
    }
    
    private static void fill(LinearLayout parent) {
        for (DiagnosticRequest req : sFavorites) {
            createAndAddRow(parent, req);
        }
    }
    
    private static void createAndAddRow(LinearLayout parent, final DiagnosticRequest req) {
        
        LinearLayout row = (LinearLayout) sContext.getLayoutInflater().inflate(R.layout.favoritestablerow, null);
        ((TextView) row.findViewById(R.id.favoritesRowLabel)).setText(req.getName() == null ? "PLACEHOLDER" : req.getName());
        
        Button sendButton =  (Button) row.findViewById(R.id.favoritesRowDetailsButton);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sContext.sendRequest(req);
            }
        });

        parent.addView(row);
    }
    
    public static void addFavoriteRequest(DiagnosticRequest req) {
        ArrayList<DiagnosticRequest> newFavorites = sFavorites;
        newFavorites.add(req);
        setFavorites(newFavorites);
    }
    
    public static void removeFavoriteRequest(DiagnosticRequest req) {
        ArrayList<DiagnosticRequest> newFavorites = sFavorites;
        newFavorites.remove(req);
        setFavorites(newFavorites);
    }
    
    private static void setFavorites(ArrayList<DiagnosticRequest> newFavorites) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(sFavorites);
        prefsEditor.putString(getFavoritesKey(), json);
        prefsEditor.commit();
        sFavorites = newFavorites;
    }
    
    private static ArrayList<DiagnosticRequest> loadFavorites() {
        Type type = new TypeToken<List<DiagnosticRequest>>(){}.getType();
        String json = sPreferences.getString(getFavoritesKey(), "");
        List<DiagnosticRequest> favoriteList = (new Gson()).fromJson(json, type);
        if (favoriteList != null) {
            return new ArrayList<>(favoriteList);
        } 
            
        return new ArrayList<>();
    }
    
    private static String getFavoritesKey() {
        return sContext.getResources().getString(R.string.favorites_key);
    }
    
}
