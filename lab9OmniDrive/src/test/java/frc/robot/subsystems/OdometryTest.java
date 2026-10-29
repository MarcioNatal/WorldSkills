package frc.robot.subsystems;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import frc.robot.Constants.DriveConstants;

/**
 * Testes da odometria rodando no PC (./gradlew test), sem robo e sem HAL.
 *
 * <p>A ideia e simular os encoders: a partir de um movimento conhecido do
 * chassi, calcular quanto cada roda teria girado (cinematica inversa, a mesma
 * de driveRobotOriented) e alimentar a odometria com isso. Se a pose estimada
 * bate com o movimento imposto, a matematica esta fechada.
 */
public class OdometryTest
{
    private static final double TOL = 1e-9;

    /** Raio usado pela odometria, para montar os casos de rotacao. */
    private static final double R = DriveConstants.kRobotRadiusMeters;

    /**
     * Cinematica inversa: quanto cada roda gira para o chassi se deslocar
     * (dx, dy, dTheta) no referencial do robo.
     *
     * @return vetor {d1, d2, d3} em metros
     */
    private static double[] wheels(double dx, double dy, double dThetaRad)
    {
        double arc = R * dThetaRad;
        return new double[] {
            -dx + arc,
            0.5 * dx + (Math.sqrt(3.0) / 2.0) * dy + arc,
            0.5 * dx - (Math.sqrt(3.0) / 2.0) * dy + arc
        };
    }

    /**
     * Alimenta a odometria com um movimento do chassi dividido em varios
     * passos, acumulando a leitura dos encoders como o hardware faz.
     */
    private static class WheelSim
    {
        private final Odometry odometry = new Odometry();
        private final double[] distance = new double[3];
        private double time;

        void drive(double dx, double dy, double dThetaRad, Rotation2d endAngle, int steps)
        {
            Rotation2d startAngle = odometry.getPose().getRotation();

            for (int i = 1; i <= steps; i++)
            {
                double[] step = wheels(dx / steps, dy / steps, dThetaRad / steps);
                distance[0] += step[0];
                distance[1] += step[1];
                distance[2] += step[2];

                // Interpola o rumo da IMU ao longo do movimento
                double fraction = (double) i / steps;
                Rotation2d angle = startAngle.plus(endAngle.minus(startAngle).times(fraction));

                time += 0.02;
                odometry.updateWithTime(time, angle, distance[0], distance[1], distance[2]);
            }
        }

        Pose2d pose()
        {
            return odometry.getPose();
        }
    }

    @Test
    public void cinematicaDiretaParaFrente()
    {
        double[] w = wheels(1.0, 0.0, 0.0);
        double[] chassis = Odometry.forwardKinematics(w[0], w[1], w[2]);

        assertEquals(1.0, chassis[0], TOL);
        assertEquals(0.0, chassis[1], TOL);
        assertEquals(0.0, chassis[2], TOL);
    }

    @Test
    public void cinematicaDiretaParaLado()
    {
        double[] w = wheels(0.0, 1.0, 0.0);
        double[] chassis = Odometry.forwardKinematics(w[0], w[1], w[2]);

        assertEquals(0.0, chassis[0], TOL);
        assertEquals(1.0, chassis[1], TOL);
        assertEquals(0.0, chassis[2], TOL);
    }

    @Test
    public void cinematicaDiretaRotacaoPura()
    {
        double[] w = wheels(0.0, 0.0, Math.PI / 2.0);
        double[] chassis = Odometry.forwardKinematics(w[0], w[1], w[2]);

        assertEquals(0.0, chassis[0], TOL);
        assertEquals(0.0, chassis[1], TOL);
        assertEquals(Math.PI / 2.0, chassis[2], TOL);
    }

    @Test
    public void andaUmMetroParaFrente()
    {
        WheelSim sim = new WheelSim();
        sim.drive(1.0, 0.0, 0.0, new Rotation2d(), 50);

        assertEquals(1.0, sim.pose().getTranslation().getX(), 1e-6);
        assertEquals(0.0, sim.pose().getTranslation().getY(), 1e-6);
        assertEquals(0.0, sim.pose().getRotation().getDegrees(), 1e-6);
    }

    @Test
    public void andaDeLadoSemGirar()
    {
        WheelSim sim = new WheelSim();
        sim.drive(0.0, 0.5, 0.0, new Rotation2d(), 50);

        assertEquals(0.0, sim.pose().getTranslation().getX(), 1e-6);
        assertEquals(0.5, sim.pose().getTranslation().getY(), 1e-6);
    }

    @Test
    public void giraNoLugarSemAndar()
    {
        WheelSim sim = new WheelSim();
        sim.drive(0.0, 0.0, Math.PI / 2.0, Rotation2d.fromDegrees(90.0), 50);

        assertEquals(0.0, sim.pose().getTranslation().getX(), 1e-6);
        assertEquals(0.0, sim.pose().getTranslation().getY(), 1e-6);
        assertEquals(90.0, sim.pose().getRotation().getDegrees(), 1e-6);
    }

    /**
     * Com o robo apontando para 90 graus, a frente do robo e o +Y do campo.
     * E o teste que pega inversao de eixo entre robo e campo.
     */
    @Test
    public void frenteDoRoboViraEixoYQuandoGirado90()
    {
        WheelSim sim = new WheelSim();
        sim.drive(0.0, 0.0, Math.PI / 2.0, Rotation2d.fromDegrees(90.0), 50);
        sim.drive(1.0, 0.0, 0.0, Rotation2d.fromDegrees(90.0), 50);

        assertEquals(0.0, sim.pose().getTranslation().getX(), 1e-6);
        assertEquals(1.0, sim.pose().getTranslation().getY(), 1e-6);
        assertEquals(90.0, sim.pose().getRotation().getDegrees(), 1e-6);
    }

