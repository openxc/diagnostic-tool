package com.openxc.openxcdiagnostic.diagnostic.output;

import java.util.ArrayList;

import android.util.Log;
import android.widget.ListView;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticManager;
import com.openxc.openxcdiagnostic.diagnostic.pair.CommandPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.DiagnosticPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;

public class OutputTableManager implements DiagnosticManager  {

    private DiagnosticActivity mContext;
    private TableSaver mSaver;
    private static String TAG = "DiagnosticOutputTable";
    private ListView outputList;
    private boolean mDisplayCommands;
    private ArrayList<OutputRow> diagnosticRows;
    private ArrayList<OutputRow> commandRows;

    public OutputTableManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        mSaver = new TableSaver(context);
        outputList = (ListView) mContext.findViewById(R.id.responseOutputScroll);
        diagnosticRows = loadSavedDiagnosticRows();
        commandRows = loadSavedCommandRows();
        setRequestCommandState(displayCommands);
    }
    
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
        updateAdapter();
    }
    
    private void updateAdapter() {
        
        TableAdapter adapter;
        if (mDisplayCommands) {
            adapter = new TableAdapter(mContext, commandRows);
        } else {
            adapter = new TableAdapter(mContext, diagnosticRows);
        }
            
        outputList.setAdapter(adapter);
    }
    
    private ArrayList<OutputRow> loadSavedCommandRows() {
        
        ArrayList<OutputRow> rows = new ArrayList<>();
        ArrayList<CommandPair> commandPairs = mSaver.getCommandPairs();
        
        for (int i=0; i < commandPairs.size(); i++) {
            Pair pair = commandPairs.get(i);
            rows.add(new OutputRow(mContext, this, pair.getRequest(), pair.getResponse()));
        }
        
        return rows;
    }
    
    private ArrayList<OutputRow> loadSavedDiagnosticRows() {
        
        ArrayList<OutputRow> rows = new ArrayList<>();
        ArrayList<DiagnosticPair> diagnosticPairs = mSaver.getDiagnosticPairs();
        
        for (int i=0; i < diagnosticPairs.size(); i++) {
            Pair pair = diagnosticPairs.get(i);
            rows.add(new OutputRow(mContext, this, pair.getRequest(), pair.getResponse()));
        }
        
        return rows;
    }
    
    public void add(VehicleMessage req, VehicleMessage resp) {
        
        if (req instanceof DiagnosticRequest && resp instanceof DiagnosticResponse) {
            save((DiagnosticRequest) req, (DiagnosticResponse) resp);
        } else if (req instanceof Command && resp instanceof CommandResponse) {
            save((Command) req, (CommandResponse) resp);
        } else {
            Log.e(TAG, "Unable to add mismatched VehicleMessage types to table.");
        }
    }

    private void save(VehicleMessage req, VehicleMessage resp) {   
        OutputRow row = new OutputRow(mContext, this, req, resp);
        if (row.getPair() instanceof DiagnosticPair) {
            diagnosticRows.add(0, row);
        } else {
            commandRows.add(0, row);
        }
        
        mSaver.add(row);
        updateAdapter();
    }

    public void removeRow(OutputRow row) {
        
        if (row.getPair() instanceof DiagnosticPair) {
            diagnosticRows.remove(row);
        } else {
            commandRows.remove(row);
        }
        
        mSaver.remove(row);
        updateAdapter();
    }
    
    public void deleteAllDiagnosticResponses() {
        diagnosticRows = new ArrayList<>();
        mSaver.deleteAllDiagnosticRows();
        updateAdapter();
    }
    
    public void deleteAllCommandResponses() {
        commandRows = new ArrayList<>();
        mSaver.deleteAllCommandRows();
        updateAdapter();
    }
    
}
