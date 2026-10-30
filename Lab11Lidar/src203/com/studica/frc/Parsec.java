package com.studica.frc;

import com.studica.frc.jni.ParsecJNI;

/**
 * Java driver for the <b>Parsec</b> time-of-flight distance sensor.
 * <p>
 * Parsec measures distance across a grid of zones and streams the results over
 * CAN. On the roboRIO the bus is classic CAN 2.0B, so one reading arrives as a
 * burst of frames that this driver reassembles into a zone array.
 * <p>
 * Zones are reported in row-major order across the sensor's field of view. A
 * zone reads {@code -1} when its data did not arrive or the sensor could not
 * resolve a distance for it — always check before using a value.
 */
public class Parsec
{
    /** Largest zone count the sensor reports (8&times;8 grid). */
    public static final int MAX_ZONES = 64;

    /** Zone value meaning "no valid reading". */
    public static final int INVALID_DISTANCE = -1;

    /**
     * Physical USB port location.
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

    /** Link the sensor is attached by. */
    public enum Transport
    {
        kCAN,
        kUSB
    }

    /**
     * One reassembled distance frame.
     */
    public static class ScanData
    {
        /** Frame sequence number reported by the sensor. */
        public int sequence;

        /**
         * Per-zone distance in millimetres.
         * {@link Parsec#INVALID_DISTANCE} means the zone had no valid reading.
         */
        public int[] distance;

        /**
         * Returns the number of zones in this frame.
         *
         * @return zone count, or 0 if no frame has been received
         */
        public int size()
        {
            return (distance != null) ? distance.length : 0;
        }

        /**
         * Returns the distance for one zone.
         *
         * @param zone zone index [0, {@link #size()})
         * @return distance in millimetres, or {@link Parsec#INVALID_DISTANCE}
         *         if the index is out of range or the zone has no reading
         */
        public int getDistance(int zone)
        {
            if (distance == null || zone < 0 || zone >= distance.length) {
                return INVALID_DISTANCE;
            }
            return distance[zone];
        }

        /**
         * Returns the smallest valid distance in this frame — the nearest
         * obstacle anywhere in the sensor's field of view.
         *
         * @return closest distance in millimetres, or
         *         {@link Parsec#INVALID_DISTANCE} if no zone has a valid reading
         */
        public int getClosest()
        {
            int closest = Integer.MAX_VALUE;
            if (distance != null) {
                for (int d : distance) {
                    if (d != INVALID_DISTANCE && d < closest) closest = d;
                }
            }
            return (closest == Integer.MAX_VALUE) ? INVALID_DISTANCE : closest;
        }

        @Override
        public String toString()
        {
            return String.format("ScanData{sequence=%d, zones=%d, closest=%d mm}",
                sequence, size(), getClosest());
        }
    }

    /**
     * Device configuration as reported by the sensor.
     */
    public static class Config
    {
        /** Firmware configuration-structure version. */
        public int version;

        /** {@code true} when distance streaming is enabled. */
        public boolean distanceEnabled;

        /** {@code true} when the device is running CAN FD (never on roboRIO). */
        public boolean canFdEnabled;

        /** {@code true} when CAN FD bit-rate switching is active. */
        public boolean canBrsEnabled;

        /** CAN node ID the device is configured for. */
        public int canID;

        @Override
        public String toString()
        {
            return String.format(
                "Config{version=%d, distanceEnabled=%b, canFd=%b, canBrs=%b, canID=%d}",
                version, distanceEnabled, canFdEnabled, canBrsEnabled, canID);
        }
    }

    /** Native driver handle — unique per instance, never shared. */
    private final long m_handle;

    /**
     * Opens a Parsec on the given CAN node ID.
     *
     * @param canID Parsec CAN node ID [0, 63]
     * @throws IllegalArgumentException if canID is outside [0, 63]
     * @throws IllegalStateException if the CAN stream could not be opened
     */
    public Parsec(int canID)
    {
        if (canID < 0 || canID > 63) {
            throw new IllegalArgumentException(
                "Parsec CAN ID must be in [0, 63], got: " + canID);
        }
        m_handle = ParsecJNI.c_Parsec_create(canID);
        if (m_handle == 0) {
            throw new IllegalStateException(
                "Failed to open Parsec on CAN ID " + canID);
        }
    }

