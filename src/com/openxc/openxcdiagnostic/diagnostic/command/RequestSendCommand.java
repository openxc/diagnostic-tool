package com.openxc.openxcdiagnostic.diagnostic.command;

import com.openxc.messages.VehicleMessage;
import com.openxc.openxcdiagnostic.diagnostic.DiagnosticActivity;

public class RequestSendCommand implements ButtonCommand {

    DiagnosticActivity mContext;

    public RequestSendCommand(DiagnosticActivity context) {
        mContext = context;
    }

    /**
     * Hides the keyboard if displaying. Generates a
     * <code>DiagnosticRequest</code> or <code>Command</code> from the input,
     * depending on what is being displayed, and sends the generated command if
     * successful
     */
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
