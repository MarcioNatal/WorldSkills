package com.studica.frc;

import com.studica.frc.jni.LidarMiniJNI;

/**
 * Java driver for the <b>T-mini Plus</b> 360° laser range scanner.
 */
public class LidarMini
{
    /**
     * Physical USB port location
     * <p>
     * {@code kUSB1} – Top USB 2.0 port
     * <br>
     * {@code kUSB2} – Bottom USB 2.0 port
     */
    public enum Port
    {
        kUSB1(2),
        kUSB2(3);

        public final int value;

        Port(int value) { this.value = value; }
    }

    /**
     * A single completed 360° scan from the T-mini Plus.
     * <p>
     * All three arrays are parallel and have the same length ({@link #size()}).
     * <ul>
     *   <li>{@code angle[i]}     – degrees, range [0, 360]</li>
     *   <li>{@code distance[i]}  – metres in [0.05, 12.0], or {@code -1.0} for
     *                              out-of-range / interference-flagged samples</li>
     *   <li>{@code intensity[i]} – raw reflectance value [0, 255]</li>
     *   <li>{@code timestamp}    – nanoseconds from the system steady clock at
     *                              the moment the lap started</li>
     * </ul>
     */
    public static class ScanData
    {
        /** Angle in degrees [0, 360) for each sample point. */
        public float[] angle;

        /**
         * Distance in metres for each sample point.
         * {@code -1.0} means out-of-range or hardware-flagged interference.
         */
        public float[] distance;

        /** Raw reflectance intensity [0, 255] for each sample point. */
        public float[] intensity;

        /** Nanosecond timestamp (steady clock) when this lap began. */
        public long timestamp;

        /**
         * Returns the number of sample points in this scan.
         *
         * @return number of points, or 0 if arrays have not been populated yet
         */
        public int size()
        {
            return (angle != null) ? angle.length : 0;
        }

        /**
         * Returns the distance (metres) at the closest angle to {@code targetDeg},
         * or {@code -1.0} if no valid sample is available near that angle.
         *
         * @param targetDeg  desired angle in degrees [0, 360]
         * @param toleranceDeg  maximum angular deviation to accept
         * @return distance in metres, or {@code -1.0}
         */
        public float getDistanceAt(float targetDeg, float toleranceDeg)
        {
            if (size() == 0) return -1.0f;

            float bestDist  = -1.0f;
            float bestDelta = Float.MAX_VALUE;

            for (int i = 0; i < angle.length; i++) {
                // Angular difference with wrap-around
                float delta = Math.abs(angle[i] - targetDeg);
                if (delta > 180.0f) delta = 360.0f - delta;

                if (delta <= toleranceDeg && delta < bestDelta && distance[i] > 0.0f) {
                    bestDelta = delta;
                    bestDist  = distance[i];
                }
            }
            return bestDist;
        }
    }

    /**
     * Device information.
     * <p>
     * The T-mini Plus model code is {@code 151}.
     */
    public static class LidarInfo
    {
        /**
         * Device model code.  T-mini Plus = 151.
         */
        public int model;

        /**
         * Combined firmware version word (little-endian: low byte = major,
         * high byte = minor).  Use {@link #getFirmwareMajor()} /
         * {@link #getFirmwareMinor()} for human-readable values.
         */
        public int firmwareVersion;

        /** Hardware revision number. */
        public int hardwareVersion;

        /** 16-byte factory serial number. */
        public byte[] serialnum;

        /**
         * Returns the firmware major version number.
         *
         * @return firmware major version
         */
        public int getFirmwareMajor()  { return firmwareVersion & 0xFF; }

        /**
         * Returns the firmware minor version number.
         *
         * @return firmware minor version
         */
        public int getFirmwareMinor()  { return (firmwareVersion >> 8) & 0xFF; }

