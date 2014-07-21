package com.openxc.openxcdiagnostic.diagnostic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.openxc.openxcdiagnostic.util.Toaster;
import com.openxc.openxcdiagnostic.util.Utilities;

public class ResponseDetailsAlertManager {
    
    private DiagnosticActivity mContext;
    private LinearLayout alertLayout; 
    private VehicleMessage mRequest;
    private VehicleMessage mResponse;
    private boolean isShowing = false;
    private Handler mHandler = new Handler();
    
    public ResponseDetailsAlertManager(DiagnosticActivity context, VehicleMessage req, VehicleMessage resp) {
        mRequest = req;
        mResponse = resp;
        mContext = context;
    }
    
    public void show() {
        
        isShowing = true;

        alertLayout = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagdetailsalert, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);       
        fill(mRequest, mResponse);
        
        builder.setView(alertLayout);
        builder.setTitle(mContext.getResources().getString(R.string.details_button_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                isShowing = false;
            }
        });
        builder.create().show();
    }
    
    public boolean isShowing() {
        return isShowing;
    }
    
    public void refresh(final VehicleMessage req, final VehicleMessage resp) {
        mRequest = req;
        mResponse = resp;
        mHandler.post(new Runnable() {
            public void run() {
                fill(req, resp);
            }
        });
    }
    
    private void fill(VehicleMessage req, VehicleMessage resp) {
        fillRequestTable(req);
        fillResponseTable(resp);
    }
    
    private void fillRequestTable(VehicleMessage reqMessage) {
        
        LinearLayout requestTable = (LinearLayout) alertLayout.findViewById(R.id.diagAlertRequestTable);
        requestTable.removeAllViews();
        if (Utilities.isDiagnosticRequest(reqMessage)) {
            DiagnosticRequest req = (DiagnosticRequest) reqMessage;
            createAndAddHeaderRow(requestTable, mContext.getResources().getString(R.string.alert_request_header));
            createAndAddRow(requestTable, "bus", Formatter.getBusOutput(req), req);
            createAndAddRow(requestTable, "id", Formatter.getIdOutput(req), req);
            createAndAddRow(requestTable, "mode", Formatter.getModeOutput(req), req);
            createAndAddRow(requestTable, "pid", Formatter.getPidOutput(req), req);
            createAndAddRow(requestTable, "payload", Formatter.getPayloadOutput(req), req);
            createAndAddRow(requestTable, "frequency", Formatter.getFrequencyOutput(req), req);
            createAndAddRow(requestTable, "name", Formatter.getNameOutput(req), req);
            createAndAddButtonsRow(requestTable, reqMessage);
        } else if (Utilities.isCommand(reqMessage)) {
            Command command = (Command) reqMessage;
            createAndAddHeaderRow(requestTable, mContext.getResources().getString(R.string.alert_command_header));
            createAndAddRow(requestTable, "command", Formatter.getCommandOutput(command), command);
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
    
    private void createAndAddButtonsRow(LinearLayout parent,
            final VehicleMessage req) {
        LinearLayout row = (LinearLayout) mContext.getLayoutInflater()
                .inflate(R.layout.diagdetailsalertbuttonrow, null);
        final Button addToFavoritesButton =  (Button) row.findViewById(R.id.addToFavoritesButton);
        
        configureFavoritesButton(addToFavoritesButton, req);
        
        addToFavoritesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String message;
                
                if (Utilities.isDiagnosticRequest(req)) {
                    message = "Request";
                } else {
                    message = "Command";
                }
                
                if (!FavoritesManager.contains(req)) {
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
       
    private void configureFavoritesButton(Button button, VehicleMessage req) {
        String text;
        int backgroundSelector;
        if (!FavoritesManager.contains(req)) {
            backgroundSelector = R.drawable.favorites_button_selector;
            text = mContext.getResources().getString(R.string.add_to_favorites_button_label);
        } else {
            backgroundSelector = R.drawable.delete_button_selector;
            text = mContext.getResources().getString(R.string.remove_from_favorites_button_label);
        }
        button.setBackground(mContext.getResources()
                .getDrawable(backgroundSelector));
        button.setText(text);
    }
    
    private void fillResponseTable(VehicleMessage respMessage) {
        
        LinearLayout responseTable = (LinearLayout) alertLayout.findViewById(R.id.diagAlertResponseTable); 
        responseTable.removeAllViews();
        createAndAddHeaderRow(responseTable, mContext.getResources().getString(R.string.alert_response_header));
        if (Utilities.isDiagnosticResponse(respMessage)) {
            DiagnosticResponse resp = (DiagnosticResponse) respMessage;
            createAndAddRow(responseTable, "bus", Formatter.getBusOutput(resp), resp);
            createAndAddRow(responseTable, "id", Formatter.getIdOutput(resp), resp);
            createAndAddRow(responseTable, "mode", Formatter.getModeOutput(resp), resp);
            createAndAddRow(responseTable, "pid", Formatter.getPidOutput(resp), resp);
            boolean responseSuccess = resp.isSuccessful();
            createAndAddRow(responseTable, "success", Formatter.getSuccessOutput(resp), resp);
            if (responseSuccess) {
                fillTableWithSuccessDetails(responseTable, mContext, resp);
            } else {
                fillTableWithFailureDetails(responseTable, mContext, resp);
            }
        } else if (Utilities.isCommandResponse(respMessage)) {
            CommandResponse resp = (CommandResponse) respMessage;
            createAndAddRow(responseTable, "message", Formatter.getMessageOutput(resp), resp);
        }
    }
    
    private void fillTableWithSuccessDetails(LinearLayout responseTable, Activity context, DiagnosticResponse resp) {
        
        createAndAddRow(responseTable, "payload", Formatter.getPayloadOutput(resp), resp);
        createAndAddRow(responseTable, "value", Formatter.getValueOutput(resp), resp);
    }
    
    private void fillTableWithFailureDetails(LinearLayout responseTable, Activity context, DiagnosticResponse resp) {
        createAndAddRow(responseTable, "code", Formatter.getResponseCodeOutput(resp), 
                resp);
    }

    private void createAndAddRow(LinearLayout parent, String label, 
            String value, VehicleMessage msg) {
    
        LinearLayout row = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagdetailsalertrow, null);
        ((TextView) row.findViewById(R.id.alertRowLabel)).setText(label);
        TextView valueText = (TextView) row.findViewById(R.id.alertRowValue);
        valueText.setText(value);
        if (Utilities.isDiagnosticResponse(msg)) {
            valueText.setTextColor(Formatter.getOutputColor(mContext, 
                    (DiagnosticResponse) msg));
        } 
        parent.addView(row);
    }
    
    private void createAndAddHeaderRow(LinearLayout parent, String header) {
        LinearLayout headerRow = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagdetailsalertheaderrow, null);
        ((TextView) headerRow.findViewById(R.id.alertHeaderLabel)).setText(header);
        parent.addView(headerRow);
    }
    
}
