package com.openxc.openxcdiagnostic.diagnostic;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.diagnostic.pair.CommandPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.DiagnosticPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;
import com.openxc.openxcdiagnostic.util.Toaster;
import com.openxc.openxcdiagnostic.util.Utilities;

public class DiagnosticOutputRow {

    private LinearLayout mView;
    private DiagnosticOutputTableManager mTableManager;
    private VehicleMessage mResponse;
    private VehicleMessage mRequest;

    public DiagnosticOutputRow(DiagnosticActivity context,
            DiagnosticOutputTableManager tableManager, VehicleMessage req,
            VehicleMessage resp) {

        mView = (LinearLayout) context.getLayoutInflater().inflate(R.layout.diagoutputrow, null);
        mTableManager = tableManager;
        mResponse = resp;
        mRequest = req;

        initButtons(context, req, resp);
        fillOutputResponseTable(context, resp);
        String timestampString;
        if (resp.getTimestamp() != null) {
            timestampString = Utilities.epochTimeToTime(resp.getTimestamp());
        } else {
            timestampString = "0:00";
        }
        ((TextView) mView.findViewById(R.id.outputRowTimestamp)).setText(timestampString);
    }

    private void initButtons(final DiagnosticActivity context,
            final VehicleMessage req, final VehicleMessage resp) {

        ((Button) mView.findViewById(R.id.outputMoreButton))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResponseDetailsAlertManager.show(context, req, resp);
            }
        });

        ((Button) mView.findViewById(R.id.responseDeleteButton))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTableManager.removeRow(DiagnosticOutputRow.this);
            }
        });

        ((Button) mView.findViewById(R.id.outputResendButton))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                if (req instanceof DiagnosticRequest) {
                    message = "Resending Request.";
                } else {
                    message = "Resending Command.";
                }
                Toaster.showToast(context, message);
                context.send(req);
            }
        });
    }

    private void createAndAddRowToOutput(Activity context, LinearLayout parent,
            String label, String value, VehicleMessage msg) {

        LinearLayout row = (LinearLayout) context.getLayoutInflater().inflate(R.layout.outputresponsetablerow, null);
        ((TextView) row.findViewById(R.id.outputTableRowLabel)).setText(label);

        TextView valueText = (TextView) row.findViewById(R.id.outputTableRowValue);
        valueText.setText(value);
        if (msg instanceof DiagnosticResponse) {
            valueText.setTextColor(Utilities.getOutputColor(context, (DiagnosticResponse) msg));
        }
        parent.addView(row);
    }

    private void fillOutputResponseTable(DiagnosticActivity context,
            final VehicleMessage msgResponse) {

        LinearLayout infoTable = (LinearLayout) mView.findViewById(R.id.outputInfo);
            if (msgResponse instanceof DiagnosticResponse) {
                DiagnosticResponse resp = (DiagnosticResponse) msgResponse;
                createAndAddRowToOutput(context, infoTable, "bus", Utilities.getBusOutput(resp), resp);
                createAndAddRowToOutput(context, infoTable, "id", Utilities.getIdOutput(resp), resp);
                createAndAddRowToOutput(context, infoTable, "mode", Utilities.getModeOutput(resp), resp);
                createAndAddRowToOutput(context, infoTable, "pid", Utilities.getPidOutput(resp), resp);
                boolean responseSuccess = resp.isSuccessful();
                createAndAddRowToOutput(context, infoTable, "success", Utilities.getSuccessOutput(resp), resp);
                if (responseSuccess) {
                    fillOutputTableWithSuccessDetails(infoTable, context, resp);
                } else {
                    fillOutputTableWithFailureDetails(infoTable, context, resp);
                }
            } else if (msgResponse instanceof CommandResponse ){
                CommandResponse cmdResponse = (CommandResponse) msgResponse;
                createAndAddRowToOutput(context, infoTable, "command response", 
                        Utilities.getMessageOutput(cmdResponse), cmdResponse);
            }
    }

    private void fillOutputTableWithSuccessDetails(LinearLayout responseTable,
            Activity context, DiagnosticResponse resp) {
        createAndAddRowToOutput(context, responseTable, "payload", Utilities.getPayloadOutput(resp), resp);
        createAndAddRowToOutput(context, responseTable, "value", Utilities.getValueOutput(resp), resp);
    }

    private void fillOutputTableWithFailureDetails(LinearLayout responseTable,
            Activity context, DiagnosticResponse resp) {
        createAndAddRowToOutput(context, responseTable, "neg. resp. code", 
                Utilities.getOutputTableResponseCodeOutput(resp), resp);
    }

    public LinearLayout getView() {
        return mView;
    }

    public Pair getDiagnosticPair() {
        if (mRequest instanceof DiagnosticRequest) {
            return new DiagnosticPair((DiagnosticRequest) mRequest, (DiagnosticResponse) mResponse);
        }
        return new CommandPair((Command) mRequest, (CommandResponse) mResponse);
    }

}
