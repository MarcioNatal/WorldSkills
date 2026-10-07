/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants 
{
    public static final class DriveConstants 
    {
        // Configuração dos motores no controlador TitanQuad (Studica)
        public static final int kTitanCanId = 42;
        public static final int kMotor1Channel = 0; // Traseira
        public static final int kMotor2Channel = 1; // Frontal Direita
        public static final int kMotor3Channel = 2; // Frontal Esquerda

        // Canais dos encoders no TitanQuad (mesmo indice do motor)
        public static final int kEncoder1Channel = kMotor1Channel;
        public static final int kEncoder2Channel = kMotor2Channel;
        public static final int kEncoder3Channel = kMotor3Channel;

        // CONFERIR NO HARDWARE: pulsos por volta da roda (Maverick + caixa de reducao)
        public static final double kEncoderTicksPerRevolution = 1464.0;
        // CONFERIR NO HARDWARE: diametro da roda omni, em metros
        public static final double kWheelDiameterMeters = 0.100;
        // Metros percorridos por pulso do encoder
        public static final double kEncoderDistancePerTick =
                (Math.PI * kWheelDiameterMeters) / kEncoderTicksPerRevolution;

        // CONFERIR NO HARDWARE: distancia do centro do robo ao centro de cada
        // roda, em metros. So entra na odometria, para converter o arco de
        // rotacao medido pelas rodas em radianos.
        public static final double kRobotRadiusMeters = 0.150;

        // Sinal da IMU: false = getAngle() ja e anti-horario positivo, que e a
        // convencao da WPILib. Valor confirmado no robo - e o que faz o
        // field-oriented apontar certo. Esta flag vale para a odometria E para
        // o driveFieldOriented ao mesmo tempo, entao os dois nunca divergem.
        public static final boolean kGyroReversed = false;

        // Limite nominal do motor DC Maverick com caixa de redução (RPM)
        public static final double kMaxRPM = 150.0;

        // Ganhos do PID de Velocidade das rodas
        public static final double kP = 0.005;
        public static final double kI = 0.001;
        public static final double kD = 0.000;

        // Limites do SlewRateLimiter (Variação máxima de potência por segundo)
        public static final double kSlewRateX = 2.0;
        public static final double kSlewRateY = 2.0;
        public static final double kSlewRateZ = 3.0;
    }

    public static final class AutoConstants 
    {
        // Lado do quadrado, em metros
        public static final double kSquareSideMeters = 0.50;

        // Velocidade de cruzeiro (fracao de kMaxRPM)
        public static final double kDriveSpeed = 0.35;
        // Piso de velocidade: abaixo disso o robo nao vence o atrito
        public static final double kMinDriveSpeed = 0.12;
        // Distancia final em que o robo comeca a desacelerar (metros)
        public static final double kRampDownDistance = 0.15;
        // Quanto antes do alvo o lado e dado por concluido (metros)
        public static final double kDistanceTolerance = 0.01;

        // Correcao proporcional de rumo durante a translacao (saida por grau de erro).
        // TESTE COM 0.0 NA PRIMEIRA VEZ: se o robo girar em vez de corrigir,
        // o sinal esta invertido - use -0.010.
        public static final double kHeadingP = 0.010;
        // Teto da correcao de rumo, para nao virar uma rotacao
        public static final double kMaxTurnOutput = 0.15;

        // Rede de seguranca: cada lado desiste depois disso (segundos)
        public static final double kLegTimeoutSeconds = 6.0;
        // Pausa entre os lados, para o robo assentar antes de trocar de direcao
        public static final double kPauseSeconds = 0.25;
    }

    public static final class OIConstants 
    {
        public static final int kJoystickPort = 0;
        public static final double kDeadband = 0.05; // 5% de zona morta
        public static final int kResetGyroButton = 1; // Botão Trigger para reset do campo
    }
}
