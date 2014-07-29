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
import com.openxc.openxcdiagnostic.util.MessageAnalyzer;

/**
 * 
 * Manager for handling the output table and all its rows.
 * 
 */
public class OutputTableManager implements DiagnosticManager {

    private DiagnosticActivity mContext;
    private TableSaver mSaver;
    private static String TAG = "DiagnosticOutputTable";
    private ListView mOutputList;
    private boolean mDisplayCommands;
    private ArrayList<OutputRow> mDiagnosticRows;
    private ArrayList<OutputRow> mCommandRows;

    public OutputTableManager(DiagnosticActivity context,
            boolean displayCommands) {
        mContext = context;
        mSaver = new TableSaver(context);
        mOutputList = (ListView) mContext
                .findViewById(R.id.responseOutputScroll);
        mDiagnosticRows = loadSavedDiagnosticRows();
        mCommandRows = loadSavedCommandRows();
        setRequestCommandState(displayCommands);
    }

    @Override
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
        setAdapter();
    }

    /**
     * Determines if the output should scroll to the top
     * 
     * @param response
     * @return True if the <code>response</code> corresponds to the table that
     *         is currently showing, and the setting is checked in settings
     */
    private boolean shouldScrollToTop(VehicleMessage response) {
        return mContext.shouldScroll()
                && ((MessageAnalyzer.isCommandResponse(response) && mContext
                        .isDisplayingCommands()) || (MessageAnalyzer
                        .isDiagnosticResponse(response) && !mContext
                        .isDisplayingCommands()));
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

    /**
     * Retro-creates an <code>OutputRow</code> for each element in
     * <code>pairs</code>
     * 
     * @param pairs
     *            An array of Pairs
     * @return The array of rows
     */
    private ArrayList<OutputRow> generateRowsFromPairs(
            ArrayList<? extends Pair> pairs) {
        ArrayList<OutputRow> rows = new ArrayList<>();
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            rows.add(new OutputRow(mContext, this, pair.getRequest(), pair
                    .getResponse()));
        }
        return rows;
    }

    /**
     * Add the given <code>req</code> and <code>resp</code> to the table in a
     * new <code>OutputRow</code>
     * 
     * @param req
     * @param resp
     */
    public void add(VehicleMessage req, VehicleMessage resp) {

        if (correspond(req, resp)) {

            // fix scroll position of table after adding row
            int index = 0;
            int distance = 0;
            if (!shouldScrollToTop(resp)) {
                index = mOutputList.getFirstVisiblePosition();
                View v = mOutputList.getChildAt(0);
                distance = (v == null) ? 0 : v.getTop() - v.getHeight();
            }
            addToTable(req, resp);
            mOutputList.setSelectionFromTop(index, distance);
        } else {
            Log.e(TAG,
                    "Unable to add mismatched VehicleMessage types to table.");
        }
    }

    /**
     * Takes a "snapshot" of the current rows in both tables and writes it to
     * memory for later use
     */
    public void save() {
        mSaver.saveDiagnosticRows(mDiagnosticRows);
        mSaver.saveCommandRows(mCommandRows);
    }

    /**
     * Searches for a row in <code>mDiagnosticRows</code> or
     * <code>mCommandRows</code> with a response that matches <code>resp</code>
     * according to an <code>ExactKeyMatcher</code> built from <code>resp</code>
     * . If it is found, the row is updated with the provided messages.
     * 
     * @param req
     * @param resp
     * @return True if a matching row was found and updated, false otherwise.
     */
    public boolean replaceIfMatchesExisting(VehicleMessage req,
            VehicleMessage resp) {

        if (MessageAnalyzer.isDiagnosticResponse(resp)) {
            ExactKeyMatcher responseMatcher = ExactKeyMatcher
                    .buildExactMatcher((DiagnosticResponse) resp);
            for (int i = 0; i < mDiagnosticRows.size(); i++) {
                OutputRow row = mDiagnosticRows.get(i);
                DiagnosticPair pair = (DiagnosticPair) row.getPair();
                if (responseMatcher.matches((DiagnosticResponse) pair
                        .getResponse())) {
                    row.setPair(new DiagnosticPair((DiagnosticRequest) req,
                            (DiagnosticResponse) resp));
                    return true;
                }
            }
        } else if (MessageAnalyzer.isCommandResponse(resp)) {
            ExactKeyMatcher responseMatcher = ExactKeyMatcher
                    .buildExactMatcher((CommandResponse) resp);
            for (int i = 0; i < mCommandRows.size(); i++) {
                OutputRow row = mCommandRows.get(i);
                CommandPair pair = (CommandPair) row.getPair();
                if (responseMatcher.matches(pair.getResponse())) {
                    row.setPair(new CommandPair((Command) req,
                            (CommandResponse) resp));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks that the provided messages are a legitimate combination of
     * <code>DiagnosticRequest</code> and <code>DiagnosticResponse</code> or
     * <code>Command</code> and <code>CommandResponse</code>.
     * 
     * @param req
     * @param resp
     * @return <code>true</code> if <code>req</code> is a
     *         <code>DiagnosticRequest</code> and <code>resp</code> is a
     *         <code>DiagnosticResponse</code> (<code>req</code> may be null) or
     *         if <code>req</code> is a <code>Command</code> and
     *         <code>resp</code> is a <code>CommandResponse</code> (
     *         <code>req</code> may again be null); <code>false</code>
     *         otherwise.
     */
    private boolean correspond(VehicleMessage req, VehicleMessage resp) {
        boolean bothValid = (MessageAnalyzer.isDiagnosticRequest(req) && MessageAnalyzer
                .isDiagnosticResponse(resp))
                || (MessageAnalyzer.isCommand(req) && MessageAnalyzer
                        .isCommandResponse(resp));
        boolean nullReqValidResp = (req == null && (MessageAnalyzer
                .isDiagnosticResponse(resp) || MessageAnalyzer
                .isCommandResponse(resp)));
        return bothValid || nullReqValidResp;
    }

    private void addToTable(VehicleMessage req, VehicleMessage resp) {
        OutputRow row = new OutputRow(mContext, this, req, resp);
        if (row.getPair() instanceof DiagnosticPair) {
            mDiagnosticRows.add(0, row);
        } else {
            mCommandRows.add(0, row);
        }

        setAdapter();
    }

    /**
     * Remove the given <code>row</code> from the table.
     * 
     * @param row
     */
    public void removeRow(OutputRow row) {

        if (row.getPair() instanceof DiagnosticPair) {
            mDiagnosticRows.remove(row);
        } else {
            mCommandRows.remove(row);
        }

        updateAdapter();
    }

    /**
     * Clears the diagnostic table of all rows.
     */
    public void deleteAllDiagnosticResponses() {
        mDiagnosticRows = new ArrayList<>();
        setAdapter();
    }

    /**
     * Clears the command response table of all rows.
     */
    public void deleteAllCommandResponses() {
        mCommandRows = new ArrayList<>();
        setAdapter();
    }

}
