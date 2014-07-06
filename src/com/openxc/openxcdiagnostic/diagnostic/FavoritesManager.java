package com.openxc.openxcdiagnostic.diagnostic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.messages.Command;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.VehicleMessage;

/**
 * 
 * Manager for storing favorite requests and commands. 
 * 
 */
public class FavoritesManager {

    private static String TAG = "DiagnosticFavoritesManager";
      
    private static ArrayList<DiagnosticRequest> sFavoriteRequests;
    private static ArrayList<Command> sFavoriteCommands;
    private static SharedPreferences sPreferences;
    
    //Don't like having to pass in a context to a static class on init, but advantageous this 
    //way so you can access favorites from any class.
    public static void init(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sFavoriteRequests = loadFavoriteRequests();
        sFavoriteCommands = loadFavoriteCommands();
    }
    
    public static void add(VehicleMessage req) {
        if (req instanceof DiagnosticRequest) {
            addFavoriteRequest((DiagnosticRequest) req);
        } else if (req instanceof Command) {
            addFavoriteCommand((Command) req);
        } else {
            Log.w(TAG, "Unable to add message to favorites of type " + req.getClass().toString());
        }
    }
    
    private static void addFavoriteRequest(DiagnosticRequest req) {
        ArrayList<DiagnosticRequest> newFavorites = sFavoriteRequests;
        newFavorites.add(0, req);
        setFavoriteRequests(newFavorites);
    }
    
    private static void addFavoriteCommand(Command command) {
        ArrayList<Command> newFavorites = sFavoriteCommands;
        newFavorites.add(0, command);
        setFavoriteCommands(newFavorites);
    }
    
    public static void remove(VehicleMessage req) {
        if (req instanceof DiagnosticRequest) {
            removeFavoriteRequest((DiagnosticRequest) req);
        } else if (req instanceof Command) {
            removeFavoriteCommand((Command) req);
        } else {
            Log.w(TAG, "Unable to remove message from favorites of type " + req.getClass().toString());
        }
    }
    
    private static void removeFavoriteRequest(DiagnosticRequest req) {
        ArrayList<DiagnosticRequest> newFavorites = sFavoriteRequests;
        newFavorites.remove(req);
        setFavoriteRequests(newFavorites);
    }
    
    private static void removeFavoriteCommand(Command command) {
        ArrayList<Command> newFavorites = sFavoriteCommands;
        newFavorites.remove(command);
        setFavoriteCommands(newFavorites);
    }
    
    public static ArrayList<DiagnosticRequest> getFavoriteRequests() {
        return sFavoriteRequests;
    }
    
    public static ArrayList<Command> getFavoriteCommands() {
        return sFavoriteCommands;
    }
    
    private static void setFavoriteRequests(ArrayList<DiagnosticRequest> newFavorites) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newFavorites);
        prefsEditor.putString(getFavoriteRequestsKey(), json);
        prefsEditor.commit();
        sFavoriteRequests = newFavorites;
    }
    
    private static void setFavoriteCommands(ArrayList<Command> newFavorites) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newFavorites);
        prefsEditor.putString(getFavoriteCommandsKey(), json);
        prefsEditor.commit();
        sFavoriteCommands = newFavorites;
    }
    
    public static boolean contains(VehicleMessage message) {
        if (message instanceof DiagnosticRequest) {
            return containsFavoriteRequest((DiagnosticRequest) message);
        } else if (message instanceof Command) {
            return containsFavoriteCommand((Command) message);
        } 
        return false;
    }
    
    private static boolean containsFavoriteRequest(DiagnosticRequest req) {
        return sFavoriteRequests.contains(req);
    }
    
    private static boolean containsFavoriteCommand(Command command) {
        return sFavoriteCommands.contains(command);
    }
    
    private static ArrayList<DiagnosticRequest> loadFavoriteRequests() {
 
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<DiagnosticRequest>>(){}.getType();
        String json = sPreferences.getString(getFavoriteRequestsKey(), "");
        List<DiagnosticRequest> favoriteList = (new Gson()).fromJson(json, type);
        if (favoriteList != null) {
            return new ArrayList<>(favoriteList);
        } 
            
        return new ArrayList<>();
    }
    
    private static ArrayList<Command> loadFavoriteCommands() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<Command>>(){}.getType();
        String json = sPreferences.getString(getFavoriteCommandsKey(), "");
        List<Command> favoriteList = (new Gson()).fromJson(json, type);
        if (favoriteList != null) {
            return new ArrayList<>(favoriteList);
        } 
            
        return new ArrayList<>();
    }
    
    private static String getFavoriteRequestsKey() {
        return "favorite_requests_key";
    }
    
    private static String getFavoriteCommandsKey() {
        return "favorite_commands_key";
    }
    
}
