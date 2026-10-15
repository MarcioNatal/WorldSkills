/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.studica.frc.Titan;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class OmniDrive extends SubsystemBase 
{
  /**
   * Creates a new OmniDrive.
   * 
   */
  /**
     * Motors
     */
    private Titan titan;
    private Titan.Motor motor1;
    private Titan.Motor motor2;
    private Titan.Motor motor3;

  public OmniDrive() 
  {

      titan = new Titan(Constants.TITAN_ID);
      motor1 = titan.getMotor(Constants.M1);
      motor2 = titan.getMotor(Constants.M2);
      motor3 = titan.getMotor(Constants.M3);
  }

  @Override
  public void periodic() 
  {
    // This method will be called once per scheduler run
  }

  public void setMotorSpeed(double speed)
  {
    motor1.set(speed);

  }

  /**
   * 
   * @param speed1
   * @param speed2
   * @param speed3
   */
  public void set3MotorSpeed(double speed1, double speed2, double speed3)
  {
    motor1.set(speed1);
    motor2.set(speed2);
    motor3.set(speed3);

  }


}
