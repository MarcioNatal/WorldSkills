package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.kauailabs.navx.frc.AHRS;
import com.studica.frc.Titan;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.DriveConstants;

public class OmnidriveSubsystem extends SubsystemBase 
{
/**
 * Declare the main Titan object
 */
    private final Titan titan0;

/**
 * Declare the motors of each Titan
 * Each object conforms with WPILib SpeedController class for integration with built-in libs
 */

    private final Titan.Motor motor1;
    private final Titan.Motor motor2;
    private final Titan.Motor motor3;

/**
 * Declare the encoders of each Titan
 * Each object conforms with WPILib Encoder class for integration with built-in libs
 */
    private final Titan.Encoder encoder1;
    private final Titan.Encoder encoder2;
    private final Titan.Encoder encoder3;
    
    private final AHRS ahrs;

    // Filtros de rampa (SlewRateLimiter)
    private final SlewRateLimiter xLimiter = new SlewRateLimiter(DriveConstants.kSlewRateX);
    private final SlewRateLimiter yLimiter = new SlewRateLimiter(DriveConstants.kSlewRateY);
    private final SlewRateLimiter zLimiter = new SlewRateLimiter(DriveConstants.kSlewRateZ);

    // Controladores PID por roda
    private final PIDController pid1 = new PIDController(DriveConstants.kP, DriveConstants.kI, DriveConstants.kD);
    private final PIDController pid2 = new PIDController(DriveConstants.kP, DriveConstants.kI, DriveConstants.kD);
    private final PIDController pid3 = new PIDController(DriveConstants.kP, DriveConstants.kI, DriveConstants.kD);

    private final Odometry odometry = new Odometry();

    public OmnidriveSubsystem() 
    {
        /**
         * Create the main Titan instances
         */
        titan0 = new Titan(DriveConstants.kTitanCanId);

        /**
         * Get motor instances and assign to objects
         */
       
        motor1 = titan0.getMotor(DriveConstants.kMotor1Channel); 
        motor2 = titan0.getMotor(DriveConstants.kMotor2Channel);
        motor3 = titan0.getMotor(DriveConstants.kMotor3Channel);

        

        /**
         * Get encoder instances and assign to objects
         */
        encoder1 = titan0.getEncoder( DriveConstants.kEncoder1Channel,DriveConstants.kEncoderDistancePerTick);
        encoder2 = titan0.getEncoder(DriveConstants.kEncoder2Channel,DriveConstants.kEncoderDistancePerTick);
        encoder3 = titan0.getEncoder(DriveConstants.kEncoder3Channel,DriveConstants.kEncoderDistancePerTick);

        motorSettings();
        resetEncoders();

        // Inicializa a navX IMU na porta MXP da placa VMX-pi
        ahrs = new AHRS(SPI.Port.kMXP);
        resetHeading();
    }

    @Override
    public void periodic() 
    {
        // Odometria: precisa rodar todo ciclo, senao o deslocamento entre duas
        // atualizacoes vira uma reta e a curva e perdida.
        double[] d = getEncoderDistances();
        odometry.update(getRotation2d(), d[0], d[1], d[2]);

        publishTelemetry();
    }

    /**
     * Manda a pose estimada para o SmartDashboard/Shuffleboard.
     */
    private void publishTelemetry() 
    {
        Pose2d pose = getPose();
        SmartDashboard.putNumber("Odom X (m)", pose.getTranslation().getX());
        SmartDashboard.putNumber("Odom Y (m)", pose.getTranslation().getY());
        SmartDashboard.putNumber("Odom Theta (deg)", pose.getRotation().getDegrees());
        SmartDashboard.putNumber("Odom Vx (m/s)", odometry.getVxMetersPerSecond());
        SmartDashboard.putNumber("Odom Vy (m/s)", odometry.getVyMetersPerSecond());
    }

    /**
     * Pose estimada do robo no campo, em metros e com rotacao anti-horaria
     * positiva (convencao da WPILib).
     *
     * @return pose atual
     */
    public Pose2d getPose() 
    {
        return odometry.getPose();
    }

    /**
     * Declara em que ponto do campo o robo esta. Usar no inicio do autonomo ou
     * quando houver uma referencia externa (visao, parede, marcacao do tapete).
     * A IMU nao precisa ser zerada junto.
     *
     * @param pose pose verdadeira do robo
     */
    public void resetPose(Pose2d pose) 
    {
        odometry.resetPose(pose, getRotation2d());
    }

    /**
     * Zera a pose: o robo passa a se considerar na origem do campo, apontando
     * para 0 grau.
     */
    public void resetOdometry() 
    {
        resetPose(new Pose2d());
    }

    public void motorSettings()
    {
        motor1.setInverted(false);
        motor2.setInverted(false);
        motor3.setInverted(false);

    }

    /**
     * Zera a contagem dos tres encoders. Chamado no construtor e util antes de
     * qualquer rotina autonoma que meca deslocamento.
     */
    public void resetEncoders() 
    {
        encoder1.reset();
        encoder2.reset();
        encoder3.reset();

        // Sem isso o salto da leitura (valor atual -> zero) entraria na
        // odometria como um deslocamento gigante para tras.
        odometry.resetWheelBaseline(0.0, 0.0, 0.0);
    }

