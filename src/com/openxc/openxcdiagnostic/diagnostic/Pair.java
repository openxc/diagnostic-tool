package com.openxc.openxcdiagnostic.diagnostic;

import com.openxc.messages.VehicleMessage;

public interface Pair {

    public VehicleMessage getReq();
    
    public VehicleMessage getResp();
    
}
