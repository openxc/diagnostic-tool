package com.openxc.openxcdiagnostic.diagnostic;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.openxc.openxcdiagnostic.R;
import com.openxc.openxcdiagnostic.diagnostic.command.ButtonCommand;
import com.openxc.openxcdiagnostic.diagnostic.command.ClearInputFieldsCommand;
import com.openxc.openxcdiagnostic.diagnostic.command.LaunchFavoritesDialogCommand;
import com.openxc.openxcdiagnostic.diagnostic.command.LaunchSettingsDialogCommand;
import com.openxc.openxcdiagnostic.diagnostic.command.RequestSendCommand;
import com.openxc.openxcdiagnostic.util.DialogLauncher;

public class DiagnosticButtonsManager implements DiagnosticManager{

    private DiagnosticActivity mContext;
    private boolean mDisplayCommands;
        
    public DiagnosticButtonsManager(DiagnosticActivity context, boolean displayCommands) {
        mContext = context;
        setRequestCommandState(displayCommands);
        initButtons();
    }
    
    @Override
    public void setRequestCommandState(boolean displayCommands) {
        mDisplayCommands = displayCommands;
        if (displayCommands) {
            initCommandInfoButton();
        } else {
            initRequestInfoButtons();
        }
        
        setRequestButtonText();
    }

    private void initButtons() {
        
        setRequestButtonText();
        
        Map<Integer, ButtonCommand> buttonActions = new HashMap<>();
        buttonActions.put(R.id.sendRequestButton, new RequestSendCommand(mContext));
        buttonActions.put(R.id.clearButton, new ClearInputFieldsCommand(mContext));
        buttonActions.put(R.id.favoritesButton, new LaunchFavoritesDialogCommand(mContext));
        buttonActions.put(R.id.settingsButton, new LaunchSettingsDialogCommand(mContext));
        
        for (final Map.Entry<Integer, ButtonCommand> entry : buttonActions.entrySet()) {
            ((Button) mContext.findViewById(entry.getKey()))
            .setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                   entry.getValue().execute();
               }
            });
        }
    }
        
    private void initCommandInfoButton() {
        final Resources res = mContext.getResources();
        Button commandInfoButton = (Button) mContext.findViewById(R.id.commandQuestionButton);
        commandInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogLauncher.launchAlert(mContext, res.getString(R.string.command_label), 
                        res.getString(R.string.command_info), "OK");
            }
        });
    }

    private void initRequestInfoButtons() {

        final Map<Integer, Integer> infoMap = new HashMap<>();
        final Map<Integer, Integer> buttonInfo = new HashMap<>();

        buttonInfo.put(R.id.frequencyQuestionButton, R.string.frequency_info);
        infoMap.put(R.string.frequency_info, R.string.frequency_label);

        buttonInfo.put(R.id.busQuestionButton, R.string.bus_info);
        infoMap.put(R.string.bus_info, R.string.bus_label);

        buttonInfo.put(R.id.idQuestionButton, R.string.id_info);
        infoMap.put(R.string.id_info, R.string.id_label);

        buttonInfo.put(R.id.modeQuestionButton, R.string.mode_info);
        infoMap.put(R.string.mode_info, R.string.mode_label);

        buttonInfo.put(R.id.pidQuestionButton, R.string.pid_info);
        infoMap.put(R.string.pid_info, R.string.pid_label);

        buttonInfo.put(R.id.payloadQuestionButton, R.string.payload_info);
        infoMap.put(R.string.payload_info, R.string.payload_label);

        buttonInfo.put(R.id.nameQuestionButton, R.string.name_info);
        infoMap.put(R.string.name_info, R.string.name_label);

        final Resources res = mContext.getResources();
        for (final Integer buttonId : buttonInfo.keySet()) {
            ((Button) mContext.findViewById(buttonId))
            .setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int infoId = buttonInfo.get(buttonId);
                    DialogLauncher.launchAlert(mContext, res.getString(infoMap.get(infoId)), 
                            res.getString(infoId), "OK");
                }
            });
        }
    }
    
    private void setRequestButtonText() {
        String label;
        if (mDisplayCommands) {
            label = "Send Command";
        } else {
            label = "Send Request";
        }
        
        ((Button) mContext.findViewById(R.id.sendRequestButton)).setText(label);
    }

}
