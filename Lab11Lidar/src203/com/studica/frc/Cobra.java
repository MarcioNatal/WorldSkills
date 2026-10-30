package com.studica.frc;

import com.studica.frc.jni.CobraJNI;

/** 
 * Cobra class c++ documentation
 * 
 * @author Studica Limited
 * 
 */
public class Cobra {
    
    private float vRef;

    /**
     * Default constructor for Cobra
     */
    public Cobra() {
        this.vRef = 5F;
        CobraJNI.c_initializeI2C(vRef);;    
    }

    /**
     * Constructor for Cobra 
     * @param _vRef Referenced voltage
     */
    public Cobra(float _vRef) {
        this.vRef = _vRef;
        CobraJNI.c_initializeI2C(vRef);
    }

    /**
     * Gets the raw value of the sensor
     * @param channel The channel to read from 
     * 
     * @return The raw value of the sensor
     */
    public int getRawValue(int channel) {
        return CobraJNI.c_getRawValue(channel);
    }

    /**
     * Gets the voltage of the sensor
     * @param channel The channel to read from
     * 
     * @return The voltage of the sensor 
     */
    public float getVoltage(int channel) {
        return CobraJNI.c_getVoltage(channel);
    }

}
