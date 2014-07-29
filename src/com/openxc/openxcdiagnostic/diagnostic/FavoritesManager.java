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
import com.openxc.openxcdiagnostic.util.MessageAnalyzer;

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

    // Don't like having to pass in a context to a static class on init, but
    // advantageous this way so you can access favorites from any class.
    public static void init(DiagnosticActivity context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sFavoriteRequests = loadFavoriteRequests();
        sFavoriteCommands = loadFavoriteCommands();
    }

    /**
     * Add the given <code>req</code> to favorites. The method will decide
     * whether it should be added as a <code>DiagnosticRequest</code> or a
     * <code>Command</code>.
     * 
     * @param req
     *            The request/command to add.
     */
    public static void add(VehicleMessage req) {
        if (MessageAnalyzer.isDiagnosticRequest(req)) {
            addFavoriteRequest((DiagnosticRequest) req);
        } else if (MessageAnalyzer.isCommand(req)) {
            addFavoriteCommand((Command) req);
        } else {
            Log.e(TAG, "Unable to add message to favorites of type "
                    + req.getClass().toString());
        }
    }

    private static void addFavoriteRequest(DiagnosticRequest req) {
        ArrayList<DiagnosticRequest> newFavorites = sFavoriteRequests;
        newFavorites.add(findAlphabeticPosition(req), req);
        setFavoriteRequests(newFavorites);
    }

    private static void addFavoriteCommand(Command command) {
        ArrayList<Command> newFavorites = sFavoriteCommands;
        newFavorites.add(findAlphabeticPosition(command), command);
        setFavoriteCommands(newFavorites);
    }

    /**
     * Determines if the given strings are in the correct alphabetic order
     * determined by the order of the arguments
     * 
     * @param s1
     * @param s2
     * @return <code>True</code> if <code>s1</code> precedes <code>s2</code>,
     *         false otherwise. If <code>s1</code> is null or empty/whitespace,
     *         the method will return <code>false</code>. If <code>s2</code> is
     *         null or empty/whitespace, the method will return
     *         <code>true</code>.
     */
    private static boolean isOrdered(String s1, String s2) {
        if (s1 == null || s1.trim() == "") {
            return false;
        }

        if (s2 == null || s2.trim() == "") {
            return true;
        }

        return s1.compareToIgnoreCase(s2) < 0;
    }

    /**
     * Finds the index at which the <code>req</code> should be placed in
     * <code>sFavoriteRequests</code>
     * 
     * @param req
     *            The request to insert
     * @return The index
     */
    private static int findAlphabeticPosition(DiagnosticRequest req) {

        int position = 0;
        for (; position < sFavoriteRequests.size(); position++) {
            if (isOrdered(req.getName(), sFavoriteRequests.get(position)
                    .getName())) {
                break;
            }
        }
        return position;
    }

    /**
     * Finds the index at which the <code>command</code> should be placed in
     * <code>sFavoriteCommands</code>
     * 
     * @param req
     *            The request to insert
     * @return The index
     */
    private static int findAlphabeticPosition(Command command) {

        int position = 0;
        for (; position < sFavoriteCommands.size(); position++) {
            if (isOrdered(command.getCommand().toString(), sFavoriteCommands
                    .get(position).getCommand().toString())) {
                break;
            }
        }
        return position;
    }

    /**
     * Remove the given <code>req</code> from favorites. The method will decide
     * whether it should be removed from <code>sFavoriteRequests</code> or
     * <code>sFavoriteCommands</code>.
     * 
     * @param req
     *            The request/command to add.
     */
    public static void remove(VehicleMessage req) {
        if (MessageAnalyzer.isDiagnosticRequest(req)) {
            removeFavoriteRequest((DiagnosticRequest) req);
        } else if (MessageAnalyzer.isCommand(req)) {
            removeFavoriteCommand((Command) req);
        } else {
            Log.w(TAG, "Unable to remove message from favorites of type "
                    + req.getClass().toString());
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

    private static void setFavoriteRequests(
            ArrayList<DiagnosticRequest> newFavorites) {
        String json = (new Gson()).toJson(newFavorites);
        save(getFavoriteRequestsKey(), json);
        sFavoriteRequests = newFavorites;
    }

    private static void setFavoriteCommands(ArrayList<Command> newFavorites) {
        String json = (new Gson()).toJson(newFavorites);
        save(getFavoriteCommandsKey(), json);
        sFavoriteCommands = newFavorites;
    }

    private static void save(String key, String json) {
        Editor prefsEditor = sPreferences.edit();
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }

    /**
     * Determines if the given VehicleMessage is in favorites
     * 
     * @param message
     * @return True if the message is in favorites, false otherwise.
     */
    public static boolean isInFavorites(VehicleMessage message) {
        if (MessageAnalyzer.isDiagnosticRequest(message)) {
            return containsFavoriteRequest((DiagnosticRequest) message);
        } else if (MessageAnalyzer.isCommand(message)) {
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
        Type type = new TypeToken<List<DiagnosticRequest>>() {
        }.getType();
        String json = sPreferences.getString(getFavoriteRequestsKey(), "");
        List<DiagnosticRequest> favoriteList = (new Gson())
                .fromJson(json, type);
        if (favoriteList != null) {
            return new ArrayList<>(favoriteList);
        }

        return new ArrayList<>();
    }

    private static ArrayList<Command> loadFavoriteCommands() {

        @SuppressWarnings("serial")
        Type type = new TypeToken<List<Command>>() {
        }.getType();
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
