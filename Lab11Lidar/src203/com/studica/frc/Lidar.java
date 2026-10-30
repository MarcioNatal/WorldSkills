package com.studica.frc;

import com.studica.frc.jni.LidarJNI;

/**
 * Lidar class Java documentation
 */
public class Lidar
{
    /**
     * kUSB1 - Top USB2.0 port
     * <p>
     * kUSB2 - Bottom USB2.0 port
     */
    public enum Port {
        kUSB1(2),
        kUSB2(3);

        public final int value;

        Port(int value) {
            this.value = value;
        }
    }

    /**
     * kCLUSTER - Cluster Filter
     * <p>
     * kKALMAN - Kalman Filter
     * <p>
     * kMOVINGAVERAGE - Moving Average Filter
     * <p>
     * kMEDIAN - Median Filter
     * <p>
     * kJITTER - Jitter Filter
     */
    public enum Filter {
        kCLUSTER(0),
        kKALMAN(1),
        kMOVINGAVERAGE(2),
        kMEDIAN(3),
        kJITTER(4);

        public final int value;

        Filter(int value) {
            this.value = value;
        }
    }

    /**
     * Class to receive Lidar data.
     * Contains two float arrays for angle data and distance data respectively.
     */
    public static class ScanData {
        public float[] angle;
        public float[] distance;
    }

    /**
     * Constructor for Lidar
     * @param port - Location of USB2.0 port
     */
    public Lidar(Port port)
    {
        LidarJNI.c_Lidar_create(port.value);
    }

    /**
    * Starts the Lidar sensor
    * The constructor by default starts the Lidar sensor
    */
    public void start() 
    {
        LidarJNI.c_Lidar_start();
    }

    /**
     * Stops the Lidar sensor 
     */
    public void stop()
    {
        LidarJNI.c_Lidar_stop();
    }

    /**
     * Gets angle and distance output of Lidar sensor 
     * @return Data struct that holds angle and distance
     */
    public Lidar.ScanData getData()
    {
        return LidarJNI.c_Lidar_getData();
    }

    /**
     * Enables the cluster filter on distance in ScanData
     * @param filter filter to enable, 0 = cluster, 1 = kalman
     * @param enable flag to enable or disable filter
     */
    public void enableFilter(Filter filter, boolean enable)
    {
        LidarJNI.c_Lidar_enableFilter(filter.value, enable);
    }

    /**
     * Configure the Cluster Filter
     * @param distanceThreshold The threshold of the distances in the cluster in mm
     * @param minClusterSize the minimum number of elements in the cluster 
     * <p>
     * defaults: 50.0mm, 5 
     */
    public void clusterConfig(float distanceThreshold, int minClusterSize)
    {
        if (minClusterSize > 10 || minClusterSize < 1)
        {
            System.out.println("Min Cluster Size is out of bounds must be 0 < x < 11");
        }
        else
        {
            LidarJNI.c_Lidar_clusterConfig(distanceThreshold, minClusterSize);
        }
    }

    /**
     * Configure the Kalman Filter
     * @param q - Process Noise
     * @param r - Measurement Noise
     * @param p - Initial Estimate Error
     * <p>
     * defaults: 1e-5, 1e-1, 1.0
     */
    public void kalmanConfig(float q, float r, float p)
    {
        LidarJNI.c_Lidar_kalmanConfig(q, r, p);
    }

    /**
     * Configure the moving average Filter
     * @param windowSize range 1 - 10
     * <p>
     * Default = 5
     */
    public void movingAverageConfig(int windowSize)
    {
        if (windowSize > 10 || windowSize < 1)
        {
            System.out.println("Window Size is out of bounds must be 0 < x < 11");
        }
        else
        {
            LidarJNI.c_Lidar_movingAverageConfig(windowSize);
        }
    }

    /**
     * Configure the median filter
     * @param windowSize range 1 - 10
     * <p>
     * Default = 5
     */
    public void medianConfig(int windowSize)
    {
        if (windowSize > 10 || windowSize < 1)
        {
            System.out.println("Window Size is out of bounds must be 0 < x < 11");
        }
        else
        {
            LidarJNI.c_Lidar_medianConfig(windowSize);
        }
    }

    /**
     * Configure the jitter filter
     * @param jitterThreshold - threshold in mm to test for jitter
     * <p>
     * Default = 50mm
     */
    public void jitterConfig(float jitterThreshold)
    {
        LidarJNI.c_Lidar_jitterConfig(jitterThreshold);
    }
}