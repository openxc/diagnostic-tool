package com.openxc.openxcdiagnostic.diagnostic.command;

import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;

public class LaunchFavoritesDialogCommand implements ButtonCommand {

    DiagnosticActivity mContext;
    
    public LaunchFavoritesDialogCommand(DiagnosticActivity context) {
        mContext = context;
    }
    
    public void execute() {
        mContext.hideKeyboard();
        mContext.launchFavorites();
    }
    
}
