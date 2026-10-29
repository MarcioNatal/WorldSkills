/*----------------------------------------------------------------------------*/
/* WorldSkills - Lab 10: controle de movimento com curva S (jerk limitado)    */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.studica.frc.Titan;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.AtiradorConstants;

/**
 * Eixo de posicao do atirador, com a malha fechada no roboRIO.
 * <p>
 * Divisao de trabalho deste lab:
 * <ul>
 *   <li>o <b>comando</b> e dono do perfil e do relogio: ele diz, a cada ciclo,
 *       qual posicao/velocidade/aceleracao o eixo deveria ter AGORA;</li>
 *   <li>o <b>subsystem</b> e dono da malha: converte esse pedido em volts
 *       (feedforward + PID) e, quando o comando acaba, segura a posicao final
 *       no {@link #periodic()}.</li>
 * </ul>
 * Com isso trocar o tipo de perfil (degrau, trapezio, curva S) nao mexe em
 * nada aqui: muda so quem calcula o setpoint.
 * <p>
 * Tudo em mm, mm/s e mm/s^2, porque o encoder mede mm e os ganhos estao em
 * V/mm.
 */
public class AtiradorSubsystem extends SubsystemBase {

  private final Titan.Motor motorAtirador;
  private final Titan.Encoder encoderAtirador;

  private final PIDController pidController = new PIDController(AtiradorConstants.kPositionP,
                                                                AtiradorConstants.kPositionI,
                                                                AtiradorConstants.kPositionD);

  private final SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(AtiradorConstants.ks,
                                                                                AtiradorConstants.kv,
                                                                                AtiradorConstants.ka);

  /** Enquanto verdadeiro, o periodic() segura a posicao sozinho. */
  private boolean holding = false;

  /** Ultimo setpoint de posicao escrito na malha, em mm. */
  private double targetPosition = 0.0;

  // Guardados so para a telemetria: e o grafico da velocidade e, principalmente,
  // da aceleracao que mostra a diferenca entre trapezio e curva S.
  private double setpointVelocity = 0.0;
  private double setpointAcceleration = 0.0;
  private double appliedVolts = 0.0;

  /**
   * @param titan controlador compartilhado. Nao criar outro Titan aqui:
   *              um CAN ID = um objeto Titan.
   */
  public AtiradorSubsystem(Titan titan) {
    motorAtirador = titan.getMotor(AtiradorConstants.M3);
    encoderAtirador = titan.getEncoder(AtiradorConstants.kEncoder3Channel,
                                       AtiradorConstants.wheelDistPerTick);

    pidController.setTolerance(AtiradorConstants.kPositionToleranceMM);

    resetPositionControl();
  }

  @Override
  public void periodic() {
    // Depois que o perfil termina, o comando chama holdAt() e sai. Quem continua
    // corrigindo e este periodic, que roda em todo ciclo do scheduler mesmo sem
    // comando agendado. E isso que faz o eixo voltar se alguem empurrar.
    if (holding) {
      setpointVelocity = 0.0;
      setpointAcceleration = 0.0;
      driveVolts(pidController.calculate(getPositionMM(), targetPosition));
    }

    telemetria();
  }

  /**
   * Zera o ponto de partida e o estado do PID. Chamar no initialize() do
   * comando, antes de montar o perfil.
   */
  public void resetPositionControl() {
    encoderAtirador.reset();
    pidController.reset();
    holding = false;
    targetPosition = 0.0;
    setpointVelocity = 0.0;
    setpointAcceleration = 0.0;
  }

