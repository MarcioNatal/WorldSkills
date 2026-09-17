/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.SquareAutoCommand;
import frc.robot.subsystems.OmnidriveSubsystem;


/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer 
{
  // The robot's subsystems and commands are defined here...
  private final OmnidriveSubsystem driveSubsystem = new OmnidriveSubsystem();
  private final Joystick joystick = new Joystick(OIConstants.kJoystickPort);


  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() 
  {
    // Configure the button bindings
    configureButtonBindings();

    configureDefaultCommands();

  }

  /**
   * Zona morta com reescalonamento: abaixo do limiar devolve 0, acima disso
   * o valor volta a variar de 0 ate 1 (sem degrau na saida do joystick).
   *
   * @param value    leitura bruta do eixo (-1.0 a 1.0)
   * @param deadband largura da zona morta (0.0 a 1.0)
   * @return valor filtrado e reescalonado
   */
  private static double applyDeadband(double value, double deadband) 
  {
    if (Math.abs(value) > deadband) 
    {
      return (value - Math.copySign(deadband, value)) / (1.0 - deadband);
    }
    return 0.0;
  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings()
  {
    // Gatilho do joystick: zera a referencia de campo (heading atual vira 0 graus)
    new JoystickButton(joystick, OIConstants.kResetGyroButton)
                      .whenPressed(new InstantCommand(driveSubsystem::resetHeading));
  }

  private void configureDefaultCommands() 
  {
    driveSubsystem.setDefaultCommand(
        new RunCommand(
            () -> driveSubsystem.driveFieldOriented(
                                applyDeadband(joystick.getX(), OIConstants.kDeadband),
                                applyDeadband(-joystick.getY(), OIConstants.kDeadband),
                                applyDeadband(joystick.getZ(), OIConstants.kDeadband)
            ),
            driveSubsystem
        )
    );
  }


  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() 
  {
    // Quadrado de 50 cm de lado, por translacao pura
    return new SquareAutoCommand(driveSubsystem);
  }
}
