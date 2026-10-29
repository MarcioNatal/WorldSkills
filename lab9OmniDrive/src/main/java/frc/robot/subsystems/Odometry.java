package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.geometry.Twist2d;

import frc.robot.Constants.DriveConstants;

/**
 * Odometria da base omnidirecional de 3 rodas a 120 graus.
 *
 * <p>A cada ciclo le a distancia acumulada dos tres encoders, converte a
 * variacao em deslocamento no referencial do robo (cinematica direta) e
 * integra esse deslocamento na pose de campo. O angulo vem sempre da IMU, que
 * e muito mais confiavel que a rotacao estimada pelas rodas (omni escorrega).
 *
 * <p>Convencao de eixos (WPILib): x para frente do campo, y para a esquerda,
 * theta positivo no sentido anti-horario.
 *
 * <p>Montagem das rodas, igual a usada em {@code driveRobotOriented}:
 * <pre>
 *   d1 = -dx                      + R*dTheta   (roda traseira)
 *   d2 =  0.5*dx + (raiz(3)/2)*dy + R*dTheta   (roda frontal direita)
 *   d3 =  0.5*dx - (raiz(3)/2)*dy + R*dTheta   (roda frontal esquerda)
 * </pre>
 */
public class Odometry
{
    /** Pose do robo no referencial do campo. */
    private Pose2d pose = new Pose2d();

    /** Diferenca entre o rumo da pose e o rumo cru da IMU (permite resetPose sem mexer na IMU). */
    private Rotation2d gyroOffset = new Rotation2d();

    /** Rumo usado na ultima integracao, para extrair o dTheta do ciclo. */
    private Rotation2d previousAngle = new Rotation2d();

    /** Leitura dos encoders no ciclo anterior, em metros. */
    private double lastDistance1;
    private double lastDistance2;
    private double lastDistance3;

    /** Instante da ultima atualizacao, para estimar velocidade. */
    private double lastTimestamp = -1.0;

    /** Velocidade estimada no referencial do robo (m/s e rad/s). */
    private double vxMetersPerSecond;
    private double vyMetersPerSecond;
    private double omegaRadPerSecond;

    /**
     * Cinematica direta: converte deslocamento das tres rodas em deslocamento
     * do chassi. Vale tanto para distancias (metros) quanto para velocidades
     * (m/s), porque o sistema e linear.
     *
     * <p>Somando as tres equacoes das rodas sobra {@code d1+d2+d3 = 3*R*dTheta};
     * o resto sai por substituicao.
     *
     * @param d1 deslocamento da roda traseira, em metros
     * @param d2 deslocamento da roda frontal direita, em metros
     * @param d3 deslocamento da roda frontal esquerda, em metros
     * @return vetor {dx, dy, dTheta} no referencial do robo (metros, metros, radianos)
     */
    public static double[] forwardKinematics(double d1, double d2, double d3)
    {
        // Componente comum as tres rodas: o arco percorrido na rotacao pura
        double rotationArc = (d1 + d2 + d3) / 3.0;

        double dx = rotationArc - d1;
        double dy = (d2 - d3) / Math.sqrt(3.0);
        double dTheta = rotationArc / DriveConstants.kRobotRadiusMeters;

        return new double[] { dx, dy, dTheta };
    }

    /**
     * Integra um ciclo de odometria usando o relogio do robo.
     *
     * @param gyroAngle  rumo cru da IMU, anti-horario positivo
     * @param distance1  distancia acumulada da roda traseira, em metros
     * @param distance2  distancia acumulada da roda frontal direita, em metros
     * @param distance3  distancia acumulada da roda frontal esquerda, em metros
     * @return a pose atualizada
     */
    public Pose2d update(Rotation2d gyroAngle, double distance1, double distance2, double distance3)
    {
        return updateWithTime(Timer.getFPGATimestamp(), gyroAngle, distance1, distance2, distance3);
    }

