package com.openxc.openxcdiagnostic.diagnostic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

        ScrollView favoritesLayoutScroll = (ScrollView) mContext.getLayoutInflater().inflate(R.layout.diagfavoritesalert, null);
        LinearLayout favoritesLayout = (LinearLayout) favoritesLayoutScroll.findViewById(R.id.favoritesAlertTable);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.favorites_alert_label));
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setView(favoritesLayoutScroll);
        mAlert = builder.create();
        fillLayout(favoritesLayout);
        mAlert.show();
    }
    
    private void fillLayout(LinearLayout favoritesLayout) {
        for (DiagnosticRequest req : DiagnosticFavoritesManager.getFavorites()) {
            createAndAddRow(favoritesLayout, req);
        }
    }
    
    private String insertInBold(String text) {
        return "<b>" + text + "</b>";
    }
    
    private String insertInItalic(String text) {
        return "<i>" + text + "</i>";
    }
    
    private void createAndAddRow(final LinearLayout favoritesLayout, final DiagnosticRequest req) {
        
        final LinearLayout row = (LinearLayout) mContext.getLayoutInflater()
                .inflate(R.layout.diagfavoritesalertrow, null);
                
        String nameText = insertInBold("name") + "<br>" + insertInItalic(Utilities.getNameOutput(req));
        ((TextView) row.findViewById(R.id.favoritesNameLabel))
            .setText(Html.fromHtml(nameText));
        
        String busText = insertInBold("bus") + "<br>" + insertInItalic(Utilities.getBusOutput(req));
        ((TextView) row.findViewById(R.id.favoritesBusLabel))
            .setText(Html.fromHtml(busText));
        
        String idText = insertInBold("id") + "<br>" + insertInItalic(Utilities.getIdOutput(req));
        ((TextView) row.findViewById(R.id.favoritesIdLabel))
            .setText(Html.fromHtml(idText));
        
        String modeText = insertInBold("mode") + "<br>" + insertInItalic(Utilities.getModeOutput(req));
        ((TextView) row.findViewById(R.id.favoritesModeLabel))
            .setText(Html.fromHtml(modeText));
        
        String pidText = insertInBold("pid") + "<br>" + insertInItalic(Utilities.getPidOutput(req));
        ((TextView) row.findViewById(R.id.favoritesPidLabel))
            .setText(Html.fromHtml(pidText));
        
        String payloadText = insertInBold("payload") + "<br>" + insertInItalic(Utilities.getPayloadOutput(req));
        ((TextView) row.findViewById(R.id.favoritesPayloadLabel))
            .setText(Html.fromHtml(payloadText));
        
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
