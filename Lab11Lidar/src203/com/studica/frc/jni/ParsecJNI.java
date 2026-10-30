package com.studica.frc.jni;

import com.studica.frc.Parsec;

/**
 * JNI wrapper for the Parsec time-of-flight sensor native driver.
 */
public class ParsecJNI extends StudicaJNIWrapper {

    /**
     * Open a Parsec on the given CAN node ID and start its receive stream.
     *
     * @param canID Parsec CAN node ID [0, 63]
     * @return      Native instance handle, or 0 on failure
     */
    public static native long c_Parsec_create(int canID);

    /**
     * Open a Parsec over USB CDC serial and start streaming binary frames.
     *
     * @param devicePath Serial device path
     * @param odrHz      Output data rate in Hz; &lt;= 0 leaves the device default
     * @return           Native instance handle, or 0 on failure
     */
    public static native long c_Parsec_createUsb(String devicePath, int odrHz);

    /**
     * Resolve a USB port index to a device path.
     *
     * @param usbPort 2 = kUSB1, 3 = kUSB2
     * @return        Device path
     */
    public static native String c_Parsec_findDevicePath(int usbPort);

    /**
     * Close the transport and release the instance.
     *
     * @param handle Native handle returned by one of the create functions
     */
    public static native void c_Parsec_destroy(long handle);

    /**
     * Drain the receive stream and reassemble the most complete distance frame.
     *
     * @param handle   Native handle
     * @param maxZones Zones to reassemble [1, 64]
     * @return         Reassembled scan, or {@code null} on failure
     */
    public static native Parsec.ScanData c_Parsec_getData(long handle, int maxZones);

    /**
     * Request the device configuration and wait for the response.
     *
     * @param handle    Native handle
     * @param timeoutMs Milliseconds to wait for the reply
     * @return          Device configuration, or {@code null} on failure
     */
    public static native Parsec.Config c_Parsec_getConfig(long handle, int timeoutMs);

    /**
     * @param handle Native handle
     * @return       CAN node ID this instance was opened on, or -1 when on USB
     */
    public static native int c_Parsec_getCanID(long handle);

    /**
     * @param handle Native handle
     * @return       0 = CAN, 1 = USB
     */
    public static native int c_Parsec_getTransport(long handle);
}
