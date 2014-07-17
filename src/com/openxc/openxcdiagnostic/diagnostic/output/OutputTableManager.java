package com.openxc.openxcdiagnostic.diagnostic.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticManager;
import com.openxc.openxcdiagnostic.diagnostic.pair.DiagnosticPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;
import com.openxc.openxcdiagnostic.util.Utilities;

public class OutputTableManager implements DiagnosticManager  {

    private DiagnosticActivity mContext;
    private TableSaver mSaver;
    private static String TAG = "DiagnosticOutputTable";
    private ListView mOutputList;
    private boolean mDisplayCommands;
    private ArrayList<OutputRow> mDiagnosticRows;
    private ArrayList<OutputRow> mCommandRows;
    private Map<Boolean, ArrayList<OutputRow>> rowsToDisplay;

    public OutputTableManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        mSaver = new TableSaver(context);
        mOutputList = (ListView) mContext.findViewById(R.id.responseOutputScroll);
        mDiagnosticRows = loadSavedDiagnosticRows();
        mCommandRows = loadSavedCommandRows();
        rowsToDisplay = new HashMap<>();
        rowsToDisplay.put(Boolean.valueOf(true), mCommandRows);
        rowsToDisplay.put(Boolean.valueOf(false), mDiagnosticRows);
        setRequestCommandState(displayCommands);
    }
    
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
        setAdapter();
    }
    
    private boolean shouldScrollToTop(VehicleMessage response) {
        return ((Utilities.isCommandResponse(response) && mContext.isDisplayingCommands()) 
                || (Utilities.isDiagnosticResponse(response) && !mContext.isDisplayingCommands()))
                && mContext.shouldScroll();
    }
        
    private void setAdapter() {            
        mOutputList.setAdapter(new TableAdapter(mContext, rowsToDisplay.get(mDisplayCommands)));
    }
    
    private void updateAdapter() {
        TableAdapter adapter = (TableAdapter) mOutputList.getAdapter();
        if (adapter != null) {
            adapter.refresh(rowsToDisplay.get(mDisplayCommands));
        }
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
        
        //if pair corresponds correctly
        if (correspond(req, resp)) {
            
            //fix scroll position of table after adding row
            int index = 0;
            int distance = 0;
            if (!shouldScrollToTop(resp)) {
                index = mOutputList.getFirstVisiblePosition();
                View v = mOutputList.getChildAt(0);
                distance = (v == null) ? 0 : v.getTop() - v.getHeight();
            }
            save(req, resp);
            mOutputList.setSelectionFromTop(index, distance);
        } else {
            Log.e(TAG, "Unable to add mismatched VehicleMessage types to table.");
        }
    }
    
    private boolean correspond(VehicleMessage req, VehicleMessage resp) {
        boolean bothValid = (Utilities.isDiagnosticRequest(req) && Utilities.isDiagnosticResponse(resp)) || 
        (Utilities.isCommand(req) && Utilities.isCommandResponse(resp));
        boolean nullReqValidResp = (req == null && (Utilities.isDiagnosticResponse(resp) 
                || Utilities.isCommandResponse(resp)));
        return bothValid || nullReqValidResp;
    }
    
    private void save(VehicleMessage req, VehicleMessage resp) {   
        OutputRow row = new OutputRow(mContext, this, req, resp);
        if (row.getPair() instanceof DiagnosticPair) {
            mDiagnosticRows.add(0, row);
        } else {
            mCommandRows.add(0, row);
        }
        
        mSaver.add(row);
        setAdapter();
    }
    
    public boolean containsResponse(VehicleMessage response) {
                
        ArrayList<OutputRow> rowsToSearch;
        if (Utilities.isDiagnosticResponse(response)) {
            rowsToSearch = mDiagnosticRows;
        } else {
            rowsToSearch = mCommandRows;
        }
        
        for (int i = 0; i < rowsToSearch.size(); i++) {
            if (response.equals(rowsToSearch.get(i).getPair().getResponse())) {
                return true;
            }
        }
        return false;
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
        setAdapter();
    }
    
    public void deleteAllCommandResponses() {
        mCommandRows = new ArrayList<>();
        mSaver.deleteAllCommandRows();
        setAdapter();
    }
    
}
