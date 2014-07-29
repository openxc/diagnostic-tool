package com.openxc.openxcdiagnostic.diagnostic.command;

import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;

public class LaunchSettingsDialogCommand implements ButtonCommand {

    DiagnosticActivity mContext;

    public LaunchSettingsDialogCommand(DiagnosticActivity context) {
        mContext = context;
    }

    /**
     * Hides the keyboard and launches the settings dialog.
     */
    public void execute() {
        mContext.hideKeyboard();
        mContext.launchSettings();
    }

}
