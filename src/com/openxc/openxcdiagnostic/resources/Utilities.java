package com.openxc.openxcdiagnostic.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        result = result + "bus : " + getBusOutput(req) + "\n";
        result = result + "id : " + getIdOutput(req) + "\n";
        result = result + "mode: " + getModeOutput(req) + "\n";
        result = result + "pid : " + getPidOutput(req) + "\n";
        result = result + "payload : " + getPayloadOutput(req) + "\n";
        result = result + "frequency : " + getFrequencyOutput(req) + "\n";
        result = result + "name : " + getNameOutput(req);
        return result;
    }

    public static String getOutputString(DiagnosticResponse resp) {
        String result = new String();
        result = result + "bus : " + getBusOutput(resp) + "\n";
        result = result + "id : " + getIdOutput(resp) + "\n";
        result = result + "mode: " + getModeOutput(resp) + "\n";
        result = result + "pid : " + getPidOutput(resp) + "\n";
        result = result + "success : " + getSuccessOutput(resp) + "\n";
        boolean success = resp.getSuccess();
        if (success) {
            result = result + "payload : " + getPayloadOutput(resp) + "\n";
            result = result + "value : " + getValueOutput(resp);
        } else {
            DiagnosticResponse.NegativeResponseCode code = resp.getNegativeResponseCode();
            result = result + "code : "
                    + code.toDocumentationString() + " (" + code.hexCodeString() + ")";
        }
        return result;
    }
    
    public static String getBusOutput(DiagnosticRequest req) {
        return String.valueOf(req.getBusId());
    }
    
    public static String getBusOutput(DiagnosticResponse resp) {
        return String.valueOf(resp.getBusId());
    }
    
    public static String getIdOutput(DiagnosticRequest req) {
        return String.valueOf(req.getId());
    }
    
    public static String getIdOutput(DiagnosticResponse resp) {
        return String.valueOf(resp.getId());
    }
    
    public static String getModeOutput(DiagnosticRequest req) {
        return "0x" + Integer.toHexString(req.getMode()).toUpperCase(Locale.US);
    }
    
    public static String getModeOutput(DiagnosticResponse resp) {
        return "0x" + Integer.toHexString(resp.getMode()).toUpperCase(Locale.US);
    }
    
    public static String getPidOutput(DiagnosticRequest req) {
        return req.getPid() == null ? "" : String.valueOf(req.getPid());
    }
    
    public static String getPidOutput(DiagnosticResponse resp) {
        return resp.getPid() == null ? "" : String.valueOf(resp.getPid());
    }
    
    public static String getPayloadOutput(DiagnosticRequest req) {
        return req.getPayload() == null ? "" : String.valueOf(req.getPayload());
    }
    
    public static String getPayloadOutput(DiagnosticResponse resp) {
        return resp.getPayload() == null ? "" : String.valueOf(resp.getPayload());
    }
    
    public static String getSuccessOutput(DiagnosticResponse resp) {
        return String.valueOf(resp.getSuccess());
    }
    
    public static String getValueOutput(DiagnosticResponse resp) {
        return String.valueOf(resp.getValue());
    }    
    
    public static String getFrequencyOutput(DiagnosticRequest req) {
        return req.getFrequency() == null ? "" : String.valueOf(req.getFrequency());
    }
    
    public static String getNameOutput(DiagnosticRequest req) {
        return req.getName() == null ? "" : String.valueOf(req.getName());
    }
    
    public static String getResponseCodeOutput(DiagnosticResponse resp) {
        return resp.getNegativeResponseCode().hexCodeString().toUpperCase(Locale.US);
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
