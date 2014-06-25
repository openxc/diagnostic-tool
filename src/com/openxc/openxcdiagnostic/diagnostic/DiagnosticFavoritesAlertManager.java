package com.openxc.openxcdiagnostic.diagnostic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
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
        LinearLayout favoritesTable = (LinearLayout) favoritesLayout.findViewById(R.id.favoritesAlertTable);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.favorites_alert_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        favoritesLayout.setMinimumHeight((int)(0.9f*Utilities.getScreenHeight(mContext)));
        builder.setView(favoritesLayout);
        mAlert = builder.create();
        fillLayout(favoritesTable);
        mAlert.show();
    }
    
    private void fillLayout(LinearLayout favoritesTable) {
        for (DiagnosticRequest req : DiagnosticFavoritesManager.getFavorites()) {
            createAndAddRow(favoritesTable, req);
        }
    }
    
    private void createAndAddRow(final LinearLayout favoritesLayout, final DiagnosticRequest req) {
        
        final LinearLayout row = (LinearLayout) mContext.getLayoutInflater()
                .inflate(R.layout.diagfavoritesalertrow, null);
                
        ((TextView) row.findViewById(R.id.favoritesNameLabel))
            .setText(Utilities.getNameOutput(req));
        
        ((TextView) row.findViewById(R.id.favoritesBusLabel))
            .setText(Utilities.getBusOutput(req));
        
        ((TextView) row.findViewById(R.id.favoritesIdLabel))
            .setText(Utilities.getIdOutput(req));
        
        ((TextView) row.findViewById(R.id.favoritesModeLabel))
            .setText(Utilities.getModeOutput(req));
        
        ((TextView) row.findViewById(R.id.favoritesPidLabel))
            .setText(Utilities.getPidOutput(req));
        
        ((TextView) row.findViewById(R.id.favoritesPayloadLabel))
            .setText(Utilities.getPayloadOutput(req));
        
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
                    public void onClick(DialogInterface dialog, int id) {
                        DiagnosticFavoritesManager.removeFavoriteRequest(req);
                        favoritesLayout.removeView(row);
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
            }
        });

        favoritesLayout.addView(row);
    }
    
}
