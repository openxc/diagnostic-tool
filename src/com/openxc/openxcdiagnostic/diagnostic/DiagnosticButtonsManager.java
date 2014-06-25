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

    private DiagnosticButtonsManager() {
    }

    public static void init(DiagnosticActivity context) {
        initButtons(context);
    }

    private static void initButtons(final DiagnosticActivity context) {

        Button sendRequestButton = (Button) context.findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.takeSendRequestButtonPush();
            }
        });

        Button clearButton = (Button) context.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.takeClearButtonPush();
            }
        });

        Button favoritesButton = (Button) context.findViewById(R.id.favoritesButton);
        favoritesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.takeFavoritesButtonPush();
            }
        });

        Button settingsButton = (Button) context.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.takeSettingsButtonPush();
            }
        });

        initInfoButtons(context);
    }

    private static void initInfoButtons(final DiagnosticActivity context) {

        Resources res = context.getResources();
        final BiMap<String, String> infoMap = HashBiMap.create();
        final Map<Button, String> buttonInfo = new HashMap<>();

        Button mFrequencyInfoButton = (Button) context.findViewById(R.id.frequencyQuestionButton);
        buttonInfo.put(mFrequencyInfoButton, res.getString(R.string.frequency_info));
        infoMap.put(res.getString(R.string.frequency_label), res.getString(R.string.frequency_info));

        Button mBusInfoButton = (Button) context.findViewById(R.id.busQuestionButton);
        buttonInfo.put(mBusInfoButton, res.getString(R.string.bus_info));
        infoMap.put(res.getString(R.string.bus_label), res.getString(R.string.bus_info));

        Button mIdInfoButton = (Button) context.findViewById(R.id.idQuestionButton);
        buttonInfo.put(mIdInfoButton, res.getString(R.string.id_info));
        infoMap.put(res.getString(R.string.id_label), res.getString(R.string.id_info));

        Button mModeInfoButton = (Button) context.findViewById(R.id.modeQuestionButton);
        buttonInfo.put(mModeInfoButton, res.getString(R.string.mode_info));
        infoMap.put(res.getString(R.string.mode_label), res.getString(R.string.mode_info));

        Button mPidInfoButton = (Button) context.findViewById(R.id.pidQuestionButton);
        buttonInfo.put(mPidInfoButton, res.getString(R.string.pid_info));
        infoMap.put(res.getString(R.string.pid_label), res.getString(R.string.pid_info));

        Button mPayloadInfoButton = (Button) context.findViewById(R.id.payloadQuestionButton);
        buttonInfo.put(mPayloadInfoButton, res.getString(R.string.payload_info));
        infoMap.put(res.getString(R.string.payload_label), res.getString(R.string.payload_info));

        Button mNameInfoButton = (Button) context.findViewById(R.id.nameQuestionButton);
        buttonInfo.put(mNameInfoButton, res.getString(R.string.name_info));
        infoMap.put(res.getString(R.string.name_label), res.getString(R.string.name_info));

        for (final Button button : buttonInfo.keySet()) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
}
