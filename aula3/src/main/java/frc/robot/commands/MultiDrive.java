/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;




import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.OmniDrive;

public class MultiDrive extends CommandBase 
{
  /**
   * Creates a new MultiDrive.
   */
  private static final OmniDrive omniDrive = RobotContainer.omniDrive;
  private double m_duration = 0;
  private boolean finish = false;
  

  private static double velocidade1;
  private static double velocidade2;
  private static double velocidade3;
  
  public MultiDrive(double speedM1, double speedM2, double speedM3) 
  {
    velocidade1 = speedM1;
    velocidade2 = speedM2;
    velocidade3 = speedM3;
    
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(omniDrive);
    
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
    omniDrive.set3MotorSpeed(velocidade1, velocidade2,  velocidade3);

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) 
  {
    omniDrive.set3MotorSpeed(0.0, 0.0,  0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() 
  {
    
    if(m_duration>100){finish =true;}
    m_duration += m_duration;
    
    return finish;

  }
}
