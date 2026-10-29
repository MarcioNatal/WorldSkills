/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;



// WPI imports
import edu.wpi.first.wpilibj2.command.CommandBase;



////Subsystem imports

import frc.robot.subsystems.AtiradorSubsystem;


/**
 * AtiradorToPositionCmd class
 * <p>
 * Espelho do {@link DriveToPosition}, porem SEM PID: o controle e liga/desliga
 * (bang-bang). A saida e sempre 0.5 ate passar do alvo e 0 depois, sem
 * desaceleracao nenhuma perto do setpoint.
 * <p>
 * Use os dois comandos lado a lado para mostrar a diferenca: este passa do
 * ponto e para onde parou, o DriveToPosition encosta no alvo e fica.
 */

public class AtiradorToPositionCmd extends CommandBase
{
  //Grab the subsystem instance from RobotContainer
  private final AtiradorSubsystem atiradorSubsystem;

  //Distancia alvo em mm, relativa ao ponto onde o comando comecou
  private final double targetMM;


  /**
   * Constructor
   */
  /**
   * Creates a new AtiradorToPositionCmd.
   *
   * @param atiradorSubsystem subsystem do atirador
   * @param targetMM distancia a percorrer em mm (apenas positiva: o bang-bang
   *                 so liga o motor para frente)
   */
  public AtiradorToPositionCmd( AtiradorSubsystem atiradorSubsystem, double targetMM)
  {
    this.atiradorSubsystem = atiradorSubsystem;
    this.targetMM = targetMM;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(atiradorSubsystem); // Adds the subsystem to the command
  }

  // Called when the command is initially scheduled.
  /**
   * Runs before execute
   */
  @Override
  public void initialize()
  {
    atiradorSubsystem.resetPositionControl(); // zera encoder e a flag de chegada

  }

  // Called every time the scheduler runs while the command is scheduled.
  /**
   * Called continously until command is ended
   */
  @Override
  public void execute()
  {
    //atiradorSubsystem.runToPosition(targetMM); // uma decisao liga/desliga por ciclo

    atiradorSubsystem.setMotorPosition(targetMM);
  }


  /**
   * Called when the command is told to end or is interrupted
   */
  @Override
  public void end(boolean interrupted)
  {
    //atiradorSubsystem.stopMotorAtirador(); // Stop motor
    //atiradorSubsystem.setMotorPosition(atiradorSubsystem.getEncoderAtiradorPosition());
    atiradorSubsystem.setMotorPosition(targetMM);
    //atiradorSubsystem.holdMotor();
  }

  // Returns true when the command should end.
  /**
   * Creates an isFinished condition if needed
   */
  @Override
  public boolean isFinished()
  {
    return atiradorSubsystem.atPosition();
  }
}
