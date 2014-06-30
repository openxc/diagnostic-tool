package com.openxc.openxcdiagnostic.diagnostic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;

/**
 * 
 * Manager for storing Commands and Command Responses.
 * 
 */
public class CommandOutputSaver implements OutputSaver {

    private ArrayList<Command> mCommands;
    private ArrayList<CommandResponse> mCommandResponses;
    private static SharedPreferences sPreferences;
    
    public CommandOutputSaver(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mCommands = loadSavedCommands();
        mCommandResponses = loadSavedCommandResponses();
    }
    
    public void add(Command command, CommandResponse resp) {
        
        //dirty...two separate arrays, entries must correspond
        ArrayList<Command> newSavedCommands = mCommands;
        newSavedCommands.add(0, command);
        setSavedCommands(newSavedCommands);
        
        ArrayList<CommandResponse> newSavedResponses = mCommandResponses;
        newSavedResponses.add(0, resp);
        setSavedCommandResponses(newSavedResponses);
    }
    
    public void remove(Command command, CommandResponse resp) {
                
        //responses should be unique because of the timestamp, so find the appropriate
        //response first, then find the corresponding request
        ArrayList<CommandResponse> newSavedCommandResponses = mCommandResponses;
        int removeIndex = newSavedCommandResponses.indexOf(resp);
        
        if (removeIndex >= 0) {
            newSavedCommandResponses.remove(resp);
            setSavedCommandResponses(newSavedCommandResponses);
            
            ArrayList<Command> newSavedRequests = mCommands;        
            newSavedRequests.remove(removeIndex);
            setSavedCommands(newSavedRequests);
        }
    }
    
    public void removeAll() {
        setSavedCommands(new ArrayList<Command>());
        setSavedCommandResponses(new ArrayList<CommandResponse>());
    }
    
    public ArrayList<Command> getSavedCommands() {
        return mCommands;
    }
    
    public ArrayList<CommandResponse> getSavedCommandResponses() {
        return mCommandResponses;
    }
    
    private void setSavedCommands(ArrayList<Command> newSavedCommands) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newSavedCommands);
        prefsEditor.putString(getSavedCommandsKey(), json);
        prefsEditor.commit();
        mCommands = newSavedCommands;
    }
    
    private void setSavedCommandResponses(ArrayList<CommandResponse> newSavedResponses) {
        Editor prefsEditor = sPreferences.edit();
        String json = (new Gson()).toJson(newSavedResponses);
        prefsEditor.putString(getSavedCommandResponsesKey(), json);
        prefsEditor.commit();
        mCommandResponses = newSavedResponses;
    }
   
    private ArrayList<Command> loadSavedCommands() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<Command>>(){}.getType();
        String json = sPreferences.getString(getSavedCommandsKey(), "");
        List<Command> requestList = (new Gson()).fromJson(json, type);
        if (requestList != null) {
            return new ArrayList<>(requestList);
        }
        
        return new ArrayList<>();
    }
    
    private ArrayList<CommandResponse> loadSavedCommandResponses() {
        
        @SuppressWarnings("serial")
        Type type = new TypeToken<List<CommandResponse>>(){}.getType();
        String json = sPreferences.getString(getSavedCommandResponsesKey(), "");
        List<CommandResponse> requestList = (new Gson()).fromJson(json, type);
        if (requestList != null) {
            return new ArrayList<>(requestList);
        }
        
        return new ArrayList<>();
    }
    
    private static String getSavedCommandsKey() {
        return "saved_commands_key";
    }
    
    private static String getSavedCommandResponsesKey() {
        return "saved_command_responses_key";
    }
    
}
