/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//java imports

//WPI imports
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
//Vendor imports
import com.studica.frc.Titan;
//Command imports
import frc.robot.commands.SimpleDrive;
import frc.robot.commands.AtiradorToPositionCmd;
import frc.robot.commands.AutoCommand;
import frc.robot.commands.DriveMotor;
import frc.robot.commands.DriveToPosition;
//Subsystem imports

import frc.robot.Constants;
import frc.robot.subsystems.AtiradorSubsystem;
import frc.robot.subsystems.DriveTrainSubsystem;
import frc.robot.subsystems.GarraSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  // Um unico objeto Titan para o TitanQuad, compartilhado pelos subsystems.
  private final Titan titan = new Titan(Constants.TITAN_ID);

  private final DriveTrainSubsystem driveSubsystem;

  private final GarraSubsystem garraSubsystem;

  private final AtiradorSubsystem atiradorSubsystem;

  private final SimpleDrive simpleDrive;
  private final AutoCommand autoCommand;

  private final XboxController joystick = new XboxController(0);

  // Define the auto selector
  private final SendableChooser<Command> autoChooser = new SendableChooser<>();
  // public static Map<String, AutoCommand> autoMode = new HashMap<>();

  /**
   * Constructor The container for the robot. Contains subsystems, OI devices, and
   * commands.
   */

  public RobotContainer() {

    /**
     * Create an instance of the subsystems
     */
    driveSubsystem = new DriveTrainSubsystem(titan);
    garraSubsystem = new GarraSubsystem(titan);
    atiradorSubsystem = new AtiradorSubsystem(titan);

    /**
     * Create an instance of the commands
     */
    simpleDrive = new SimpleDrive(driveSubsystem);
    autoCommand = new AutoCommand(driveSubsystem);

    // set default command to autocommand
    garraSubsystem.setDefaultCommand(new RunCommand(() -> garraSubsystem.stopMotorGarra(), garraSubsystem));

    // Estado seguro do drive: parado quando nenhum comando o requer
    driveSubsystem.setDefaultCommand(new RunCommand(() -> driveSubsystem.stopMotorDrive(), driveSubsystem));

    // Sem default command que escreva no motor: o hold de posicao do PID
    // continuaria sendo apagado por um stopMotorAtirador() a cada ciclo.
    /*atiradorSubsystem.setDefaultCommand(new RunCommand(() -> {
    }, atiradorSubsystem));*/

    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {

    // Setup SmartDashboard options

    autoChooser.setDefaultOption("None", new InstantCommand());
    autoChooser.addOption("Auto Time", new DriveMotor(driveSubsystem));
    autoChooser.addOption("Auto Posicao 300 mm", new DriveToPosition(driveSubsystem, 300.0));
    SmartDashboard.putData(autoChooser);

    // new Trigger(()->driveSubsystem.atSpeed()).whenActive(new
    // DriveToPosition(driveSubsystem, 50.0));

    // Drive gira enquanto a garra estiver em velocidade. whileActiveContinuous
    // cancela o comando na borda de descida; o default command volta a parar o
    // motor.
    new Trigger(() -> garraSubsystem.isAtSpeed()).whileActiveContinuous(driveSubsystem.ligaMotor());

    JoystickButton ligaMotorGarra = new JoystickButton(joystick, XboxController.Button.kBumperLeft.value);

    ligaMotorGarra.whileHeld(new RunCommand(() -> garraSubsystem.setMotorGarra(), garraSubsystem));

    new JoystickButton(joystick, XboxController.Button.kA.value).whenPressed(new AtiradorToPositionCmd(atiradorSubsystem, 100));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // the call to return the autonomous routine that we want the scheduler to run.

    return autoChooser.getSelected();
  }
}
