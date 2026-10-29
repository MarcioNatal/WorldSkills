/*----------------------------------------------------------------------------*/
/* WorldSkills - Lab 10: controle de movimento com curva S (jerk limitado)    */
/*----------------------------------------------------------------------------*/

package frc.robot.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Verifica o perfil sem robo: as tres restricoes sao respeitadas e as pontas
 * comecam e terminam paradas. Roda com "./gradlew test".
 */
public class SCurveProfileTest {

  private static final double kMaxVelocity = 300.0;      // mm/s
  private static final double kMaxAcceleration = 600.0;  // mm/s^2
  private static final double kMaxJerk = 3000.0;         // mm/s^3

  private static final SCurveProfile.Constraints kConstraints =
      new SCurveProfile.Constraints(kMaxVelocity, kMaxAcceleration, kMaxJerk);

  /** Tolerancia de 0.1% para a amostragem numerica nao acusar falso positivo. */
  private static final double kSlack = 1.001;

  @Test
  public void comecaEterminaParado() {
    SCurveProfile profile = new SCurveProfile(kConstraints, 0.0, 100.0);

    SCurveProfile.State inicio = profile.calculate(0.0);
    assertEquals(0.0, inicio.position, 1e-9);
    assertEquals(0.0, inicio.velocity, 1e-9);
    assertEquals(0.0, inicio.acceleration, 1e-9);

    SCurveProfile.State fim = profile.calculate(profile.totalTime());
    assertEquals(100.0, fim.position, 1e-9);
    assertEquals(0.0, fim.velocity, 1e-9);
    assertEquals(0.0, fim.acceleration, 1e-9);

    assertTrue(profile.isFinished(profile.totalTime()));
  }

  @Test
  public void respeitaOsTresLimites() {
    SCurveProfile profile = new SCurveProfile(kConstraints, 0.0, 100.0);

    double picoVelocidade = 0.0;
    double picoAceleracao = 0.0;
    double picoJerk = 0.0;

    for (int i = 0; i <= 2000; i++) {
      double t = profile.totalTime() * i / 2000.0;
      SCurveProfile.State state = profile.calculate(t);
      picoVelocidade = Math.max(picoVelocidade, Math.abs(state.velocity));
      picoAceleracao = Math.max(picoAceleracao, Math.abs(state.acceleration));
      picoJerk = Math.max(picoJerk, Math.abs(profile.jerk(t)));
    }

    assertTrue("velocidade de pico " + picoVelocidade, picoVelocidade <= kMaxVelocity * kSlack);
    assertTrue("aceleracao de pico " + picoAceleracao, picoAceleracao <= kMaxAcceleration * kSlack);
    assertTrue("jerk de pico " + picoJerk, picoJerk <= kMaxJerk * kSlack);
  }

  @Test
  public void quemMandaNaDuracaoEoLimiteMaisApertado() {
    // Com 100 mm e estes limites, o jerk e a restricao ativa: 1.26 s contra
    // 0.63 s da velocidade e 0.98 s da aceleracao.
    SCurveProfile profile = new SCurveProfile(kConstraints, 0.0, 100.0);
    assertEquals(Math.cbrt(60.0 * 100.0 / kMaxJerk), profile.totalTime(), 1e-9);

    // Afrouxando so o jerk, quem passa a mandar e a aceleracao.
    SCurveProfile semJerk =
        new SCurveProfile(new SCurveProfile.Constraints(kMaxVelocity, kMaxAcceleration, 1.0e9), 0.0, 100.0);
    assertEquals(Math.sqrt(10.0 / Math.sqrt(3.0) * 100.0 / kMaxAcceleration), semJerk.totalTime(), 1e-9);
  }

  @Test
  public void integralDaVelocidadeDaOCurso() {
    SCurveProfile profile = new SCurveProfile(kConstraints, 50.0, 250.0);

    int passos = 20000;
    double dt = profile.totalTime() / passos;
    double percorrido = 0.0;
    for (int i = 0; i < passos; i++) {
      percorrido += profile.calculate((i + 0.5) * dt).velocity * dt;
    }

    assertEquals(200.0, percorrido, 0.01);
    assertEquals(250.0, profile.goalPosition(), 1e-9);
  }

  @Test
  public void funcionaNoSentidoNegativo() {
    SCurveProfile profile = new SCurveProfile(kConstraints, 100.0, 0.0);

    SCurveProfile.State meio = profile.calculate(profile.totalTime() / 2.0);
    assertTrue("deveria estar recuando", meio.velocity < 0.0);
    assertEquals(50.0, meio.position, 1e-9);
    assertEquals(0.0, profile.calculate(profile.totalTime()).position, 1e-9);
  }

  @Test
  public void cursoZeroNaoTravaOComando() {
    SCurveProfile profile = new SCurveProfile(kConstraints, 42.0, 42.0);

    assertEquals(0.0, profile.totalTime(), 1e-9);
    assertTrue(profile.isFinished(0.0));
    assertEquals(42.0, profile.calculate(0.0).position, 1e-9);
  }
}
