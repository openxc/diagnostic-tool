package com.openxc.openxcdiagnostic.dump;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.openxc.openxcdiagnostic.R;

public class SettingsManager {

    private static String TAG = "Dump Settings Manager";
    private SharedPreferences mPreferences;
    private DumpActivity mContext;
    private EditText mMessagesInput;

    public SettingsManager(DumpActivity context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * Display the settings alert.
     */
    public void showAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LinearLayout settingsLayout = (LinearLayout) mContext
                .getLayoutInflater().inflate(R.layout.dumpsettingsalert, null);

        builder.setView(settingsLayout);
        builder.setTitle(mContext.getResources().getString(
                R.string.dump_settings_alert_label));
        builder.setPositiveButton("Done", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int maxMessageCount = Integer.valueOf(mMessagesInput
                            .getText().toString());
                    save(maxMessageCount);
                    mContext.limitMessageCount(maxMessageCount);
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Invalid number entered");
                }
            }
        });
        builder.create().show();

        mMessagesInput = (EditText) settingsLayout
                .findViewById(R.id.numMessagesInput);
        mMessagesInput.setText(String.valueOf(getNumMessages()));
    }

    /**
     * 
     * @return The number of messages to keep in the queue specified by the
     *         user. Defaults to 999.
     */
    public int getNumMessages() {
        return mPreferences.getInt(getNumMessagesKey(), 999);
    }

    private void save(int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(getNumMessagesKey(), value);
        editor.commit();
    }

    private static String getNumMessagesKey() {
        return "num_messages_key";
    }
}