  /**
   * Segue um ponto do perfil. Precisa ser chamado a cada ciclo enquanto o
   * movimento acontece.
   * <p>
   * A conta e sempre a mesma: o feedforward diz quanta tensao o movimento
   * pedido deveria custar, e o PID corrige o que sobrou. Com um perfil suave o
   * feedforward acerta quase tudo e o PID trabalha pouco; com um degrau o
   * feedforward nao tem o que fazer e o PID leva o eixo no empurrao.
   *
   * @param positionMM     posicao desejada agora, em mm
   * @param velocityMMps   velocidade desejada agora, em mm/s
   * @param accelMMps2     aceleracao desejada agora, em mm/s^2
   */
  public void followSetpoint(double positionMM, double velocityMMps, double accelMMps2) {
    holding = false; // quem escreve no motor agora e o comando, nao o periodic

    targetPosition = positionMM;
    setpointVelocity = velocityMMps;
    setpointAcceleration = accelMMps2;

    driveVolts(feedforward.calculate(velocityMMps, accelMMps2)
               + pidController.calculate(getPositionMM(), positionMM));
  }

  /**
   * Arma o hold de posicao: basta chamar uma vez, o periodic cuida do resto.
   *
   * @param positionMM posicao a segurar, em mm
   */
  public void holdAt(double positionMM) {
    targetPosition = positionMM;
    holding = true;
  }

  /**
   * Desliga a malha e para o motor.
   */
  public void stop() {
    holding = false;
    setpointVelocity = 0.0;
    setpointAcceleration = 0.0;
    appliedVolts = 0.0;
    motorAtirador.stopMotor();
  }

  /**
   * @return posicao do eixo em mm, contada desde o resetPositionControl()
   */
  public double getPositionMM() {
    return encoderAtirador.getDistance();
  }

  /**
   * Velocidade medida convertida para a mesma unidade do perfil.
   * <p>
   * getSpeed() devolve RPM do eixo do encoder; uma volta do encoder vale
   * pulsePerRevolution * wheelDistPerTick mm, pela mesma convencao usada na
   * distancia.
   *
   * @return velocidade em mm/s
   */
  public double getVelocityMMps() {
    double mmPerRevolution = AtiradorConstants.pulsePerRevolution * AtiradorConstants.wheelDistPerTick;
    return encoderAtirador.getSpeed() / 60.0 * mmPerRevolution;
  }

  /**
   * @return verdadeiro quando o erro de posicao esta dentro da tolerancia
   */
  public boolean atPosition() {
    return Math.abs(targetPosition - getPositionMM()) <= AtiradorConstants.kPositionToleranceMM;
  }

  /**
   * Aplica a tensao respeitando o teto de kPositionMaxVolts. O clamp e
   * obrigatorio: sem ele um erro grande no inicio do movimento manda 12 V e o
   * tranco que a curva S deveria evitar volta pela porta dos fundos.
   */
  private void driveVolts(double volts) {
    double limit = AtiradorConstants.kPositionMaxVolts;
    appliedVolts = Math.max(-limit, Math.min(limit, volts));
    motorAtirador.setVoltage(appliedVolts);
  }

  /**
   * Telemetria pensada para o grafico do Glass/Shuffleboard. O par importante e
   * "Setpoint Acel" das duas estrategias: no trapezio ele e uma onda quadrada
   * (jerk infinito nas bordas), na curva S e um trapezio (jerk limitado).
   */
  private void telemetria() {
    SmartDashboard.putNumber("Atirador/Posicao mm", getPositionMM());
    SmartDashboard.putNumber("Atirador/Velocidade mmps", getVelocityMMps());
    SmartDashboard.putNumber("Atirador/Setpoint Pos mm", targetPosition);
    SmartDashboard.putNumber("Atirador/Setpoint Vel mmps", setpointVelocity);
    SmartDashboard.putNumber("Atirador/Setpoint Acel mmps2", setpointAcceleration);
    SmartDashboard.putNumber("Atirador/Erro mm", targetPosition - getPositionMM());
    SmartDashboard.putNumber("Atirador/Tensao V", appliedVolts);
    SmartDashboard.putBoolean("Atirador/Atingiu Posicao", atPosition());
    SmartDashboard.putBoolean("Atirador/Segurando", holding);
  }
}
