package com.openxc.openxcdiagnostic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridImageAdapter extends BaseAdapter {
    private Context mContext;
    private double screenWidth;
    private double screenHeight;
    private Integer[] images;
    private Integer initialBackground;

    public GridImageAdapter(Context c, Integer[] img, Integer backgrnd) {
        mContext = c;
        images = img;
        initialBackground = backgrnd;
        screenHeight = c.getResources().getDisplayMetrics().heightPixels;
    	screenWidth = c.getResources().getDisplayMetrics().widthPixels;
    }

    public int getCount() {
        return images.length;
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

        imageView.setImageResource(images[position]);
        imageView.setBackground(mContext.getResources().getDrawable(initialBackground));
        return imageView;
    }

}
