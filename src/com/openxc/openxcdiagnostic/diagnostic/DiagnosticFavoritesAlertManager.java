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

        LinearLayout favoritesLayout = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagfavoritesalert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.favorites_alert_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setView(favoritesLayout);
        mAlert = builder.create();
        fillLayout(favoritesLayout);
        mAlert.show();
    }
    
    private void fillLayout(LinearLayout favoritesLayout) {
        for (DiagnosticRequest req : DiagnosticFavoritesManager.getFavorites()) {
            createAndAddRow(favoritesLayout, req);
        }
    }
    
    private void createAndAddRow(final LinearLayout favoritesLayout, final DiagnosticRequest req) {
        
        final LinearLayout row = (LinearLayout) mContext.getLayoutInflater().inflate(R.layout.diagfavoritestablerow, null);
        ((TextView) row.findViewById(R.id.favoritesRowLabel)).setText(req.getName() == null ? "PLACEHOLDER" : req.getName());
        
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
                DiagnosticFavoritesManager.removeFavoriteRequest(req);
                favoritesLayout.removeView(row);
            }
        });

        favoritesLayout.addView(row);
    }
    
}
