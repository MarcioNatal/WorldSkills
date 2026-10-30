package com.studica.frc;

import com.studica.frc.jni.TitanJNI;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.CounterBase;

/**
 * Motor controller features for Studica Titan Quad motor controllers
 */
public class Titan {

    // Constants 

    /** Default PWM frequency used when none is specified (Hz). */
    public static final int DEFAULT_FREQUENCY = 15600;

    /** Total number of motor outputs on the Titan Quad. */
    public static final int NUM_MOTORS = 4;

    /** Total number of encoder inputs on the Titan Quad. */
    public static final int NUM_ENCODERS = 4;

    /** PID mode: no internal closed loop, output duty is driven directly. */
    public static final int PID_TYPE_OFF = 0;

    /** PID mode: legacy user Kp/Ki/Kd path. */
    public static final int PID_TYPE_LEGACY = 1;

    /** PID mode: MCV2 cascade control. */
    public static final int PID_TYPE_MCV2 = 2;

    // Instance state 

    /** Native CAN handle. Package-private so Motor/Encoder can share it. */
    final long m_handle;

    /** Cached per-motor objects — created by {@link #getMotor(int)}. */
    private final Motor[] motors = new Motor[NUM_MOTORS];

    /** Cached per-encoder objects — created by {@link #getEncoder(int, double)}. */
    private final Encoder[] encoders = new Encoder[NUM_ENCODERS];

    // Populated device-info cache (populated on first request)
    private int canID = 0;
    private String fwVersion = null;
    private String hwVersion = null;

    // Constructors 

    /**
     * Initialise the Titan Quad with a custom PWM frequency.
     * All four motors are configured to the same frequency; individual motors
     * can be reconfigured afterwards via {@link Motor#configure(int)}.
     *
     * @param deviceID  CAN device ID of the controller [1, 63]
     * @param frequency PWM frequency for all motors [0, 20000] Hz
     */
    public Titan(int deviceID, int frequency)
    {
        m_handle = TitanJNI.c_Titan_create(deviceID, frequency);
    }

    /**
     * Initialise the Titan Quad with the default PWM frequency
     * ({@value #DEFAULT_FREQUENCY} Hz).
     *
     * @param deviceID CAN device ID of the controller [1, 63]
     */
    public Titan(int deviceID)
    {
        this(deviceID, DEFAULT_FREQUENCY);
    }

    // Factory methods

    /**
     * Return the {@link Motor} object for the given motor index.
     * Objects are cached — calling this multiple times with the same index
     * returns the same instance.
     *
     * @param motorIndex Motor index [0, 3]
     * @return {@link Motor} for that output, implementing {@link SpeedController}
     * @throws IllegalArgumentException if motorIndex is outside [0, 3]
     */
    public Motor getMotor(int motorIndex)
    {
        checkMotorIndex(motorIndex);
        if (motors[motorIndex] == null) {
            motors[motorIndex] = new Motor(motorIndex);
        }
        return motors[motorIndex];
    }

    /**
     * Return the {@link Encoder} object for the given encoder index.
     * Objects are cached — calling this multiple times with the same index
     * returns the same instance. The {@code distPerTick} supplied on the
     * <em>first</em> call is stored in the native driver and used for all
     * subsequent distance calculations.
     *
     * @param encoderIndex Encoder index [0, 3]
     * @param distPerTick  Distance per encoder tick (any unit, e.g. metres)
     * @return {@link Encoder} for that input, implementing {@link CounterBase}
     * @throws IllegalArgumentException if encoderIndex is outside [0, 3]
     */
    public Encoder getEncoder(int encoderIndex, double distPerTick)
    {
        checkEncoderIndex(encoderIndex);
        if (encoders[encoderIndex] == null) {
            TitanJNI.c_Titan_setEncoder(m_handle, encoderIndex, distPerTick);
            encoders[encoderIndex] = new Encoder(encoderIndex);
        }
        return encoders[encoderIndex];
    }

