package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.diagnostic.pair.CommandPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.DiagnosticPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;
import com.openxc.openxcdiagnostic.diagnostic.saver.CommandSaver;
import com.openxc.openxcdiagnostic.diagnostic.saver.DiagnosticSaver;
import com.openxc.openxcdiagnostic.diagnostic.saver.Saver;

public class OutputTableManager implements DiagnosticManager  {

    private DiagnosticActivity mContext;
    private DiagnosticSaver mDiagnosticSaver;
    private CommandSaver mCommandSaver;
    private LinearLayout mDiagnosticTable;
    private LinearLayout mCommandTable;
    private static String TAG = "DiagnosticOutputTable";
    private ScrollView outputScroll;
    private boolean mDisplayCommands;
    private Map<LinearLayout, Saver> tablesAndSavers = new HashMap<>();

    public OutputTableManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        mDiagnosticTable = inflateOutputTable();
        mDiagnosticSaver = new DiagnosticSaver(context);
        tablesAndSavers.put(mDiagnosticTable, mDiagnosticSaver);
        mCommandTable = inflateOutputTable();
        mCommandSaver = new CommandSaver(context);
        tablesAndSavers.put(mCommandTable, mCommandSaver);
        outputScroll = (ScrollView) mContext.findViewById(R.id.responseOutputScroll);
        setRequestCommandState(displayCommands);
    }
    
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
        outputScroll.removeAllViews();
        outputScroll.addView(selectTable());
    }
    
    private LinearLayout selectTable() {
        if (mDisplayCommands) {
            return  mCommandTable;
        }
        return mDiagnosticTable;
    }
    
    private LinearLayout inflateOutputTable() {
        return (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagoutputtable, null);
    }

    public void load() {
        
        outputScroll.removeAllViews();
        LinearLayout newView = selectTable();
        outputScroll.addView(newView);
        loadTables();
    }
    
    private void loadTables() {
        
        for (Map.Entry<LinearLayout, Saver> entry : tablesAndSavers.entrySet()) {
           ArrayList<Pair> pairs = entry.getValue().getPairs();
           
           for (int i = pairs.size() - 1; i >= 0; i--) {
               Pair pair = pairs.get(i);
                   addToTable(entry.getKey(), pair.getRequest(), pair.getResponse());
            } 
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
        mDiagnosticSaver.add(new DiagnosticPair(req, resp));
        addToTable(mDiagnosticTable, req, resp);
    }
    
    private void add(Command command, CommandResponse resp) {
        mCommandSaver.add(new CommandPair(command, resp));
        addToTable(mCommandTable, command, resp);
    }

    private void addToTable(LinearLayout table, VehicleMessage req, VehicleMessage resp) {
        OutputRow row = new OutputRow(mContext, this, req, resp);
        table.addView(row.getView(), 0);
    }

    public void removeRow(OutputRow row) {
        
        Pair pair = row.getDiagnosticPair();
        
        if (pair instanceof DiagnosticPair) {
            mDiagnosticTable.removeView(row.getView());
            mDiagnosticSaver.remove((DiagnosticPair) pair);
        } else {
            mCommandTable.removeView(row.getView());
            mCommandSaver.remove((CommandPair) pair);
        }
    }

    private void deleteAllRows(LinearLayout table, Saver saver) {
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
