package com.openxc.openxcdiagnostic.resources;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.res.ColorStateList;

import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.InvalidMessageFieldsException;
import com.openxc.openxcdiagnostic.R;

public class Utilities {

    private Utilities() {
    };
    
    private static final List<DiagnosticResponse.NegativeResponseCode> negativeResponseCodes = 
            Collections.unmodifiableList(Arrays.asList(DiagnosticResponse.NegativeResponseCode.values()));

    public static String getOutputString(DiagnosticRequest req) {
        String result = new String();
        result = result + "bus : " + req.getBusId() + "\n";
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
        result = result + "bus : " + resp.getBusId() + "\n";
        result = result + "id : " + resp.getId() + "\n";
        result = result + "mode: " + resp.getMode() + "\n";
        result = result + "pid : "
                + (resp.getPid() == -1 ? "N/A" : resp.getPid()) + "\n";
        boolean success = resp.getSuccess();
        result = result + "success : " + success + "\n";
        if (success) {
            result = result
                    + "payload : "
                    + (resp.getPayload() == null ? "N/A"
                            : String.valueOf(resp.getPayload())) + "\n";
            result = result + "value : " + String.valueOf(resp.getValue());
        } else {
            result = result + "negative_response_code : "
                    + resp.getNegativeResponseCode().toString();
        }
        return result;
    }
    
    public static int getOutputColor(Activity context, DiagnosticResponse resp) {
        int color = resp.getSuccess() ? R.color.lightBlue : R.color.darkRed;
        return context.getResources().getColor(color);
    }
    
    public static DiagnosticResponse generateRandomFakeResponse(DiagnosticRequest request) {
        Random rnd = new Random();
        Map<String, Object> map = new HashMap<>();
        map.put(DiagnosticResponse.BUS_KEY, request.getBusId());
        map.put(DiagnosticResponse.ID_KEY, request.getId());
        map.put(DiagnosticResponse.MODE_KEY, request.getMode());
        boolean success = rnd.nextBoolean();
        map.put(DiagnosticResponse.SUCCESS_KEY, success);
        if (success) {
            if (request.getPayload() != null) {
                map.put(DiagnosticResponse.PAYLOAD_KEY, request.getPayload());
            }
            map.put(DiagnosticResponse.VALUE_KEY, rnd.nextFloat());
        } else {
            map.put(DiagnosticResponse.NEGATIVE_RESPONSE_CODE_KEY, 
                    negativeResponseCodes.get(rnd.nextInt(negativeResponseCodes.size())));
        }
        try {
            return new DiagnosticResponse(map);
        } catch (InvalidMessageFieldsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
}
