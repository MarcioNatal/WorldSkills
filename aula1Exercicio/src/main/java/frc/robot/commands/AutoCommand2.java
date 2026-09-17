/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.OmniDrive;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class AutoCommand2 extends SequentialCommandGroup 
{
  private final OmniDrive mOmniDrive;
  /**
   * Creates a new AutoCommand2.
   */
  public AutoCommand2(OmniDrive omniDrive) 
  {
    this.mOmniDrive = omniDrive;
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());
    addCommands(new MultiDrive(mOmniDrive,0.5, 0.0, -0.5).withTimeout(5), 
                new MultiDrive(mOmniDrive,0.2, 0.6, -0.2).withTimeout(2));
  }
}
