/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.OmniDrive;

public class DriveToPositionCommand extends CommandBase 
{
  private final OmniDrive driveSubsystem;
  private final Translation2d targetPosition;
  private final double tolerance = 0.05; // Tolerance in meters for reaching the target
  private double kDistanceToTarget=0.0;
  private final Timer timer = new Timer();
  /**
   * Creates a new DriveToPositionCommand.
   */
  public DriveToPositionCommand(OmniDrive driveSubsystem, double targetX, double targetY) 
  {
    // Use addRequirements() here to declare subsystem dependencies.

    this.driveSubsystem = driveSubsystem;
    this.targetPosition = new Translation2d(targetX, targetY);
    addRequirements(driveSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    timer.reset();
    timer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
     // Calculate the current position error
     double xError = targetPosition.getX() - driveSubsystem.getXPosition();
     double yError = targetPosition.getY() - driveSubsystem.getYPosition();

     // Calculate the distance to the target
     double distanceToTarget = Math.sqrt(xError * xError + yError * yError);

     kDistanceToTarget=distanceToTarget;

     // Calculate the angle to the target
     double angleToTarget = Math.atan2(yError, xError);

     // Convert angle to radians
     angleToTarget = Math.toRadians(angleToTarget);

     // Calculate the desired robot angle (using the angle to target and current robot angle)
     double desiredRobotAngle = driveSubsystem.getRobotAngle() + angleToTarget;

     // Use a proportional controller to adjust the robot angle
     double angleControllerOutput = 0.5 * (desiredRobotAngle - driveSubsystem.getRobotAngle());

     // Calculate the desired speed for the robot (using the distance to target and a proportional controller)
     double speedControllerOutput = 0.5 * distanceToTarget;

     // Set the drive speeds for the robot
     driveSubsystem.holonomicDrive(speedControllerOutput * Math.cos(driveSubsystem.getRobotAngle()),
                                  speedControllerOutput * Math.sin(driveSubsystem.getRobotAngle()),
                                  angleControllerOutput);

     // Set the drive speeds for the robot
     /*driveSubsystem.driveMotorControl(speedControllerOutput * Math.cos(driveSubsystem.getRobotAngle()),
                                  speedControllerOutput * Math.sin(driveSubsystem.getRobotAngle()),
                                  angleControllerOutput);*/

     // Publish data to the SmartDashboard
     SmartDashboard.putNumber("X Error", xError);
     SmartDashboard.putNumber("Y Error", yError);
     SmartDashboard.putNumber("Distance to Target", distanceToTarget);
     SmartDashboard.putNumber("Angle to Target", angleToTarget);
     SmartDashboard.putNumber("Desired Robot Angle", desiredRobotAngle);
     SmartDashboard.putNumber("Speed Output", speedControllerOutput);
     SmartDashboard.putNumber("Angle Output", angleControllerOutput);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    driveSubsystem.stopMotors();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() 
  {
    return kDistanceToTarget <= tolerance || timer.get() >= 5.0; // Timeout after 5 seconds
  }
}
