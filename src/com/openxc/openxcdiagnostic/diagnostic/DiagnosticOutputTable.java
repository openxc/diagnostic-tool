package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;

import android.util.Log;
import android.widget.LinearLayout;

import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;

public class DiagnosticOutputTable {

    private DiagnosticActivity mContext;
    private DiagnosticOutputTableSaver mSaver;
    private LinearLayout mView;
    private static String TAG = "DiagnosticOutputTable";

    public DiagnosticOutputTable(DiagnosticActivity context) {
        mContext = context;
        mSaver = new DiagnosticOutputTableSaver(context);
        mView = (LinearLayout) context.findViewById(R.id.outputRows);
    }

    public void load() {

        clearTable();
        ArrayList<DiagnosticRequest> savedRequests = mSaver.getSavedRequests();
        ArrayList<DiagnosticResponse> savedResponses = mSaver.getSavedResponses();

        if (savedRequests.size() == savedResponses.size()) {
            for (int i = savedRequests.size() - 1; i >= 0; i--) {
                addToTable(savedRequests.get(i), savedResponses.get(i));
            }
        } else {
            Log.e(TAG, "Unmatched requests and responses...cannot load table.");
        }

    }

    public void addRow(DiagnosticRequest req, DiagnosticResponse resp) {
        addToTable(req, resp);
        mSaver.add(req, resp);
    }

    private void addToTable(DiagnosticRequest req, DiagnosticResponse resp) {
        DiagnosticOutputRow row = new DiagnosticOutputRow(mContext, this, req, resp);
        mView.addView(row.getView(), 0);
    }

    public void removeRow(DiagnosticOutputRow row) {
        mView.removeView(row.getView());
        mSaver.remove(row.getRequest(), row.getResponse());
    }

    public void deleteAllRows() {
        clearTable();
        mSaver.removeAll();
    }

    private void clearTable() {
        mView.removeAllViews();
    }

}
