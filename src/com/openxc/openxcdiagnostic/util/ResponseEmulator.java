package com.openxc.openxcdiagnostic.util;

import java.util.Timer;

import com.openxc.messages.Command;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.VehicleMessage;

/**
 * 
 * Class for emulating fake responses.
 * 
 */
public class ResponseEmulator {

    private static Timer mTimer = new Timer();

    public static void emulate(VehicleMessage request,
            VehicleMessage.Listener listener) {
        if (MessageAnalyzer.isDiagnosticRequest(request)) {
            DiagnosticRequest diagReq = (DiagnosticRequest) request;
            if (diagReq.getFrequency() != null && diagReq.getFrequency() > 0) {
                RecurringResponseGenerator generator = new RecurringResponseGenerator(
                        diagReq, listener);
                mTimer.schedule(generator, 100, 1000);
            }
        }

        if (MessageAnalyzer.isCommand(request)) {
            listener.receive(Utilities
                    .generateRandomFakeCommandResponse((Command) request));
        } else if (MessageAnalyzer.isDiagnosticRequest(request)) {
            listener.receive(Utilities
                    .generateRandomFakeResponse((DiagnosticRequest) request));
        }
    }

}