        /**
         * Returns the serial number as a hex string (32 hex characters).
         *
         * @return 32-character uppercase hex string, or empty string if serialnum is null
         */
        public String getSerialString()
        {
            if (serialnum == null) return "";
            StringBuilder sb = new StringBuilder(32);
            for (byte b : serialnum) {
                sb.append(String.format("%02X", b & 0xFF));
            }
            return sb.toString();
        }

        @Override
        public String toString()
        {
            return String.format(
                "LidarInfo{model=%d, firmware=%d.%d, hardware=%d, serial=%s}",
                model, getFirmwareMajor(), getFirmwareMinor(),
                hardwareVersion, getSerialString());
        }
    }

    /**
     * Device health status.
     *
     * <h3>Status byte bit map</h3>
     * <table border="1">
     *   <caption>Health status bit map</caption>
     *   <tr><th>Bit</th><th>Module</th><th>0</th><th>1</th></tr>
     *   <tr><td>0</td><td>Sensor (image sensor)</td><td>Normal</td><td>Abnormal</td></tr>
     *   <tr><td>1</td><td>Encoder (angle detection)</td><td>Normal</td><td>Abnormal</td></tr>
     *   <tr><td>2</td><td>Wireless power supply</td><td>Normal</td><td>Abnormal</td></tr>
     *   <tr><td>3</td><td>PD (laser feedback voltage)</td><td>Normal</td><td>Abnormal</td></tr>
     *   <tr><td>4</td><td>LD (laser drive current)</td><td>Normal</td><td>Abnormal</td></tr>
     *   <tr><td>5</td><td>Data (scan data validity)</td><td>Normal</td><td>Abnormal</td></tr>
     * </table>
     * <p>
     * Note: Bits 3 and 4 (PD/LD) are only sampled once at power-on.
     * Bits 0, 1, and 5 are updated in real time during scanning.
     */
    public static class LidarHealth
    {
        /** Raw status byte.  Each bit represents a module; 0 = normal, 1 = abnormal. */
        public int status;

        /** Extended error code (consult the T-mini Plus development manual). */
        public int errorCode;

        // ── Convenience accessors ──────────────────────────────────────────

        /** @return {@code true} if the image sensor is operating normally. */
        public boolean isSensorOk()          { return (status & (1 << 0)) == 0; }

        /** @return {@code true} if the angle-detection encoder is operating normally. */
        public boolean isEncoderOk()         { return (status & (1 << 1)) == 0; }

        /** @return {@code true} if the wireless power supply is operating normally. */
        public boolean isWirelessPowerOk()   { return (status & (1 << 2)) == 0; }

        /** @return {@code true} if the laser feedback voltage (PD) is normal. */
        public boolean isPDOk()              { return (status & (1 << 3)) == 0; }

        /** @return {@code true} if the laser drive current (LD) is normal. */
        public boolean isLDOk()              { return (status & (1 << 4)) == 0; }

        /** @return {@code true} if the scan data output is normal. */
        public boolean isDataOk()            { return (status & (1 << 5)) == 0; }

        /**
         * @return {@code true} if all monitored modules report no abnormalities
         *         (status bits 0-5 are all 0).
         */
        public boolean isHealthy()           { return (status & 0x3F) == 0; }

        @Override
        public String toString()
        {
            return String.format(
                "LidarHealth{healthy=%b, sensor=%b, encoder=%b, wirelessPower=%b, "
                + "pd=%b, ld=%b, data=%b, errorCode=0x%04X}",
                isHealthy(),
                isSensorOk(), isEncoderOk(), isWirelessPowerOk(),
                isPDOk(), isLDOk(), isDataOk(),
                errorCode);
        }
    }

    /** Minimum scan frequency permitted by the T-mini Plus hardware: 6 Hz. */
    public static final float FREQ_MIN_HZ      =  6.0f;

    /** Maximum scan frequency permitted by the T-mini Plus hardware: 12 Hz. */
    public static final float FREQ_MAX_HZ      = 12.0f;

