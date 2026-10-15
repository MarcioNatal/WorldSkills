/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants 
{
    /**
     * Joystick ports
     */
     public static final int kDriverControllerPort = 0;
     public static final int kDriverYAxis = 1;
     public static final int kDriverXAxis = 0;
     public static final int kDriverRotAxis = 4;

     public static final double kDeadband = 0.1;// 0.05 anterior
     /**
     * CAN ID's
     */
    public static final int TITAN_ID        = 42;
    
    
    //public static final int MOTOR           = 2;
    /**
     * Motors
     */
    public static final int M1                      = 1; //Right Motor
    public static final int M2                      = 2; //Back Motor
    public static final int M3                      = 3; //Left Motor

    /**
     * Encoders
     */
    
    // Omni wheel radius
    public static final double wheelRadius          = 52; //mm  Diametro 105 mm

    // Encoder pulse per revolution
    public static final double pulsePerRevolution   = 1440;

    // Gear Ratio between encoder and wheel 
    public static final double gearRatio            = 1.0/61.0; // 1/61 em int dava 0

    // Pulse per revolution of wheel
    public static final double wheelPulseRatio      = pulsePerRevolution * gearRatio;

    // Distance per tick
    public static final double wheelDistPerTick  = (Math.PI * 2 * wheelRadius) / pulsePerRevolution; //mm
	

    

}
