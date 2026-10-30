package com.studica.frc.jni;

import com.studica.frc.MockDS;

public class MockDSJNI extends StudicaJNIWrapper
{
    public static native void c_MockDS_create();
    public static native void c_MockDS_destroy();
    public static native void c_MockDS_enable();
    public static native void c_MockDS_disable();
}