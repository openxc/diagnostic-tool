package com.openxc.openxcdiagnostic.util;

import java.util.ArrayList;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticMessage;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.ExactKeyMatcher;
import com.openxc.messages.KeyedMessage;
import com.openxc.messages.VehicleMessage;

/**
 * 
 * Class for inquiring about <code>VehicleMessage(s)</code>
 * 
 */
public class MessageAnalyzer {

    public static boolean isCommand(VehicleMessage msg) {
        return msg instanceof Command;
    }

    public static boolean isDiagnosticRequest(VehicleMessage msg) {
        return msg instanceof DiagnosticRequest;
    }

    public static boolean isDiagnosticResponse(VehicleMessage msg) {
        return msg instanceof DiagnosticResponse;
    }

    public static boolean isCommandResponse(VehicleMessage msg) {
        return msg instanceof CommandResponse;
    }

    public static boolean canBeSent(VehicleMessage msg) {
        return isDiagnosticRequest(msg) || isCommand(msg);
    }

    /**
     * Determines if the given messages have equal bus, mode, and pid.
     * 
     * @param msg1
     * @param msg2
     * @return
     */
    public static boolean exactMatchExceptId(DiagnosticMessage msg1,
            DiagnosticMessage msg2) {
        return msg1.getBusId() == msg2.getBusId()
                && msg1.getMode() == msg2.getMode()
                && msg1.getPid() == msg2.getPid();
    }

    /**
     * Finds a matching <code>KeyedMessage</code> in <code>arr</code>. Matching
     * is determined by matcher.matches(KeyedMessage)
     * 
     * @param matcher
     * @param arr
     * @return
     */
    public static VehicleMessage findMatching(ExactKeyMatcher matcher,
            ArrayList<? extends KeyedMessage> arr) {

        if (matcher != null) {
            for (int i = 0; i < arr.size(); i++) {
                KeyedMessage request = arr.get(i);
                if (matcher.matches(request)) {
                    return request;
                }
            }
        }
        return null;
    }

}
