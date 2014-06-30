package com.openxc.openxcdiagnostic.diagnostic;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;

public class CommandPair implements Pair {

    private Command mCommand;
    private CommandResponse mCommandResponse;
    
    public CommandPair(Command request, CommandResponse response) {
        mCommand = request;
        mCommandResponse = response;
    }
    
    public Command getReq() {
        return mCommand;
    }
    
    public CommandResponse getResp() {
        return mCommandResponse;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final CommandPair other = (CommandPair) obj;
        return mCommand.equals(other.mCommand) && mCommandResponse.equals(other.mCommandResponse);
    }
    
}