    /**
     * Distancia percorrida por cada roda desde o ultimo reset, em metros.
     *
     * @return vetor {roda1, roda2, roda3}
     */
    public double[] getEncoderDistances() 
    {
        return new double[] { encoder1.getDistance(),
                              encoder2.getDistance(),
                              encoder3.getDistance() };
    }

    /**
     * Deslocamento do robo desde o ultimo resetEncoders(), no referencial do
     * proprio robo. A inversao da cinematica das 3 rodas esta em
     * {@link Odometry#forwardKinematics(double, double, double)}.
     *
     * @return vetor {dx, dy} em metros
     */
    public double[] getRobotDisplacement() 
    {
        double[] d = getEncoderDistances();
        double[] delta = Odometry.forwardKinematics(d[0], d[1], d[2]);

        return new double[] { delta[0], delta[1] };
    }

    /**
     * 
     * @return accumulated IMU yaw angle
     */
    public double getHeading() 
    {
        return ahrs.getAngle();
    }

    /**
     * Rumo do robo no formato da WPILib: anti-horaverio positivo. O sinal da IMU
     * e controlado por kGyroReversed.
     *
     * @return rumo acumulado como Rotation2d
     */
    public Rotation2d getRotation2d() 
    {
        return Rotation2d.fromDegrees(DriveConstants.kGyroReversed ? -getHeading() : getHeading());
    }

    /**
     * Reset yaw. Redefine o referencial de campo: a posicao e o rumo atuais
     * passam a ser a origem (0, 0) apontando para 0 grau. Como o eixo de
     * referencia girou, a pose antiga perde sentido e a odometria e zerada
     * junto - senao x e y ficariam num frame e theta em outro.
     */
    public void resetHeading() 
    {
        ahrs.reset();
        odometry.resetPose(new Pose2d(), getRotation2d());
    }

    /**
     * Movimentação relativa ao referencial do robô com PID em malha fechada
     */
    public void driveRobotOriented(double vx, double vy, double wz) 
    {
        // Cinemática holonômica de 3 rodas a 120°
        double targetPercent1 = -vx + wz;
        double targetPercent2 = 0.5 * vx + (Math.sqrt(3) / 2.0) * vy + wz;
        double targetPercent3 = 0.5 * vx - (Math.sqrt(3) / 2.0) * vy + wz;

        // Normalização de velocidade
        double maxSpeed = Math.max(Math.abs(targetPercent1), Math.max(Math.abs(targetPercent2), Math.abs(targetPercent3)));
        if (maxSpeed > 1.0) 
        {
            targetPercent1 /= maxSpeed;
            targetPercent2 /= maxSpeed;
            targetPercent3 /= maxSpeed;
        }

        // Converte em RPM Alvo
        double targetRPM1 = targetPercent1 * DriveConstants.kMaxRPM;
        double targetRPM2 = targetPercent2 * DriveConstants.kMaxRPM;
        double targetRPM3 = targetPercent3 * DriveConstants.kMaxRPM;

        // Leitura real dos encoders
        double currentRPM1 = motor1.getRPM();
        double currentRPM2 = motor2.getRPM();
        double currentRPM3 = motor3.getRPM();

        // Correção PID
        double output1 = pid1.calculate(currentRPM1, targetRPM1);
        double output2 = pid2.calculate(currentRPM2, targetRPM2);
        double output3 = pid3.calculate(currentRPM3, targetRPM3);

        motor1.set(output1);
        motor2.set(output2);
        motor3.set(output3);
        
    
    }

  

    /**
     * Movimentação orientada ao campo (Field-Oriented Drive)
     */
    public void driveFieldOriented(double fieldVx, double fieldVy, double wz) 
    {
        double smoothVx = xLimiter.calculate(fieldVx);
        double smoothVy = yLimiter.calculate(fieldVy);
        double smoothWz = zLimiter.calculate(wz);

        // Rotacao campo -> robo: gira o vetor de -theta. Mesma fonte de rumo
        // que a odometria usa, para as duas nao divergirem de convencao.
        Rotation2d angle = getRotation2d();
        double robotVx =  smoothVx * angle.getCos() + smoothVy * angle.getSin();
        double robotVy = -smoothVx * angle.getSin() + smoothVy * angle.getCos();

        driveRobotOriented(robotVx, robotVy, smoothWz);
    }

    /**
     * Stop motors and reset PIDControllers
     */
    public void stop() 
    {
        motor1.set(0);
        motor2.set(0);
        motor3.set(0);
        pid1.reset();
        pid2.reset();
        pid3.reset();

        // Zera as rampas: sem isso a proxima perna do trajeto comeca acelerando
        // a partir da velocidade antiga em vez de partir do zero.
        xLimiter.reset(0.0);
        yLimiter.reset(0.0);
        zLimiter.reset(0.0);
    }

}
