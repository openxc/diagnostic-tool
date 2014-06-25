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
 * Manager for storing Diagnostic Responses. 
 * 
 */
public class DiagnosticOutputTableSaver {

    private ArrayList<DiagnosticResponse> mResponses;
    private ArrayList<DiagnosticRequest> mRequests;
    private static SharedPreferences sPreferences;
    
    public DiagnosticOutputTableSaver(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mResponses = loadSavedResponses();
        mRequests = loadSavedRequests();
    }
    
    public void add(DiagnosticRequest req, DiagnosticResponse resp) {
        
        //dirty...two separate arrays, entries must correspond
        ArrayList<DiagnosticRequest> newSavedRequests = mRequests;
        newSavedRequests.add(0, req);
        setSavedRequests(newSavedRequests);
        
        ArrayList<DiagnosticResponse> newSavedResponses = mResponses;
        newSavedResponses.add(0, resp);
        setSavedResponses(newSavedResponses);
    }
    
    public void remove(DiagnosticRequest req, DiagnosticResponse resp) {
                
        //responses should be unique because of the timestamp, so find the appropriate
        //response first, then find the corresponding request
        ArrayList<DiagnosticResponse> newSavedResponses = mResponses;
        int removeIndex = newSavedResponses.indexOf(resp);
        
        if (removeIndex >= 0) {
            newSavedResponses.remove(resp);
            setSavedResponses(newSavedResponses);
            
            ArrayList<DiagnosticRequest> newSavedRequests = mRequests;        
            newSavedRequests.remove(removeIndex);
            setSavedRequests(newSavedRequests);
        }
    }
    
    public void removeAll() {
        setSavedRequests(new ArrayList<DiagnosticRequest>());
        setSavedResponses(new ArrayList<DiagnosticResponse>());
    }
    
    public ArrayList<DiagnosticResponse> getSavedResponses() {
        return mResponses;
    }
    
    public ArrayList<DiagnosticRequest> getSavedRequests() {
        return mRequests;
    }
    
    private void setSavedRequests(ArrayList<DiagnosticRequest> newSavedRequests) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newSavedRequests);
        prefsEditor.putString(getSavedRequestsKey(), json);
        prefsEditor.commit();
        mRequests = newSavedRequests;
    }
    
    private void setSavedResponses(ArrayList<DiagnosticResponse> newSavedResponses) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newSavedResponses);
        prefsEditor.putString(getSavedResponsesKey(), json);
        prefsEditor.commit();
        mResponses = newSavedResponses;
    }
    
    private ArrayList<DiagnosticRequest> loadSavedRequests() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<DiagnosticRequest>>(){}.getType();
        String json = sPreferences.getString(getSavedRequestsKey(), "");
        List<DiagnosticRequest> requestList = (new Gson()).fromJson(json, type);
        if (requestList != null) {
            return new ArrayList<>(requestList);
        }
        
        return new ArrayList<>();
    }
    
    private ArrayList<DiagnosticResponse> loadSavedResponses() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<DiagnosticResponse>>(){}.getType();
        String json = sPreferences.getString(getSavedResponsesKey(), "");
        List<DiagnosticResponse> responseList = (new Gson()).fromJson(json, type);
        if (responseList != null) {
            return new ArrayList<>(responseList);
        } 
            
        return new ArrayList<>();
    }
    
    private static String getSavedResponsesKey() {
        return "saved_responses_key";
    }
    
    private static String getSavedRequestsKey() {
        return "saved_requests_key";
    }
    
}
