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
import com.openxc.messages.VehicleMessage;

/**
 * 
 * Manager for storing Diagnostic Responses. 
 * 
 */
public class DiagnosticOutputTableSaver {

    private ArrayList<VehicleMessage> mResponses;
    private ArrayList<VehicleMessage> mRequests;
    private static SharedPreferences sPreferences;
    
    public DiagnosticOutputTableSaver(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mResponses = loadSavedResponses();
        mRequests = loadSavedRequests();
    }
    
    public void add(VehicleMessage req, VehicleMessage resp) {
        
        //dirty...two separate arrays, entries must correspond
        ArrayList<VehicleMessage> newSavedRequests = mRequests;
        newSavedRequests.add(0, req);
        setSavedRequests(newSavedRequests);
        
        ArrayList<VehicleMessage> newSavedResponses = mResponses;
        newSavedResponses.add(0, resp);
        setSavedResponses(newSavedResponses);
    }
    
    public void remove(VehicleMessage req, VehicleMessage resp) {
                
        //responses should be unique because of the timestamp, so find the appropriate
        //response first, then find the corresponding request
        ArrayList<VehicleMessage> newSavedResponses = mResponses;
        int removeIndex = newSavedResponses.indexOf(resp);
        
        if (removeIndex >= 0) {
            newSavedResponses.remove(resp);
            setSavedResponses(newSavedResponses);
            
            ArrayList<VehicleMessage> newSavedRequests = mRequests;        
            newSavedRequests.remove(removeIndex);
            setSavedRequests(newSavedRequests);
        }
    }
    
    public void removeAll() {
        setSavedRequests(new ArrayList<VehicleMessage>());
        setSavedResponses(new ArrayList<VehicleMessage>());
    }
    
    public ArrayList<VehicleMessage> getSavedResponses() {
        return mResponses;
    }
    
    public ArrayList<VehicleMessage> getSavedRequests() {
        return mRequests;
    }
    
    private void setSavedRequests(ArrayList<VehicleMessage> newSavedRequests) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newSavedRequests);
        prefsEditor.putString(getSavedRequestsKey(), json);
        prefsEditor.commit();
        mRequests = newSavedRequests;
    }
    
    private void setSavedResponses(ArrayList<VehicleMessage> newSavedResponses) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newSavedResponses);
        prefsEditor.putString(getSavedResponsesKey(), json);
        prefsEditor.commit();
        mResponses = newSavedResponses;
    }
    
    private ArrayList<VehicleMessage> loadSavedRequests() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<DiagnosticRequest>>(){}.getType();
        String json = sPreferences.getString(getSavedRequestsKey(), "");
        List<VehicleMessage> requestList = (new Gson()).fromJson(json, type);
        if (requestList != null) {
            return new ArrayList<>(requestList);
        }
        
        return new ArrayList<>();
    }
    
    private ArrayList<VehicleMessage> loadSavedResponses() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<DiagnosticResponse>>(){}.getType();
        String json = sPreferences.getString(getSavedResponsesKey(), "");
        List<VehicleMessage> responseList = (new Gson()).fromJson(json, type);
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
