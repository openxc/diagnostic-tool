package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;

public class DiagnosticOutputTable {

    private Activity mContext;
    private List<DiagnosticOutputRow> rows = new ArrayList<>();
    private LinearLayout mView;

    public DiagnosticOutputTable(Activity context) {
        mContext = context;
        mView = (LinearLayout) context.findViewById(R.id.outputRows);
    }

    public void addRow(DiagnosticRequest req, DiagnosticResponse resp) {

        DiagnosticOutputRow row = new DiagnosticOutputRow(mContext, this, req, resp);
        rows.add(0, row);
        mView.addView(row.getView(), 0);
    }

    public void respondToConfigurationChange() {
        // Programmatically added views in the output disappear on orientation
        // change. This is a workaround to add them back
        mView.removeAllViews();
        for (int i = 0; i < rows.size(); i++) {
            View row = rows.get(i).getView();
            mView.addView(row);
        }
    }

    public void removeRow(DiagnosticOutputRow row) {
        mView.removeView(row.getView());
        rows.remove(row);
    }

}
