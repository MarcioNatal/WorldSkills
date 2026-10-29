/*----------------------------------------------------------------------------*/
/* WorldSkills - Lab 10: controle de movimento com curva S (jerk limitado)    */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.AtiradorConstants;
import frc.robot.controllers.SCurveProfile;
import frc.robot.subsystems.AtiradorSubsystem;

/**
 * Vai ate a posicao alvo seguindo uma curva S de jerk limitado.
 * <p>
 * Compare com {@link AtiradorTrapezoidCmd} e {@link AtiradorStepCmd} plotando
 * "Atirador/Setpoint Acel mmps2": aqui a aceleracao sobe e desce em rampa, nos
 * outros ela salta. O salto e o tranco.
 */
public class AtiradorSCurveCmd extends CommandBase {

  private final AtiradorSubsystem atiradorSubsystem;
  private final double targetMM;
  private final Timer timer = new Timer();

  private SCurveProfile profile;

  /**
   * @param atiradorSubsystem subsystem do atirador
   * @param targetMM          distancia a percorrer em mm, contada do ponto
   *                          onde o comando comecou (pode ser negativa)
   */
  public AtiradorSCurveCmd(AtiradorSubsystem atiradorSubsystem, double targetMM) {
    this.atiradorSubsystem = atiradorSubsystem;
    this.targetMM = targetMM;
    addRequirements(atiradorSubsystem);
  }

  @Override
  public void initialize() {
    atiradorSubsystem.resetPositionControl();

    // O perfil tem que ser montado no initialize, nao no construtor: no
    // construtor (que roda no RobotContainer, uma vez) a posicao inicial ainda
    // nao existe, e o comando pode ser agendado varias vezes.
    SCurveProfile.Constraints constraints =
        new SCurveProfile.Constraints(AtiradorConstants.kMaxVelocityMMps,
                                      AtiradorConstants.kMaxAccelerationMMps2,
                                      AtiradorConstants.kMaxJerkMMps3);

    profile = new SCurveProfile(constraints, atiradorSubsystem.getPositionMM(), targetMM);

    SmartDashboard.putString("Atirador/Perfil", "Curva S");
    SmartDashboard.putNumber("Atirador/Perfil Duracao s", profile.totalTime());

    timer.reset();
    timer.start();
  }

  @Override
  public void execute() {
    double t = timer.get();
    SCurveProfile.State setpoint = profile.calculate(t);

    atiradorSubsystem.followSetpoint(setpoint.position, setpoint.velocity, setpoint.acceleration);

    // Jerk desejado: finito em todo o movimento. No trapezio este numero seria
    // infinito nas bordas, por isso nem faz sentido plotar lado a lado.
    SmartDashboard.putNumber("Atirador/Setpoint Jerk mmps3", profile.jerk(t));
  }

  @Override
  public void end(boolean interrupted) {
    timer.stop();
    SmartDashboard.putNumber("Atirador/Setpoint Jerk mmps3", 0.0);

    if (interrupted) {
      // Interrompido no meio: segura onde parou, nao no alvo, para nao dar um
      // degrau justamente no comando que existe para evitar degraus.
      atiradorSubsystem.holdAt(atiradorSubsystem.getPositionMM());
    } else {
      atiradorSubsystem.holdAt(profile.goalPosition());
    }
  }

  @Override
  public boolean isFinished() {
    // Espera o perfil acabar E o eixo chegar. Sem a segunda condicao o comando
    // terminaria no tempo certo mesmo com o eixo atrasado por falta de
    // feedforward.
    return profile.isFinished(timer.get()) && atiradorSubsystem.atPosition();
  }
}
