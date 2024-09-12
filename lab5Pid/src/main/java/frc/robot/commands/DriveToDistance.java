/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.OmniDrive;

public class DriveToDistance extends CommandBase 
{
  //Grab the subsystem instance from RobotContainer
  private static final OmniDrive omniDrive = RobotContainer.omniDrive;

  private double distanceSetpoint;

  private final PIDController pidController;
  

  //PID Constants
  private double kP = 0.05;
  private double kI = 0.0;
  private double kD = 0.0;
  //
    
  //private boolean finish = false;

  /**
   * Creates a new DriveToDistance.
   */
  public DriveToDistance(double milimetro ) 

  {
    pidController = new PIDController(kP, kI, kD);
    
    // Use addRequirements() here to declare subsystem dependencies.
    
    addRequirements(omniDrive); // Adds the subsystem to the command
    distanceSetpoint = milimetro;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() 
  {

    System.out.println("Drive to distance started!");
    pidController.reset();
    omniDrive.resetEncoders();
    omniDrive.resetYaw();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() 
  {
    double speed = pidController.calculate(omniDrive.getAverageForwardEncoderDistance(),distanceSetpoint);

    omniDrive.holonomicDrive(0.0, -speed, 0.0);
    SmartDashboard.putNumber("Distancia Percorrida", omniDrive.getAverageForwardEncoderDistance()) ;
    SmartDashboard.putNumber("Setpoint distancia", distanceSetpoint);
    

  }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) 
    {
      omniDrive.stopMotors();
      System.out.println("Drive to distance stopped!");

    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() 
    {
      return ((omniDrive.getAverageForwardEncoderDistance()) >= distanceSetpoint);
    }
}
