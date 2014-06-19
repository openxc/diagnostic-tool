package com.openxc.openxcdiagnostic.diagnostic;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openxc.messages.DiagnosticMessage;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.resources.Utilities;

public class DiagnosticOutputRow {

    private LinearLayout mView;
    private DiagnosticOutputTable mTable;

    public DiagnosticOutputRow(DiagnosticActivity context, DiagnosticOutputTable table,
            DiagnosticRequest req, DiagnosticResponse resp, int rowNumber) {

        mTable = table;
        mView = (LinearLayout) context.getLayoutInflater().inflate(R.layout.diagoutputrow, null);
        
        initButtons(context, req, resp);
        fillOutputResponseTable(context, resp);        
        ((TextView) mView.findViewById(R.id.outputRowNumberText)).setText(String.valueOf(rowNumber));
    }

    private void initButtons(final DiagnosticActivity context, final DiagnosticRequest req,
            final DiagnosticResponse resp) {

        Button detailsButton = (Button) mView.findViewById(R.id.outputMoreButton);
        detailsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DiagnosticResponseDetailsAlertManager.show(context, req, resp);
            }
        });

        final Button deleteButton = (Button) mView.findViewById(R.id.responseDeleteButton);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTable.removeRow(DiagnosticOutputRow.this);
            }
        });
        
        Button resendButton = (Button) mView.findViewById(R.id.outputResendButton);
        resendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendRequest(req);
            }
        });

    }
    
    private void createAndAddRowToOutput(Activity context, LinearLayout parent, String label, 
            String value, DiagnosticMessage msg) {
        
        LinearLayout row = (LinearLayout) context.getLayoutInflater().inflate(R.layout.outputresponsetablerow, null);
        ((TextView) row.findViewById(R.id.outputTableRowLabel)).setText(label);
        
        TextView valueText =  (TextView) row.findViewById(R.id.outputTableRowValue);
        valueText.setText(value);
        if (msg instanceof DiagnosticResponse) {
            valueText.setTextColor(Utilities.getOutputColor(context, (DiagnosticResponse) msg));
        }
        parent.addView(row);
    }
    
    private void fillOutputResponseTable(DiagnosticActivity context, DiagnosticResponse resp) {
        
        LinearLayout infoTable = (LinearLayout) mView.findViewById(R.id.outputInfo);
        
        createAndAddRowToOutput(context, infoTable, "bus", Utilities.getBusOutput(resp), resp);
        createAndAddRowToOutput(context, infoTable, "id", Utilities.getIdOutput(resp), resp);
        createAndAddRowToOutput(context, infoTable, "mode", Utilities.getModeOutput(resp), resp);
        createAndAddRowToOutput(context, infoTable, "pid", Utilities.getPidOutput(resp), resp);
        boolean responseSuccess = resp.getSuccess();
        createAndAddRowToOutput(context, infoTable, "success", Utilities.getSuccessOutput(resp), resp);
        if (responseSuccess) {
            fillOutputTableWithSuccessDetails(infoTable, context, resp);
        } else {
            fillOutputTableWithFailureDetails(infoTable, context, resp);
        }
    }    
    
    private void fillOutputTableWithSuccessDetails(LinearLayout responseTable, 
            Activity context, DiagnosticResponse resp) {
        createAndAddRowToOutput(context, responseTable, "payload", 
                Utilities.getPayloadOutput(resp), resp);
        createAndAddRowToOutput(context, responseTable, "value", 
                Utilities.getValueOutput(resp), resp);
    }
    
    private void fillOutputTableWithFailureDetails(LinearLayout responseTable, Activity context, 
            DiagnosticResponse resp) {
        createAndAddRowToOutput(context, responseTable, "neg. resp. code", 
                Utilities.getOutputTableResponseCodeOutput(resp), resp);
    }

    public LinearLayout getView() {
        return mView;
    }
    
}
