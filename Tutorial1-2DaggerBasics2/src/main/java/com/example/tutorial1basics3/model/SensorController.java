package com.example.tutorial1basics3.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorController implements SensorEventListener {

    // Convert from radian to degrees
    private static final float RAD_TO_DEG = (float) (180 / Math.PI);
    private Sensor rotationVector;

    private SensorManager mSensorManager;


    // Identity matrix
    private float I[] = new float[9];
    // Rotation Matrices
    private float[] rotationMatrix = new float[9];
    private float remappedRM[] = new float[9];
    // Array that contains azimuth, pitch and roll
    private float[] orientation = new float[3];


    // Output values received from MF or Rotation Vector Sensor
    private float azimuth, pitch, roll;


    private OnSensorEventChangeListener mOnSensorEventChangeListener;

    public SensorController(Context context) {
        // Sensor Manager
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }


    public void onResume(int sensorDelay) {
        mSensorManager.registerListener(this, rotationVector, sensorDelay);
    }

    public void onPause() {
        mSensorManager.unregisterListener(this);
        mOnSensorEventChangeListener = null;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                break;
        }

        getOrientationAnglesFromRVSensor();

        // Listener Values
        if (mOnSensorEventChangeListener != null) {
            mOnSensorEventChangeListener.getOrientationValues(azimuth, pitch, roll);
        }
    }


    private void getOrientationAnglesFromRVSensor() {
        SensorManager.getOrientation(rotationMatrix, orientation);
        azimuth = (orientation[0] * RAD_TO_DEG + 360) % 360;
        pitch = -orientation[1] * RAD_TO_DEG;
        roll = (90 + orientation[2] * RAD_TO_DEG);
    }


    public void setSensorEventChangeListener(OnSensorEventChangeListener listener) {
        this.mOnSensorEventChangeListener = listener;
    }

    public interface OnSensorEventChangeListener {
        void getOrientationValues(float azimuth, float pitch, float roll);
    }

}
