package com.openxc.openxcdiagnostic.diagnostic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

/**
 * 
 * Manager for storing Diagnostic Responses and Requests for the table.
 * 
 */
public class DiagnosticSaver implements Saver {

    private ArrayList<DiagnosticPair> diagnosticPairs;
    private static SharedPreferences sPreferences;
    
    public DiagnosticSaver(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        diagnosticPairs = loadDiagnosticPairs();
    }
    
    public void add(DiagnosticPair pair) {   
        diagnosticPairs.add(pair);
        save(diagnosticPairs);
    }
    
    public void remove(DiagnosticPair pair) {        
        diagnosticPairs.remove(pair);
        save(diagnosticPairs);
    }
    
    public ArrayList<DiagnosticPair> getPairs() {
        return diagnosticPairs;
    }
    
    public void removeAll() {
        diagnosticPairs = new ArrayList<DiagnosticPair>();
        save(diagnosticPairs);
    }
    
    private void save(ArrayList<DiagnosticPair> newPairs) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newPairs);
        prefsEditor.putString(getSavedDiagnosticPairsKey(), json);
        prefsEditor.commit();
    }
    
    private ArrayList<DiagnosticPair> loadDiagnosticPairs() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<ArrayList<DiagnosticPair>>(){}.getType();
        String json = sPreferences.getString(getSavedDiagnosticPairsKey(), "");
        ArrayList<DiagnosticPair> requestList 
            = (new Gson()).fromJson(json, type);
        if (requestList != null) {
            return new ArrayList<>(requestList);
        }
        
        return new ArrayList<>();
    }
    
    private static String getSavedDiagnosticPairsKey() {
        return "saved_diagnostic_pairs_key";
    }
    
}
