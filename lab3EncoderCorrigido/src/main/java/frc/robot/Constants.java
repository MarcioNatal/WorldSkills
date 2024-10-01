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
    
    //Dados das rodas omni em MILIMETRO
    public static final double wheelDiameterMeter = 104; //milimetros
    public static final double wheelRadius          = wheelDiameterMeter/2; //mm  52
    public static final double wheelCircumferenceMeters= wheelDiameterMeter*Math.PI;// mm 


    // Encoder pulse per revolution
    public static final double pulsePerRevolution   = 1440.00;

    // Gear Ratio between encoder and wheel 
    public static final double gearRatio = 0.01639344262295081967213114754098; // 1/61;

    // Pulse per revolution of wheel
    public static final double wheelPulseRatio      = pulsePerRevolution * gearRatio;

    //Velocidade da redução
    public static final double motorGearSpeedRpm = 100;
    public static final double motorGearSpeedRps = motorGearSpeedRpm/60;
    
    // Distance per tick
    public static final double wheelDistPerTick  = (Math.PI * 2 * wheelRadius) / pulsePerRevolution; //mm
    public static final double wheelPerimiter = (Math.PI * 2 * wheelRadius);
    public static final double rot2MeterPerSec =   (Math.PI * 2 * wheelRadius) *motorGearSpeedRps; //rps * perimetro
    public static final double dt = 2; // 2 secondes
    public static final double acelMeterPerSecSquare = rot2MeterPerSec/dt; // dV/dt; em m/s²
    

    //Conversão de unidades de escalar e angular
    public static final double encoderRot2Meter = gearRatio * Math.PI * wheelDiameterMeter; // milimetro/segundo
    public static final double encoderRot2RadPerSec = gearRatio * 2 * Math.PI; // VELOCIDADE ANGULAR RAD/S
    public static final double encoderRPM2MeterPerSec = encoderRot2Meter/60; // VELOCIDAE LINEAR MM/S
    
    //Encoders Ports
    //Motor 1
    public static final int kMotor1tEncoderPortA = 0;
    public static final int kMotor1tEncoderPortB = 1;
    public static final boolean  kMotor1EncoderReversed = false;
    
    //Motor 2
    public static final int kMotor2tEncoderPortA = 2;
    public static final int kMotor2tEncoderPortB = 3;
    public static final boolean  kMotor2EncoderReversed = false;

    //Motor 3
    public static final int kMotor3tEncoderPortA = 4;
    public static final int kMotor3tEncoderPortB = 5;
    public static final boolean  kMotor3EncoderReversed = true;

    

}
