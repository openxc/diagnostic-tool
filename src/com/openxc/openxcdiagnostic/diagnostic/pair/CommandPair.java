package com.openxc.openxcdiagnostic.diagnostic.pair;

import com.google.common.base.Objects;
import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;

public class CommandPair implements Pair {

    private Command mCommand;
    private CommandResponse mCommandResponse;
    
    public CommandPair(Command request, CommandResponse response) {
        mCommand = request;
        mCommandResponse = response;
    }
    
    public Command getRequest() {
        return mCommand;
    }
    
    public CommandResponse getResponse() {
        return mCommandResponse;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        
        final CommandPair other = (CommandPair) obj;
        return Objects.equal(mCommand, other.mCommand) && mCommandResponse.equals(other.mCommandResponse);
    }

}
