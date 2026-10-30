package com.studica.frc.jni;

/**
 * JNI wrapper for the unified Titan motor controller native driver.
 */
public class TitanJNI extends StudicaJNIWrapper {

    /**
     * Initialise the Titan controller, configure all four motors, and start the
     * single background enable/disable thread.
     *
     * @param deviceID  CAN device ID [1, 63]
     * @param frequency PWM frequency for all motors [0, 20000] Hz
     * @return          CAN handle, or 0 on failure
     */
    public static native long c_Titan_create(int deviceID, int frequency);

    /**
     * Stop the background thread and release the CAN handle.
     *
     * @param handle CAN handle returned by {@link #c_Titan_create}
     */
    public static native void c_Titan_destroy(long handle);

    /**
     * @param handle CAN handle
     * @return CAN device ID reported by the controller
     */
    public static native int c_Titan_getID(long handle);

    /**
     * @param handle CAN handle
     * @return PWM frequency currently set on the controller
     */
    public static native int c_Titan_getFrequency(long handle);

    /**
     * @param handle CAN handle
     * @return Firmware version string, e.g. {@code "Firmware Version: [1.2.3]"}
     */
    public static native String c_Titan_getFirmwareVersion(long handle);

    /**
     * @param handle CAN handle
     * @return Hardware description string, e.g. {@code "Hardware: Titan Quad, Version: 2"}
     */
    public static native String c_Titan_getHardwareVersion(long handle);

    /**
     * @param handle CAN handle
     * @return 96-bit serial number as {@code "XXXXXXXX-XXXXXXXX-XXXXXXXX"}
     */
    public static native String c_Titan_getSerialNumber(long handle);

    /**
     * @param handle CAN handle
     * @return MCU die temperature in °C
     */
    public static native double c_Titan_getControllerTemp(long handle);

    /**
     * Set a motor's output speed.
     *
     * @param handle CAN handle
     * @param motor  Motor index [0, 3]
     * @param speed  Normalised speed [-1.0, 1.0]
     */
    public static native void c_Titan_setMotorSpeed(long handle, int motor, double speed);

    /**
     * Configure a motor's PWM frequency.
     *
     * @param handle    CAN handle
     * @param motor     Motor index [0, 3]
     * @param frequency PWM frequency [0, 20000] Hz
     */
    public static native void c_Titan_configureMotor(long handle, int motor, int frequency);

    /**
     * Disable a single motor output.
     *
     * @param handle CAN handle
     * @param motor  Motor index [0, 3]
     */
    public static native void c_Titan_disableMotor(long handle, int motor);

    /**
     * @param handle CAN handle
     * @param motor  Motor index [0, 3]
     * @return signed RPM, or 0.0 if no frame has been received yet
     */
    public static native double c_Titan_getRPM(long handle, int motor);

    /**
     * Read a limit switch state.
     *
     * @param handle    CAN handle
     * @param motor     Motor index [0, 3]
     * @param direction {@code false} = forward limit, {@code true} = reverse limit
     * @return          0 = open, 1 = closed
     */
    public static native int c_Titan_getLimitSwitch(long handle, int motor, boolean direction);

    /**
     * Read an absolute-encoder (Cypher Max) position.
     *
     * @param handle CAN handle
     * @param port   Cypher Max port [0, 3]
     * @return       Position value
     */
    public static native double c_Titan_getCypherPosition(long handle, int port);

    /**
     * Store the distance-per-tick conversion factor for an encoder in the
     * native driver. Must be called before {@link #c_Titan_getEncoderDistance}.
     *
     * @param handle       CAN handle
     * @param encoder      Encoder index [0, 3]
     * @param distPerTick  Distance (any unit) per encoder tick
     */
    public static native void c_Titan_setEncoder(long handle, int encoder, double distPerTick);

    /**
     * @param handle  CAN handle
     * @param encoder Encoder index [0, 3]
     * @return        Raw ticks × distPerTick in the configured unit
     */
    public static native double c_Titan_getEncoderDistance(long handle, int encoder);

    /**
     * Zero an encoder's tick count on the controller.
     *
     * @param handle  CAN handle
     * @param encoder Encoder index [0, 3]
     */
    public static native void c_Titan_resetEncoder(long handle, int encoder);

    /**
     * @param handle  CAN handle
     * @param encoder Encoder index [0, 3]
     * @return        Signed raw tick count
     */
    public static native double c_Titan_getEncoderRaw(long handle, int encoder);

    /**
     * @param handle  CAN handle
     * @param encoder Encoder index [0, 3]
     * @return        Signed speed in RPM
     */
    public static native double c_Titan_getEncoderSpeed(long handle, int encoder);

    /**
     * @param handle  CAN handle
     * @param encoder Encoder index [0, 3]
     * @return        Signed raw tick count as int (no double conversion)
     */
    public static native int c_Titan_getEncoderCount(long handle, int encoder);

    // Titan 2.0 firmware extended API
    // Requires firmware >= 2.0.0

