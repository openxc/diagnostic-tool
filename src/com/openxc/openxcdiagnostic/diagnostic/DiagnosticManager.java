package com.openxc.openxcdiagnostic.diagnostic;

public interface DiagnosticManager {

    /**
     * Update the current state of the manager.
     * 
     * @param displayCommands
     *            If true, the activity wants to switch to a command
     *            sending/receiving state. If false, the activity wants to
     *            switch to a request sending/receiving state.
     */
    public void setRequestCommandState(boolean displayCommands);

}
