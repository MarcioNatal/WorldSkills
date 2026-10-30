package com.studica.frc.jni;

import com.studica.frc.LidarMini;

/**
 * JNI wrapper for T-mini Plus native C++ driver.
 * <p>
 * This class declares the native methods that bridge to {@code LidarMiniJNI.cpp}.
 */
public class LidarMiniJNI extends StudicaJNIWrapper
{
    /**
     * Initialises the serial port and configures it for the T-mini Plus
     * (230 400 baud, 8N1).  Does <em>not</em> start scanning.
     *
     * @param port HAL serial port number (USB1 = 2, USB2 = 3)
     * @return native handle, or 0 on failure
     */
    public static native long c_LidarMini_create(int port);

    /**
     * Stops scanning (if active) and closes the serial port.
     *
     * @param handle native handle returned by {@link #c_LidarMini_create}
     */
    public static native void c_LidarMini_destroy(long handle);

    /**
     * Sends the scan-start command to the T-mini Plus and launches
     * the background packet-parsing thread.
     *
     * @param handle native handle returned by {@link #c_LidarMini_create}
     */
    public static native void c_LidarMini_start(long handle);

    /**
     * Sends the stop command to the T-mini Plus and shuts down the
     * background thread.  The serial port remains open.
     *
     * @param handle native handle returned by {@link #c_LidarMini_create}
     */
    public static native void c_LidarMini_stop(long handle);

    /**
     * Increases the scan frequency by one step.
     * <p>
     * Must <em>not</em> be called while the device is in scan mode.
     *
     * @param handle native handle returned by {@link #c_LidarMini_create}
     * @param hz {@code true} → +1 Hz, {@code false} → +0.1 Hz
     */
    public static native void c_LidarMini_increaseFrequency(long handle, boolean hz);

    /**
     * Decreases the scan frequency by one step.
     * <p>
     * Must <em>not</em> be called while the device is in scan mode.
     *
     * @param handle native handle returned by {@link #c_LidarMini_create}
     * @param hz {@code true} → −1 Hz, {@code false} → −0.1 Hz
     */
    public static native void c_LidarMini_decreaseFrequency(long handle, boolean hz);

    /**
     * Queries device information (model, firmware/hardware version, serial number).
     * <p>
     * Must be called while the device is <em>not</em> scanning.
     *
     * @param handle native handle returned by {@link #c_LidarMini_create}
     * @return populated {@link LidarMini.LidarInfo}, or an empty struct on failure
     */
    public static native LidarMini.LidarInfo c_LidarMini_getInfo(long handle);

    /**
     * Queries the device health status.
     * <p>
     * Must be called while the device is <em>not</em> scanning.
     * Real-time health is also embedded in the scan packet CT field and is
     * parsed automatically during scanning.
     *
     * @param handle native handle returned by {@link #c_LidarMini_create}
     * @return populated {@link LidarMini.LidarHealth}, or an empty struct on failure
     */
    public static native LidarMini.LidarHealth c_LidarMini_getHealth(long handle);

    /**
     * Returns a snapshot of the last completed 360° scan.
     * <p>
     * Thread-safe; may return an empty {@link LidarMini.ScanData} if the first
     * full lap has not yet finished.
     *
     * @param handle native handle returned by {@link #c_LidarMini_create}
     * @return {@link LidarMini.ScanData} containing parallel arrays of angle,
     *         distance, and intensity for every sample point in the last lap
     */
    public static native LidarMini.ScanData c_LidarMini_getData(long handle);
}
