package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;

import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;

public class DiagnosticOutputTableManager implements DiagnosticManager  {

    private DiagnosticActivity mContext;
    private DiagnosticOutputSaver mDiagnosticSaver;
    private CommandOutputSaver mCommandSaver;
    private LinearLayout mDiagnosticTable;
    private LinearLayout mCommandTable;
    private static String TAG = "DiagnosticOutputTable";
    private ScrollView outputScroll;
    private boolean mDisplayCommands;

    public DiagnosticOutputTableManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        mDiagnosticSaver = new DiagnosticOutputSaver(context);
        mCommandSaver = new CommandOutputSaver(context);
        mDiagnosticTable = inflateOutputTable();
        mCommandTable = inflateOutputTable();
        outputScroll = (ScrollView) mContext.findViewById(R.id.responseOutputScroll);
        setRequestCommandState(displayCommands);
    }
    
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
        outputScroll.removeAllViews();
        outputScroll.addView(selectTable(displayCommands));
    }
    
    private LinearLayout selectTable(boolean displayCommands) {
        if (displayCommands) {
            return  mCommandTable;
        }
        return mDiagnosticTable;
    }
    
    private LinearLayout inflateOutputTable() {
        return (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagoutputtable, null);
    }

    public void load() {
        
        outputScroll.removeAllViews();
        LinearLayout newView = selectTable(mDisplayCommands);
        outputScroll.addView(newView);
        
        loadDiagnosticTable();       
        loadCommandTable();
    }

    private void loadDiagnosticTable() {
        
        clearTable(mDiagnosticTable);
        ArrayList<DiagnosticRequest> savedRequests = mDiagnosticSaver.getSavedRequests();
        ArrayList<DiagnosticResponse> savedResponses = mDiagnosticSaver.getSavedResponses();

        if (savedRequests.size() == savedResponses.size()) {
            for (int i = savedRequests.size() - 1; i >= 0; i--) {
                addToTable(mDiagnosticTable, savedRequests.get(i), savedResponses.get(i));
            }
        } else {
            Log.e(TAG, "Mismatched requests and responses...cannot load table.");
        }
    }
    
    private void loadCommandTable() {
        
        clearTable(mCommandTable);
        ArrayList<Command> savedCommands = mCommandSaver.getSavedCommands();
        ArrayList<CommandResponse> savedCommandResponses = mCommandSaver.getSavedCommandResponses();

        if (savedCommands.size() == savedCommandResponses.size()) {
            for (int i = savedCommands.size() - 1; i >= 0; i--) {
                addToTable(mCommandTable, savedCommands.get(i), savedCommandResponses.get(i));
            }
        } else {
            Log.e(TAG, "Mismatched commands and command responses...cannot load table.");
        }
    }
    
    public void add(VehicleMessage req, VehicleMessage resp) {
        
        if (req instanceof DiagnosticRequest && resp instanceof DiagnosticResponse) {
            add((DiagnosticRequest) req, (DiagnosticResponse) resp);
        } else if (req instanceof Command && resp instanceof CommandResponse) {
            add((Command) req, (CommandResponse) resp);
        } else {
            Log.e(TAG, "Unable to add mismatched VehicleMessage types to table.");
        }
    }

    private void add(DiagnosticRequest req, DiagnosticResponse resp) {
        mDiagnosticSaver.add(req, resp);
        addToTable(mDiagnosticTable, req, resp);
    }
    
    private void add(Command command, CommandResponse resp) {
        mCommandSaver.add(command, resp);
        addToTable(mCommandTable, command, resp);
    }

    private void addToTable(LinearLayout table, VehicleMessage req, VehicleMessage resp) {
        DiagnosticOutputRow row = new DiagnosticOutputRow(mContext, this, req, resp);
        table.addView(row.getView(), 0);
    }

    public void removeRow(DiagnosticOutputRow row) {
        if (row.getRequest() instanceof DiagnosticRequest) {
            mDiagnosticTable.removeView(row.getView());
            mDiagnosticSaver.remove((DiagnosticRequest)row.getRequest(), 
                    (DiagnosticResponse) row.getResponse());
        } else {
            mCommandTable.removeView(row.getView());
            mCommandSaver.remove((Command) row.getRequest(), (CommandResponse) row.getResponse());
        }
    }

    private void deleteAllRows(LinearLayout table, OutputSaver saver) {
        clearTable(table);
        saver.removeAll();
    }
    
    public void deleteAllDiagnosticResponses() {
        deleteAllRows(mDiagnosticTable, mDiagnosticSaver);
    }
    
    public void deleteAllCommandResponses() {
        deleteAllRows(mCommandTable, mCommandSaver);
    }

    private void clearTable(LinearLayout table) {
        table.removeAllViews();
    }
    
}
