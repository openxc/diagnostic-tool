package com.openxc.openxcdiagnostic.diagnostic;

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

import com.openxc.messages.DiagnosticRequest;
import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.util.Utilities;

/**
 * 
 * Manager for storing favorite requests. 
 * 
 */
public class DiagnosticFavoritesAlertManager {

    private AlertDialog mAlert;
    private DiagnosticActivity mContext;
    
    public DiagnosticFavoritesAlertManager(DiagnosticActivity context) {
        mContext = context;
    }
        
    public void showAlert() {

        LinearLayout favoritesLayout = (LinearLayout) mContext.getLayoutInflater()
                .inflate(R.layout.diagfavoritesalert, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.favorites_alert_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        favoritesLayout.setMinimumHeight((int)(0.9f*Utilities.getScreenHeight(mContext)));
        builder.setView(favoritesLayout);
        fillTable(favoritesLayout);
        mAlert = builder.create();
        mAlert.show();
    }
    
    private void fillTable(LinearLayout favoritesLayout) {
        for (DiagnosticRequest req : DiagnosticFavoritesManager.getFavorites()) {
            createAndAddRowToTable(favoritesLayout, req);
        }
    }
    
    private void createAndAddRowToTable(final LinearLayout favoritesLayout, final DiagnosticRequest req) {
        
        final LinearLayout favoritesTable = (LinearLayout) favoritesLayout.findViewById(R.id.favoritesAlertTable);
        final LinearLayout row = (LinearLayout) mContext.getLayoutInflater()
                .inflate(R.layout.diagfavoritesalertrow, null);
        
        Map<TextView, TextView> headerMatcher = new HashMap<>();
                
        final TextView nameData = (TextView) row.findViewById(R.id.favoritesNameData);
        TextView nameHeader = (TextView) favoritesLayout.findViewById(R.id.nameHeader);
        headerMatcher.put(nameHeader, nameData);
        nameData.setText(Utilities.getNameOutput(req));
        
        final TextView busData = (TextView) row.findViewById(R.id.favoritesBusData);
        TextView busHeader = (TextView) favoritesLayout.findViewById(R.id.busHeader);
        headerMatcher.put(busHeader, busData);
        busData.setText(Utilities.getBusOutput(req));
        
        final TextView idData = (TextView) row.findViewById(R.id.favoritesIdData);
        TextView idHeader = (TextView) favoritesLayout.findViewById(R.id.idHeader);
        headerMatcher.put(idHeader, idData);
        idData.setText(Utilities.getIdOutput(req));
        
        final TextView modeData = (TextView) row.findViewById(R.id.favoritesModeData);
        TextView modeHeader = (TextView) favoritesLayout.findViewById(R.id.modeHeader);
        headerMatcher.put(modeHeader, modeData);
        modeData.setText(Utilities.getModeOutput(req));
        
        final TextView pidData = (TextView) row.findViewById(R.id.favoritesPidData);
        TextView pidHeader = (TextView) favoritesLayout.findViewById(R.id.pidHeader);
        headerMatcher.put(pidHeader, pidData);
        pidData.setText(Utilities.getPidOutput(req));
        
        final TextView payloadData = (TextView) row.findViewById(R.id.favoritesPayloadData);
        TextView payloadHeader = (TextView) favoritesLayout.findViewById(R.id.payloadHeader);
        headerMatcher.put(payloadHeader, payloadData);
        payloadData.setText(Utilities.getPayloadOutput(req));
        
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
                mContext.populateFields(req);
            }
        });
        
        Button deleteButton =  (Button) row.findViewById(R.id.favoritesRowDeleteButton);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getResources().getString(R.string.delete_favorite_verification));
                builder.setTitle("Delete Request?");
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DiagnosticFavoritesManager.removeFavoriteRequest(req);
                        favoritesTable.removeView(row);
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
            }
        });

        favoritesTable.addView(row);
    }
    
}
