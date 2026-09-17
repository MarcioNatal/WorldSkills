package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.studica.frc.TitanQuad;
import com.studica.frc.TitanQuadEncoder;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SlewRateLimiter;
import edu.wpi.first.wpilibj.controller.PIDController;
import frc.robot.Constants.DriveConstants;

public class OmnidriveSubsystem extends SubsystemBase {
    private final TitanQuad motor1;
    private final TitanQuad motor2;
    private final TitanQuad motor3;

    private final TitanQuadEncoder encoder1;
    private final TitanQuadEncoder encoder2;
    private final TitanQuadEncoder encoder3;
    
    private final AHRS ahrs;

    // Filtros de rampa (SlewRateLimiter)
    private final SlewRateLimiter xLimiter = new SlewRateLimiter(DriveConstants.kSlewRateX);
    private final SlewRateLimiter yLimiter = new SlewRateLimiter(DriveConstants.kSlewRateY);
    private final SlewRateLimiter zLimiter = new SlewRateLimiter(DriveConstants.kSlewRateZ);

    // Controladores PID por roda
    private final PIDController pid1 = new PIDController(DriveConstants.kP, DriveConstants.kI, DriveConstants.kD);
    private final PIDController pid2 = new PIDController(DriveConstants.kP, DriveConstants.kI, DriveConstants.kD);
    private final PIDController pid3 = new PIDController(DriveConstants.kP, DriveConstants.kI, DriveConstants.kD);

    public OmnidriveSubsystem() {
        motor1 = new TitanQuad(DriveConstants.kTitanCanId, DriveConstants.kMotor1Channel); 
        motor2 = new TitanQuad(DriveConstants.kTitanCanId, DriveConstants.kMotor2Channel);
        motor3 = new TitanQuad(DriveConstants.kTitanCanId, DriveConstants.kMotor3Channel);

        motor1.setInverted(false);
        motor2.setInverted(false);
        motor3.setInverted(false);

        // Encoders em quadratura do TitanQuad (um por canal de motor)
        encoder1 = new TitanQuadEncoder(motor1, DriveConstants.kEncoder1Channel,
                                        DriveConstants.kEncoderDistancePerTick);
        encoder2 = new TitanQuadEncoder(motor2, DriveConstants.kEncoder2Channel,
                                        DriveConstants.kEncoderDistancePerTick);
        encoder3 = new TitanQuadEncoder(motor3, DriveConstants.kEncoder3Channel,
                                        DriveConstants.kEncoderDistancePerTick);

        resetEncoders();

        // Inicializa a navX IMU na porta MXP da placa VMX-pi
        ahrs = new AHRS(SPI.Port.kMXP);
        resetHeading();
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
    }

    /**
     * Distancia percorrida por cada roda desde o ultimo reset, em metros.
     *
     * @return vetor {roda1, roda2, roda3}
     */
    public double[] getEncoderDistances() 
    {
        return new double[] { encoder1.getEncoderDistance(),
                              encoder2.getEncoderDistance(),
                              encoder3.getEncoderDistance() };
    }

    /**
     * Deslocamento do robo desde o ultimo resetEncoders(), no referencial do
     * proprio robo, obtido invertendo a cinematica holonomica das 3 rodas:
     *
     *   d1 = -dx + dtheta
     *   d2 =  0.5*dx + (raiz(3)/2)*dy + dtheta
     *   d3 =  0.5*dx - (raiz(3)/2)*dy + dtheta
     *
     * Somando as tres: d1+d2+d3 = 3*dtheta, e o resto sai por substituicao.
     *
     * @return vetor {dx, dy} em metros
     */
    public double[] getRobotDisplacement() 
    {
        double[] d = getEncoderDistances();

        double dTheta = (d[0] + d[1] + d[2]) / 3.0;
        double dx = dTheta - d[0];
        double dy = (d[1] - d[2]) / Math.sqrt(3.0);

        return new double[] { dx, dy };
    }

    public double getHeading() 
    {
        return ahrs.getAngle();
    }

    public void resetHeading() 
    {
        ahrs.reset();
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
    public void driveFieldOriented(double fieldVx, double fieldVy, double wz) {
        double smoothVx = xLimiter.calculate(fieldVx);
        double smoothVy = yLimiter.calculate(fieldVy);
        double smoothWz = zLimiter.calculate(wz);

        double angleRad = Math.toRadians(-getHeading());
        double robotVx = smoothVx * Math.cos(angleRad) - smoothVy * Math.sin(angleRad);
        double robotVy = smoothVx * Math.sin(angleRad) + smoothVy * Math.cos(angleRad);

        driveRobotOriented(robotVx, robotVy, smoothWz);
    }

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
