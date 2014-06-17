package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.List;

import android.widget.LinearLayout;

import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;

public class DiagnosticOutputTable {

    private DiagnosticActivity mContext;
    private List<DiagnosticOutputRow> rows = new ArrayList<>();
    private LinearLayout mView;

    public DiagnosticOutputTable(DiagnosticActivity context) {
        mContext = context;
        mView = (LinearLayout) context.findViewById(R.id.outputRows);
    }

    public void addRow(DiagnosticRequest req, DiagnosticResponse resp) {

        DiagnosticOutputRow row = new DiagnosticOutputRow(mContext, this, req, resp);
        rows.add(0, row);
        mView.addView(row.getView(), 0);
    }

    public void removeRow(DiagnosticOutputRow row) {
        mView.removeView(row.getView());
        rows.remove(row);
    }

}
