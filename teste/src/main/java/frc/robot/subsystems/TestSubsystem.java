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

public class TestSubsystem extends SubsystemBase 
{
  /**
   * Creates a new TestSubsystem.
   */
  private  Titan titan0;
  private  Titan.Motor motor1;

  public TestSubsystem() 
  {
    titan0 = new Titan(Constants.TITAN_ID);

    motor1 = titan0.getMotor(Constants.M1);

  }

  @Override
  public void periodic() 
  {
    // This method will be called once per scheduler run
  }

  public void setSpeedM1()
  {
    motor1.set(0.5);

  }
}
