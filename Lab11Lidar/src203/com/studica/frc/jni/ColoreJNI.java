package com.studica.frc.jni;

import com.studica.frc.Colore;

/**
 * JNI wrapper for the Colore colour sensor native driver.
 */
public class ColoreJNI extends StudicaJNIWrapper {

    /**
     * Open a Colore and start its receive stream.
     *
     * @param canID CAN ID [0, 63]
     * @return      Native instance handle, or 0 on failure
     */
    public static native long c_Colore_create(int canID);

    /**
     * Open a Colore over USB CDC serial and start streaming.
     *
     * @param devicePath Serial device path
     * @param sampleMs   Sample interval in ms; &lt;= 0 leaves the device default
     * @return           Native instance handle, or 0 on failure
     */
    public static native long c_Colore_createUsb(String devicePath, int sampleMs);

    /**
     * Resolve a USB port index to a device path.
     *
     * @param usbPort 2 = kUSB1, 3 = kUSB2
     * @return        Device path
     */
    public static native String c_Colore_findDevicePath(int usbPort);

    /**
     * Close the transport and release the instance.
     *
     * @param handle Native handle returned by one of the create functions
     */
    public static native void c_Colore_destroy(long handle);

    /**
     * Read the most recent colour telemetry frame. Non-blocking.
     *
     * @param handle Native handle
     * @return       Latest CIE-XYZ reading, or {@code null} if none has arrived
     */
    public static native Colore.ColorData c_Colore_getColor(long handle);

    /**
     * Select the colour output format.
     *
     * @param handle Native handle
     * @param format One of the Colore.ColorFormat codes
     * @return       true if the device acknowledged
     */
    public static native boolean c_Colore_setColorFormat(long handle, int format);

    /**
     * Set the sample interval.
     *
     * @param handle Native handle
     * @param ms     Sample interval in milliseconds
     * @return       true if the device acknowledged
     */
    public static native boolean c_Colore_setSampleTimeMs(long handle, int ms);

    /**
     * Set the illumination LED brightness. CAN only.
     *
     * @param handle  Native handle
     * @param percent Brightness [0, 100]
     * @return        true if the device acknowledged
     */
    public static native boolean c_Colore_setBrightness(long handle, int percent);

    /**
     * Enable or disable the status LED and set its brightness. CAN only.
     *
     * @param handle     Native handle
     * @param enable     true to light the status LED
     * @param brightness Brightness [0, 100]
     * @return           true if the device acknowledged
     */
    public static native boolean c_Colore_setStatusLed(long handle, boolean enable, int brightness);

    /**
     * Set the distance-measurement mode. CAN only.
     *
     * @param handle  Native handle
     * @param mode    One of the Colore.MeasureMode codes
     * @param zUserMm Fixed target distance in mm; used only by the fixed mode
     * @return        true if the device acknowledged
     */
    public static native boolean c_Colore_setMeasureMode(long handle, int mode, float zUserMm);

    /**
     * Set the fixed sample distance. CAN only.
     *
     * @param handle  Native handle
     * @param zUserMm Distance in millimetres
     * @return        true if the device acknowledged
     */
    public static native boolean c_Colore_setZmm(long handle, float zUserMm);

    /**
     * Enable or disable the USB output override. CAN only.
     *
     * @param handle Native handle
     * @param enable true to let USB take over the output stream
     * @return       true if the device acknowledged
     */
    public static native boolean c_Colore_setUsbOverride(long handle, boolean enable);

    /**
     * Change the device's stored CAN ID. CAN only.
     *
     * @param handle Native handle
     * @param canID  New CAN ID
     * @return       true if the device acknowledged
     */
    public static native boolean c_Colore_setCanId(long handle, int canID);

    /**
     * Read one configuration item back from the device.
     *
     * @param handle   Native handle
     * @param item     Configuration item index
     * @param valueOut long[1] output array receiving the unsigned 32-bit value
     * @return         true if the device acknowledged
     */
    public static native boolean c_Colore_getConfig(long handle, int item, long[] valueOut);

    /**
     * @param handle Native handle
     * @return       CAN ID this instance was opened with, or -1 when on USB
     */
    public static native int c_Colore_getCanID(long handle);

    /**
     * @param handle Native handle
     * @return       0 = CAN, 1 = USB
     */
    public static native int c_Colore_getTransport(long handle);
}
