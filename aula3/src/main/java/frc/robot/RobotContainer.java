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

import frc.robot.commands.DriveMotor;
import frc.robot.commands.DriveToDistance;

import frc.robot.commands.SimpleDrive;

import frc.robot.subsystems.OmniDrive;
 
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer 
{
  // The robot's subsystems and commands are defined here...
 public static OmniDrive omniDrive;
 /*private final SimpleDrive simpleDrive;
 private final DriveMotor driveMotor;
 private final AutoCommand autoCommand;
 private final AutoCommand2 autoCommand2;
 private final MultiDrive multiDrive;*/
 //private final DriveToDistance driveToDistance;

 //Declara joystick
 public static XboxController driverJoystick;

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
    /*simpleDrive = new SimpleDrive ();
    driveMotor = new DriveMotor( );
    autoCommand = new AutoCommand();

    autoCommand2 = new AutoCommand2();
    multiDrive = new MultiDrive(0.0, 0.0, 0.0);*/
    //driveToDistance = new DriveToDistance();


    driverJoystick = new XboxController (Constants.kDriverControllerPort);

    /*********************************
    * Declare objects to inititalize *
    * Set the default commands       *
    **********************************/
    //set default command to autocommand
    /*simpleDrive.addRequirements(omniDrive);
    driveMotor.addRequirements(omniDrive);
    autoCommand.addRequirements(omniDrive);
    multiDrive.addRequirements(omniDrive);
    autoCommand2.addRequirements(omniDrive);*/


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

    autoChooser.setDefaultOption("Auto Routine", new DriveToDistance());
    autoChooser.addOption("Auto Time", new DriveMotor( omniDrive));
    //autoChooser.addOption("Auto Novo", new AutoCommand2());
    //autoChooser.addOption("Auto Distancia", new DriveToDistance());
    SmartDashboard.putData(autoChooser);

    JoystickButton motorTempo = new JoystickButton(driverJoystick, XboxController.Button.kBumperLeft.value);
    motorTempo.whenPressed(new DriveMotor(omniDrive));

    //Liga o motor1 enquanto estiver pressionado o botao 
    JoystickButton motor1Forward = new JoystickButton (driverJoystick,XboxController.Button.kBumperRight.value);
    motor1Forward.whenPressed(new SimpleDrive());

    
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