    // Controller-level accessors

    /**
     * @return CAN device ID reported by the controller
     */
    public int getID()
    {
        populateDeviceInfo();
        return canID;
    }

    /**
     * @return PWM frequency currently set on the controller (Hz)
     */
    public int getFrequency()
    {
        return TitanJNI.c_Titan_getFrequency(m_handle);
    }

    /**
     * @return Firmware version string, e.g. {@code "Firmware Version: [1.2.3]"}
     */
    public String getFirmwareVersion()
    {
        populateDeviceInfo();
        return fwVersion;
    }

    /**
     * @return Hardware description, e.g. {@code "Hardware: Titan Quad, Version: 2"}
     */
    public String getHardwareVersion()
    {
        populateDeviceInfo();
        return hwVersion;
    }

    /**
     * @return 96-bit factory serial number as {@code "XXXXXXXX-XXXXXXXX-XXXXXXXX"}
     */
    public String getSerialNumber()
    {
        return TitanJNI.c_Titan_getSerialNumber(m_handle);
    }

    /**
     * @return MCU die temperature in °C
     */
    public double getControllerTemp()
    {
        return TitanJNI.c_Titan_getControllerTemp(m_handle);
    }

    /**
     * Stop the background thread and release the CAN handle.
     * The object should not be used after this call.
     */
    public void destroy()
    {
        TitanJNI.c_Titan_destroy(m_handle);
    }

    // Titan 2.0 controller-level methods
    // Requires firmware >= 2.0.0

    /**
     * Set the global motor stop behaviour for all channels.
     *
     * @param mode Stop mode (device-defined)
     */
    public void setMotorStopMode(int mode)
    {
        TitanJNI.c_Titan_setMotorStopMode(m_handle, mode);
    }

    /**
     * Select the PID algorithm type on all four motor channels.
     *
     * @param type {@link #PID_TYPE_OFF}, {@link #PID_TYPE_LEGACY} or
     *             {@link #PID_TYPE_MCV2}
     */
    public void setPIDType(int type)
    {
        TitanJNI.c_Titan_setPIDType(m_handle, type);
    }

    /**
     * Trigger PID autotune on all four motor channels simultaneously.
     * Motors must be free-spinning during autotune.
     */
    public void autotuneAll()
    {
        TitanJNI.c_Titan_autotuneAll(m_handle);
    }

    /**
     * Set how often the controller transmits its periodic sensor frames
     * (RPM, encoder, limit switch, Cypher). Clamped to the firmware minimum
     * of 5 ms and persisted to EEPROM, so it only needs setting when the rate
     * must change.
     *
     * @param periodMs Transmit period in milliseconds (&gt;= 5)
     */
    public void setCANSensorOsDelay(int periodMs)
    {
        TitanJNI.c_Titan_setCANSensorOsDelay(m_handle, periodMs);
    }

    /**
     * Read back the current closed-loop target RPM for all four motors from
     * their periodic frames. Non-blocking.
     *
     * @return double[4] with target RPMs for motors 0-3, or {@code null} if any
     *         channel has not reported yet
     */
    public double[] getTargetRPMFromDevice()
    {
        return TitanJNI.c_Titan_getTargetRPMFromDevice(m_handle);
    }

    // Private helpers

    /** Populate the device-info cache on first request (avoids repeated CAN reads). */
    private void populateDeviceInfo()
    {
        if (fwVersion == null || hwVersion == null || canID == 0) {
            canID     = TitanJNI.c_Titan_getID(m_handle);
            fwVersion = TitanJNI.c_Titan_getFirmwareVersion(m_handle);
            hwVersion = TitanJNI.c_Titan_getHardwareVersion(m_handle);
        }
    }

    private static void checkMotorIndex(int index) {
        if (index < 0 || index >= NUM_MOTORS) {
            throw new IllegalArgumentException(
                "Motor index must be in [0, " + (NUM_MOTORS - 1) + "], got: " + index);
        }
    }

