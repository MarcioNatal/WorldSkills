package com.studica.frc;

import com.studica.frc.jni.MockDSJNI;

public class MockDS
{
    /**
     * MockDS Constructor
     * <p>
     * Will create a new MockDS instance
     */
    public MockDS()
    {
        MockDSJNI.c_MockDS_create();
    }

    /**
     * Enable the robot in auto mode
     */
    public void enable()
    {
        MockDSJNI.c_MockDS_enable();
    }

    /**
     * Disable the robot in auto mode
     */
    public void disable()
    {
        MockDSJNI.c_MockDS_disable();
    }
}