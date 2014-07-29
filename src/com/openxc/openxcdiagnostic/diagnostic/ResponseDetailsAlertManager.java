package com.openxc.openxcdiagnostic.diagnostic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
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
import com.openxc.openxcdiagnostic.util.Formatter;
import com.openxc.openxcdiagnostic.util.MessageAnalyzer;
import com.openxc.openxcdiagnostic.util.Toaster;

/**
 * 
 * Manager for showing details about a Request/Command and
 * Response/CommandResponse.
 * 
 */
public class ResponseDetailsAlertManager {

    private DiagnosticActivity mContext;
    private LinearLayout mAlertLayout;
    private VehicleMessage mRequest;
    private VehicleMessage mResponse;
    private boolean mIsShowing = false;
    private Handler mHandler = new Handler();
    private Resources mResources;

    public ResponseDetailsAlertManager(DiagnosticActivity context,
            VehicleMessage req, VehicleMessage resp) {
        mRequest = req;
        mResponse = resp;
        mContext = context;
        mResources = context.getResources();
    }

    /**
     * Display the request and the response in table form in an alert dialog.
     */
    public void show() {

        mIsShowing = true;

        mAlertLayout = (LinearLayout) mContext.getLayoutInflater().inflate(
                R.layout.diagdetailsalert, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        fill(mRequest, mResponse);

        builder.setView(mAlertLayout);
        builder.setTitle(mResources.getString(R.string.details_alert_label));
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mIsShowing = false;
                    }
                });
        builder.create().show();
    }

    /**
     * @return <code>true</code> if the alert is showing; <code>false</code>
     *         otherwise
     */
    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * Sets the request and response to the new <code>req</code> and
     * <code>resp</code>, respectively
     * 
     * @param req
     *            The new <code>DiagnosticRequest</code> or <code>Command</code>
     * @param resp
     *            The new <code>DiagnosticResponse</code> or
     *            <code>CommandResponse</code>
     */
    public void refresh(final VehicleMessage req, final VehicleMessage resp) {
        mRequest = req;
        mResponse = resp;

        if (isShowing()) {
            mHandler.post(new Runnable() {
                public void run() {
                    fill(req, resp);
                }
            });
        }
    }

    private void fill(VehicleMessage req, VehicleMessage resp) {
        fillRequestTable(req);
        fillResponseTable(resp);
    }

    private void fillRequestTable(VehicleMessage reqMessage) {

        LinearLayout requestTable = (LinearLayout) mAlertLayout
                .findViewById(R.id.diagAlertRequestTable);
        requestTable.removeAllViews();
        if (MessageAnalyzer.isDiagnosticRequest(reqMessage)) {
            DiagnosticRequest req = (DiagnosticRequest) reqMessage;
            createAndAddHeaderRow(requestTable,
                    mResources.getString(R.string.alert_request_header));
            createAndAddRow(requestTable,
                    mResources.getString(R.string.bus_label),
                    Formatter.getBusOutput(req), req);
            createAndAddRow(requestTable,
                    mResources.getString(R.string.id_label),
                    Formatter.getIdOutput(req), req);
            createAndAddRow(requestTable,
                    mResources.getString(R.string.mode_label),
                    Formatter.getModeOutput(req), req);
            createAndAddRow(requestTable,
                    mResources.getString(R.string.pid_label),
                    Formatter.getPidOutput(req), req);
            createAndAddRow(requestTable,
                    mResources.getString(R.string.payload_label),
                    Formatter.getPayloadOutput(req), req);
            createAndAddRow(requestTable,
                    mResources.getString(R.string.frequency_label),
                    Formatter.getFrequencyOutput(req), req);
            createAndAddRow(requestTable,
                    mResources.getString(R.string.name_label),
                    Formatter.getNameOutput(req), req);
            createAndAddButtonsRow(requestTable, reqMessage);
        } else if (MessageAnalyzer.isCommand(reqMessage)) {
            Command command = (Command) reqMessage;
            createAndAddHeaderRow(requestTable,
                    mResources.getString(R.string.alert_command_header));
            createAndAddRow(requestTable,
                    mResources.getString(R.string.command_label),
                    Formatter.getCommandOutput(command), command);
            createAndAddButtonsRow(requestTable, reqMessage);
        } else if (reqMessage == null) {
            String message;
            if (mContext.isDisplayingCommands()) {
                message = "No Command Found";
            } else {
                message = "No Request Found";
            }
            createAndAddHeaderRow(requestTable, message);
        }
    }

    /**
     * Adds a row with the favorites button to the <code>parent</code>
     * 
     * @param parent
     * @param req
     */
    private void createAndAddButtonsRow(LinearLayout parent,
            final VehicleMessage req) {
        LinearLayout row = (LinearLayout) mContext.getLayoutInflater().inflate(
                R.layout.diagdetailsalertbuttonrow, null);
        final Button addToFavoritesButton = (Button) row
                .findViewById(R.id.addToFavoritesButton);

        configureFavoritesButton(addToFavoritesButton, req);

        addToFavoritesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String message;

                if (MessageAnalyzer.isDiagnosticRequest(req)) {
                    message = "Request";
                } else {
                    message = "Command";
                }

                if (!FavoritesManager.isInFavorites(req)) {
                    FavoritesManager.add(req);
                    message = message + " Added to Favorites.";
                } else {
                    FavoritesManager.remove(req);
                    message = message + " Removed from Favorites.";
                }
                Toaster.showToast(mContext, message);
                configureFavoritesButton(addToFavoritesButton, req);
            }
        });
        parent.addView(row);
    }

    /**
     * Sets the color and text of the favorites button depending on if the
     * request is already in favorites or not
     * 
     * @param button
     * @param req
     */
    private void configureFavoritesButton(Button button, VehicleMessage req) {
        String text;
        int backgroundSelector;
        if (!FavoritesManager.isInFavorites(req)) {
            backgroundSelector = R.drawable.favorites_button_selector;
            text = mResources.getString(R.string.add_to_favorites_button_label);
        } else {
            backgroundSelector = R.drawable.delete_button_selector;
            text = mResources
                    .getString(R.string.remove_from_favorites_button_label);
        }
        button.setBackground(mResources.getDrawable(backgroundSelector));
        button.setText(text);
    }

    private void fillResponseTable(VehicleMessage respMessage) {

        LinearLayout responseTable = (LinearLayout) mAlertLayout
                .findViewById(R.id.diagAlertResponseTable);
        responseTable.removeAllViews();
        createAndAddHeaderRow(responseTable,
                mResources.getString(R.string.alert_response_header));
        if (MessageAnalyzer.isDiagnosticResponse(respMessage)) {
            DiagnosticResponse resp = (DiagnosticResponse) respMessage;
            createAndAddRow(responseTable,
                    mResources.getString(R.string.bus_label),
                    Formatter.getBusOutput(resp), resp);
            createAndAddRow(responseTable,
                    mResources.getString(R.string.id_label),
                    Formatter.getIdOutput(resp), resp);
            createAndAddRow(responseTable,
                    mResources.getString(R.string.mode_label),
                    Formatter.getModeOutput(resp), resp);
            createAndAddRow(responseTable,
                    mResources.getString(R.string.pid_label),
                    Formatter.getPidOutput(resp), resp);
            boolean responseSuccess = resp.isSuccessful();
            createAndAddRow(responseTable,
                    mResources.getString(R.string.success_label),
                    Formatter.getSuccessOutput(resp), resp);
            if (responseSuccess) {
                fillTableWithSuccessDetails(responseTable, mContext, resp);
            } else {
                fillTableWithFailureDetails(responseTable, mContext, resp);
            }
        } else if (MessageAnalyzer.isCommandResponse(respMessage)) {
            CommandResponse resp = (CommandResponse) respMessage;
            createAndAddRow(responseTable,
                    mResources.getString(R.string.command_response_label),
                    Formatter.getMessageOutput(resp), resp);
        }
    }

    private void fillTableWithSuccessDetails(LinearLayout responseTable,
            Activity context, DiagnosticResponse resp) {

        createAndAddRow(responseTable,
                mResources.getString(R.string.payload_label),
                Formatter.getPayloadOutput(resp), resp);
        createAndAddRow(responseTable,
                mResources.getString(R.string.value_label),
                Formatter.getValueOutput(resp), resp);
    }

    private void fillTableWithFailureDetails(LinearLayout responseTable,
            Activity context, DiagnosticResponse resp) {
        createAndAddRow(responseTable,
                mResources.getString(R.string.negative_response_code_label),
                Formatter.getResponseCodeOutput(resp), resp);
    }

    /**
     * Adds a row to the given <code>parent</code> with the given
     * <code>label</code> and <code>value</code>.
     * 
     * @param parent
     *            The table to add the row to
     * @param label
     *            The label for the row, displayed on the left
     * @param value
     *            The value of the row, displayed on the right
     * @param msg
     *            The message from which the value comes.
     */
    private void createAndAddRow(LinearLayout parent, String label,
            String value, VehicleMessage msg) {

        LinearLayout row = (LinearLayout) mContext.getLayoutInflater().inflate(
                R.layout.diagdetailsalertrow, null);
        ((TextView) row.findViewById(R.id.alertRowLabel)).setText(label);
        TextView valueText = (TextView) row.findViewById(R.id.alertRowValue);
        valueText.setText(value);
        if (MessageAnalyzer.isDiagnosticResponse(msg)) {
            valueText.setTextColor(Formatter.getOutputColor(mContext,
                    (DiagnosticResponse) msg));
        }
        parent.addView(row);
    }

    /**
     * Adds a header row to the given <code>parent</code> with the given
     * <code>header</code>
     * 
     * @param parent
     * @param header
     */
    private void createAndAddHeaderRow(LinearLayout parent, String header) {
        LinearLayout headerRow = (LinearLayout) mContext.getLayoutInflater()
                .inflate(R.layout.diagdetailsalertheaderrow, null);
        ((TextView) headerRow.findViewById(R.id.alertHeaderLabel))
                .setText(header);
        parent.addView(headerRow);
    }

}