    private static void checkEncoderIndex(int index) {
        if (index < 0 || index >= NUM_ENCODERS) {
            throw new IllegalArgumentException(
                "Encoder index must be in [0, " + (NUM_ENCODERS - 1) + "], got: " + index);
        }
    }

    // =========================================================================
    // Inner class: Motor
    // =========================================================================

    /**
     * Represents one motor output on the Titan Quad controller.
     * <p>
     * Obtain an instance via {@link Titan#getMotor(int)} rather than
     * constructing directly.
     * <p>
     * Implements {@link SpeedController} so it can be used anywhere WPILib
     * expects a speed controller (PID, drive trains, etc.).
     */
    public class Motor implements SpeedController {

        private final int motorIndex;
        private double speed = 0.0;
        private boolean isInverted = false;
        private boolean invertRPM = false;

        Motor(int motorIndex) {
            this.motorIndex = motorIndex;
        }

        // SpeedController

        /**
         * Set the motor output speed.
         *
         * @param speed Normalised speed [-1.0, 1.0]; 0.0 = stopped
         */
        @Override
        public void set(double speed)
        {
            double applied = isInverted ? -speed : speed;
            this.speed = applied;
            TitanJNI.c_Titan_setMotorSpeed(m_handle, motorIndex, applied);
        }

        /**
         * @return last speed value sent to this motor (after inversion is applied)
         */
        @Override
        public double get()
        {
            return speed;
        }

        @Override
        public void setInverted(boolean isInverted)
        {
            this.isInverted = isInverted;
        }

        @Override
        public boolean getInverted()
        {
            return isInverted;
        }

        /** Disable this motor output (sets it to the controller's disabled state). */
        @Override
        public void disable()
        {
            TitanJNI.c_Titan_disableMotor(m_handle, motorIndex);
        }

        /** Stop this motor by setting its speed to 0. */
        @Override
        public void stopMotor()
        {
            set(0.0);
        }

        @Override
        public void pidWrite(double output)
        {
            set(output);
        }

        // Motor-specific

        /**
         * Configure this motor's PWM frequency independently of the others.
         *
         * @param frequency PWM frequency [0, 20000] Hz
         */
        public void configure(int frequency)
        {
            TitanJNI.c_Titan_configureMotor(m_handle, motorIndex, frequency);
        }

        /**
         * Invert the sign of RPM readings returned by {@link #getRPM()}.
         * Does not affect motor direction — use {@link #setInverted(boolean)} for that.
         */
        public void invertRPM()
        {
            invertRPM = true;
        }

        /**
         * @return Current shaft speed in RPM, to 0.01 RPM resolution
         *         (sign may be inverted by {@link #invertRPM()})
         */
        public double getRPM()
        {
            double rpm = TitanJNI.c_Titan_getRPM(m_handle, motorIndex);
            return invertRPM ? -rpm : rpm;
        }

        /**
         * Non-blocking RPM read.
         * Returns the RPM value if a CAN frame was available, or
         * {@link Double#NaN} if the controller had no frame yet.
         *
         * @return signed RPM, or {@code Double.NaN} if no data available
         */
        public double tryGetRPM()
        {
            double[] buf = new double[1];
            boolean ok = TitanJNI.c_Titan_tryGetRPM(m_handle, motorIndex, buf);
            if (!ok) return Double.NaN;
            return invertRPM ? -buf[0] : buf[0];
        }

        /**
         * Non-blocking read of this motor's closed-loop target RPM, as reported
         * by the controller.
         * Requires firmware &gt;= 2.0.0.
         *
         * @return signed target RPM, or {@code Double.NaN} if no data available
         */
        public double tryGetTargetRPM()
        {
            double[] buf = new double[1];
            boolean ok = TitanJNI.c_Titan_tryGetTargetRPM(m_handle, motorIndex, buf);
            return ok ? buf[0] : Double.NaN;
        }

