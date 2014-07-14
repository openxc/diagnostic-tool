package com.openxc.openxcdiagnostic.diagnostic.pair;

import com.google.common.base.Objects;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;

public class DiagnosticPair implements Pair {

    private DiagnosticRequest mRequest;
    private DiagnosticResponse mResponse;
    
    public DiagnosticPair(DiagnosticRequest request, DiagnosticResponse response) {
        mRequest = request;
        mResponse = response;
    }
    
    public DiagnosticRequest getRequest() {
        return mRequest;
    }
    
    public DiagnosticResponse getResponse() {
        return mResponse;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        final DiagnosticPair other = (DiagnosticPair) obj;
        return Objects.equal(mRequest, other.mRequest) && mResponse.equals(other.mResponse);
    }
    
}
