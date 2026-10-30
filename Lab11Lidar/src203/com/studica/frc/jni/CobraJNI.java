package com.studica.frc.jni;


public class CobraJNI extends StudicaJNIWrapper {

    public static native void c_initializeI2C(float vRef);
    public static native void c_closeI2C();
    public static native int c_getRawValue(int channel);
    public static native float c_getVoltage(int channel);
}
