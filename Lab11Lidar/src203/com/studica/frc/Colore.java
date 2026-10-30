package com.studica.frc;

import com.studica.frc.jni.ColoreJNI;

/**
 * Java driver for the <b>Colore</b> colour sensor, over either CAN or USB.
 * <p>
 * The sensor streams CIE-XYZ tristimulus values. XYZ is device- and
 * illuminant-independent, so the host derives everything else from it:
 * {@link ColorData#toRGB()} converts to display sRGB and
 * {@link ColorData#getChromaticity()} reduces to a brightness-independent
 * (x, y) pair. Chromaticity is the right basis for deciding <em>which</em>
 * colour something is, because it does not change when the target gets closer
 * to or further from the LED.
 * <p>
 * Pick the transport at construction — {@code new Colore(0)} for CAN,
 * {@code new Colore(Colore.Port.kUSB1)} for USB. {@link #getColor()} behaves
 * identically either way.
 * <p>
 * Over CAN every setter is acknowledged by the device and returns
 * {@code false} if no acknowledgement arrives, so a failed call is visible
 * rather than silent. The USB firmware only accepts colour-format and
 * sample-time commands, so the setters marked <b>CAN only</b> return
 * {@code false} on a USB instance.
 */
public class Colore
{
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

    /** Colour output formats. Values match the firmware COLORFORMAT codes. */
    public enum ColorFormat
    {
        /** Device-native RGB. */
        kRGB(0),
        /** Unprocessed sensor channels. */
        kRaw(1),
        /** Gamma-encoded sRGB. */
        kSRGB(2),
        /** Packed hexadecimal RGB. */
        kHex(3),
        /** CIE-XYZ tristimulus — required for {@link #getColor()}. */
        kXYZ(4);

        public final int value;

        ColorFormat(int value) { this.value = value; }
    }

    /** Distance-measurement modes. */
    public enum MeasureMode
    {
        /** Distance compensation disabled. */
        kOff(0),
        /** Sensor measures target distance itself. */
        kAuto(1),
        /** Use a fixed distance supplied by the host. */
        kFixed(2);

        public final int value;

        MeasureMode(int value) { this.value = value; }
    }

    /** Configuration items readable via {@link #getConfig(ConfigItem)}. */
    public enum ConfigItem
    {
        kColorFormat(1),
        kSampleTime(2),
        kMeasureModeFlags(3),
        kBrightness(4),
        kUsbOverride(5),
        kStatusLed(6),
        kCanID(7);

        public final int value;

        ConfigItem(int value) { this.value = value; }
    }

    /**
     * One CIE-XYZ colour reading.
     */
    public static class ColorData
    {
        /** Frame sequence number reported by the sensor. */
        public int sequence;

        /** CIE X tristimulus value. */
        public float x;

        /** CIE Y tristimulus value; also relative luminance. */
        public float y;

        /** CIE Z tristimulus value. */
        public float z;

        /**
         * Converts this reading to display sRGB.
         * <p>
         * Applies the standard XYZ&rarr;linear-RGB matrix followed by the sRGB
         * transfer function, matching the conversion the ROS driver publishes.
         * Components are clamped to [0, 1].
         *
         * @return {@code float[3]} of {r, g, b}, each in [0, 1]
         */
        public float[] toRGB()
        {
            float rl =  3.2406f * x - 1.5372f * y - 0.4986f * z;
            float gl = -0.9689f * x + 1.8758f * y + 0.0415f * z;
            float bl =  0.0557f * x - 0.2040f * y + 1.0570f * z;
            return new float[] { encodeSRGB(rl), encodeSRGB(gl), encodeSRGB(bl) };
        }

        /** sRGB transfer function, with clamping to the displayable range. */
        private static float encodeSRGB(float c)
        {
            if (c < 0.0f) c = 0.0f;
            if (c > 1.0f) c = 1.0f;
            return (c <= 0.0031308f)
                ? 12.92f * c
                : (float) (1.055f * Math.pow(c, 1.0 / 2.4) - 0.055f);
        }

        /**
         * Reduces this reading to CIE 1931 chromaticity coordinates.
         * <p>
         * Chromaticity discards brightness, so a target's (x, y) stays stable as
         * lighting or distance changes. Use it to identify colours; use
         * {@link #y} if you need brightness.
         *
         * @return {@code float[2]} of {x, y}, or {@code null} if the reading
         *         carries no light and chromaticity is undefined
         */
        public float[] getChromaticity()
        {
            final float sum = x + y + z;
            if (sum <= 1e-6f) return null;
            return new float[] { x / sum, y / sum };
        }

