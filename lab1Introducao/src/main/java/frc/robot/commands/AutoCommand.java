/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;


//WPI imports

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html

/**
 * AutoCommand Class
 * <p>
 * This class is used to create the inline command stackup for autonomous routines
 */
public class AutoCommand extends SequentialCommandGroup 
{
  /**
   * Creates a new AutoCommand.
   */
  public AutoCommand() 
  {
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());
    addCommands( new SimpleDrive().withTimeout(1.5));
  }
  
}
