package com.openxc.openxcdiagnostic.diagnostic.command;

import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;

public class LaunchSettingsDialogCommand implements ButtonCommand {

    DiagnosticActivity mContext;
    
    public LaunchSettingsDialogCommand(DiagnosticActivity context) {
        mContext = context;
    }
    
    public void execute() {
        mContext.hideKeyboard();
        mContext.launchSettings();
    }
    
}