        /**
         * Returns the distance in chromaticity space between this reading and a
         * reference colour — smaller means a closer match.
         * <p>
         * Compare against a threshold to decide whether the match counts; the
         * ROS driver defaults to {@code 0.05}.
         *
         * @param refX reference chromaticity x
         * @param refY reference chromaticity y
         * @return distance, or {@link Float#MAX_VALUE} if chromaticity is undefined
         */
        public float distanceTo(float refX, float refY)
        {
            float[] xy = getChromaticity();
            if (xy == null) return Float.MAX_VALUE;
            return (float) Math.hypot(xy[0] - refX, xy[1] - refY);
        }

        /**
         * Returns this reading packed as a 24-bit {@code 0xRRGGBB} value.
         *
         * @return packed sRGB colour
         */
        public int toHex()
        {
            float[] rgb = toRGB();
            int r = Math.round(rgb[0] * 255.0f);
            int g = Math.round(rgb[1] * 255.0f);
            int b = Math.round(rgb[2] * 255.0f);
            return (r << 16) | (g << 8) | b;
        }

        @Override
        public String toString()
        {
            return String.format("ColorData{sequence=%d, X=%.4f, Y=%.4f, Z=%.4f, hex=#%06X}",
                sequence, x, y, z, toHex());
        }
    }

    /** Native driver handle — unique per instance, never shared. */
    private final long m_handle;

    /**
     * Opens a Colore on the given CAN ID.
     *
     * @param canID CAN ID [0, 63]
     * @throws IllegalArgumentException if canID is outside [0, 63]
     * @throws IllegalStateException if the CAN stream could not be opened
     */
    public Colore(int canID)
    {
        if (canID < 0 || canID > 63) {
            throw new IllegalArgumentException(
                "Colore CAN ID must be in [0, 63], got: " + canID);
        }
        m_handle = ColoreJNI.c_Colore_create(canID);
        if (m_handle == 0) {
            throw new IllegalStateException(
                "Failed to open Colore on CAN ID " + canID);
        }
    }

    /**
     * Opens a Colore on the default CAN ID (0).
     */
    public Colore()
    {
        this(0);
    }

    /**
     * Opens a Colore over USB on the given port, at the sensor's default rate.
     *
     * @param port USB port the sensor is plugged into
     * @throws IllegalStateException if the port could not be opened
     */
    public Colore(Port port)
    {
        this(port, 0);
    }

    /**
     * Opens a Colore over USB on the given port.
     * <p>
     * With more than one USB serial device attached, the mapping from
     * {@link Port} to device depends on enumeration order — use
     * {@link #Colore(String, int)} with an explicit path when that matters.
     *
     * @param port     USB port the sensor is plugged into
     * @param sampleMs sample interval in milliseconds; 0 leaves the default
     * @throws IllegalStateException if the port could not be opened
     */
    public Colore(Port port, int sampleMs)
    {
        this(ColoreJNI.c_Colore_findDevicePath(port.value), sampleMs);
    }

    /**
     * Opens a Colore over USB on an explicit device path.
     * <p>
     * The sensor is put into XYZ output mode on open, so {@link #getColor()}
     * works without further setup.
     *
     * @param devicePath serial device path, e.g. {@code "/dev/ttyACM0"}
     * @param sampleMs   sample interval in milliseconds; 0 leaves the default
     * @throws IllegalStateException if the port could not be opened
     */
    public Colore(String devicePath, int sampleMs)
    {
        m_handle = ColoreJNI.c_Colore_createUsb(devicePath, sampleMs);
        if (m_handle == 0) {
            throw new IllegalStateException(
                "Failed to open Colore on " + devicePath);
        }
    }

    /**
     * Opens a Colore over USB on an explicit device path, at the sensor's
     * default rate.
     *
     * @param devicePath serial device path, e.g. {@code "/dev/ttyACM0"}
     * @throws IllegalStateException if the port could not be opened
     */
    public Colore(String devicePath)
    {
        this(devicePath, 0);
    }

    /**
     * Returns the most recent colour reading. Non-blocking.
     * <p>
     * Requires the sensor to be streaming XYZ — call
     * {@code setColorFormat(ColorFormat.kXYZ)} once during initialisation.
     *
     * @return the latest reading, or {@code null} if none has arrived yet
     */
    public ColorData getColor()
    {
        return ColoreJNI.c_Colore_getColor(m_handle);
    }