    /** Factory default scan frequency: 6 Hz. */
    public static final float FREQ_DEFAULT_HZ  =  6.0f;

    /** Native driver handle — unique per instance, never shared. */
    private final long m_handle;

    /** Tracks the current scan frequency in software to enforce bounds. */
    private float currentFrequencyHz = FREQ_DEFAULT_HZ;

    /**
     * Creates a LidarMini instance, opens the serial port, and performs a brief
     * scan-start → stop wake sequence to bring the T-mini Plus into a
     * responsive idle state.
     * <p>
     * The wake sequence takes approximately 250 ms.  After the constructor
     * returns, {@link #getInfo()} and {@link #getHealth()} are safe to call
     * immediately.  Call {@link #start()} when ready to begin scanning.
     *
     * @param port USB port the T-mini Plus is connected to
     */
    public LidarMini(Port port)
    {
        m_handle = LidarMiniJNI.c_LidarMini_create(port.value);
    }

    /**
     * Sends the scan-start command to the T-mini Plus and
     * launches the background packet-parsing thread.
     * <p>
     * Call {@link #getInfo()} / {@link #getHealth()} <em>before</em> calling
     * this method, as those commands cannot be issued while the device is scanning.
     */
    public void start()
    {
        LidarMiniJNI.c_LidarMini_start(m_handle);
    }

    /**
     * Sends the stop command to the T-mini Plus and shuts down
     * the background thread. The serial port remains open; call {@link #start()}
     * to resume scanning.
     */
    public void stop()
    {
        LidarMiniJNI.c_LidarMini_stop(m_handle);
    }

    /**
     * Stops scanning (if active) and closes the serial port.
     * The object should not be used after this call.
     */
    public void destroy()
    {
        LidarMiniJNI.c_LidarMini_destroy(m_handle);
    }

    /**
     * Increases the scan frequency by <b>1 Hz</b>.
     * <p>
     * Must be called while the device is <em>not</em> scanning (call {@link #stop()}
     * first if necessary).
     * <p>
     * Clamped to the datasheet maximum of {@value #FREQ_MAX_HZ} Hz — prints a
     * warning and takes no action if the step would exceed that limit.
     * <p>
     * Example: current frequency 9 Hz → becomes 10 Hz.
     */
    public void increaseFrequencyHz()
    {
        applyFrequencyChange(1.0f, true);
    }

    /**
     * Increases the scan frequency by <b>0.1 Hz</b>.
     * <p>
     * Must be called while the device is <em>not</em> scanning (call {@link #stop()}
     * first if necessary).
     * <p>
     * Clamped to the datasheet maximum of {@value #FREQ_MAX_HZ} Hz — prints a
     * warning and takes no action if the step would exceed that limit.
     * <p>
     * Example: current frequency 9.0 Hz → becomes 9.1 Hz.
     */
    public void increaseFrequencyTenthHz()
    {
        applyFrequencyChange(0.1f, true);
    }

    /**
     * Decreases the scan frequency by <b>1 Hz</b>.
     * <p>
     * Must be called while the device is <em>not</em> scanning (call {@link #stop()}
     * first if necessary).
     * <p>
     * Clamped to the datasheet minimum of {@value #FREQ_MIN_HZ} Hz — prints a
     * warning and takes no action if the step would go below that limit.
     * <p>
     * Example: current frequency 9 Hz → becomes 8 Hz.
     */
    public void decreaseFrequencyHz()
    {
        applyFrequencyChange(1.0f, false);
    }

    /**
     * Decreases the scan frequency by <b>0.1 Hz</b>.
     * <p>
     * Must be called while the device is <em>not</em> scanning (call {@link #stop()}
     * first if necessary).
     * <p>
     * Clamped to the datasheet minimum of {@value #FREQ_MIN_HZ} Hz — prints a
     * warning and takes no action if the step would go below that limit.
     * <p>
     * Example: current frequency 9.0 Hz → becomes 8.9 Hz.
     */
    public void decreaseFrequencyTenthHz()
    {
        applyFrequencyChange(0.1f, false);
    }

