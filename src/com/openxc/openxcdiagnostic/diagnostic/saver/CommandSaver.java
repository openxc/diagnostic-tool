package com.openxc.openxcdiagnostic.diagnostic.saver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.diagnostic.pair.CommandPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;

/**
 * 
 * Manager for storing Diagnostic Responses and Requests for the table.
 * 
 */
public class CommandSaver implements Saver {

    private ArrayList<CommandPair> commandPairs;
    private static SharedPreferences sPreferences;
    
    public CommandSaver(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        commandPairs = loadCommandPairs();
    }
    
    public void add(CommandPair pair) {   
        commandPairs.add(0, pair);
        save(commandPairs);
    }
    
    public void remove(CommandPair pair) {        
        commandPairs.remove(pair);
        save(commandPairs);
    }
    
    public ArrayList<Pair> getPairs() {
        //cannot cast from ArrayList<CommandPair> to ArrayList<Pair>
        return new ArrayList<Pair>(commandPairs);
    }
    
    public void removeAll() {
        commandPairs = new ArrayList<CommandPair>();
        save(commandPairs);
    }
    
    private void save(ArrayList<CommandPair> newPairs) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newPairs);
        prefsEditor.putString(getSavedCommandPairsKey(), json);
        prefsEditor.commit();
    }
    
    private ArrayList<CommandPair> loadCommandPairs() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<ArrayList<CommandPair>>(){}.getType();
        String json = sPreferences.getString(getSavedCommandPairsKey(), "");
        ArrayList<CommandPair> requestList 
            = (new Gson()).fromJson(json, type);
        if (requestList != null) {
            return new ArrayList<>(requestList);
        }
        
        return new ArrayList<>();
    }
    
    private static String getSavedCommandPairsKey() {
        return "saved_command_pairs_key";
    }
    
}
