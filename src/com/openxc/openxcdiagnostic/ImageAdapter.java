package com.openxc.openxcdiagnostic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private static double screenWidth;
    private static double screenHeight;

    public ImageAdapter(Context c) {
        mContext = c;
        screenHeight = c.getResources().getDisplayMetrics().heightPixels;
    	screenWidth = c.getResources().getDisplayMetrics().widthPixels;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        int pad = 20;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams((int)(screenWidth/3), (int)(screenHeight/4)));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(pad, pad, pad, pad);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        imageView.setBackground(mContext.getResources().getDrawable(R.drawable.graybuttonbackground));
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.steeringwheel, R.drawable.speedometer,
            R.drawable.tachometer, R.drawable.gaspump, 
            R.drawable.odometer, R.drawable.windshieldwiper, 
            R.drawable.brakepedal, R.drawable.parkingbrake, 
            R.drawable.headlamp, R.drawable.transmissiontorque
    };
}