        /**
         * Read a limit switch attached to this motor's channel.
         *
         * @param direction {@code false} = forward limit, {@code true} = reverse limit
         * @return 0 = open, 1 = closed
         */
        public int getLimitSwitch(boolean direction)
        {
            return TitanJNI.c_Titan_getLimitSwitch(m_handle, motorIndex, direction);
        }

        /**
         * Read the Cypher Max absolute-encoder position for this motor's port. Requires a Cypher Max to be set using the Studica Hardware Manager and a Cypher Max to be plugged into the motor port instead of the motors Encoder.
         *
         * @return Position in degrees [0 - 360]
         */
        public double getCypherPosition() 
        {
            return TitanJNI.c_Titan_getCypherPosition(m_handle, motorIndex);
        }

        /**
         * @return Motor index [0, 3] within the controller
         */
        public int getMotorIndex()
        {
            return motorIndex;
        }

        /**
         * Set closed-loop target velocity (RPM).
         * Negative values command reverse rotation.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param velocityRpm Target RPM (signed)
         */
        public void setTargetVelocity(float velocityRpm)
        {
            TitanJNI.c_Titan_setTargetVelocity(m_handle, motorIndex, velocityRpm);
        }

        /**
         * Set closed-loop target distance in encoder counts.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param distanceCounts Signed encoder tick target
         */
        public void setTargetDistance(int distanceCounts)
        {
            TitanJNI.c_Titan_setTargetDistance(m_handle, motorIndex, distanceCounts);
        }

        /**
         * Set closed-loop target angle for a Cypher Max equipped motor.
         * Clamped to [0.0, 360.0] degrees.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param angleDeg Target angle in degrees
         */
        public void setTargetAngle(double angleDeg)
        {
            TitanJNI.c_Titan_setTargetAngle(m_handle, motorIndex, angleDeg);
        }

        /**
         * Enable or disable position-hold mode.
         * When enabled the controller actively maintains the last commanded position.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param hold {@code true} to hold, {@code false} to release
         */
        public void setPositionHold(boolean hold)
        {
            TitanJNI.c_Titan_setPositionHold(m_handle, motorIndex, hold);
        }

        /**
         * Set velocity-control sensitivity for this motor.
         * Higher values increase responsiveness but may cause oscillation.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param sensitivity Sensitivity level [0, 10]
         */
        public void setSensitivity(int sensitivity)
        {
            if (sensitivity < 0 || sensitivity > 10) {
                System.out.println("[Titan] Sensitivity must be in [0, 10], got: " + sensitivity);
                return;
            }
            TitanJNI.c_Titan_setSensitivity(m_handle, motorIndex, sensitivity);
        }

        /**
         * Set the current limit for this motor channel.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param limitAmps Maximum current in amperes (&gt;= 0)
         */
        public void setCurrentLimit(float limitAmps)
        {
            TitanJNI.c_Titan_setCurrentLimit(m_handle, motorIndex, limitAmps);
        }

        /**
         * Set the current-limit enforcement mode for this motor channel.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param mode 0 = disabled, 1 = enabled
         */
        public void setCurrentLimitMode(int mode)
        {
            TitanJNI.c_Titan_setCurrentLimitMode(m_handle, motorIndex, mode);
        }

        /**
         * Set the encoder counts-per-revolution (CPR) for this motor's channel.
         * Used by the device to scale PID calculations.
         * It is better to set this in the Studica Hardware Manager.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param cpr Counts per revolution
         */
        public void setEncoderResolution(int cpr)
        {
            TitanJNI.c_Titan_setEncoderResolution(m_handle, motorIndex, cpr);
        }