    /**
     * Opens a Parsec on the default CAN node ID (0).
     */
    public Parsec()
    {
        this(0);
    }

    /**
     * Opens a Parsec over USB on the given port, at the sensor's default rate.
     *
     * @param port USB port the sensor is plugged into
     * @throws IllegalStateException if the port could not be opened
     */
    public Parsec(Port port)
    {
        this(port, 0);
    }

    /**
     * Opens a Parsec over USB on the given port.
     * <p>
     * With more than one USB serial device attached, the mapping from
     * {@link Port} to device depends on enumeration order — use
     * {@link #Parsec(String, int)} with an explicit path when that matters.
     *
     * @param port  USB port the sensor is plugged into
     * @param odrHz output data rate in Hz; 0 leaves the sensor's default
     * @throws IllegalStateException if the port could not be opened
     */
    public Parsec(Port port, int odrHz)
    {
        this(ParsecJNI.c_Parsec_findDevicePath(port.value), odrHz);
    }

    /**
     * Opens a Parsec over USB on an explicit device path.
     *
     * @param devicePath serial device path, e.g. {@code "/dev/ttyACM0"}
     * @param odrHz      output data rate in Hz; 0 leaves the sensor's default
     * @throws IllegalStateException if the port could not be opened
     */
    public Parsec(String devicePath, int odrHz)
    {
        m_handle = ParsecJNI.c_Parsec_createUsb(devicePath, odrHz);
        if (m_handle == 0) {
            throw new IllegalStateException(
                "Failed to open Parsec on " + devicePath);
        }
    }

    /**
     * Opens a Parsec over USB on an explicit device path, at the sensor's
     * default rate.
     *
     * @param devicePath serial device path, e.g. {@code "/dev/ttyACM0"}
     * @throws IllegalStateException if the port could not be opened
     */
    public Parsec(String devicePath)
    {
        this(devicePath, 0);
    }

    /**
     * Returns the most recent distance frame.
     * <p>
     * Non-blocking. Over CAN the reading is reassembled from a burst of frames,
     * which takes roughly 25 ms, so call this once per robot loop rather than
     * several times in a row. Over USB a background thread does the collecting
     * and this simply copies the newest frame.
     *
     * @param maxZones number of zones to return [1, {@link #MAX_ZONES}]
     * @return the latest frame, never {@code null}; {@link ScanData#size()}
     *         is 0 when no frames have arrived
     */
    public ScanData getData(int maxZones)
    {
        if (maxZones < 1) maxZones = 1;
        if (maxZones > MAX_ZONES) maxZones = MAX_ZONES;

        ScanData data = ParsecJNI.c_Parsec_getData(m_handle, maxZones);
        return (data != null) ? data : new ScanData();
    }

    /**
     * Returns the most recent distance frame across all {@link #MAX_ZONES} zones.
     *
     * @return the latest frame, never {@code null}
     */
    public ScanData getData()
    {
        return getData(MAX_ZONES);
    }

    /**
     * Queries the sensor configuration.
     * <p>
     * Over USB the reply is free-form text whose field layout varies between
     * firmware builds, so only {@link Config#distanceEnabled} is populated and
     * {@link Config#canID} stays 0.
     *
     * @param timeoutMs milliseconds to wait for the device to reply
     * @return the configuration, or {@code null} if the device did not respond
     */
    public Config getConfig(int timeoutMs)
    {
        return ParsecJNI.c_Parsec_getConfig(m_handle, timeoutMs);
    }

    /**
     * Queries the sensor configuration, waiting up to 100 ms.
     *
     * @return the configuration, or {@code null} if the device did not respond
     */
    public Config getConfig()
    {
        return getConfig(100);
    }

    /**
     * @return CAN node ID this instance was opened on, or {@code -1} on USB
     */
    public int getCanID()
    {
        return ParsecJNI.c_Parsec_getCanID(m_handle);
    }

    /**
     * @return the link this sensor is attached by
     */
    public Transport getTransport()
    {
        return ParsecJNI.c_Parsec_getTransport(m_handle) == 1
            ? Transport.kUSB : Transport.kCAN;
    }

    /**
     * Closes the CAN stream and releases the sensor.
     * The object should not be used after this call.
     */
    public void destroy()
    {
        ParsecJNI.c_Parsec_destroy(m_handle);
    }
}