    /**
     * Quadrado de 0.5 m andando sempre de lado, sem girar: o robo tem que
     * voltar exatamente para a origem. E o SquareAutoCommand no papel.
     */
    @Test
    public void quadradoFechaNaOrigem()
    {
        WheelSim sim = new WheelSim();
        double side = 0.5;

        sim.drive( side,   0.0, 0.0, new Rotation2d(), 25);
        sim.drive(  0.0,  side, 0.0, new Rotation2d(), 25);
        sim.drive(-side,   0.0, 0.0, new Rotation2d(), 25);
        sim.drive(  0.0, -side, 0.0, new Rotation2d(), 25);

        assertEquals(0.0, sim.pose().getTranslation().getX(), 1e-6);
        assertEquals(0.0, sim.pose().getTranslation().getY(), 1e-6);
        assertEquals(0.0, sim.pose().getRotation().getDegrees(), 1e-6);
    }

    /**
     * resetWheelBaseline existe para o resetEncoders() do subsystem nao jogar
     * um salto de leitura dentro da pose.
     */
    @Test
    public void zerarEncodersNaoMoveAPose()
    {
        Odometry odometry = new Odometry();
        double[] w = wheels(1.0, 0.0, 0.0);

        odometry.updateWithTime(0.02, new Rotation2d(), w[0], w[1], w[2]);
        assertEquals(1.0, odometry.getPose().getTranslation().getX(), 1e-6);

        // Encoders zerados por fora: sem reancorar, a odometria veria -1 m
        odometry.resetWheelBaseline(0.0, 0.0, 0.0);
        odometry.updateWithTime(0.04, new Rotation2d(), 0.0, 0.0, 0.0);

        assertEquals(1.0, odometry.getPose().getTranslation().getX(), 1e-6);
    }

    /**
     * resetPose teleporta o robo sem precisar zerar a IMU: o rumo continua
     * sendo lido com offset.
     */
    @Test
    public void resetPoseMantemORumoDaImu()
    {
        Odometry odometry = new Odometry();
        Rotation2d gyro = Rotation2d.fromDegrees(30.0);

        odometry.resetPose(new Pose2d(2.0, 1.0, Rotation2d.fromDegrees(90.0)), gyro);

        assertEquals(2.0, odometry.getPose().getTranslation().getX(), TOL);
        assertEquals(1.0, odometry.getPose().getTranslation().getY(), TOL);
        assertEquals(90.0, odometry.getPose().getRotation().getDegrees(), 1e-6);

        // IMU andou 10 graus, a pose tem que andar os mesmos 10 graus
        odometry.updateWithTime(0.02, Rotation2d.fromDegrees(40.0), 0.0, 0.0, 0.0);
        assertEquals(100.0, odometry.getPose().getRotation().getDegrees(), 1e-6);
    }

    @Test
    public void estimaVelocidade()
    {
        Odometry odometry = new Odometry();
        double[] w = wheels(0.02, 0.0, 0.0); // 2 cm em 20 ms = 1 m/s

        odometry.updateWithTime(1.00, new Rotation2d(), 0.0, 0.0, 0.0);
        odometry.updateWithTime(1.02, new Rotation2d(), w[0], w[1], w[2]);

        assertEquals(1.0, odometry.getVxMetersPerSecond(), 1e-6);
        assertEquals(0.0, odometry.getVyMetersPerSecond(), 1e-6);
    }

    /**
     * Com o robo a 90 graus, 1 m/s para a frente do robo e 1 m/s no +Y do
     * campo. E o par do frenteDoRoboViraEixoYQuandoGirado90, mas para a
     * velocidade em vez da posicao.
     */
    @Test
    public void velocidadeDeCampoGiraComORumo()
    {
        Odometry odometry = new Odometry();
        odometry.resetPose(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(90.0)), new Rotation2d());

        double[] w = wheels(0.02, 0.0, 0.0); // 2 cm em 20 ms = 1 m/s

        odometry.updateWithTime(1.00, new Rotation2d(), 0.0, 0.0, 0.0);
        odometry.updateWithTime(1.02, new Rotation2d(), w[0], w[1], w[2]);

        // No referencial do robo continua sendo "para frente"
        assertEquals(1.0, odometry.getVxMetersPerSecond(), 1e-6);
        assertEquals(0.0, odometry.getVyMetersPerSecond(), 1e-6);

        // No campo, essa frente aponta para o +Y
        assertEquals(0.0, odometry.getFieldVelocity().getX(), 1e-6);
        assertEquals(1.0, odometry.getFieldVelocity().getY(), 1e-6);
    }

    /**
     * Com o robo alinhado ao campo (theta = 0), as duas leituras tem que ser
     * identicas: se divergirem aqui, o sinal da rotacao esta trocado.
     */
    @Test
    public void velocidadeDeCampoIgualALocalSemGiro()
    {
        Odometry odometry = new Odometry();
        double[] w = wheels(0.01, 0.01, 0.0);

        odometry.updateWithTime(1.00, new Rotation2d(), 0.0, 0.0, 0.0);
        odometry.updateWithTime(1.02, new Rotation2d(), w[0], w[1], w[2]);

        assertEquals(odometry.getVxMetersPerSecond(), odometry.getFieldVelocity().getX(), 1e-9);
        assertEquals(odometry.getVyMetersPerSecond(), odometry.getFieldVelocity().getY(), 1e-9);
    }
}
