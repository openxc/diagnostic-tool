package com.openxc.openxcdiagnostic.diagnostic.output;

import java.util.ArrayList;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.ExactKeyMatcher;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticManager;
import com.openxc.openxcdiagnostic.diagnostic.pair.CommandPair;
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
    
    private boolean shouldScrollToTop(VehicleMessage response) {
        return ((Utilities.isCommandResponse(response) && mContext.isDisplayingCommands()) 
                || (Utilities.isDiagnosticResponse(response) && !mContext.isDisplayingCommands()))
                && mContext.shouldScroll();
    }
        
    private void setAdapter() {            
        ArrayList<OutputRow> rows;
        if (mDisplayCommands) {
            rows = mCommandRows;
        } else {
            rows = mDiagnosticRows;
        }        
        
        mOutputList.setAdapter(new TableAdapter(mContext, rows));
    }
    
    private void updateAdapter() {
        TableAdapter adapter = (TableAdapter) mOutputList.getAdapter();
        if (adapter != null) {
            ArrayList<OutputRow> rows;
            if (mDisplayCommands) {
                rows = mCommandRows;
            } else {
                rows = mDiagnosticRows;
            }    
            adapter.refresh(rows);
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
    
    public boolean replaceIfMatchesExisting(VehicleMessage req, VehicleMessage resp) {
        
        if (Utilities.isDiagnosticResponse(resp)) {
            ExactKeyMatcher responseMatcher = ExactKeyMatcher.buildExactMatcher((DiagnosticResponse) resp);
            for (int i = 0; i < mDiagnosticRows.size(); i++) {
                OutputRow row = mDiagnosticRows.get(i);
                DiagnosticPair pair = (DiagnosticPair) row.getPair();
                if (responseMatcher.matches((DiagnosticResponse) pair.getResponse())) {
                    row.setPair(new DiagnosticPair((DiagnosticRequest) req, (DiagnosticResponse) resp));
                    return true;
                }
            }
        } else if (Utilities.isCommandResponse(resp)){
            ExactKeyMatcher responseMatcher = ExactKeyMatcher.buildExactMatcher((CommandResponse) resp);
            for (int i = 0; i < mCommandRows.size(); i++) {
                OutputRow row = mCommandRows.get(i);
                CommandPair pair = (CommandPair) row.getPair();
                if (responseMatcher.matches(pair.getResponse())) {
                    row.setPair(new CommandPair((Command) req, (CommandResponse) resp));
                    return true;
                }
            }
        }
        return false;
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
