package com.openxc.openxcdiagnostic.util;

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
import com.openxc.messages.DiagnosticResponse.NegativeResponseCode;
import com.openxc.openxcdiagnostic.R;

public class Utilities {

    private static Random rnd = new Random();

    private Utilities() {
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
        Integer pid = request.getPid();
        boolean success = rnd.nextBoolean();
        float value = 0;
        NegativeResponseCode responseCode = NegativeResponseCode.NONE;
        if (success) {
            value = rnd.nextFloat();
        } else {
            responseCode = negativeResponseCodes.get(rnd.nextInt(negativeResponseCodes.size()));
        }

        DiagnosticResponse response = new DiagnosticResponse(bus, id, mode, pid, 
                request.getPayload(), responseCode, value);
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
