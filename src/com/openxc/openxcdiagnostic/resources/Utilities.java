package com.openxc.openxcdiagnostic.resources;

import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;

public class Utilities {

    private Utilities() {
    };

    public static String getOutputString(DiagnosticRequest req) {
        String result = new String();
        result = result + "bus : " + req.getCanBus() + "\n";
        result = result + "id : " + req.getId() + "\n";
        result = result + "mode: " + req.getMode() + "\n";
        result = result + "pid : " + (req.getPid() == 0 ? "N/A" : req.getPid())
                + "\n";
        result = result
                + "payload : "
                + (req.getPayload() == null ? "N/A"
                        : String.valueOf(req.getPayload())) + "\n";
        result = result + "factor : " + req.getFactor() + "\n";
        result = result + "offset : " + req.getOffset() + "\n";
        result = result + "frequency : " + req.getFrequency() + "\n";
        result = result + "name : "
                + (req.getName() == null ? "N/A" : req.getName());
        return result;
    }

    public static String getOutputString(DiagnosticResponse resp) {
        String result = new String();
        result = result + "bus : " + resp.getCanBus() + "\n";
        result = result + "id : " + resp.getId() + "\n";
        result = result + "mode: " + resp.getMode() + "\n";
        result = result + "pid : "
                + (resp.getPid() == 0 ? "N/A" : resp.getPid()) + "\n";
        boolean success = resp.getSuccess();
        result = result + "success : " + success + "\n";
        if (success) {
            result = result
                    + "payload : "
                    + (resp.getPayload() == null ? "N/A"
                            : String.valueOf(resp.getPayload())) + "\n";
            result = result + "value : " + String.valueOf(resp.getValue());
        } else {
            result = result + "negative_response_code"
                    + resp.getNegativeResponseCode().toString();
        }
        return result;
    }
}
