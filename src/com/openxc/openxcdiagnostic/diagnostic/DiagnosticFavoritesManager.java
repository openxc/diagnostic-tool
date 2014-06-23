package com.openxc.openxcdiagnostic.diagnostic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.messages.DiagnosticRequest;

/**
 * 
 * Manager for storing favorite requests. 
 * 
 */
public class DiagnosticFavoritesManager {

    //TODO could use refactoring.  Really don't like having to pass in a context, preferable
    //to not have to ensure that you call init before doing anything else, but good news is it crashes
    //right away if you don't so easy to detect
    private static DiagnosticActivity sContext; 
    private static ArrayList<DiagnosticRequest> sFavorites;
    private static SharedPreferences sPreferences;
    
    public static void init(DiagnosticActivity context) {
        sContext = context;
        sPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
        sFavorites = loadFavorites();
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
    
    public static ArrayList<DiagnosticRequest> getFavorites() {
        return sFavorites;
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
        return "favorite_requests_key";
    }
    
}
