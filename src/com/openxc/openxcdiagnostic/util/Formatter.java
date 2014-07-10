package com.openxc.openxcdiagnostic.util;

import java.util.Locale;

import android.app.Activity;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.openxcdiagnostic.R;

public class Formatter {
    
    private Formatter() {      
    }
    
    private static String padRight(String st) {
        return st + " ";
    }

    // all these outputs are italicized so the space prevents them from being
    // cut off at the end (mostly)
    public static String getBusOutput(DiagnosticRequest req) {
        return padRight(String.valueOf(req.getBusId()));
    }

    public static String getBusOutput(DiagnosticResponse resp) {
        return padRight(String.valueOf(resp.getBusId()));
    }

    public static String getIdOutput(DiagnosticRequest req) {
        return padRight("0x"
                + Integer.toHexString(req.getId()).toUpperCase(Locale.US));
    }

    public static String getIdOutput(DiagnosticResponse resp) {
        return padRight("0x"
                + Integer.toHexString(resp.getId()).toUpperCase(Locale.US));
    }

    public static String getModeOutput(DiagnosticRequest req) {
        return padRight("0x" + Integer.toHexString(req.getMode()).toUpperCase(Locale.US));
    }

    public static String getModeOutput(DiagnosticResponse resp) {
        return padRight("0x"
                + Integer.toHexString(resp.getMode()).toUpperCase(Locale.US));
    }

    public static String getPidOutput(DiagnosticRequest req) {
        return req.getPid() == null ? "" : padRight(String.valueOf(req.getPid()));
    }

    public static String getPidOutput(DiagnosticResponse resp) {
        return resp.getPid() == null ? "" : padRight(String.valueOf(resp.getPid()));
    }

    public static String getPayloadOutput(DiagnosticRequest req) {
        return req.getPayload() == null ? "" : padRight("0x"
                + new String(req.getPayload()));
    }

    public static String getPayloadOutput(DiagnosticResponse resp) {
        return resp.getPayload() == null ? "" : padRight("0x"
                + new String(resp.getPayload()));
    }

    public static String getSuccessOutput(DiagnosticResponse resp) {
        return padRight(String.valueOf(resp.isSuccessful()));
    }

    public static String getValueOutput(DiagnosticResponse resp) {
        return padRight(String.valueOf(resp.getValue()));
    }

    public static String getFrequencyOutput(DiagnosticRequest req) {
        return req.getFrequency() == null ? ""
                : padRight(String.valueOf(req.getFrequency()));
    }

    public static String getNameOutput(DiagnosticRequest req) {
        return req.getName() == null ? "" : padRight(String.valueOf(req.getName()));
    }
    
    public static String getMessageOutput(CommandResponse resp) {
        return resp.getMessage() == null ? "" : padRight(resp.getMessage());
    }
    
    public static String getCommandOutput(CommandResponse resp) {
        return resp.getCommand() == null ? "" : padRight(resp.getCommand().toString());
    }
    
    public static String getCommandOutput(Command command) {
        return command.getCommand() == null ? "" : padRight(command.getCommand().toString());
    }

    public static String getResponseCodeOutput(DiagnosticResponse resp) {
        return padRight(resp.getNegativeResponseCode().hexCodeString());
    }

    public static String getOutputTableResponseCodeOutput(
            DiagnosticResponse resp) {
        return padRight(Utilities.getDocumentationError(resp) + " : "
                + getResponseCodeOutput(resp));
    }
    
    public static int getOutputColor(Activity context, DiagnosticResponse resp) {
        int color = resp.isSuccessful() ? R.color.lightBlue : R.color.darkRed;
        return context.getResources().getColor(color);
    }
    
}
