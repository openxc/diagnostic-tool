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
import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;

/**
 * 
 * Manager for storing Diagnostic Responses and Requests for the table.
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
    
    public void add(OutputRow row) { 
        if (row.getPair() instanceof DiagnosticPair) {
            diagnosticPairs.add(0, (DiagnosticPair) row.getPair());
            saveDiagnosticPairs();
        } else {
            commandPairs.add(0, (CommandPair) row.getPair());
            saveCommandPairs();
        }
    }
    
    public void remove(OutputRow row) { 
        Pair pair = row.getPair();
        if (pair instanceof DiagnosticPair) {
            diagnosticPairs.remove(pair);
            saveDiagnosticPairs();
        } else {
            commandPairs.remove(pair);
            saveCommandPairs();
        }
    }
    
    public ArrayList<DiagnosticPair> getDiagnosticPairs() {
        return diagnosticPairs;
    }
   
    public ArrayList<CommandPair> getCommandPairs() {
        return commandPairs;
    }
    
    public void deleteAllCommandRows() {
        commandPairs = new ArrayList<CommandPair>();
        saveCommandPairs();
    }
    
    public void deleteAllDiagnosticRows() {
        diagnosticPairs = new ArrayList<DiagnosticPair>() ;
        saveDiagnosticPairs();
    }
    
    private void saveDiagnosticPairs() {
        
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(diagnosticPairs);
        prefsEditor.putString(getSavedDiagnosticRowsKey(), json);
        prefsEditor.commit();
    }
    
    private void saveCommandPairs() {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(commandPairs);
        prefsEditor.putString(getSavedCommandRowsKey(), json);
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
