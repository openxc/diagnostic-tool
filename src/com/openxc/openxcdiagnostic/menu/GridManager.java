package com.openxc.openxcdiagnostic.menu;

import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.openxc.measurements.BrakePedalStatus;
import com.openxc.measurements.FuelConsumed;
import com.openxc.measurements.HeadlampStatus;
import com.openxc.measurements.IgnitionStatus;
import com.openxc.measurements.Longitude;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.Odometer;
import com.openxc.measurements.ParkingBrakeStatus;
import com.openxc.measurements.SteeringWheelAngle;
import com.openxc.measurements.TorqueAtTransmission;
import com.openxc.measurements.TransmissionGearPosition;
import com.openxc.measurements.VehicleSpeed;
import com.openxc.measurements.WindshieldWiperStatus;
import com.openxc.openxcdiagnostic.R;

public class GridManager {

    public static final int MenuBackgroundImgID = R.drawable.graybackground;

    private static final SparseArray<Class<? extends Measurement>> VehicleClassImgDict;
    static {
        VehicleClassImgDict = new SparseArray<Class<? extends Measurement>>();
        VehicleClassImgDict.append(R.drawable.steering_wheel, SteeringWheelAngle.class);
        VehicleClassImgDict.append(R.drawable.speedrpm, VehicleSpeed.class);
        VehicleClassImgDict.append(R.drawable.gaspump, FuelConsumed.class);
        VehicleClassImgDict.append(R.drawable.odometer, Odometer.class);
        VehicleClassImgDict.append(R.drawable.windshield_wiper, WindshieldWiperStatus.class);
        VehicleClassImgDict.append(R.drawable.pedals, BrakePedalStatus.class);
        VehicleClassImgDict.append(R.drawable.parkingbrake, ParkingBrakeStatus.class);
        VehicleClassImgDict.append(R.drawable.headlamp, HeadlampStatus.class);
        VehicleClassImgDict.append(R.drawable.transmission_torque, TorqueAtTransmission.class);
        VehicleClassImgDict.append(R.drawable.transmission_gear, TransmissionGearPosition.class);
        VehicleClassImgDict.append(R.drawable.key, IgnitionStatus.class);
        VehicleClassImgDict.append(R.drawable.location, Longitude.class);
    }

    public static final Integer[] MenuThumbIDs = { R.drawable.steering_wheel,
            R.drawable.speedrpm, R.drawable.gaspump, R.drawable.odometer,
            R.drawable.windshield_wiper, R.drawable.pedals,
            R.drawable.parkingbrake, R.drawable.headlamp,
            R.drawable.transmission_torque, R.drawable.transmission_gear,
            R.drawable.key, R.drawable.location };

    public static Class<? extends Measurement> getClass(Integer position) {
        return VehicleClassImgDict.get(MenuThumbIDs[position]);
    }

    public static final int MenuButtonUnpressedImgID = R.drawable.graybuttonunpressedbackground;
    public static final int MenuButtonPressedImgID = R.drawable.graybuttonpressedbackground;

    public static final boolean drawablesAreEqual(Drawable d1, Drawable d2) {
        return d1.getConstantState().equals(d2.getConstantState());
    }

}
