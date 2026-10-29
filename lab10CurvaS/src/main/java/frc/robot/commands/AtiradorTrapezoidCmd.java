/*----------------------------------------------------------------------------*/
/* WorldSkills - Lab 10: controle de movimento com curva S (jerk limitado)    */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.AtiradorConstants;
import frc.robot.subsystems.AtiradorSubsystem;

/**
 * Mesmo movimento, com o {@link TrapezoidProfile} que ja vem no WPILib.
 * <p>
 * Serve de linha de base para a curva S. O trapezio limita velocidade e
 * aceleracao, e so isso. A aceleracao vale +a_max, depois 0, depois -a_max, em
 * degraus: o jerk e infinito em cada troca. No eixo isso vira tranco na
 * partida, tranco no inicio da frenagem e, se houver folga, batida.
 * <p>
 * Note tambem que o trapezio chega ANTES da curva S para os mesmos limites de
 * velocidade e aceleracao - ele tem fase de velocidade constante, a curva S
 * quintica nao. O ganho de maciez custa tempo.
 */
public class AtiradorTrapezoidCmd extends CommandBase {

  /** Periodo nominal do scheduler, usado para derivar a aceleracao do perfil. */
  private static final double kDt = 0.02;

  private final AtiradorSubsystem atiradorSubsystem;
  private final double targetMM;
  private final Timer timer = new Timer();

  private TrapezoidProfile profile;

  /**
   * @param atiradorSubsystem subsystem do atirador
   * @param targetMM          distancia a percorrer em mm
   */
  public AtiradorTrapezoidCmd(AtiradorSubsystem atiradorSubsystem, double targetMM) {
    this.atiradorSubsystem = atiradorSubsystem;
    this.targetMM = targetMM;
    addRequirements(atiradorSubsystem);
  }

  @Override
  public void initialize() {
    atiradorSubsystem.resetPositionControl();

    TrapezoidProfile.Constraints constraints =
        new TrapezoidProfile.Constraints(AtiradorConstants.kMaxVelocityMMps,
                                         AtiradorConstants.kMaxAccelerationMMps2);

    profile = new TrapezoidProfile(constraints,
                                   new TrapezoidProfile.State(targetMM, 0.0),
                                   new TrapezoidProfile.State(atiradorSubsystem.getPositionMM(), 0.0));

    SmartDashboard.putString("Atirador/Perfil", "Trapezoidal");
    SmartDashboard.putNumber("Atirador/Perfil Duracao s", profile.totalTime());

    timer.reset();
    timer.start();
  }

  @Override
  public void execute() {
    double t = timer.get();
    TrapezoidProfile.State setpoint = profile.calculate(t);

    // O TrapezoidProfile devolve posicao e velocidade, mas nao aceleracao.
    // Derivando por diferenca finita sai a onda quadrada que e justamente o que
    // a curva S vem consertar.
    double previousVelocity = profile.calculate(Math.max(0.0, t - kDt)).velocity;
    double acceleration = (setpoint.velocity - previousVelocity) / kDt;

    atiradorSubsystem.followSetpoint(setpoint.position, setpoint.velocity, acceleration);
  }

  @Override
  public void end(boolean interrupted) {
    timer.stop();

    if (interrupted) {
      atiradorSubsystem.holdAt(atiradorSubsystem.getPositionMM());
    } else {
      atiradorSubsystem.holdAt(targetMM);
    }
  }

  @Override
  public boolean isFinished() {
    return profile.isFinished(timer.get()) && atiradorSubsystem.atPosition();
  }
}
