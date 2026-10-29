/*----------------------------------------------------------------------------*/
/* WorldSkills - Lab 10: controle de movimento com curva S (jerk limitado)    */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * Constantes do robo. Este lab usa um unico mecanismo (o atirador, no M3 do
 * TitanQuad) para comparar tres formas de ir do ponto A ao ponto B:
 * degrau no PID, perfil trapezoidal e curva S.
 */
public final class Constants {

    /**
     * CAN ID do TitanQuad.
     */
    public static final int TITAN_ID = 42;

    public static class AtiradorConstants {

        /** Motor e encoder: mesmo indice no TitanQuad. */
        public static final int M3 = 3;
        public static final int kEncoder3Channel = M3;

        public static final double wheelRadius = 50.0;          // mm
        public static final double pulsePerRevolution = 1440.0;
        public static final double gearRatio = 1.0 / 61.0;      // 1/61 em int dava 0

        /** mm de deslocamento por pulso do encoder: ~0.218 mm/tick. */
        public static final double wheelDistPerTick = (Math.PI * 2 * wheelRadius) / pulsePerRevolution;

        /**
         * Malha de posicao fechada no roboRIO. O erro e medido em mm (igual ao
         * getDistance() do encoder) e a saida e tensao em volts, porque
         * setVoltage() compensa a queda da bateria. Logo kP esta em V/mm.
         */
        public static final double kPositionP = 0.1;
        public static final double kPositionI = 0.0;
        public static final double kPositionD = 0.0025;

        /** Erro aceitavel para considerar que chegou, em mm. */
        public static final double kPositionToleranceMM = 10.0;

        /** Teto de tensao no posicionamento, em volts. */
        public static final double kPositionMaxVolts = 6.0;

        /**
         * Feedforward do motor. ATENCAO: comeca em zero, e com zero o perfil e
         * seguido so pelo feedback - funciona, mas o eixo fica sempre um pouco
         * atrasado em relacao ao perfil.
         * <p>
         * Para ganhar de verdade com a curva S, caracterize o mecanismo:
         * ks = volts para o eixo sair da inercia (rampa manual ate ele mexer);
         * kv = volts por mm/s em regime (tensao fixa / velocidade medida);
         * ka = volts por mm/s^2 (o mais chato; comece em 0 e suba de pouco).
         * O termo ka e o que faz o motor antecipar a aceleracao em vez de
         * reagir a ela.
         */
        public static final double ks = 0.0;   // V
        public static final double kv = 0.0;   // V / (mm/s)
        public static final double ka = 0.0;   // V / (mm/s^2)

        /**
         * Limites cinematicos dos perfis.
         * <p>
         * Com alvo de 100 mm, estes valores dao T ~= 1.26 s na curva S, e quem
         * manda e o jerk (os limites de velocidade e aceleracao pedem 0.63 s e
         * 0.98 s). Para sentir a diferenca na bancada, baixe kMaxJerk: o
         * movimento fica mais lento e mais macio. Suba muito e a curva S
         * converge para o trapezio, trancos inclusive.
         */
        public static final double kMaxVelocityMMps = 300.0;
        public static final double kMaxAccelerationMMps2 = 600.0;
        public static final double kMaxJerkMMps3 = 3000.0;

        /** Alvo usado pelos botoes do joystick, em mm. */
        public static final double kTargetMM = 100.0;
    }
}
