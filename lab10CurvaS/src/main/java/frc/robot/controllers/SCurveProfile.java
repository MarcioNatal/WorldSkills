/*----------------------------------------------------------------------------*/
/* WorldSkills - Lab 10: controle de movimento com curva S (jerk limitado)    */
/*----------------------------------------------------------------------------*/

package frc.robot.controllers;

/**
 * Perfil de movimento em curva S com jerk limitado.
 * <p>
 * O WPILib nao tem perfil com limite de jerk. O {@link
 * edu.wpi.first.wpilibj.trajectory.TrapezoidProfile} limita velocidade e
 * aceleracao, mas a aceleracao salta de 0 para a_max no instante zero: o jerk
 * (derivada da aceleracao) e infinito nos cantos do trapezio. E esse salto que
 * aparece na mecanica como tranco, folga batendo e oscilacao no fim do
 * movimento.
 * <p>
 * Esta classe resolve isso com o polinomio quintico de jerk minimo. Com
 * {@code u = t/T}:
 *
 * <pre>
 *   s(u) = d * (10u^3 - 15u^4 + 6u^5)
 *   v(u) = 30 * d/T   * u^2 * (1-u)^2
 *   a(u) = 60 * d/T^2 * u * (1-u) * (1-2u)
 *   j(u) = 60 * d/T^3 * (1 - 6u + 6u^2)
 * </pre>
 *
 * Nas duas pontas v = 0 E a = 0, ou seja, o motor nao recebe degrau nenhum nem
 * na partida nem na chegada. Os picos saem direto das formulas acima:
 *
 * <pre>
 *   |v|max = 1.8750 * |d| / T     (em u = 0.5)
 *   |a|max = 5.7735 * |d| / T^2   (= 10/sqrt(3))
 *   |j|max = 60.0   * |d| / T^3   (em u = 0 e u = 1)
 * </pre>
 *
 * Invertendo cada uma delas, descobre-se a duracao minima T que respeita cada
 * limite; o construtor fica com a maior das tres, que e a unica que respeita
 * todas.
 * <p>
 * <b>Preco honesto:</b> o quintico nao tem fase de velocidade constante. A
 * velocidade media e 1/1.875 = 53% do pico, entao para o mesmo
 * {@code maxVelocity} um curso longo demora ~1.9x mais que um trapezio. Para
 * um mecanismo de curso curto (atirador, garra, elevador) isso nao se nota;
 * para atravessar o campo, nota.
 * <p>
 * A interface imita a do {@code TrapezoidProfile} de proposito: construir,
 * {@link #calculate(double)} a cada ciclo, {@link #isFinished(double)} para
 * terminar. Trocar um pelo outro no comando e trocar duas linhas.
 */
public class SCurveProfile {

  /**
   * Limites cinematicos do movimento. As unidades sao livres, mas tem que ser
   * coerentes entre si e com a medida do encoder. Neste lab tudo esta em mm:
   * mm/s, mm/s^2 e mm/s^3.
   */
  public static class Constraints {
    public final double maxVelocity;
    public final double maxAcceleration;
    public final double maxJerk;

    /**
     * @param maxVelocity     velocidade de pico permitida (mm/s)
     * @param maxAcceleration aceleracao de pico permitida (mm/s^2)
     * @param maxJerk         jerk de pico permitido (mm/s^3) - e este que a
     *                        curva S acrescenta ao trapezio
     */
    public Constraints(double maxVelocity, double maxAcceleration, double maxJerk) {
      this.maxVelocity = maxVelocity;
      this.maxAcceleration = maxAcceleration;
      this.maxJerk = maxJerk;
    }
  }

  /**
   * Um ponto do perfil. Alem de posicao e velocidade (o que o
   * {@code TrapezoidProfile} devolve), carrega a aceleracao, porque e ela que
   * alimenta o termo ka do feedforward.
   */
  public static class State {
    public double position;
    public double velocity;
    public double acceleration;

