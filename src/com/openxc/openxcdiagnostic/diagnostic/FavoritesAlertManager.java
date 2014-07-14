package com.openxc.openxcdiagnostic.diagnostic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openxc.messages.Command;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.util.Formatter;
import com.openxc.openxcdiagnostic.util.Utilities;

/**
 * 
 * Manager for storing favorite requests. 
 * 
 */
public class FavoritesAlertManager implements DiagnosticManager {

    private AlertDialog mAlert;
    private DiagnosticActivity mContext;
    private boolean mDisplayCommands;
    
    public FavoritesAlertManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        setRequestCommandState(displayCommands);
    }
    
    @Override
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
    }
        
    public void showAlert() {

        LinearLayout favoritesLayout = (LinearLayout) mContext.getLayoutInflater()
                .inflate(R.layout.diagfavoritesalert, null);
        favoritesLayout.setMinimumHeight((int)(Utilities.getScreenHeight(mContext)));
        setHeader(favoritesLayout);
        fillTable(favoritesLayout);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.favorites_alert_label));
        builder.setPositiveButton("Done", null);
        builder.setView(favoritesLayout);
        
        mAlert = builder.create();
        mAlert.show();
    }
    
    private void setHeader(LinearLayout favoritesLayout) {
        int alertHeaderId = R.id.favoritesAlertHeader;
        LinearLayout oldView = (LinearLayout) favoritesLayout.findViewById(alertHeaderId);
        LinearLayout newView;
        if (mDisplayCommands) {
            newView = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagfavoritesalertcommandheaderrow, null);
        } else {
            newView = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagfavoritesalertrequestheaderrow, null);
        }
        newView.setId(alertHeaderId);
        Utilities.replaceView(favoritesLayout, oldView, newView);
    }
    
    private void fillTable(LinearLayout favoritesLayout) {
        
        ArrayList<? extends VehicleMessage> favs;
        if (mDisplayCommands)
            favs = FavoritesManager.getFavoriteCommands();
        else {
            favs = FavoritesManager.getFavoriteRequests();
        }
        
        for (VehicleMessage req : favs) {
            createAndAddRowToTable(favoritesLayout, req);
        }
    }
    
    private void setRowDataFormat(LinearLayout row) {
        int alertRowDataId = R.id.diagFavoritesAlertRowData;
        LinearLayout oldView = (LinearLayout) row.findViewById(alertRowDataId);
        int layoutId;
        if (mDisplayCommands) {
            layoutId = R.layout.diagfavoritesalertcommanddata;
        } else {
            layoutId = R.layout.diagfavoritesalertrequestdata;
        }
        LinearLayout newView = (LinearLayout) mContext.getLayoutInflater().inflate(layoutId, null);
        newView.setId(alertRowDataId);
        Utilities.replaceView(row, oldView, newView);
    }
    
    private void createAndAddRowToTable(final LinearLayout favoritesLayout, final VehicleMessage reqMessage) {
        
        Map<TextView, TextView> headerMatcher = new HashMap<>();
        final LinearLayout favoritesTable = (LinearLayout) favoritesLayout.findViewById(R.id.favoritesAlertTable);
        final LinearLayout row = (LinearLayout) mContext.getLayoutInflater()
                .inflate(R.layout.diagfavoritesalertrow, null);
        
        setRowDataFormat(row);
        
        if (Utilities.isDiagnosticRequest(reqMessage)) {
             
            DiagnosticRequest req = (DiagnosticRequest) reqMessage;
            
            final TextView nameData = (TextView) row.findViewById(R.id.favoritesNameData);
            TextView nameHeader = (TextView) favoritesLayout.findViewById(R.id.nameHeader);
            headerMatcher.put(nameHeader, nameData);
            nameData.setText(Formatter.getNameOutput(req));
            
            final TextView busData = (TextView) row.findViewById(R.id.favoritesBusData);
            TextView busHeader = (TextView) favoritesLayout.findViewById(R.id.busHeader);
            headerMatcher.put(busHeader, busData);
            busData.setText(Formatter.getBusOutput(req));
            
            final TextView idData = (TextView) row.findViewById(R.id.favoritesIdData);
            TextView idHeader = (TextView) favoritesLayout.findViewById(R.id.idHeader);
            headerMatcher.put(idHeader, idData);
            idData.setText(Formatter.getIdOutput(req));
            
            final TextView modeData = (TextView) row.findViewById(R.id.favoritesModeData);
            TextView modeHeader = (TextView) favoritesLayout.findViewById(R.id.modeHeader);
            headerMatcher.put(modeHeader, modeData);
            modeData.setText(Formatter.getModeOutput(req));
            
            final TextView pidData = (TextView) row.findViewById(R.id.favoritesPidData);
            TextView pidHeader = (TextView) favoritesLayout.findViewById(R.id.pidHeader);
            headerMatcher.put(pidHeader, pidData);
            pidData.setText(Formatter.getPidOutput(req));
            
            final TextView payloadData = (TextView) row.findViewById(R.id.favoritesPayloadData);
            TextView payloadHeader = (TextView) favoritesLayout.findViewById(R.id.payloadHeader);
            headerMatcher.put(payloadHeader, payloadData);
            payloadData.setText(Formatter.getPayloadOutput(req));   
        } else {
                        
            final TextView commandData = (TextView) row.findViewById(R.id.favoritesCommandData);
            TextView commandHeader = (TextView) favoritesLayout.findViewById(R.id.commandHeader);
            headerMatcher.put(commandHeader, commandData);
            commandData.setText(Formatter.getCommandOutput((Command) reqMessage));
        }
        
        //the columns of the table don't line up if you let android do it "on layout",
        //so this lets android create the layout, then adjust according to the properties
        //in the xml, then the rest of the rows' columns will adjust to match
        Iterator<Map.Entry<TextView, TextView>> it = headerMatcher.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<TextView, TextView> pair = it.next();
            pair.getKey().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right,
                        int bottom, int oldLeft, int oldTop, int oldRight,
                        int oldBottom) {
                        LayoutParams param = pair.getKey().getLayoutParams();
                        param.width = right - left;
                        pair.getValue().setLayoutParams(param);
                }
            }); 
        }
        
        Button selectButton =  (Button) row.findViewById(R.id.favoritesRowSelectButton);
        selectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlert.cancel();
                mContext.populateFields(reqMessage);
            }
        });
        
        Button deleteButton =  (Button) row.findViewById(R.id.favoritesRowDeleteButton);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                String message;
                String title;
                if (mDisplayCommands) {
                    title = "Delete Command?";
                    message = mContext.getResources().getString(R.string.delete_favorite_command_verification);
                } else {
                    title = "Delete Request?";
                    message = mContext.getResources().getString(R.string.delete_favorite_request_verification);
                }
                builder.setMessage(message);
                builder.setTitle(title);
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FavoritesManager.remove(reqMessage);
                        favoritesTable.removeView(row);
                    }
                });
                builder.setPositiveButton("Cancel", null);
                builder.create().show();
            }
        });

        favoritesTable.addView(row);
    }
   
}
