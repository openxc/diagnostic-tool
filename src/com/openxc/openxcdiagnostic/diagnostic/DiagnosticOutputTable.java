package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.List;

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

public class DiagnosticOutputTable {

    private Activity mContext;
    private List<View> rows = new ArrayList<>();
    LinearLayout outputRows;

    public DiagnosticOutputTable(Activity context) {
        mContext = context;
        outputRows = (LinearLayout) context.findViewById(R.id.outputRows);
    }

    public void AddRow(DiagnosticRequest req, DiagnosticResponse response) {

        LinearLayout row = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.createsingleoutputrow, null);
        TextView output = (TextView) row.getChildAt(0);

        final Button detailsButton = (Button) row.getChildAt(1);
        detailsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Details").setTitle("Details");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
            }
        });

        final Button deleteButton = (Button) row.getChildAt(2);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View row = (View) deleteButton.getParent();
                outputRows.removeView(row);
                rows.remove(row);
            }
        });

        Utilities.writeLine(output, "bus : "
                + String.valueOf(response.getCanBus()));
        Utilities.writeLine(output, "id : " + String.valueOf(response.getId()));
        Utilities.writeLine(output, "mode: "
                + String.valueOf(response.getMode()));
        boolean success = response.getSuccess();
        Utilities.writeLine(output, "success : " + String.valueOf(success));
        if (success) {
            Utilities.writeLine(output, "payload : "
                    + String.valueOf(response.getPayload()));
            output.append("value : " + String.valueOf(response.getValue()));
        } else {
            output.append("negative_response_code"
                    + response.getNegativeResponseCode().toString());
        }

        outputRows.addView(row, 0);
        rows.add(0, row);
    }

    public void respondToConfigurationChange() {
        // Programmatically added views in the output disappear on orientation
        // change. This is a workaround to add them back
        LinearLayout outputRows = (LinearLayout) mContext.findViewById(R.id.outputRows);
        outputRows.removeAllViews();
        for (int i = 0; i < rows.size(); i++) {
            View row = rows.get(i);
            outputRows.addView(row);
        }
    }

}
