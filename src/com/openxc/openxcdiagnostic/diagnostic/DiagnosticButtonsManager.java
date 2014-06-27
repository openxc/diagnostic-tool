package com.openxc.openxcdiagnostic.diagnostic;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.openxc.openxcdiagnostic.R;

public class DiagnosticButtonsManager {

    private DiagnosticActivity mContext;
    
    public DiagnosticButtonsManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        initButtons(displayCommands);
    }

    private void initButtons(boolean displayCommands) {

        Button sendRequestButton = (Button) mContext.findViewById(R.id.sendRequestButton);
        setRequestButtonText(displayCommands);
        sendRequestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.takeSendButtonPush();
            }
        });

        Button clearButton = (Button) mContext.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.takeClearButtonPush();
            }
        });

        Button favoritesButton = (Button) mContext.findViewById(R.id.favoritesButton);
        favoritesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.takeFavoritesButtonPush();
            }
        });

        Button settingsButton = (Button) mContext.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.takeSettingsButtonPush();
            }
        });
        
        if (displayCommands) {
            initCommandInfoButtons();
        } else {
            initRequestInfoButtons();
        }
    }
    
    private void initCommandInfoButtons() {
        final Resources res = mContext.getResources();
        Button commandInfoButton = (Button) mContext.findViewById(R.id.commandQuestionButton);
        commandInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder
                .setMessage(res.getString(R.string.command_info))
                .setTitle(res.getString(R.string.command_label));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
            }
        });
    }

    private void initRequestInfoButtons() {

        Resources res = mContext.getResources();
        final BiMap<String, String> infoMap = HashBiMap.create();
        final Map<Button, String> buttonInfo = new HashMap<>();

        Button mFrequencyInfoButton = (Button) mContext.findViewById(R.id.frequencyQuestionButton);
        buttonInfo.put(mFrequencyInfoButton, res.getString(R.string.frequency_info));
        infoMap.put(res.getString(R.string.frequency_label), res.getString(R.string.frequency_info));

        Button mBusInfoButton = (Button) mContext.findViewById(R.id.busQuestionButton);
        buttonInfo.put(mBusInfoButton, res.getString(R.string.bus_info));
        infoMap.put(res.getString(R.string.bus_label), res.getString(R.string.bus_info));

        Button mIdInfoButton = (Button) mContext.findViewById(R.id.idQuestionButton);
        buttonInfo.put(mIdInfoButton, res.getString(R.string.id_info));
        infoMap.put(res.getString(R.string.id_label), res.getString(R.string.id_info));

        Button mModeInfoButton = (Button) mContext.findViewById(R.id.modeQuestionButton);
        buttonInfo.put(mModeInfoButton, res.getString(R.string.mode_info));
        infoMap.put(res.getString(R.string.mode_label), res.getString(R.string.mode_info));

        Button mPidInfoButton = (Button) mContext.findViewById(R.id.pidQuestionButton);
        buttonInfo.put(mPidInfoButton, res.getString(R.string.pid_info));
        infoMap.put(res.getString(R.string.pid_label), res.getString(R.string.pid_info));

        Button mPayloadInfoButton = (Button) mContext.findViewById(R.id.payloadQuestionButton);
        buttonInfo.put(mPayloadInfoButton, res.getString(R.string.payload_info));
        infoMap.put(res.getString(R.string.payload_label), res.getString(R.string.payload_info));

        Button mNameInfoButton = (Button) mContext.findViewById(R.id.nameQuestionButton);
        buttonInfo.put(mNameInfoButton, res.getString(R.string.name_info));
        infoMap.put(res.getString(R.string.name_label), res.getString(R.string.name_info));

        for (final Button button : buttonInfo.keySet()) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    String info = buttonInfo.get(button);
                    builder.setMessage(info).setTitle(infoMap.inverse().get(info));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.create().show();
                }
            });
        }
    }
    
    private void setRequestButtonText(boolean displayCommands) {
        String label;
        if (displayCommands) {
            label = "Send Command";
        } else {
            label = "Send Request";
        }
        
        ((Button) mContext.findViewById(R.id.sendRequestButton)).setText(label);
    }
    
    public void toggleRequestCommand(boolean displayCommands) {
        if (displayCommands) {
            initCommandInfoButtons();
        } else {
            initRequestInfoButtons();
        }
        
        setRequestButtonText(displayCommands);
    }
}
