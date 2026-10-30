package com.studica.frc.jni;

import com.studica.frc.Lidar;

public class LidarJNI extends StudicaJNIWrapper
{
    public static native void c_Lidar_create(int port);
    public static native void c_Lidar_destroy();
    public static native void c_Lidar_start();
    public static native void c_Lidar_stop();
    public static native Lidar.ScanData c_Lidar_getData();
    public static native void c_Lidar_enableFilter(int filter, boolean enable);
    public static native void c_Lidar_clusterConfig(float distanceThreshold, int minClusterSize);
    public static native void c_Lidar_kalmanConfig(float q, float r, float p);
    public static native void c_Lidar_movingAverageConfig(int windowSize);
    public static native void c_Lidar_medianConfig(int windowSize);
    public static native void c_Lidar_jitterConfig(float jitterThreshold);


}