    /**
     * Selects the colour output format.
     * <p>
     * {@link #getColor()} decodes the CIE-XYZ frame, so use
     * {@link ColorFormat#kXYZ} unless you are reading the device another way.
     *
     * @param format output format
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setColorFormat(ColorFormat format)
    {
        return ColoreJNI.c_Colore_setColorFormat(m_handle, format.value);
    }

    /**
     * Sets the sensor sample interval.
     * <p>
     * Shorter intervals give fresher readings at the cost of CAN traffic. The
     * firmware accepts roughly 20-2000 ms.
     *
     * @param ms sample interval in milliseconds
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setSampleTimeMs(int ms)
    {
        return ColoreJNI.c_Colore_setSampleTimeMs(m_handle, ms);
    }

    /**
     * Sets the illumination LED brightness.
     * <p>
     * <b>CAN only</b> — returns {@code false} on a USB instance, as the USB
     * firmware exposes no equivalent command.
     *
     * @param percent brightness [0, 100]; clamped by the driver
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setBrightness(int percent)
    {
        return ColoreJNI.c_Colore_setBrightness(m_handle, percent);
    }

    /**
     * Enables or disables the status LED.
     * <p>
     * <b>CAN only</b> — returns {@code false} on a USB instance, as the USB
     * firmware exposes no equivalent command.
     *
     * @param enable     {@code true} to light the status LED
     * @param brightness brightness [0, 100]; clamped by the driver
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setStatusLed(boolean enable, int brightness)
    {
        return ColoreJNI.c_Colore_setStatusLed(m_handle, enable, brightness);
    }

    /**
     * Sets the distance-measurement mode.
     * <p>
     * {@link MeasureMode#kFixed} uses {@code zUserMm}; the other modes ignore it.
     * <p>
     * <b>CAN only</b> — returns {@code false} on a USB instance.
     *
     * @param mode    measurement mode
     * @param zUserMm fixed target distance in millimetres
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setMeasureMode(MeasureMode mode, float zUserMm)
    {
        return ColoreJNI.c_Colore_setMeasureMode(m_handle, mode.value, zUserMm);
    }

    /**
     * Sets the distance-measurement mode, without a fixed distance.
     *
     * @param mode measurement mode
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setMeasureMode(MeasureMode mode)
    {
        return setMeasureMode(mode, 0.0f);
    }

    /**
     * Sets the fixed sample distance used by {@link MeasureMode#kFixed}.
     * <p>
     * <b>CAN only</b> — returns {@code false} on a USB instance, as the USB
     * firmware exposes no equivalent command.
     *
     * @param zUserMm distance in millimetres
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setZmm(float zUserMm)
    {
        return ColoreJNI.c_Colore_setZmm(m_handle, zUserMm);
    }

    /**
     * Enables or disables the USB output override.
     * <p>
     * <b>CAN only</b> — returns {@code false} on a USB instance, as the USB
     * firmware exposes no equivalent command.
     *
     * @param enable {@code true} to let USB take over the output stream
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setUsbOverride(boolean enable)
    {
        return ColoreJNI.c_Colore_setUsbOverride(m_handle, enable);
    }

    /**
     * Changes the CAN ID stored on the device.
     * <p>
     * The change persists on the sensor. This instance keeps talking on the ID
     * it was opened with, so construct a new {@link Colore} afterwards.
     *
     * @param canID new CAN ID
     * @return {@code true} if the device acknowledged the change
     */
    public boolean setCanId(int canID)
    {
        return ColoreJNI.c_Colore_setCanId(m_handle, canID);
    }

    /**
     * Reads one configuration item back from the device.
     * <p>
     * <b>CAN only</b> — returns {@code false} on a USB instance, as the USB
     * firmware exposes no equivalent command.
     *
     * @param item item to read
     * @return the item value, or {@code -1} if the device did not respond
     */
    public long getConfig(ConfigItem item)
    {
        long[] out = new long[1];
        if (!ColoreJNI.c_Colore_getConfig(m_handle, item.value, out)) return -1L;
        return out[0];
    }

    /**
     * @return CAN ID this instance was opened with, or {@code -1} on USB
     */
    public int getCanID()
    {
        return ColoreJNI.c_Colore_getCanID(m_handle);
    }

    /**
     * @return the link this sensor is attached by
     */
    public Transport getTransport()
    {
        return ColoreJNI.c_Colore_getTransport(m_handle) == 1
            ? Transport.kUSB : Transport.kCAN;
    }

    /**
     * Closes the CAN stream and releases the sensor.
     * The object should not be used after this call.
     */
    public void destroy()
    {
        ColoreJNI.c_Colore_destroy(m_handle);
    }
}
