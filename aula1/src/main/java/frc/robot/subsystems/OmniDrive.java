/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.studica.frc.TitanQuad;

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
    private TitanQuad motor;

  public OmniDrive() 
  {

      motor = new TitanQuad(Constants.TITAN_ID, Constants.M1);
  }

  @Override
  public void periodic() 
  {
    // This method will be called once per scheduler run
  }

  public void setMotorSpeed(double speed)
  {
    motor.set(speed);

  }


}
