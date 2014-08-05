package com.openxc.openxcdiagnostic.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openxc.messages.Command;
import com.openxc.messages.CommandResponse;
import com.openxc.messages.DiagnosticRequest;
import com.openxc.messages.DiagnosticResponse;
import com.openxc.messages.DiagnosticResponse.NegativeResponseCode;

public class Utilities {

    private static Random rnd = new Random();

    private Utilities() {
    }

    // this contains NegativeResponseCode.None, but ok because it's just
    // for testing anyway
    private static final List<DiagnosticResponse.NegativeResponseCode> negativeResponseCodes = Collections
            .unmodifiableList(Arrays
                    .asList(DiagnosticResponse.NegativeResponseCode.values()));

    /**
     * Gets the error as it appears in documentation
     * 
     * @param resp
     * @return The result of calling
     *         resp.getNegativeResponseCode().toDocumentationString().
     */
    public static String getDocumentationError(DiagnosticResponse resp) {
        return resp.getNegativeResponseCode().toDocumentationString() + " ";
    }

    /**
     * Generates a random fake response that will match the given
     * <code>DiagnosticRequest</code>
     * 
     * @param request
     * @return A fake <code>DiagnosticResponse</code>
     */
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
            responseCode = negativeResponseCodes.get(rnd
                    .nextInt(negativeResponseCodes.size()));
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

    /**
     * Generates a random fake response that will match the given
     * <code>Command</code>
     * 
     * @param command
     * @return A fake <code>CommandResponse</code>
     */
    public static CommandResponse generateRandomFakeCommandResponse(
            Command command) {
        CommandResponse resp = new CommandResponse(command.getCommand(),
                "test command response");
        resp.timestamp();
        return resp;
    }

    /**
     * Converts a <code>long</code> into a familiar string
     * 
     * @param time
     * @return The time in HH:mm:ss format and the date, separated by a newline
     *         character.
     */
    public static String epochTimeToTime(long time) {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        DateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.US);
        Date date = new Date(Long.parseLong(String.valueOf(time)));
        return timeFormat.format(date) + "\n" + dateFormat.format(date);
    }

    public static int getScreenHeight(Activity context) {
        Rect displayRect = new Rect();
        context.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(displayRect);
        return displayRect.height();
    }

    public static int getScreenWidth(Activity context) {
        Rect displayRect = new Rect();
        context.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(displayRect);
        return displayRect.width();
    }

    /**
     * Swaps <code>oldView</code> out for <code>newView</code> in
     * <code>layout</code>
     * 
     * @param layout
     * @param oldView
     * @param newView
     */
    public static void replaceView(LinearLayout layout, View oldView,
            View newView) {
        layout.addView(newView, layout.indexOfChild(oldView));
        layout.removeView(oldView);
    }

    /**
     * Recursively finds all TextViews in a given layout and returns them in an
     * <code>ArrayList</code>.
     * 
     * @param layout
     * @return
     */
    public static ArrayList<TextView> getAllLabels(ViewGroup layout) {

        ArrayList<TextView> views = new ArrayList<>();

        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof ViewGroup) {
                views.addAll(getAllLabels((ViewGroup) layout.getChildAt(i)));
            } else if (view instanceof TextView) {
                views.add((TextView) view);
            }
        }

        return views;
    }

    public static void scaleDownAllLabelsToFit(Activity context,
            ViewGroup layout) {

        for (TextView tv : getAllLabels(layout)) {
            fitTextInTextView(context, tv);
        }
    }

    public static void fitTextInTextView(final Activity context,
            final TextView tv) {

        if (tv.getText().toString().trim().equals("")) {
            return;
        }

        if (tv.getWidth() == 0) {

            tv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top,
                        int right, int bottom, int oldLeft, int oldTop,
                        int oldRight, int oldBottom) {
                    if (right - left > 0) {
                        fitText(context, tv);
                        tv.removeOnLayoutChangeListener(this);
                    }
                }
            });
        } else {
            fitText(context, tv);
        }
    }

    private static boolean textTooWide(TextView tv) {
        return (int) tv.getPaint().measureText(tv.getText().toString()) > tv
                .getWidth() - tv.getPaddingLeft() - tv.getPaddingRight();
    }

    private static boolean textTooTall(Activity context, TextView tv) {
        return getRequiredHeight(context, tv) > tv.getHeight()
                - tv.getPaddingBottom() - tv.getPaddingTop();
    }

    private static void fitText(Activity context, TextView tv) {

        if (tv.getText().toString().trim().equals("")) {
            return;
        }

        tv.setTextSize(getMaxFittingFontSize(context, tv));
    }

    private static int getRequiredHeight(Activity context, TextView tv) {
        TextView textView = new TextView(context);
        textView.setText(tv.getText());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getTextSize());
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                getScreenWidth(context), MeasureSpec.AT_MOST);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    public static float getMaxFittingFontSize(Activity context, TextView tv) {

        if (tv.getText().toString().trim().equals("")) {
            return Float.MAX_VALUE;
        }

        float fontSize = 2;
        float originalFontSize = tv.getTextSize();
        tv.setTextSize(fontSize);
        while (!textTooTall(context, tv) && !textTooWide(tv)) {
            tv.setTextSize(fontSize++);
        }
        tv.setTextSize(originalFontSize);
        return fontSize - 2;
    }
}
