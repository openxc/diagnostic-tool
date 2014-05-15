package com.openxc.openxcdiagnostic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DiagnosticView extends SurfaceView implements
	SurfaceHolder.Callback, Runnable {
	
    private static String TAG = "DiagnosticView";

    Thread mainLoop = null;
    private static Bitmap sOverlayLinesBitmap = null;
    private static double screenHeight; 
    private static double screenWidth; 

    public DiagnosticView(Context context) {
        super(context);
        init();
    }

    public DiagnosticView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
    	screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
    	screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        /*if(sOverlayLinesBitmap == null) {
            sOverlayLinesBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.overlay);
        }
         */
    }

    protected void drawOnCanvas(Canvas canvas, Bitmap videoBitmap) {
        canvas.drawColor(Color.BLACK);
        //canvas.drawBitmap(videoBitmap, createVideoFeedMatrix(videoBitmap),
              //  null);
        
    }

    @Override
    public void run() {
        while (true) {
            Canvas canvas = this.getHolder().lockCanvas();

            if (canvas != null) {

                canvas.drawColor(Color.BLACK);
                this.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

	public void surfaceCreated(SurfaceHolder holder) {

        Log.w("Dash", "SurfaceCreated");

        /*if (bmpDash == null) {
            bmpDash = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.mustangdash);
        }
        if (bmpNeedle == null) {
            bmpNeedle = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.needle2);
        }
        if (bmpBackground == null) {
            bmpBackground = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.twoflags);
        }*/

        this.mainLoop = new Thread(this);
        this.mainLoop.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            Thread.sleep(100); // wait for thread stopping
        } catch (Exception e) {
        }

        Log.w("Dash", "SurfaceDestroyed");
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    }
}
