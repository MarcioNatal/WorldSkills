/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

//Vendor imports
import com.studica.frc.Titan;
import frc.robot.Constants;

/**
 * DriveTrain class
 * <p>
 * This class creates the instance of the Titan and enables and sets the speed of the defined motor.
 */

public class DriveTrain extends SubsystemBase 
{
  /**
     * Motors
     */
    private Titan titan;
    private Titan.Motor motor;

    /**
     * Constructor
     */
  /**
   * Creates a new DriveTrain.
   */
  public DriveTrain() 
  {
    //Motors
    titan = new Titan(Constants.TITAN_ID);
    motor = titan.getMotor(Constants.M1);

  }

  @Override
  public void periodic() 
  {
    // This method will be called once per scheduler run
  }

  /**
     * Sets the speed of the motor
     * <p>
     * @param speed range -1 to 1 (0 stop)
     */
    public void setMotorSpeed(double speed)
    {
        motor.set(speed);
    }
}
