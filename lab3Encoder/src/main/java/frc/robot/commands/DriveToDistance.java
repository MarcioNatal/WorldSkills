/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.OmniDrive;

public class DriveToDistance extends CommandBase 
{
  //Grab the subsystem instance from RobotContainer
  private static final OmniDrive drive = RobotContainer.drive;

  private double distance = 500;

  /**
   * Creates a new DriveToDistance.
   */
  public DriveToDistance() 
  {
    // Use addRequirements() here to declare subsystem dependencies.
    
    addRequirements(drive); // Adds the subsystem to the command
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    drive.resetEncoders();
    drive.resetYaw();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    drive.holonomicDrive(0, -.5, 0);

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    drive.holonomicDrive(0, 0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() 
  {
    return (drive.getAverageForwardEncoderDistance() >= distance);
  }
}
