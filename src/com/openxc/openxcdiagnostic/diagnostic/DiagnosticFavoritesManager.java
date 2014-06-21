package com.openxc.openxcdiagnostic.diagnostic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.openxcdiagnostic.R;

public class DiagnosticFavoritesManager {

    private DiagnosticActivity mContext;
    private SharedPreferences mPreferences;
    private ArrayList<DiagnosticRequest> favorites;
    
    public DiagnosticFavoritesManager(DiagnosticActivity context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        loadFavorites();
    }
        
    public void showAlert() {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LinearLayout favoritesLayout = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagfavoritesalert, null);
        
        fill(favoritesLayout);
        builder.setView(favoritesLayout);

        builder.setTitle(mContext.getResources().getString(R.string.favorites_alert_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
        
    }
    
    private void fill(LinearLayout parent) {
        for (DiagnosticRequest req : favorites) {
            createAndAddRow(parent, req);
        }
    }
    
    private void createAndAddRow(LinearLayout parent, DiagnosticRequest req) {
        
        LinearLayout row = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.favoritestablerow, null);
        ((TextView) row.findViewById(R.id.favoritesRowLabel)).setText(req.getName() == null ? "PLACEHOLDER" : req.getName());
        
        Button detailsButton =  (Button) row.findViewById(R.id.favoritesRowDetailsButton);

        parent.addView(row);
    }
    
    private void set() {
        Editor prefsEditor = mPreferences.edit();
        ArrayList<DiagnosticRequest> favs = new ArrayList<>();
        String json = (new Gson()).toJson(favs);
        prefsEditor.putString(getFavoritesKey(), json);
        prefsEditor.commit();
    }
    
    private void loadFavorites() {
        Type type = new TypeToken<List<DiagnosticRequest>>(){}.getType();
        String json = mPreferences.getString(getFavoritesKey(), "");
        List<DiagnosticRequest> favoriteList = (new Gson()).fromJson(json, type);
        if (favoriteList != null) {
            favorites = new ArrayList<>(favoriteList);
        } else {
            favorites = new ArrayList<>();
        }
    }
    
    private String getFavoritesKey() {
        return mContext.getResources().getString(R.string.favorites_key);
    }
    
}
