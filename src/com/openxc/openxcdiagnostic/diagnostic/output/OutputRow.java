package com.openxc.openxcdiagnostic.diagnostic.output;

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
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;
import com.openxc.openxcdiagnostic.diagnostic.ResponseDetailsAlertManager;
import com.openxc.openxcdiagnostic.diagnostic.pair.CommandPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.DiagnosticPair;
import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;
import com.openxc.openxcdiagnostic.util.Formatter;
import com.openxc.openxcdiagnostic.util.Toaster;
import com.openxc.openxcdiagnostic.util.Utilities;

public class OutputRow {

    private LinearLayout mView;
    private OutputTableManager mTableManager;
    private VehicleMessage mResponse;
    private VehicleMessage mRequest;
    private DiagnosticActivity mContext;
    private ResponseDetailsAlertManager mAlertManager;

    public OutputRow(DiagnosticActivity context,
            OutputTableManager tableManager, VehicleMessage req,
            VehicleMessage resp) {

        mView = (LinearLayout) context.getLayoutInflater().inflate(R.layout.diagoutputrow, null);
        mTableManager = tableManager;
        mAlertManager = new ResponseDetailsAlertManager(context, req, resp);
        mResponse = resp;
        mRequest = req;
        mContext = context;

        initButtons();
        fillOutputResponseTable();
        setTimestamp();
    }
    
    public void setPair(final Pair pair) {
        mRequest = pair.getRequest();
        mResponse = pair.getResponse();
        
        if(mAlertManager.isShowing()) {
            mAlertManager.refresh(mRequest, mResponse);
        }
        
        fillOutputResponseTable();
        setTimestamp();
    }
    
    private void setTimestamp() {
        String timestampString;
        if (mResponse.getTimestamp() != null) {
            timestampString = Utilities.epochTimeToTime(mResponse.getTimestamp());
        } else {
            timestampString = "0:00";
        }
        ((TextView) mView.findViewById(R.id.outputRowTimestamp)).setText(timestampString);
    }

    private void initButtons() {

        ((Button) mView.findViewById(R.id.outputMoreButton))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertManager.show();
            }
        });

        ((Button) mView.findViewById(R.id.responseDeleteButton))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTableManager.removeRow(OutputRow.this);
            }
        });

        ((Button) mView.findViewById(R.id.outputResendButton))
        .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                if (Utilities.isDiagnosticRequest(mRequest)) {
                    message = "Resending Request.";
                    mContext.send(mRequest);
                } else if (Utilities.isCommand(mRequest)) {
                    message = "Resending Command.";
                    mContext.send(mRequest);
                } else {
                    message = "Cannot be resent...no request/command found.";
                }
                Toaster.showToast(mContext, message);
            }
        });
    }
    
    private void setOutputInfoFormat() {
        LinearLayout infoTable = (LinearLayout) mView.findViewById(R.id.outputInfo);
        int layoutId;
        if (Utilities.isDiagnosticResponse(mResponse)) {
            layoutId = R.layout.diagresponseoutputinfo;
        } else {
            layoutId = R.layout.commandresponseoutputinfo;
        }
        LinearLayout newView = (LinearLayout) mContext.getLayoutInflater().inflate(layoutId, null);
        infoTable.removeAllViews();
        infoTable.addView(newView);
    }

    private void fillOutputResponseTable() {
        
        setOutputInfoFormat();
        LinearLayout infoTable = (LinearLayout) mView.findViewById(R.id.outputInfo);
            if (Utilities.isDiagnosticResponse(mResponse)) {
                DiagnosticResponse resp = (DiagnosticResponse) mResponse;
                ((TextView) infoTable.findViewById(R.id.busValue)).setText(Formatter.getBusOutput(resp));
                ((TextView) infoTable.findViewById(R.id.idValue)).setText(Formatter.getIdOutput(resp));
                ((TextView) infoTable.findViewById(R.id.modeValue)).setText(Formatter.getModeOutput(resp));
                ((TextView) infoTable.findViewById(R.id.pidValue)).setText(Formatter.getPidOutput(resp));
                ((TextView) infoTable.findViewById(R.id.successValue)).setText(Formatter.getSuccessOutput(resp));
                ((TextView) infoTable.findViewById(R.id.payloadValue)).setText(Formatter.getPayloadOutput(resp));
                ((TextView) infoTable.findViewById(R.id.valueValue)).setText(Formatter.getValueOutput(resp));
            } else if (Utilities.isCommandResponse(mResponse)){
                CommandResponse cmdResponse = (CommandResponse) mResponse;
                ((TextView) infoTable.findViewById(R.id.commandValue)).setText( 
                        Formatter.getCommandOutput(cmdResponse));
                ((TextView) infoTable.findViewById(R.id.commandResponseValue)).setText( 
                        Formatter.getMessageOutput(cmdResponse));
            }
    }

    /*private void fillOutputTableWithSuccessDetails(LinearLayout responseTable,
            DiagnosticResponse resp) {
        createAndAddRowToOutput(responseTable, "payload", Formatter.getPayloadOutput(resp), resp);
        createAndAddRowToOutput(responseTable, "value", Formatter.getValueOutput(resp), resp);
    }

    private void fillOutputTableWithFailureDetails(LinearLayout responseTable,
            DiagnosticResponse resp) {
        createAndAddRowToOutput(responseTable, "neg. resp. code", 
                Formatter.getOutputTableResponseCodeOutput(resp), resp);
    }*/

    public LinearLayout getView() {
        return mView;
    }

    public Pair getPair() {
        if (Utilities.isDiagnosticResponse(mResponse)) {
            return new DiagnosticPair(mRequest == null ? null : (DiagnosticRequest) mRequest,
                    (DiagnosticResponse) mResponse);
        }
        return new CommandPair(mRequest == null ? null : (Command) mRequest, (CommandResponse) mResponse);
    }

}
