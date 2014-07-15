package com.openxc.openxcdiagnostic.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.VehicleMessage;
import com.openxc.messages.DiagnosticResponse.NegativeResponseCode;

public class Utilities {

    private static Random rnd = new Random();

    private Utilities() {
    }
    
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

    // this contains NegativeResponseCode.None, but ok because it's just
    // for testing anyway
    private static final List<DiagnosticResponse.NegativeResponseCode> negativeResponseCodes 
    = Collections.unmodifiableList(Arrays.asList(DiagnosticResponse.NegativeResponseCode.values()));

    public static String getDocumentationError(DiagnosticResponse resp) {
        return resp.getNegativeResponseCode().toDocumentationString() + " ";
    }

    public static DiagnosticResponse generateRandomFakeResponse(
            DiagnosticRequest request) {
        int bus = request.getBusId();
        int id = request.getId();
        int mode = request.getMode();
        boolean success = rnd.nextBoolean();
        double value = 0;
        NegativeResponseCode responseCode = NegativeResponseCode.NONE;
        if (success) {
            BigDecimal bd = new BigDecimal(rnd.nextFloat());
            bd = bd.round(new MathContext(4, RoundingMode.HALF_UP));
            value = bd.doubleValue();
        } else {
            responseCode = negativeResponseCodes.get(rnd.nextInt(negativeResponseCodes.size()));
        }

        DiagnosticResponse response = new DiagnosticResponse(bus, id, mode);
        if (request.getPid() != null) {
            response.setPid(request.getPid()); 
        }
        response.setPayload(request.getPayload());
        response.setNegativeResponseCode(responseCode);
        response.setValue(value);
        response.timestamp();
        return response;
    }
    
    public static CommandResponse generateRandomFakeCommandResponse(Command command) {
        CommandResponse resp = new CommandResponse(command.getCommand(), "test command response");
        resp.timestamp();
        return resp;
    }

    public static String epochTimeToTime(long time) {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        DateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.US);
        Date date = new Date(Long.parseLong(String.valueOf(time)));
        return timeFormat.format(date) + "\n" + dateFormat.format(date);
    }

    public static int getScreenHeight(Activity context) {
        Rect displayRect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRect);
        return displayRect.height();
    }

    public static int getScreenWidth(Activity context) {
        Rect displayRect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRect);
        return displayRect.width();
    }
    
    public static void replaceView(LinearLayout layout, View oldView, View newView) {
        layout.addView(newView, layout.indexOfChild(oldView));
        layout.removeView(oldView);
    }
}
