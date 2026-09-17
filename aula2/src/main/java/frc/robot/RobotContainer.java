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
import frc.robot.commands.MultiDrive;
import frc.robot.subsystems.OmniDrive;
 
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;


/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
 private final OmniDrive omniDrive = new OmniDrive();
 
 private final MultiDrive multiDrive = new MultiDrive(omniDrive, 0, 0, 0);

 //Declara joystick
 public static XboxController driverJoystick = new XboxController (Constants.kDriverControllerPort);

 //Define the auto selector
 private final SendableChooser<Command> autoChooser = new SendableChooser<>();




  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() 
  {
    // Configure the button bindings
    configureButtonBindings();

    

    
    /*********************************
    * Declare objects to inititalize *
    * Set the default commands       *
    **********************************/
    //set default command to autocommand
    
    


    //set default command to drive with joystick

    omniDrive.setDefaultCommand (new RunCommand(
                                () -> omniDrive.holonomicDrive (
                                                driverJoystick.getRawAxis(Constants.kDriverXAxis),
                                                driverJoystick.getRawAxis(Constants.kDriverYAxis),
                                                driverJoystick.getRawAxis(Constants.kDriverRotAxis)),
                                                omniDrive
                                                )
                                );



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

    //autoChooser.setDefaultOption("Auto Routine", new AutoCommand(omniDrive));
    //autoChooser.addOption("Auto Time", new DriveMotor(omniDrive ));
    //autoChooser.addOption("Auto Novo", new AutoCommand2());
    SmartDashboard.putData(autoChooser);

    //JoystickButton motorTempo = new JoystickButton(driverJoystick, XboxController.Button.kBumperLeft.value);
    //motorTempo.whenPressed(new DriveMotor(omniDrive));

    //Liga o motor1 enquanto estiver pressionado o botao 
    //JoystickButton motor1Forward = new JoystickButton (driverJoystick,XboxController.Button.kBumperRight.value);
    //motor1Forward.whenPressed(new SimpleDrive());

    
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
