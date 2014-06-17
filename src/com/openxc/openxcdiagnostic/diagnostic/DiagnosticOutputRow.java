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

    public DiagnosticOutputRow(DiagnosticActivity context, DiagnosticOutputTable table,
            DiagnosticRequest req, DiagnosticResponse resp, int rowNumber) {

        mTable = table;
        mView = (LinearLayout) context.getLayoutInflater().inflate(R.layout.diagoutputrow, null);
        initButtons(context, req, resp);
        
        TextView outputText = (TextView) mView.findViewById(R.id.outputText);
        outputText.setTextColor(getOutputColor(context, resp));
        outputText.setText(Utilities.getOutputString(resp));
        
        ((TextView) mView.findViewById(R.id.outputRowNumberText)).setText(String.valueOf(rowNumber));
    }

    private void initButtons(final DiagnosticActivity context, final DiagnosticRequest req,
            final DiagnosticResponse resp) {

        Button moreButton = (Button) mView.findViewById(R.id.outputMoreButton);
        moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailAlert(context, req, resp);
            }
        });

        final Button deleteButton = (Button) mView.findViewById(R.id.outputDeleteButton);
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
    
    private void showDetailAlert(Activity context, DiagnosticRequest req, DiagnosticResponse resp) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout alertLayout = (LinearLayout) context.getLayoutInflater().inflate(R.layout.morealertlayout, null);
                
        fillRequestTable(alertLayout, context, req);
        fillResponseTable(alertLayout, context, resp);
        
        builder.setView(alertLayout);

        builder.setTitle(context.getResources().getString(R.string.details_button_label));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }

    public LinearLayout getView() {
        return mView;
    }
    
    private void fillRequestTable(LinearLayout alertLayout, Activity context, DiagnosticRequest req) {
        
        LinearLayout requestTable = (LinearLayout) alertLayout.findViewById(R.id.diagAlertRequestTable);
        createAndAddHeaderRow(context, requestTable, "REQUEST");
        createAndAddRow(context, requestTable, "bus", Utilities.getBusOutput(req));
        createAndAddRow(context, requestTable, "id", Utilities.getIdOutput(req));
        createAndAddRow(context, requestTable, "mode", Utilities.getModeOutput(req));
        createAndAddRow(context, requestTable, "pid", Utilities.getPidOutput(req));
        createAndAddRow(context, requestTable, "payload", Utilities.getPayloadOutput(req));
        createAndAddRow(context, requestTable, "frequency", Utilities.getFrequencyOutput(req));
        createAndAddRow(context, requestTable, "name", Utilities.getNameOutput(req));
    }
    
    private void createAndAddRow(Activity context, LinearLayout parent, String label, String value) {
        
        LinearLayout row = (LinearLayout) context.getLayoutInflater().inflate(R.layout.morealertrow, null);
        ((TextView) row.findViewById(R.id.alertRowLabel)).setText(label);
        ((TextView) row.findViewById(R.id.alertRowValue)).setText(value);
        parent.addView(row);
    }
    
    private void createAndAddHeaderRow(Activity context, LinearLayout parent, String header) {
        LinearLayout headerRow = (LinearLayout) context.getLayoutInflater().inflate(R.layout.morealertheaderrow, null);
        ((TextView) headerRow.findViewById(R.id.alertHeaderLabel)).setText(header);
        parent.addView(headerRow);
    }
    
    private void fillResponseTable(LinearLayout alertLayout, Activity context, DiagnosticResponse resp) {
        LinearLayout responseTable = (LinearLayout) alertLayout.findViewById(R.id.diagAlertResponseTable); 
        createAndAddHeaderRow(context, responseTable, "RESPONSE");
        
        createAndAddRow(context, responseTable, "bus", Utilities.getBusOutput(resp));
        createAndAddRow(context, responseTable, "id", Utilities.getIdOutput(resp));
        createAndAddRow(context, responseTable, "mode", Utilities.getModeOutput(resp));
        createAndAddRow(context, responseTable, "pid", Utilities.getPidOutput(resp));
        boolean responseSuccess = resp.getSuccess();
        createAndAddRow(context, responseTable, "success", Utilities.getSuccessOutput(resp));
        if (responseSuccess) {
            fillTableWithSuccessDetails(responseTable, context, resp);
        } else {
            fillTableWithFailureDetails(responseTable, context, resp);
        }
    }
    
    private void fillTableWithSuccessDetails(LinearLayout responseTable, Activity context, DiagnosticResponse resp) {
          
        createAndAddRow(context, responseTable, "payload", Utilities.getPayloadOutput(resp));
        createAndAddRow(context, responseTable, "value", Utilities.getValueOutput(resp));
    }
    
    private void fillTableWithFailureDetails(LinearLayout responseTable, Activity context, DiagnosticResponse resp) {
        createAndAddRow(context, responseTable, "code", Utilities.getResponseCodeOutput(resp));
    }
    
    private static int getOutputColor(Activity context, DiagnosticResponse resp) {
        int color = resp.getSuccess() ? R.color.lightBlue : R.color.darkRed;
        return context.getResources().getColor(color);
    }
    
}
