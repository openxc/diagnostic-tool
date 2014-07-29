package com.openxc.openxcdiagnostic.diagnostic.command;

import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;

public class LaunchFavoritesDialogCommand implements ButtonCommand {

    DiagnosticActivity mContext;

    public LaunchFavoritesDialogCommand(DiagnosticActivity context) {
        mContext = context;
    }

    /**
     * Hides the keyboard and launches the favorites dialog.
     */
    public void execute() {
        mContext.hideKeyboard();
        mContext.launchFavorites();
    }

}
