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
import com.openxc.messages.DiagnosticResponse;

/**
 * 
 * Manager for storing Diagnostic Responses and Requests for the table.
 * 
 */
public class DiagnosticOutputSaver implements OutputSaver {

    private ArrayList<DiagnosticRequest> mDiagnosticRequests;
    private ArrayList<DiagnosticResponse> mDiagnosticResponses;
    private static SharedPreferences sPreferences;
    
    public DiagnosticOutputSaver(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mDiagnosticResponses = loadSavedDiagnosticResponses();
        mDiagnosticRequests = loadSavedDiagnosticRequests();
    }
    
    public void add(DiagnosticRequest req, DiagnosticResponse resp) {
        
        //dirty...two separate arrays, entries must correspond
        ArrayList<DiagnosticRequest> newSavedRequests = mDiagnosticRequests;
        newSavedRequests.add(0, req);
        setSavedRequests(newSavedRequests);
        
        ArrayList<DiagnosticResponse> newSavedResponses = mDiagnosticResponses;
        newSavedResponses.add(0, resp);
        setSavedResponses(newSavedResponses);
    }
    
    public void remove(DiagnosticRequest req, DiagnosticResponse resp) {
                
        //responses should be unique because of the timestamp, so find the appropriate
        //response first, then find the corresponding request
        ArrayList<DiagnosticResponse> newSavedResponses = mDiagnosticResponses;
        int removeIndex = newSavedResponses.indexOf(resp);
        
        if (removeIndex >= 0) {
            newSavedResponses.remove(resp);
            setSavedResponses(newSavedResponses);
            
            ArrayList<DiagnosticRequest> newSavedRequests = mDiagnosticRequests;        
            newSavedRequests.remove(removeIndex);
            setSavedRequests(newSavedRequests);
        }
    }
    
    public void removeAll() {
        setSavedRequests(new ArrayList<DiagnosticRequest>());
        setSavedResponses(new ArrayList<DiagnosticResponse>());
    }
    
    public ArrayList<DiagnosticResponse> getSavedResponses() {
        return mDiagnosticResponses;
    }
    
    public ArrayList<DiagnosticRequest> getSavedRequests() {
        return mDiagnosticRequests;
    }
    
    private void setSavedRequests(ArrayList<DiagnosticRequest> newSavedRequests) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newSavedRequests);
        prefsEditor.putString(getSavedDiagnosticRequestsKey(), json);
        prefsEditor.commit();
        mDiagnosticRequests = newSavedRequests;
    }
    
    private void setSavedResponses(ArrayList<DiagnosticResponse> newSavedResponses) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newSavedResponses);
        prefsEditor.putString(getSavedDiagnosticResponsesKey(), json);
        prefsEditor.commit();
        mDiagnosticResponses = newSavedResponses;
    }
    
    private ArrayList<DiagnosticRequest> loadSavedDiagnosticRequests() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<DiagnosticRequest>>(){}.getType();
        String json = sPreferences.getString(getSavedDiagnosticRequestsKey(), "");
        List<DiagnosticRequest> requestList = (new Gson()).fromJson(json, type);
        if (requestList != null) {
            return new ArrayList<>(requestList);
        }
        
        return new ArrayList<>();
    }
    
    private ArrayList<DiagnosticResponse> loadSavedDiagnosticResponses() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<DiagnosticResponse>>(){}.getType();
        String json = sPreferences.getString(getSavedDiagnosticResponsesKey(), "");
        List<DiagnosticResponse> responseList = (new Gson()).fromJson(json, type);
        if (responseList != null) {
            return new ArrayList<>(responseList);
        } 
            
        return new ArrayList<>();
    }
    
    private static String getSavedDiagnosticResponsesKey() {
        return "saved_diagnositc_responses_key";
    }
    
    private static String getSavedDiagnosticRequestsKey() {
        return "saved_diagnostic_requests_key";
    }
    
}
