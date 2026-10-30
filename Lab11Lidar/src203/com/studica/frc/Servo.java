package com.studica.frc;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

public class Servo extends PWM {
    private static final double kMaxServoAngle = 300.0;
    private static final double kMinServoAngle = 0.0;

    protected static final double kDefaultMaxServoPWM = 2.5;
    protected static final double kDefaultMinServoPWM = 0.5;

    /** 
     * Constructor for Servo
     * @param channel The channel that the servo is plugged into
     */
    public Servo(final int channel) {
        super(channel);
        setBounds(kDefaultMaxServoPWM, 0, 0, 0, kDefaultMinServoPWM);
        setPeriodMultiplier(PeriodMultiplier.k4X);

        HAL.report(tResourceType.kResourceType_Servo, getChannel() + 1);
        SendableRegistry.setName(this, "Servo", getChannel());
    }

    /**
     * Sets the position of the servo
     * @param value Servo position value, range from 0.0 to 1.0
     */
    public void set(double value) {
        setPosition(value);
    }

    /**
     * Gets the servo position
     * 
     * @return Position from 0.0 to 1.0
     */
    public double get() {
        return getPosition();
    }

    /**
     * Sets the servo angle
     * @param degrees The angle in degrees to set the servo
     */
    public void setAngle(double degrees) {
        if (degrees < kMinServoAngle) {
        degrees = kMinServoAngle;
        } else if (degrees > kMaxServoAngle) {
        degrees = kMaxServoAngle;
        }

        setPosition(((degrees - kMinServoAngle)) / getServoAngleRange());
    }

    /** 
     * Gets the servo angle
     * 
     * @return The angle in degrees to which the servo is set
     */
    public double getAngle() {
        return getPosition() * getServoAngleRange() + kMinServoAngle;
    }

    private double getServoAngleRange() {
        return kMaxServoAngle - kMinServoAngle;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Servo");
        builder.addDoubleProperty("Value", this::get, this::set);
    }
}