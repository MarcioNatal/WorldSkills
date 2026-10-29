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
 * DriveToPosition class
 * <p>
 * This class drives the motor a given distance in mm using the WPILib
 * PIDController and ends once the encoder reaches the target.
 */

public class DriveToPosition extends CommandBase
{
  //Grab the subsystem instance from RobotContainer
  private final DriveTrainSubsystem driveSubsystem;

  //Distancia alvo em mm, relativa ao ponto onde o comando comecou
  private final double targetMM;


  /**
   * Constructor
   */
  /**
   * Creates a new DriveToPosition.
   *
   * @param driveSubsystem subsystem do drivetrain
   * @param targetMM distancia a percorrer em mm (negativa para tras)
   */
  public DriveToPosition( DriveTrainSubsystem driveSubsystem, double targetMM)
  {
    this.driveSubsystem = driveSubsystem;
    this.targetMM = targetMM;
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
    driveSubsystem.resetPositionControl(); // zera encoder e estado do PID

  }

  // Called every time the scheduler runs while the command is scheduled.
  /**
   * Called continously until command is ended
   */
  @Override
  public void execute()
  {
    driveSubsystem.setMotorPosition(targetMM); // uma iteracao do PID por ciclo
  }


  /**
   * Called when the command is told to end or is interrupted
   */
  @Override
  public void end(boolean interrupted)
  {
    driveSubsystem.stopMotorDrive(); // Stop motor

  }

  // Returns true when the command should end.
  /**
   * Creates an isFinished condition if needed
   */
  @Override
  public boolean isFinished()
  {
    return driveSubsystem.atPosition();
  }
}
