package com.openxc.openxcdiagnostic.diagnostic.pair;

import com.openxc.messages.VehicleMessage;

public interface Pair {

    public VehicleMessage getRequest();

    public VehicleMessage getResponse();

}
