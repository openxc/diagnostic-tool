package com.openxc.openxcdiagnostic.diagnostic;

import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;

public class DiagnosticPair implements Pair {

    private DiagnosticRequest mRequest;
    private DiagnosticResponse mResponse;
    
    public DiagnosticPair(DiagnosticRequest request, DiagnosticResponse response) {
        mRequest = request;
        mResponse = response;
    }
    
    public DiagnosticRequest getReq() {
        return mRequest;
    }
    
    public DiagnosticResponse getResp() {
        return mResponse;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final DiagnosticPair other = (DiagnosticPair) obj;
        return mRequest.equals(other.getReq()) && mResponse.equals(other.getResp());
    }
    
}
