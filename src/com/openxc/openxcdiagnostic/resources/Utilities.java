package com.openxc.openxcdiagnostic.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.InvalidMessageFieldsException;

public class Utilities {

    private static Random rnd = new Random();
    
    private Utilities() {
    };
    
    //TODO this contains NegativeResponseCode.None, but ok because it's just for testing anyway
    private static final List<DiagnosticResponse.NegativeResponseCode> negativeResponseCodes = 
            Collections.unmodifiableList(Arrays.asList(DiagnosticResponse.NegativeResponseCode.values()));

    public static String getOutputString(DiagnosticRequest req) {
        String result = new String();
        result = result + "bus : " + req.getBusId() + "\n";
        result = result + "id : " + req.getId() + "\n";
        result = result + "mode: " + req.getMode() + "\n";
        result = result + "pid : " + (req.hasPid() ? req.getPid() : "N/A")
                + "\n";
        result = result
                + "payload : "
                + (req.getPayload() == null ? "N/A"
                        : String.valueOf(req.getPayload())) + "\n";
        result = result + "frequency : " + req.getFrequency() + "\n";
        result = result + "name : "
                + (req.getName() == null ? "N/A" : req.getName());
        return result;
    }

    public static String getOutputString(DiagnosticResponse resp) {
        String result = new String();
        result = result + "bus : " + resp.getBusId() + "\n";
        result = result + "id : " + resp.getId() + "\n";
        result = result + "mode: " + resp.getMode() + "\n";
        result = result + "pid : "
                + (resp.hasPid() ? resp.getPid() : "N/A") + "\n" ;
        boolean success = resp.getSuccess();
        result = result + "success : " + success + "\n";
        if (success) {
            result = result
                    + "payload : "
                    + (resp.getPayload() == null ? "N/A"
                            : String.valueOf(resp.getPayload())) + "\n";
            result = result + "value : " + String.valueOf(resp.getValue());
        } else {
            DiagnosticResponse.NegativeResponseCode code = resp.getNegativeResponseCode();
            result = result + "code : "
                    + code.toDocumentationString() + " (" + code.hexCodeString() + ")";
        }
        return result;
    }
    
    public static DiagnosticResponse generateRandomFakeResponse(DiagnosticRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put(DiagnosticRequest.BUS_KEY, request.getBusId());
        map.put(DiagnosticRequest.ID_KEY, request.getId());
        map.put(DiagnosticRequest.MODE_KEY, request.getMode());
        boolean success = rnd.nextBoolean();
        map.put(DiagnosticResponse.SUCCESS_KEY, success);
        if (success) {
            if (request.getPayload() != null) {
                map.put(DiagnosticRequest.PAYLOAD_KEY, request.getPayload());
            }
            map.put(DiagnosticResponse.VALUE_KEY, rnd.nextFloat());
        } else {
            map.put(DiagnosticResponse.NEGATIVE_RESPONSE_CODE_KEY, 
                    negativeResponseCodes.get(rnd.nextInt(negativeResponseCodes.size())));
        }
        try {
            return new DiagnosticResponse(map);
        } catch (InvalidMessageFieldsException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
