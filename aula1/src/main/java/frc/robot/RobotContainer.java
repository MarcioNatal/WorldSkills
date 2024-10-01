/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.AutoCommand;
import frc.robot.commands.DriveMotor;
import frc.robot.commands.SimpleDrive;

import frc.robot.subsystems.OmniDrive;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
 private final OmniDrive omniDrive;
 private final SimpleDrive simpleDrive;
 private final DriveMotor driveMotor;
 private final AutoCommand autoCommand;

 //Define the auto selector
 private final SendableChooser<Command> autoChooser = new SendableChooser<>();




  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() 
  {
    // Configure the button bindings
    configureButtonBindings();

    /**
     * Create an instance of the subsystems
     */
    omniDrive = new OmniDrive();


     /**
     * Create an instance of the commands
     */
    simpleDrive = new SimpleDrive ();
    driveMotor = new DriveMotor( );
    autoCommand = new AutoCommand();

    /*********************************
    * Declare objects to inititalize *
    * Set the default commands       *
    **********************************/
    //set default command to autocommand
    simpleDrive.addRequirements(omniDrive);
    driveMotor.addRequirements(omniDrive);
    autoCommand.addRequirements(omniDrive);



  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() 
  {
    // Setup SmartDashboard options

    autoChooser.setDefaultOption("Auto Routine", new AutoCommand());
    autoChooser.addOption("Auto Time", new DriveMotor( ));
    SmartDashboard.putData(autoChooser);

  }


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() 
  {
    // An ExampleCommand will run in autonomous
    return autoChooser.getSelected();
  }
}
