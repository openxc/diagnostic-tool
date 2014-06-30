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
    private DiagnosticSaver mDiagnosticSaver;
    private CommandSaver mCommandSaver;
    private LinearLayout mDiagnosticTable;
    private LinearLayout mCommandTable;
    private static String TAG = "DiagnosticOutputTable";
    private ScrollView outputScroll;
    private boolean mDisplayCommands;

    public DiagnosticOutputTableManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        mDiagnosticSaver = new DiagnosticSaver(context);
        mCommandSaver = new CommandSaver(context);
        mDiagnosticTable = inflateOutputTable();
        mCommandTable = inflateOutputTable();
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
        
        loadDiagnosticTable();       
        loadCommandTable();
    }

    private void loadDiagnosticTable() {
        
        clearTable(mDiagnosticTable);
        ArrayList<DiagnosticPair> pairs = mDiagnosticSaver.getPairs();

        for (int i = pairs.size() - 1; i >= 0; i--) {
            DiagnosticPair pair = pairs.get(i);
                addToTable(mDiagnosticTable, pair.getReq(), pair.getResp());
         } 
    }
    
    private void loadCommandTable() {
        
        clearTable(mCommandTable);
        ArrayList<CommandPair> pairs = mCommandSaver.getPairs();

        for (int i = pairs.size() - 1; i >= 0; i--) {
            CommandPair pair = pairs.get(i);
                addToTable(mCommandTable, pair.getReq(), pair.getResp());
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
        DiagnosticOutputRow row = new DiagnosticOutputRow(mContext, this, req, resp);
        table.addView(row.getView(), 0);
    }

    public void removeRow(DiagnosticOutputRow row) {
        
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
