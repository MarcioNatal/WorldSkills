/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;
//WPI imports

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

//Classes imports
import frc.robot.RobotContainer;
import frc.robot.subsystems.OmniDrive;

public class Turn extends CommandBase 
{
  private static final OmniDrive omniDrive = RobotContainer.omniDrive;
  private double angleSetpoint;
  private double turnSpeed;
  //private double wheelTrack = Units.metersToInches(0.2);


  /**
   * Creates a new Turn.
   */
  public Turn(double angle) 
  {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(omniDrive);

    /**How far do we need to turn */
    //inches = Math.abs(angle) * Math.PI* wheelTrack / 360;
    angleSetpoint = angle;
    turnSpeed = 0.5*Math.signum(angle);

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {
    omniDrive.resetEncoders();
    omniDrive.resetYaw();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    omniDrive.holonomicDrive(0.0, 0.0, turnSpeed);
    SmartDashboard.putNumber("Setpoint", angleSetpoint);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    omniDrive.holonomicDrive(0, 0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return  Math.abs(omniDrive.getYaw())>=angleSetpoint;
  }
}
