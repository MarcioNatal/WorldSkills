/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class Constants {

    /**
     * CAN ID do TitanQuad. Um unico controlador atende o drive (M1) e a garra (M2),
     * por isso o ID fica aqui e nao dentro de um subsystem especifico.
     */
    public static final int TITAN_ID = 42;

    public static class DriveTrainConstants {
        /**
         * Motors
         */
        public static final int M1 = 1; // Right Motor

        /**
         * Encoders
         */

        // Canais dos encoders no TitanQuad (mesmo indice do motor)
        public static final int kEncoder1Channel = M1;
        // Omni wheel radius
        public static final double wheelRadius = 50; // mm Diametro 105 mm

        // Encoder pulse per revolution
        public static final double pulsePerRevolution = 1440;

        // Gear Ratio between encoder and wheel
        public static final double gearRatio = 1.0 / 61.0; // 1/61 em int dava 0

        // Pulse per revolution of wheel
        public static final double wheelPulseRatio = pulsePerRevolution * gearRatio;

        // Distance per tick
        public static final double wheelDistPerTick = (Math.PI * 2 * wheelRadius) / pulsePerRevolution; // mm
                                                                                                        // wheelPulseRatio;

        /**
         * Controle de posicao (malha fechada no roboRIO) A medida e a distancia do
         * encoder em mm, a saida e tensao em volts (setVoltage compensa a bateria).
         */
        // Ganho proporcional: volts por mm de erro. 0.048 -> satura em ~125 mm
        public static final double kPositionP = 0.048;
        public static final double kPositionI = 0.0;
        public static final double kPositionD = 0.0;

        // Erro aceitavel para considerar que chegou, em mm
        public static final double kPositionToleranceMM = 5.0;

        // Limite de tensao durante o posicionamento, em volts (metade de 12 V)
        public static final double kPositionMaxVolts = 6.0;
    }

    public static class GarraConstants {

        public static final int M2 = 2; // Back Motor

        /**
         * Encoders
         */

        // Canais dos encoders no TitanQuad (mesmo indice do motor)
        public static final int kEncoder2Channel = M2;
        // Omni wheel radius
        public static final double wheelRadius = 50; // mm Diametro 105 mm

        // Encoder pulse per revolution
        public static final double pulsePerRevolution = 1440;

        // Gear Ratio between encoder and wheel
        public static final double gearRatio = 1.0 / 61.0; // 1/61 em int dava 0

        // Pulse per revolution of wheel
        public static final double wheelPulseRatio = pulsePerRevolution * gearRatio;

        // Distance per tick
        public static final double wheelDistPerTick = (Math.PI * 2 * wheelRadius) / pulsePerRevolution; // mm
                                                                                                        // wheelPulseRatio;

        /**
         * Limite para considerar a garra "em velocidade". Unidade: RPM do eixo do
         * encoder, ANTES da reducao (gearRatio = 1/61). Para exigir 50 RPM na saida da
         * garra, usar 50 / gearRatio = 3050.0
         */
        public static final double kAtSpeedRPM = 50.0;

    }

    public static class AtiradorConstants {

        public static final int M3 = 3; // Back Motor

        /**
         * Encoders
         */

        // Canais dos encoders no TitanQuad (mesmo indice do motor)
        public static final int kEncoder3Channel = M3;
        // Omni wheel radius
        public static final double wheelRadius = 50; // mm Diametro 105 mm

        // Encoder pulse per revolution
        public static final double pulsePerRevolution = 1440;

        // Gear Ratio between encoder and wheel
        public static final double gearRatio = 1.0 / 61.0; // 1/61 em int dava 0

        // Pulse per revolution of wheel
        public static final double wheelPulseRatio = pulsePerRevolution * gearRatio;

        // Distance per tick
        public static final double wheelDistPerTick = (Math.PI * 2 * wheelRadius) / pulsePerRevolution; // mm
                                                                                                        // wheelPulseRatio;

        /**
         * Limite para considerar a garra "em velocidade". Unidade: RPM do eixo do
         * encoder, ANTES da reducao (gearRatio = 1/61). Para exigir 50 RPM na saida da
         * garra, usar 50 / gearRatio = 3050.0
         */
        public static final double kAtSpeedRPM = 50.0;

        public static final double kAtPositionMM = 100.0;

		public static final double kPositionP= 0.1;

		public static final double kPositionI= 0.0;

		public static final double kPositionD= 0.0025;

		public static final double kPositionToleranceMM = 10.0;

		public static final double ks = 0.0;

		public static final double kv = 0.0;

		public static final double ka = 0.0;
    }
}
