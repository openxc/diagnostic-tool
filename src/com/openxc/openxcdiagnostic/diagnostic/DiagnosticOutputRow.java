package com.openxc.openxcdiagnostic.diagnostic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.resources.Utilities;

public class DiagnosticOutputRow {

    private LinearLayout mView;
    private DiagnosticOutputTable mTable;

    public DiagnosticOutputRow(Activity context, DiagnosticOutputTable table,
            DiagnosticRequest req, DiagnosticResponse resp) {

        mTable = table;
        mView = (LinearLayout) context.getLayoutInflater().inflate(R.layout.createsingleoutputrow, null);
        initButtons(context, table, req, resp);
        populateText(resp);
    }

    private void initButtons(final Activity context,
            DiagnosticOutputTable table, final DiagnosticRequest req,
            final DiagnosticResponse resp) {

        Button detailsButton = (Button) mView.getChildAt(1);
        detailsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(resp.toString() + " " + req.toString()).setTitle("Details");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
            }
        });

        final Button deleteButton = (Button) mView.getChildAt(2);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTable.removeRow(DiagnosticOutputRow.this);
            }
        });

    }

    public LinearLayout getView() {
        return mView;
    }

    private void populateText(DiagnosticResponse resp) {

        TextView outputText = (TextView) mView.getChildAt(0);
        Utilities.writeLine(outputText, "bus : "
                + String.valueOf(resp.getCanBus()));
        Utilities.writeLine(outputText, "id : " + String.valueOf(resp.getId()));
        Utilities.writeLine(outputText, "mode: "
                + String.valueOf(resp.getMode()));
        boolean success = resp.getSuccess();
        Utilities.writeLine(outputText, "success : " + String.valueOf(success));
        if (success) {
            Utilities.writeLine(outputText, "payload : "
                    + String.valueOf(resp.getPayload()));
            outputText.append("value : " + String.valueOf(resp.getValue()));
        } else {
            outputText.append("negative_response_code"
                    + resp.getNegativeResponseCode().toString());
        }
    }
}
