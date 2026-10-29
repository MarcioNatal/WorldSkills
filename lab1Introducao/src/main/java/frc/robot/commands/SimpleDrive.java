/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;



// WPI imports
import edu.wpi.first.wpilibj2.command.CommandBase;



////Subsystem imports

import frc.robot.subsystems.DriveTrainSubsystem;


/**
 * SimpleDrive class
 * <p>
 * This class drives a motor at 50% speed until the command is ended
 */

public class SimpleDrive extends CommandBase 
{
  //Grab the subsystem instance from RobotContainer
  private final DriveTrainSubsystem driveSubsystem ;

 


  /**
   * Constructor
   */
  /**
   * Creates a new SimpleDrive.
   */
  public SimpleDrive( DriveTrainSubsystem driveSubsystem) 
  {
    this.driveSubsystem = driveSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(driveSubsystem); // Adds the subsystem to the command
  }

  // Called when the command is initially scheduled.
  /**
   * Runs before execute
   */
  @Override
  public void initialize() 
  {
    

  }

  // Called every time the scheduler runs while the command is scheduled.
  /**
   * Called continously until command is ended
   */
  @Override
  public void execute() 
  {
    driveSubsystem.setMotorSpeed(0.5); // Set motor speed to 50%
  }

  
  /**
   * Called when the command is told to end or is interrupted
   */
  @Override
  public void end(boolean interrupted) 
  {
    driveSubsystem.stopMotorDrive(); // Stop moto

  }

  // Returns true when the command should end.
  /**
   * Creates an isFinished condition if needed
   */
  @Override
  public boolean isFinished() 
  {
    return false;//
  }
}