        /**
         * Select the PID algorithm type for this motor channel only.
         * Requires firmware &gt;= 2.0.0.
         *
         * @param type {@link Titan#PID_TYPE_OFF}, {@link Titan#PID_TYPE_LEGACY}
         *             or {@link Titan#PID_TYPE_MCV2}
         */
        public void setPIDType(int type)
        {
            TitanJNI.c_Titan_setMotorPIDType(m_handle, motorIndex, type);
        }

        /**
         * Trigger PID autotune on this motor channel only.
         * The motor must be free-spinning during autotune.
         * Requires firmware &gt;= 2.0.0.
         */
        public void autotune()
        {
            TitanJNI.c_Titan_autotuneMotor(m_handle, motorIndex);
        }
    }

    // =========================================================================
    // Inner class: Encoder
    // =========================================================================

    /**
     * Represents one encoder input on the Titan Quad controller.
     * <p>
     * Obtain an instance via {@link Titan#getEncoder(int, double)} rather than
     * constructing directly.
     * <p>
     * Implements {@link CounterBase} for WPILib compatibility.
     */
    public class Encoder implements CounterBase {

        private final int encoderIndex;
        private boolean reverseDirection = false;
        private boolean invertSpeed = false;

        Encoder(int encoderIndex) {
            this.encoderIndex = encoderIndex;
        }

        // Distance 

        /**
         * Return the accumulated travel distance in the units configured by
         * the {@code distPerTick} value supplied to {@link Titan#getEncoder}.
         *
         * @return Distance (sign may be reversed by {@link #setReverseDirection()})
         */
        public double getDistance()
        {
            double d = TitanJNI.c_Titan_getEncoderDistance(m_handle, encoderIndex);
            return reverseDirection ? -d : d;
        }

        /**
         * Return the raw signed tick count from the controller.
         *
         * @return Raw encoder tick count
         */
        public double getRaw()
        {
            return TitanJNI.c_Titan_getEncoderRaw(m_handle, encoderIndex);
        }

        /**
         * Return the raw signed tick count as an {@code int}.
         * More precise than {@link #getRaw()} as it avoids double conversion.
         *
         * @return Signed 32-bit encoder tick count
         */
        public int getCount()
        {
            return TitanJNI.c_Titan_getEncoderCount(m_handle, encoderIndex);
        }

        // Speed

        /**
         * Invert the sign of speed readings returned by {@link #getSpeed()}.
         */
        public void invertSpeed()
        {
            invertSpeed = true;
        }

        /**
         * @return Encoder shaft speed in RPM, to 0.01 RPM resolution
         *         (sign may be inverted by {@link #invertSpeed()})
         */
        public double getSpeed()
        {
            double s = TitanJNI.c_Titan_getEncoderSpeed(m_handle, encoderIndex);
            return invertSpeed ? -s : s;
        }

        // Direction

        /**
         * Reverse the sign of all distance readings from this encoder.
         * Equivalent to mounting the encoder on the opposite side of the shaft.
         */
        public void setReverseDirection()
        {
            reverseDirection = true;
        }

        /**
         * @return encoder index [0, 3] within the controller
         */
        public int getEncoderIndex()
        {
            return encoderIndex;
        }

        /**
         * Returns 0 — use {@link #getRaw()} for the actual tick count.
         *
         * @return 0
         */
        @Override
        public int get()
        {
            return 0;
        }

        /** Zero the encoder tick count on the controller. */
        @Override
        public void reset()
        {
            TitanJNI.c_Titan_resetEncoder(m_handle, encoderIndex);
        }

        /**
         * Not implemented.
         *
         * @return 0
         */
        @Override
        public double getPeriod()
        {
            return 0;
        }

        /** Not implemented. */
        @Override
        public void setMaxPeriod(double maxPeriod) { }

        /**
         * Not implemented.
         *
         * @return false
         */
        @Override
        public boolean getStopped()
        {
            return false;
        }

        /**
         * @return {@code true} if reverse direction is active
         */
        @Override
        public boolean getDirection()
        {
            return reverseDirection;
        }
    }
}