    /**
     * Set closed-loop target velocity. Negative = reverse.
     *
     * @param handle      CAN handle
     * @param motor       Motor index [0, 3]
     * @param velocityRpm Target RPM (signed)
     */
    public static native void c_Titan_setTargetVelocity(long handle, int motor, float velocityRpm);

    /**
     * Set closed-loop target distance in encoder counts.
     *
     * @param handle         CAN handle
     * @param motor          Motor index [0, 3]
     * @param distanceCounts Signed encoder tick target
     */
    public static native void c_Titan_setTargetDistance(long handle, int motor, int distanceCounts);

    /**
     * Set closed-loop target angle [0.0, 360.0] degrees.
     *
     * @param handle   CAN handle
     * @param motor    Motor index [0, 3]
     * @param angleDeg Target angle in degrees
     */
    public static native void c_Titan_setTargetAngle(long handle, int motor, double angleDeg);

    /**
     * Enable or disable position-hold mode for a motor.
     *
     * @param handle CAN handle
     * @param motor  Motor index [0, 3]
     * @param hold   true to hold position, false to release
     */
    public static native void c_Titan_setPositionHold(long handle, int motor, boolean hold);

    /**
     * Set the encoder counts-per-revolution (CPR).
     *
     * @param handle  CAN handle
     * @param channel Encoder/motor channel [0, 3]
     * @param cpr     Counts per revolution
     */
    public static native void c_Titan_setEncoderResolution(long handle, int channel, int cpr);

    /**
     * Set the motor current limit in amperes.
     *
     * @param handle    CAN handle
     * @param channel   Motor channel [0, 3]
     * @param limitAmps Current limit in amperes
     */
    public static native void c_Titan_setCurrentLimit(long handle, int channel, float limitAmps);

    /**
     * Set the current-limit mode.
     *
     * @param handle  CAN handle
     * @param channel Motor channel [0, 3]
     * @param mode    0 = disabled, 1 = enabled
     */
    public static native void c_Titan_setCurrentLimitMode(long handle, int channel, int mode);

    /**
     * Set the global motor stop behaviour mode.
     *
     * @param handle CAN handle
     * @param mode   Stop mode byte (device-defined)
     */
    public static native void c_Titan_setMotorStopMode(long handle, int mode);

    /**
     * Select the PID algorithm type on every channel (broadcast).
     *
     * @param handle CAN handle
     * @param type   0 = off (open loop), 1 = legacy Kp/Ki/Kd, 2 = MCV2 cascade
     */
    public static native void c_Titan_setPIDType(long handle, int type);

    /**
     * Select the PID algorithm type for a single motor channel.
     *
     * @param handle CAN handle
     * @param motor  Motor index [0, 3]
     * @param type   0 = off (open loop), 1 = legacy Kp/Ki/Kd, 2 = MCV2 cascade
     */
    public static native void c_Titan_setMotorPIDType(long handle, int motor, int type);

    /**
     * Trigger PID autotune on all four channels simultaneously.
     *
     * @param handle CAN handle
     */
    public static native void c_Titan_autotuneAll(long handle);

    /**
     * Trigger PID autotune on a single motor channel.
     *
     * @param handle CAN handle
     * @param motor  Motor index [0, 3]
     */
    public static native void c_Titan_autotuneMotor(long handle, int motor);

    /**
     * Set the device's CAN sensor transmit period in milliseconds.
     * Clamped to the firmware minimum of 5 ms. Persisted to EEPROM.
     *
     * @param handle   CAN handle
     * @param periodMs Transmit period in milliseconds
     */
    public static native void c_Titan_setCANSensorOsDelay(long handle, int periodMs);

    /**
     * Set velocity-control sensitivity for a motor.
     *
     * @param handle      CAN handle
     * @param motor       Motor index [0, 3]
     * @param sensitivity Sensitivity level [0, 10]
     */
    public static native void c_Titan_setSensitivity(long handle, int motor, int sensitivity);

    /**
     * Non-blocking RPM read. Writes the result into {@code outRpm[0]}.
     *
     * @param handle CAN handle
     * @param motor  Motor index [0, 3]
     * @param outRpm double[1] output array
     * @return       {@code true} if a valid CAN frame was available
     */
    public static native boolean c_Titan_tryGetRPM(long handle, int motor, double[] outRpm);

    /**
     * Non-blocking closed-loop target RPM read for one motor.
     * Writes the result into {@code outRpm[0]}.
     *
     * @param handle CAN handle
     * @param motor  Motor index [0, 3]
     * @param outRpm double[1] output array
     * @return       {@code true} if a valid CAN frame was available
     */
    public static native boolean c_Titan_tryGetTargetRPM(long handle, int motor, double[] outRpm);

    /**
     * Read back the target RPM for all four motors from their periodic frames.
     *
     * @param handle CAN handle
     * @return double[4] with target RPMs for motors 0-3, or {@code null} if any
     *         channel had no frame available
     */
    public static native double[] c_Titan_getTargetRPMFromDevice(long handle);
}