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

//Command imports
import frc.robot.commands.SimpleDrive;
import frc.robot.commands.Turn;
import frc.robot.commands.AutoCommand;
import frc.robot.commands.DriveToDistance;
import frc.robot.commands.Labyrinth;

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
  //Subsystems
  public  static OmniDrive omniDrive;


  //Commands
  public static SimpleDrive simpleDrive; 
  public static AutoCommand autoCommand;
  //public static DriveToDistance driveToDistance;

  //Controllers
  public static XboxController driverJoystick; 
  


  

   // Create SmartDashboard chooser for autonomous routines
   private final SendableChooser<Command> m_chooser = new SendableChooser<>();

  
  /**
   * Constructor
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  
  public RobotContainer() 
  {

    /**
     * Create an instance of the subsystems
     */
    omniDrive = new OmniDrive();

     /**
     * Create an instance of the commands
     */
    autoCommand = new AutoCommand( );
    simpleDrive = new SimpleDrive ( );
    //driveToDistance = new DriveToDistance(0);

    //instantiate Xbox Controller
    driverJoystick = new XboxController(Constants.kDriverControllerPort);



    /*********************************
    * Declare objects to inititalize *
    * Set the default commands       *
    **********************************/
    //set default command to autocommand
    autoCommand.addRequirements(omniDrive);
    simpleDrive.addRequirements (omniDrive);
    //driveToDistance.addRequirements(omniDrive);

    //driveSubsystem.setDefaultCommand(manualDriveCommnand);//command that subsystem will look for

    //set default commmand to drive autonomous mode
    //autonomousOne.addRequirements(driveSubsystem);


    //set default command to drive with joystick
/*
    swerveSubsystem.setDefaultCommand(new SwerveJoystickCmd(
                swerveSubsystem,vision,
                () -> -driverJoystick.getRawAxis(OIConstants.kDriverYAxis),
                () -> driverJoystick.getRawAxis(OIConstants.kDriverXAxis),
                () -> driverJoystick.getRawAxis(OIConstants.kDriverRotAxis),
                () -> !driverJoystick.getRawButton(OIConstants.kDriverFieldOrientedButtonIdx)));*/

    omniDrive.setDefaultCommand (new RunCommand (
      () -> omniDrive.holonomicDrive (
        driverJoystick.getRawAxis(Constants.kDriverXAxis),
        driverJoystick.getRawAxis(Constants.kDriverYAxis),
        driverJoystick.getRawAxis(Constants.kDriverRotAxis)
        ),
        omniDrive
        )
        );
                                          


    
    // Configure the button bindings
    configureButtonBindings();

    //Add to choices to smart dashboard
    //
    
    
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
    m_chooser.setDefaultOption("Auto Routine", new AutoCommand());
    m_chooser.addOption("Auto Time", new SimpleDrive( ));
    m_chooser.addOption("Auto Distancia", new DriveToDistance(500 ));
    m_chooser.addOption("Auto Rotacionar", new Turn (90));
    m_chooser.addOption("Auto Labirinto", new Labyrinth());
    SmartDashboard.putData(m_chooser);

    JoystickButton motor1Forward = new JoystickButton (driverJoystick,XboxController.Button.kBumperLeft.value);
    motor1Forward.whenPressed(new SimpleDrive( ));



  }


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
    public Command getAutonomousCommand() 
    {
      //the call to return the autonomous routine that we want the scheduler to run.
      return m_chooser.getSelected();
    }
}
