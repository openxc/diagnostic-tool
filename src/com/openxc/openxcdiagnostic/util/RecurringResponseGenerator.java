package com.openxc.openxcdiagnostic.util;

import java.util.TimerTask;

import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.VehicleMessage;

public class RecurringResponseGenerator extends TimerTask {

    DiagnosticRequest mRequest;
    VehicleMessage.Listener mListener;

    public RecurringResponseGenerator(DiagnosticRequest request,
            VehicleMessage.Listener listener) {
        mRequest = request;
        mListener = listener;
    }

    public void run() {
        mListener.receive(Utilities.generateRandomFakeResponse(mRequest));
    }
}
