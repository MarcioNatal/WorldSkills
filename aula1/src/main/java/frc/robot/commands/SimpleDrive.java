/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.OmniDrive;

public class SimpleDrive extends CommandBase {
  /**
   * Creates a new SimpleDrive.
   */
  private final  OmniDrive m_omniDrive; // = new OmniDrive();

  public SimpleDrive(OmniDrive omniDrive) 
  {
    this.m_omniDrive = omniDrive;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_omniDrive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {

  }



  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
   m_omniDrive.setMotorSpeed(0.5);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    m_omniDrive.setMotorSpeed(0.0);
  }


  // Returns true when the command should end.
  @Override
  public boolean isFinished() 
  {
    return false;
  }
}