    /**
     * Shared implementation for all four frequency-adjustment methods.
     *
     * @param step      magnitude of the change in Hz (1.0 or 0.1)
     * @param increase  {@code true} to increase, {@code false} to decrease
     */
    private void applyFrequencyChange(float step, boolean increase)
    {
        float nextFreq = increase
            ? currentFrequencyHz + step
            : currentFrequencyHz - step;

        if (increase && nextFreq > FREQ_MAX_HZ + 0.001f) {
            System.out.printf("[LidarMini] Frequency increase blocked: %.1f + %.1f Hz "
                + "would exceed maximum %.0f Hz%n",
                currentFrequencyHz, step, FREQ_MAX_HZ);
            return;
        }
        if (!increase && nextFreq < FREQ_MIN_HZ - 0.001f) {
            System.out.printf("[LidarMini] Frequency decrease blocked: %.1f - %.1f Hz "
                + "would go below minimum %.0f Hz%n",
                currentFrequencyHz, step, FREQ_MIN_HZ);
            return;
        }

        if (increase) {
            LidarMiniJNI.c_LidarMini_increaseFrequency(m_handle, step == 1.0f);
        } else {
            LidarMiniJNI.c_LidarMini_decreaseFrequency(m_handle, step == 1.0f);
        }
        currentFrequencyHz = nextFreq;
        System.out.printf("[LidarMini] Scan frequency → %.1f Hz%n", currentFrequencyHz);
    }

    /**
     * Returns the current scan frequency as tracked by this driver instance.
     * Reflects all changes made via {@link #increaseFrequencyHz()},
     * {@link #increaseFrequencyTenthHz()}, {@link #decreaseFrequencyHz()}, and
     * {@link #decreaseFrequencyTenthHz()} since construction.
     *
     * @return current scan frequency in Hz
     */
    public float getCurrentFrequency()
    {
        return currentFrequencyHz;
    }

    /**
     * Queries the T-mini Plus for its model, firmware version, hardware version,
     * and factory serial number.
     * <p>
     * <b>Must be called before {@link #start()}.</b>  Sending this command while
     * the device is in scan mode will corrupt the data stream.
     *
     * @return a {@link LidarInfo} populated with device information,
     *         or a zeroed struct if communication failed
     */
    public LidarInfo getInfo()
    {
        return LidarMiniJNI.c_LidarMini_getInfo(m_handle);
    }

    /**
     * Queries the T-mini Plus health status.
     * <p>
     * <b>Must be called before {@link #start()}.</b>  Check
     * {@link LidarHealth#isHealthy()} before beginning a scanning session.
     * Real-time health updates are also embedded in the point-cloud stream
     * during scanning (parsed automatically by the driver).
     *
     * @return a {@link LidarHealth} with per-module status flags,
     *         or a zeroed struct if communication failed
     */
    public LidarHealth getHealth()
    {
        return LidarMiniJNI.c_LidarMini_getHealth(m_handle);
    }

    /**
     * Returns a snapshot of the last completed 360° scan.
     * <p>
     * This method is thread-safe and non-blocking.  It returns an empty
     * {@link ScanData} (with {@link ScanData#size()} == 0) if the first full
     * lap has not yet completed since {@link #start()} was called.
     * <p>
     * Call this method periodically (e.g. every robot loop iteration).  The
     * underlying driver replaces the completed buffer once per revolution, so
     * calling more frequently than the scan rate returns the same data.
     *
     * @return the most recent complete scan, never {@code null}
     */
    public ScanData getData()
    {
        ScanData data = LidarMiniJNI.c_LidarMini_getData(m_handle);
        // Guard against null return from JNI if called before first lap completes
        return (data != null) ? data : new ScanData();
    }
}