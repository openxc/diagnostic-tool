package com.openxc.openxcdiagnostic.diagnostic.command;

import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;

public class ClearInputFieldsCommand implements ButtonCommand {

    DiagnosticActivity mContext;
    
    public ClearInputFieldsCommand(DiagnosticActivity context) {
        mContext = context;
    }
    
    public void execute() {
        mContext.hideKeyboard();
        mContext.clearFields();
    }
    
}
