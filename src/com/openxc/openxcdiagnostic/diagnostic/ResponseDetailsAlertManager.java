package com.openxc.openxcdiagnostic.diagnostic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private ResponseDetailsAlertManager() { }
    
    public static void show(Activity context, VehicleMessage req, VehicleMessage resp) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout alertLayout = (LinearLayout) context.getLayoutInflater().inflate(R.layout.diagdetailsalert, null);
                
        fillRequestTable(alertLayout, context, req);
        fillResponseTable(alertLayout, context, resp);
        
        builder.setView(alertLayout);
        builder.setTitle(context.getResources().getString(R.string.details_button_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }
    
    private static void fillRequestTable(LinearLayout alertLayout, Activity context, VehicleMessage reqMessage) {
        
        LinearLayout requestTable = (LinearLayout) alertLayout.findViewById(R.id.diagAlertRequestTable);
        
        if (reqMessage instanceof DiagnosticRequest) {
            DiagnosticRequest req = (DiagnosticRequest) reqMessage;
            createAndAddHeaderRow(context, requestTable, context.getResources().getString(R.string.alert_request_header));
            createAndAddRow(context, requestTable, "bus", Formatter.getBusOutput(req), req);
            createAndAddRow(context, requestTable, "id", Formatter.getIdOutput(req), req);
            createAndAddRow(context, requestTable, "mode", Formatter.getModeOutput(req), req);
            createAndAddRow(context, requestTable, "pid", Formatter.getPidOutput(req), req);
            createAndAddRow(context, requestTable, "payload", Formatter.getPayloadOutput(req), req);
            createAndAddRow(context, requestTable, "frequency", Formatter.getFrequencyOutput(req), req);
            createAndAddRow(context, requestTable, "name", Formatter.getNameOutput(req), req);
        } else if (reqMessage instanceof Command) {
            Command command = (Command) reqMessage;
            createAndAddHeaderRow(context, requestTable, context.getResources().getString(R.string.alert_command_header));
            createAndAddRow(context, requestTable, "command", Formatter.getCommandOutput(command), command);
        }
        createAndAddButtonsRow(context, requestTable, reqMessage);
    }
    
    private static void createAndAddButtonsRow(final Activity context, LinearLayout parent,
            final VehicleMessage req) {
        LinearLayout row = (LinearLayout) context.getLayoutInflater()
                .inflate(R.layout.diagdetailsalertbuttonrow, null);
        final Button addToFavoritesButton =  (Button) row.findViewById(R.id.addToFavoritesButton);
        
        configureFavoritesButton(context, addToFavoritesButton, req);
        
        addToFavoritesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String message;
                
                if (req instanceof DiagnosticRequest) {
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
                Toaster.showToast(context, message);
                configureFavoritesButton(context, addToFavoritesButton, req);
            }
        });
        parent.addView(row);
    }
       
    private static void configureFavoritesButton(Activity context, Button button, 
            VehicleMessage req) {
        String text;
        int backgroundSelector;
        if (!FavoritesManager.contains(req)) {
            backgroundSelector = R.drawable.favorites_button_selector;
            text = context.getResources().getString(R.string.add_to_favorites_button_label);
        } else {
            backgroundSelector = R.drawable.delete_button_selector;
            text = context.getResources().getString(R.string.remove_from_favorites_button_label);
        }
        button.setBackground(context.getResources()
                .getDrawable(backgroundSelector));
        button.setText(text);
    }
    
    private static void fillResponseTable(LinearLayout alertLayout, Activity context, VehicleMessage respMessage) {
        
        LinearLayout responseTable = (LinearLayout) alertLayout.findViewById(R.id.diagAlertResponseTable); 
        createAndAddHeaderRow(context, responseTable, context.getResources().getString(R.string.alert_response_header));
        if (respMessage instanceof DiagnosticResponse) {
            DiagnosticResponse resp = (DiagnosticResponse) respMessage;
            createAndAddRow(context, responseTable, "bus", Formatter.getBusOutput(resp), resp);
            createAndAddRow(context, responseTable, "id", Formatter.getIdOutput(resp), resp);
            createAndAddRow(context, responseTable, "mode", Formatter.getModeOutput(resp), resp);
            createAndAddRow(context, responseTable, "pid", Formatter.getPidOutput(resp), resp);
            boolean responseSuccess = resp.isSuccessful();
            createAndAddRow(context, responseTable, "success", Formatter.getSuccessOutput(resp), resp);
            if (responseSuccess) {
                fillTableWithSuccessDetails(responseTable, context, resp);
            } else {
                fillTableWithFailureDetails(responseTable, context, resp);
            }
        } else if (respMessage instanceof CommandResponse) {
            CommandResponse resp = (CommandResponse) respMessage;
            createAndAddRow(context, responseTable, "message", Formatter.getMessageOutput(resp), resp);
        }
    }
    
    private static void fillTableWithSuccessDetails(LinearLayout responseTable, Activity context, DiagnosticResponse resp) {
        
        createAndAddRow(context, responseTable, "payload", Formatter.getPayloadOutput(resp), resp);
        createAndAddRow(context, responseTable, "value", Formatter.getValueOutput(resp), resp);
    }
    
    private static void fillTableWithFailureDetails(LinearLayout responseTable, Activity context, DiagnosticResponse resp) {
        createAndAddRow(context, responseTable, "code", Formatter.getResponseCodeOutput(resp), 
                resp);
    }

    private static void createAndAddRow(Activity context, LinearLayout parent, String label, 
            String value, VehicleMessage msg) {
    
        LinearLayout row = (LinearLayout) context.getLayoutInflater().inflate(R.layout.diagdetailsalertrow, null);
        ((TextView) row.findViewById(R.id.alertRowLabel)).setText(label);
        TextView valueText = (TextView) row.findViewById(R.id.alertRowValue);
        valueText.setText(value);
        if (msg instanceof DiagnosticResponse) {
            valueText.setTextColor(Formatter.getOutputColor(context, 
                    (DiagnosticResponse) msg));
        } 
        parent.addView(row);
    }
    
    private static void createAndAddHeaderRow(Activity context, LinearLayout parent, String header) {
        LinearLayout headerRow = (LinearLayout) context.getLayoutInflater().inflate(R.layout.diagdetailsalertheaderrow, null);
        ((TextView) headerRow.findViewById(R.id.alertHeaderLabel)).setText(header);
        parent.addView(headerRow);
    }
    
}
