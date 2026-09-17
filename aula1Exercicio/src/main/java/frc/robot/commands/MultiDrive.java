/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.subsystems.OmniDrive;

public class MultiDrive extends CommandBase 
{
  /**
   * Creates a new MultiDrive.
   */
  private final OmniDrive mOmniDrive;

  double velocidade1;
  double velocidade2;
  double velocidade3;
  
  public MultiDrive(OmniDrive omniDrive,double speedM1, double speedM2, double speedM3) 
  {
    this.mOmniDrive = omniDrive;
    velocidade1 = speedM1;
    velocidade2 = speedM2;
    velocidade3 = speedM3;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(mOmniDrive);
    
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
    mOmniDrive.set3MotorSpeed(velocidade1, velocidade2,  velocidade3);

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    mOmniDrive.set3MotorSpeed(0.0, 0.0,  0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() 
  {
    return false;
  }
}
