package com.openxc.openxcdiagnostic.diagnostic.command;

import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;

public class RequestSendCommand implements ButtonCommand {

    DiagnosticActivity mContext;
    
    public RequestSendCommand(DiagnosticActivity context) {
        mContext = context;
    }
    
    public void execute() {
        mContext.hideKeyboard();
        
        VehicleMessage request;
        if (mContext.isDisplayingCommands()) {
            request = mContext.generateCommandFromInput();
        } else {
            request = mContext.generateDiagnosticRequestFromInput();
        }
        if (request != null) {
            mContext.send(request);
        }
    }
    
}
