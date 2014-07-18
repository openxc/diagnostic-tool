package com.openxc.openxcdiagnostic.diagnostic.output;

import java.lang.reflect.Type;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.diagnostic.pair.CommandPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.DiagnosticPair;

/**
 * 
 * Manager for storing Requests and Responses for the table.
 * 
 */
public class TableSaver {

    private ArrayList<DiagnosticPair> diagnosticPairs;
    private ArrayList<CommandPair> commandPairs;
    private static SharedPreferences sPreferences;
    
    public TableSaver(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        diagnosticPairs = loadDiagnosticPairs();
        commandPairs = loadCommandPairs();
    }
    
    public void saveDiagnosticRows(ArrayList<OutputRow> rows) {
        diagnosticPairs = new ArrayList<>();
        for (int i=0; i < rows.size(); i++) {
            diagnosticPairs.add((DiagnosticPair) rows.get(i).getPair());
        }
        saveDiagnosticPairs();
    }
    
    public void saveCommandRows(ArrayList<OutputRow> rows) {
        commandPairs = new ArrayList<>();
        for (int i=0; i < rows.size(); i++) {
            commandPairs.add((CommandPair) rows.get(i).getPair());
        }
        saveCommandPairs();
    }
    
    public ArrayList<DiagnosticPair> getDiagnosticPairs() {
        return diagnosticPairs;
    }
   
    public ArrayList<CommandPair> getCommandPairs() {
        return commandPairs;
    }
    
    private void saveDiagnosticPairs() {  
        save(getSavedDiagnosticRowsKey(), (new Gson()).toJson(diagnosticPairs));
    }
    
    private void saveCommandPairs() {
        save(getSavedCommandRowsKey(), (new Gson()).toJson(commandPairs));
    }
    
    private void save(String key, String jsonString) {
        Editor prefsEditor = sPreferences.edit();
        prefsEditor.putString(key, jsonString);
        prefsEditor.commit();
    }
    
    private ArrayList<CommandPair> loadCommandPairs() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<ArrayList<CommandPair>>(){}.getType();
        String json = sPreferences.getString(getSavedCommandRowsKey(), "");
        ArrayList<CommandPair> rowList 
            = (new Gson()).fromJson(json, type);
        if (rowList != null) {
            return new ArrayList<>(rowList);
        }
        
        return new ArrayList<>();
    }
    
    private ArrayList<DiagnosticPair> loadDiagnosticPairs() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<ArrayList<DiagnosticPair>>(){}.getType();
        String json = sPreferences.getString(getSavedDiagnosticRowsKey(), "");
        ArrayList<DiagnosticPair> rowList 
            = (new Gson()).fromJson(json, type);
        if (rowList != null) {
            return new ArrayList<>(rowList);
        }
        
        return new ArrayList<>();
    }
    
    private static String getSavedCommandRowsKey() {
        return "saved_command_rows_key";
    }
    
    private static String getSavedDiagnosticRowsKey() {
        return "saved_diagnostic_rows_key";
    }
    
}