    /**
     * Integra um ciclo de odometria com o instante informado por fora. Existe
     * para a classe poder ser testada no PC, onde nao ha relogio da FPGA.
     *
     * @param currentTimeSeconds instante desta leitura, em segundos
     * @param gyroAngle  rumo cru da IMU, anti-horario positivo
     * @param distance1  distancia acumulada da roda traseira, em metros
     * @param distance2  distancia acumulada da roda frontal direita, em metros
     * @param distance3  distancia acumulada da roda frontal esquerda, em metros
     * @return a pose atualizada
     */
    public Pose2d updateWithTime(double currentTimeSeconds, Rotation2d gyroAngle,
                                 double distance1, double distance2, double distance3)
    {
        double delta1 = distance1 - lastDistance1;
        double delta2 = distance2 - lastDistance2;
        double delta3 = distance3 - lastDistance3;

        lastDistance1 = distance1;
        lastDistance2 = distance2;
        lastDistance3 = distance3;

        double[] robotDelta = forwardKinematics(delta1, delta2, delta3);

        // Rumo absoluto: IMU + offset acumulado pelos resetPose()
        Rotation2d angle = gyroAngle.plus(gyroOffset);
        double dTheta = angle.minus(previousAngle).getRadians();

        // exp() integra o arco de verdade (trajetoria curva), em vez de
        // assumir que o robo andou em linha reta dentro do ciclo
        Pose2d newPose = pose.exp(new Twist2d(robotDelta[0], robotDelta[1], dTheta));

        pose = new Pose2d(newPose.getTranslation(), angle);
        previousAngle = angle;

        updateVelocities(currentTimeSeconds, robotDelta[0], robotDelta[1], dTheta);

        return pose;
    }

    /**
     * Converte o deslocamento do ciclo em velocidade, usando o tempo real
     * entre chamadas (o periodo do scheduler nao e exatamente 20 ms).
     */
    private void updateVelocities(double now, double dx, double dy, double dTheta)
    {
        double period = (lastTimestamp >= 0.0) ? (now - lastTimestamp) : 0.0;
        lastTimestamp = now;

        if (period > 1e-6)
        {
            vxMetersPerSecond = dx / period;
            vyMetersPerSecond = dy / period;
            omegaRadPerSecond = dTheta / period;
        }
    }

    /**
     * Teleporta o robo para uma pose conhecida (inicio do autonomo, correcao
     * por visao, etc). Nao e preciso zerar a IMU: o offset cuida disso.
     *
     * @param newPose   pose verdadeira do robo no campo
     * @param gyroAngle rumo cru da IMU nesse instante
     */
    public void resetPose(Pose2d newPose, Rotation2d gyroAngle)
    {
        pose = newPose;
        previousAngle = newPose.getRotation();
        gyroOffset = newPose.getRotation().minus(gyroAngle);
    }

    /**
     * Reancora a leitura dos encoders sem mexer na pose. Precisa ser chamado
     * sempre que os encoders forem zerados por fora (resetEncoders), senao o
     * salto de leitura entra na odometria como um deslocamento que nao houve.
     *
     * @param distance1 leitura atual da roda traseira, em metros
     * @param distance2 leitura atual da roda frontal direita, em metros
     * @param distance3 leitura atual da roda frontal esquerda, em metros
     */
    public void resetWheelBaseline(double distance1, double distance2, double distance3)
    {
        lastDistance1 = distance1;
        lastDistance2 = distance2;
        lastDistance3 = distance3;
    }

    /**
     * @return pose do robo no campo (x e y em metros, rotacao anti-horaria)
     */
    public Pose2d getPose()
    {
        return pose;
    }

    /** @return velocidade para frente do robo, em m/s */
    public double getVxMetersPerSecond()
    {
        return vxMetersPerSecond;
    }

    /** @return velocidade lateral do robo (positivo para a esquerda), em m/s */
    public double getVyMetersPerSecond()
    {
        return vyMetersPerSecond;
    }

    /** @return velocidade angular, em rad/s */
    public double getOmegaRadPerSecond()
    {
        return omegaRadPerSecond;
    }

    /**
     * Mesma velocidade dos getters acima, mas rotacionada para o referencial do
     * campo: gira o vetor local de +theta (o inverso do que
     * {@code driveFieldOriented} faz, que vai de campo para robo).
     *
     * <p>Util para comparar com o alvo de um trajeto, que normalmente e dado em
     * coordenadas de campo, e para checar no dashboard se o robo esta de fato
     * indo para onde se pediu.
     *
     * @return vetor velocidade no campo, em m/s (x para frente do campo,
     *         y para a esquerda do campo)
     */
    public Translation2d getFieldVelocity()
    {
        return new Translation2d(vxMetersPerSecond, vyMetersPerSecond)
                   .rotateBy(pose.getRotation());
    }
}
