package com.openxc.openxcdiagnostic.resources;

import android.graphics.drawable.Drawable;

import com.openxc.openxcdiagnostic.R;

public class ResourceManager {

	public static final int MenuBackgroundImgID = R.drawable.graybackground;
    
	public static final Integer[] MenuThumbIDs = {
            R.drawable.steeringwheel, R.drawable.speedrpm,
            R.drawable.gaspump, R.drawable.odometer, 
            R.drawable.windshieldwiper, R.drawable.pedals,
            R.drawable.parkingbrake, R.drawable.headlamp, 
            R.drawable.transmissiontorque, R.drawable.transmissiongear,
            R.drawable.key, R.drawable.location
    };
	
	public static final int MenuButtonUnpressedImgID = R.drawable.graybuttonunpressedbackground;
	public static final int MenuButtonPressedImgID = R.drawable.graybuttonpressedbackground;
	
	public static final boolean drawablesAreEqual(Drawable d1, Drawable d2) {
		return d1.getConstantState().equals(d2.getConstantState());
	}
	
}