    public State(double position, double velocity, double acceleration) {
      this.position = position;
      this.velocity = velocity;
      this.acceleration = acceleration;
    }
  }

  // Coeficientes de pico do quintico, deduzidos no javadoc da classe.
  private static final double kPeakVelocityFactor = 1.875;              // 30/16
  private static final double kPeakAccelerationFactor = 10.0 / Math.sqrt(3.0); // 5.7735
  private static final double kPeakJerkFactor = 60.0;

  private final double startPosition;
  private final double delta;
  private final double totalTime;

  /**
   * Monta um perfil de repouso a repouso entre duas posicoes.
   *
   * @param constraints   limites de velocidade, aceleracao e jerk
   * @param startPosition posicao inicial (mm) - normalmente a leitura atual do
   *                      encoder, para o perfil comecar onde o eixo esta
   * @param goalPosition  posicao final (mm)
   */
  public SCurveProfile(Constraints constraints, double startPosition, double goalPosition) {
    this.startPosition = startPosition;
    this.delta = goalPosition - startPosition;

    double distance = Math.abs(this.delta);

    // Curso zero: perfil de duracao zero, isFinished() ja responde true.
    if (distance == 0.0) {
      this.totalTime = 0.0;
      return;
    }

    // Duracao minima exigida por cada limite, isoladamente.
    double timeFromVelocity = kPeakVelocityFactor * distance / constraints.maxVelocity;
    double timeFromAcceleration = Math.sqrt(kPeakAccelerationFactor * distance / constraints.maxAcceleration);
    double timeFromJerk = Math.cbrt(kPeakJerkFactor * distance / constraints.maxJerk);

    // A maior das tres e a unica que respeita as tres ao mesmo tempo.
    this.totalTime = Math.max(timeFromVelocity, Math.max(timeFromAcceleration, timeFromJerk));
  }

  /**
   * Estado do perfil no instante t, contado do inicio do movimento.
   * <p>
   * Fora do intervalo [0, T] devolve as pontas paradas (v = 0, a = 0), entao
   * segurar o comando depois do fim nao gera comando estranho nenhum.
   *
   * @param t tempo em segundos desde o inicio do perfil
   * @return posicao (mm), velocidade (mm/s) e aceleracao (mm/s^2) desejadas
   */
  public State calculate(double t) {
    if (t <= 0.0) {
      return new State(startPosition, 0.0, 0.0);
    }
    if (t >= totalTime) {
      return new State(startPosition + delta, 0.0, 0.0);
    }

    double u = t / totalTime;
    double oneMinusU = 1.0 - u;

    double position = startPosition + delta * u * u * u * (10.0 - 15.0 * u + 6.0 * u * u);
    double velocity = 30.0 * delta / totalTime * u * u * oneMinusU * oneMinusU;
    double acceleration = 60.0 * delta / (totalTime * totalTime) * u * oneMinusU * (1.0 - 2.0 * u);

    return new State(position, velocity, acceleration);
  }

  /**
   * Jerk desejado no instante t. Nao entra na malha de controle: serve para
   * plotar na dashboard e mostrar que, ao contrario do trapezio, ele e finito.
   *
   * @param t tempo em segundos desde o inicio do perfil
   * @return jerk em mm/s^3
   */
  public double jerk(double t) {
    if (totalTime == 0.0 || t < 0.0 || t > totalTime) {
      return 0.0;
    }
    double u = t / totalTime;
    return 60.0 * delta / (totalTime * totalTime * totalTime) * (1.0 - 6.0 * u + 6.0 * u * u);
  }

  /**
   * @return duracao total do movimento em segundos
   */
  public double totalTime() {
    return totalTime;
  }

  /**
   * @param t tempo em segundos desde o inicio do perfil
   * @return verdadeiro quando o perfil ja entregou a posicao final
   */
  public boolean isFinished(double t) {
    return t >= totalTime;
  }

  /**
   * @return posicao final do perfil em mm
   */
  public double goalPosition() {
    return startPosition + delta;
  }
}
