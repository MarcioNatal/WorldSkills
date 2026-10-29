/*----------------------------------------------------------------------------*/
/* WorldSkills - Lab 10: controle de movimento com curva S (jerk limitado)    */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.AtiradorSubsystem;

/**
 * O caso sem perfil nenhum: joga o alvo final no PID de uma vez (degrau).
 * <p>
 * E o que o lab 1 faz. O PID comeca com erro maximo, satura a tensao, e a
 * forma do movimento passa a ser decidida pelos ganhos e pela saturacao, nao
 * por um plano. Serve de pior caso na comparacao: aqui nao existe limite de
 * velocidade, de aceleracao nem de jerk.
 */
public class AtiradorStepCmd extends CommandBase {

  private final AtiradorSubsystem atiradorSubsystem;
  private final double targetMM;

  /**
   * @param atiradorSubsystem subsystem do atirador
   * @param targetMM          distancia a percorrer em mm
   */
  public AtiradorStepCmd(AtiradorSubsystem atiradorSubsystem, double targetMM) {
    this.atiradorSubsystem = atiradorSubsystem;
    this.targetMM = targetMM;
    addRequirements(atiradorSubsystem);
  }

  @Override
  public void initialize() {
    atiradorSubsystem.resetPositionControl();
    SmartDashboard.putString("Atirador/Perfil", "Degrau (sem perfil)");
    SmartDashboard.putNumber("Atirador/Perfil Duracao s", 0.0);
  }

  @Override
  public void execute() {
    // Velocidade e aceleracao desejadas = 0: o feedforward nao tem o que
    // antecipar, sobra tudo para o feedback.
    atiradorSubsystem.followSetpoint(targetMM, 0.0, 0.0);
  }

  @Override
  public void end(boolean interrupted) {
    atiradorSubsystem.holdAt(interrupted ? atiradorSubsystem.getPositionMM() : targetMM);
  }

  @Override
  public boolean isFinished() {
    return atiradorSubsystem.atPosition();
  }
}
