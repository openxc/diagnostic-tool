package com.openxc.openxcdiagnostic.diagnostic.output;

import java.util.ArrayList;

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
import com.openxc.openxcdiagnostic.util.MessageAnalyzer;
import com.openxc.openxcdiagnostic.util.Toaster;
import com.openxc.openxcdiagnostic.util.Utilities;

/**
 * 
 * Logically represents a single entry in the output table. Responsible for
 * managing the corresponding buttons and details alert.
 * 
 */
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

        mView = (LinearLayout) context.getLayoutInflater().inflate(
                R.layout.diagoutputrow, null);
        mTableManager = tableManager;
        mAlertManager = new ResponseDetailsAlertManager(context, req, resp);
        mResponse = resp;
        mRequest = req;
        mContext = context;

        setFields();
        initButtons();
    }

    /**
     * Sets the pair of the row to the given <code>pair</code>
     * 
     * @param pair
     *            The new pair
     */
    public void setPair(final Pair pair) {
        mRequest = pair.getRequest();
        mResponse = pair.getResponse();

        mAlertManager.refresh(mRequest, mResponse);
        setFields();
    }

    private void setFields() {
        fillOutputResponseTable();
        setTimestamp();
    }

    private void setTimestamp() {
        String timestampString;
        if (mResponse.getTimestamp() != null) {
            timestampString = Utilities.epochTimeToTime(mResponse
                    .getTimestamp());
        } else {
            timestampString = "0:00";
        }
        ((TextView) mView.findViewById(R.id.outputRowTimestamp))
                .setText(timestampString);
    }

    private void initButtons() {

        ((Button) mView.findViewById(R.id.outputMoreButton))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mAlertManager.isShowing()) {
                            mAlertManager.show();
                        }
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
                        if (MessageAnalyzer.isDiagnosticRequest(mRequest)) {
                            message = "Resending Request.";
                            mContext.send(mRequest);
                        } else if (MessageAnalyzer.isCommand(mRequest)) {
                            message = "Resending Command.";
                            mContext.send(mRequest);
                        } else {
                            message = "Cannot be resent...no request/command found.";
                        }
                        Toaster.showToast(mContext, message);
                    }
                });
    }

    /**
     * Sets the correct output layout, dependent on if the response is a
     * <code>DiagnosticResponse</code> or a <code>CommandResponse</code>
     */
    private void setOutputInfoFormat() {
        LinearLayout infoTable = (LinearLayout) mView
                .findViewById(R.id.outputInfo);
        int layoutId;
        if (MessageAnalyzer.isDiagnosticResponse(mResponse)) {
            layoutId = R.layout.diagresponseoutputinfo;
        } else {
            layoutId = R.layout.commandresponseoutputinfo;
        }
        LinearLayout newView = (LinearLayout) mContext.getLayoutInflater()
                .inflate(layoutId, null);
        infoTable.removeAllViews();
        infoTable.addView(newView);
    }

    private void fillOutputResponseTable() {

        setOutputInfoFormat();
        LinearLayout infoTable = (LinearLayout) mView
                .findViewById(R.id.outputInfo);
        if (MessageAnalyzer.isDiagnosticResponse(mResponse)) {
            DiagnosticResponse resp = (DiagnosticResponse) mResponse;

            ArrayList<TextView> outputs = new ArrayList<>();

            TextView busOutput = (TextView) infoTable
                    .findViewById(R.id.busValue);
            busOutput.setText(Formatter.getBusOutput(resp));
            outputs.add(busOutput);

            TextView idOutput = (TextView) infoTable.findViewById(R.id.idValue);
            idOutput.setText(Formatter.getIdOutput(resp));
            outputs.add(idOutput);

            TextView modeOutput = (TextView) infoTable
                    .findViewById(R.id.modeValue);
            modeOutput.setText(Formatter.getModeOutput(resp));
            outputs.add(modeOutput);

            TextView pidOutput = (TextView) infoTable
                    .findViewById(R.id.pidValue);
            pidOutput.setText(Formatter.getPidOutput(resp));
            outputs.add(pidOutput);

            TextView successOutput = (TextView) infoTable
                    .findViewById(R.id.successValue);
            successOutput.setText(Formatter.getSuccessOutput(resp));
            outputs.add(successOutput);

            TextView payloadOutput = (TextView) infoTable
                    .findViewById(R.id.payloadValue);
            payloadOutput.setText(Formatter.getPayloadOutput(resp));
            outputs.add(payloadOutput);

            TextView valueOutput = (TextView) infoTable
                    .findViewById(R.id.valueValue);
            valueOutput.setText(Formatter.getValueOutput(resp));
            outputs.add(valueOutput);

            int outputColor = Formatter.getOutputColor(mContext, resp);
            for (TextView tv : outputs) {
                tv.setTextColor(outputColor);
                Utilities.fitTextInTextView(mContext, tv);
            }

        } else if (MessageAnalyzer.isCommandResponse(mResponse)) {
            CommandResponse cmdResponse = (CommandResponse) mResponse;
            ((TextView) infoTable.findViewById(R.id.commandValue))
                    .setText(Formatter.getCommandOutput(cmdResponse));
            ((TextView) infoTable.findViewById(R.id.commandResponseValue))
                    .setText(Formatter.getMessageOutput(cmdResponse));
        }
    }

    /**
     * Get the view.
     * 
     * @return The linearlayout used to layout the row
     */
    public LinearLayout getView() {
        return mView;
    }

    /**
     * Get the current (DiagnosticRequest + DiagnosticResponse) or (Command +
     * CommandResponse) wrapped in the appropriate pair.
     * 
     * @return
     */
    public Pair getPair() {
        if (MessageAnalyzer.isDiagnosticResponse(mResponse)) {
            return new DiagnosticPair(mRequest == null ? null
                    : (DiagnosticRequest) mRequest,
                    (DiagnosticResponse) mResponse);
        }
        return new CommandPair(mRequest == null ? null : (Command) mRequest,
                (CommandResponse) mResponse);
    }

}
