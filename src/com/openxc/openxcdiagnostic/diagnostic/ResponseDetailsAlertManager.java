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

    private ResponseDetailsAlertManager() { }
    
    private static DiagnosticActivity sContext;
    private static LinearLayout alertLayout; 
    private static VehicleMessage sRequest;
    private static boolean isShowing = false;
    private static Handler sHandler = new Handler();
    
    public static void show(DiagnosticActivity context, VehicleMessage req, VehicleMessage resp) {
        
        isShowing = true;
        sContext = context;
        sRequest = req;
        alertLayout = (LinearLayout) context.getLayoutInflater().inflate(R.layout.diagdetailsalert, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                
        fill(req, resp);
        
        builder.setView(alertLayout);
        builder.setTitle(context.getResources().getString(R.string.details_button_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                isShowing = false;
            }
        });
        builder.create().show();
    }
    
    public static boolean isShowing(VehicleMessage request) {
        return isShowing && request.equals(sRequest);
    }
    
    public static void refresh(final VehicleMessage req, final VehicleMessage resp) {
        sHandler.post(new Runnable() {
            public void run() {
                fill(req, resp);
            }
        });
    }
    
    private static void fill(VehicleMessage req, VehicleMessage resp) {
        fillRequestTable(req);
        fillResponseTable(resp);
    }
    
    private static void fillRequestTable(VehicleMessage reqMessage) {
        
        LinearLayout requestTable = (LinearLayout) alertLayout.findViewById(R.id.diagAlertRequestTable);
        requestTable.removeAllViews();
        if (Utilities.isDiagnosticRequest(reqMessage)) {
            DiagnosticRequest req = (DiagnosticRequest) reqMessage;
            createAndAddHeaderRow(requestTable, sContext.getResources().getString(R.string.alert_request_header));
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
            createAndAddHeaderRow(requestTable, sContext.getResources().getString(R.string.alert_command_header));
            createAndAddRow(requestTable, "command", Formatter.getCommandOutput(command), command);
            createAndAddButtonsRow(requestTable, reqMessage);
        } else if (reqMessage == null) {
            String message;
            if (sContext.isDisplayingCommands()) {
                message = "No Command Found";
            } else {
                message = "No Request Found";
            }
            createAndAddHeaderRow(requestTable, message);
        }
    }
    
    private static void createAndAddButtonsRow(LinearLayout parent,
            final VehicleMessage req) {
        LinearLayout row = (LinearLayout) sContext.getLayoutInflater()
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
                Toaster.showToast(sContext, message);
                configureFavoritesButton(addToFavoritesButton, req);
            }
        });
        parent.addView(row);
    }
       
    private static void configureFavoritesButton(Button button, VehicleMessage req) {
        String text;
        int backgroundSelector;
        if (!FavoritesManager.contains(req)) {
            backgroundSelector = R.drawable.favorites_button_selector;
            text = sContext.getResources().getString(R.string.add_to_favorites_button_label);
        } else {
            backgroundSelector = R.drawable.delete_button_selector;
            text = sContext.getResources().getString(R.string.remove_from_favorites_button_label);
        }
        button.setBackground(sContext.getResources()
                .getDrawable(backgroundSelector));
        button.setText(text);
    }
    
    private static void fillResponseTable(VehicleMessage respMessage) {
        
        LinearLayout responseTable = (LinearLayout) alertLayout.findViewById(R.id.diagAlertResponseTable); 
        responseTable.removeAllViews();
        createAndAddHeaderRow(responseTable, sContext.getResources().getString(R.string.alert_response_header));
        if (Utilities.isDiagnosticResponse(respMessage)) {
            DiagnosticResponse resp = (DiagnosticResponse) respMessage;
            createAndAddRow(responseTable, "bus", Formatter.getBusOutput(resp), resp);
            createAndAddRow(responseTable, "id", Formatter.getIdOutput(resp), resp);
            createAndAddRow(responseTable, "mode", Formatter.getModeOutput(resp), resp);
            createAndAddRow(responseTable, "pid", Formatter.getPidOutput(resp), resp);
            boolean responseSuccess = resp.isSuccessful();
            createAndAddRow(responseTable, "success", Formatter.getSuccessOutput(resp), resp);
            if (responseSuccess) {
                fillTableWithSuccessDetails(responseTable, sContext, resp);
            } else {
                fillTableWithFailureDetails(responseTable, sContext, resp);
            }
        } else if (Utilities.isCommandResponse(respMessage)) {
            CommandResponse resp = (CommandResponse) respMessage;
            createAndAddRow(responseTable, "message", Formatter.getMessageOutput(resp), resp);
        }
    }
    
    private static void fillTableWithSuccessDetails(LinearLayout responseTable, Activity context, DiagnosticResponse resp) {
        
        createAndAddRow(responseTable, "payload", Formatter.getPayloadOutput(resp), resp);
        createAndAddRow(responseTable, "value", Formatter.getValueOutput(resp), resp);
    }
    
    private static void fillTableWithFailureDetails(LinearLayout responseTable, Activity context, DiagnosticResponse resp) {
        createAndAddRow(responseTable, "code", Formatter.getResponseCodeOutput(resp), 
                resp);
    }

    private static void createAndAddRow(LinearLayout parent, String label, 
            String value, VehicleMessage msg) {
    
        LinearLayout row = (LinearLayout) sContext.getLayoutInflater().inflate(R.layout.diagdetailsalertrow, null);
        ((TextView) row.findViewById(R.id.alertRowLabel)).setText(label);
        TextView valueText = (TextView) row.findViewById(R.id.alertRowValue);
        valueText.setText(value);
        if (Utilities.isDiagnosticResponse(msg)) {
            valueText.setTextColor(Formatter.getOutputColor(sContext, 
                    (DiagnosticResponse) msg));
        } 
        parent.addView(row);
    }
    
    private static void createAndAddHeaderRow(LinearLayout parent, String header) {
        LinearLayout headerRow = (LinearLayout) sContext.getLayoutInflater().inflate(R.layout.diagdetailsalertheaderrow, null);
        ((TextView) headerRow.findViewById(R.id.alertHeaderLabel)).setText(header);
        parent.addView(headerRow);
    }
    
}
