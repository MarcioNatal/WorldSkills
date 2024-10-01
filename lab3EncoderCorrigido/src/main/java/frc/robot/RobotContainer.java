/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//WPI imports
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.commands.DriveToDistance;
//Command imports
import frc.robot.commands.SimpleDrive;


//Subsystem imports

import frc.robot.subsystems.OmniDrive;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer 
{
  // The robot's subsystems and commands are defined here...
  public  static OmniDrive drive;

  public static SimpleDrive simpleDrive; 

  public static XboxController driverJoystick; 
  

//Define the auto selector
private final SendableChooser<Command> autoChooser = new SendableChooser<>();
  //Define the auto selector
 

  
  /**
   * Constructor
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  
  public RobotContainer() 
  {

    /**
     * Create an instance of the subsystems
     */
    drive = new OmniDrive();


     /**
     * Create an instance of the commands
     */
    simpleDrive = new SimpleDrive ();

    //instantiate Xbox Controller
    driverJoystick = new XboxController(Constants.kDriverControllerPort);


    //set default command to drive with joystick

    

    drive.setDefaultCommand (new RunCommand (
                            () -> drive.holonomicDrive (
                              driverJoystick.getRawAxis(Constants.kDriverXAxis),
                              driverJoystick.getRawAxis(Constants.kDriverYAxis),
                              driverJoystick.getRawAxis(Constants.kDriverRotAxis)
                              ),
                              drive
                              )
                              );
                                          

    
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() 
  {
      
    JoystickButton motor1Forward = new JoystickButton (driverJoystick,XboxController.Button.kBumperLeft.value);
    motor1Forward.whenPressed(new SimpleDrive());


    //autoChooser.setDefaultOption("Auto Routine", new AutoCommand());
    autoChooser.addOption("Auto Time", new DriveToDistance( ));
    SmartDashboard.putData(autoChooser);



  }


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
    public Command getAutonomousCommand() 
    {
      //the call to return the autonomous routine that we want the scheduler to run.
      return autoChooser.getSelected();
    }
}
