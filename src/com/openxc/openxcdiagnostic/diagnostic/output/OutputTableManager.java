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
import com.openxc.openxcdiagnostic.diagnostic.pair.DiagnosticPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;

public class OutputTableManager implements DiagnosticManager  {

    private DiagnosticActivity mContext;
    private TableSaver mSaver;
    private static String TAG = "DiagnosticOutputTable";
    private ListView mOutputList;
    private boolean mDisplayCommands;
    private ArrayList<OutputRow> mDiagnosticRows;
    private ArrayList<OutputRow> mCommandRows;

    public OutputTableManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        mSaver = new TableSaver(context);
        mOutputList = (ListView) mContext.findViewById(R.id.responseOutputScroll);
        mDiagnosticRows = loadSavedDiagnosticRows();
        mCommandRows = loadSavedCommandRows();
        setRequestCommandState(displayCommands);
    }
    
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
        setAdapter();
    }
    
    public void scrollToTop() {
        mOutputList.setSelection(0);
    }
    
    private void setAdapter() {    
        TableAdapter adapter;
        if (mDisplayCommands) {
            adapter = new TableAdapter(mContext, mCommandRows);
        } else {
            adapter = new TableAdapter(mContext, mDiagnosticRows);
        }  
        mOutputList.setAdapter(adapter);
    }
    
    private void updateAdapter() {
        ((TableAdapter) mOutputList.getAdapter()).notifyDataSetChanged();
    }
    
    private ArrayList<OutputRow> loadSavedCommandRows() {  
        return generateRowsFromPairs(mSaver.getCommandPairs());
    }
    
    private ArrayList<OutputRow> loadSavedDiagnosticRows() {   
        return generateRowsFromPairs(mSaver.getDiagnosticPairs());
    }
    
    private ArrayList<OutputRow> generateRowsFromPairs(ArrayList<? extends Pair> pairs) {
        
        ArrayList<OutputRow> rows = new ArrayList<>();

        for (int i=0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
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
            mDiagnosticRows.add(0, row);
        } else {
            mCommandRows.add(0, row);
        }
        
        mSaver.add(row);
        updateAdapter();
    }

    public void removeRow(OutputRow row) {
        
        if (row.getPair() instanceof DiagnosticPair) {
            mDiagnosticRows.remove(row);
        } else {
            mCommandRows.remove(row);
        }
        
        mSaver.remove(row);
        updateAdapter();
    }
    
    public void deleteAllDiagnosticResponses() {
        mDiagnosticRows = new ArrayList<>();
        mSaver.deleteAllDiagnosticRows();
        updateAdapter();
    }
    
    public void deleteAllCommandResponses() {
        mCommandRows = new ArrayList<>();
        mSaver.deleteAllCommandRows();
        updateAdapter();
    }
    
}
