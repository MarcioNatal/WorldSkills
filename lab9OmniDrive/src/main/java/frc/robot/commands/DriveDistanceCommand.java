package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.AutoConstants;
import frc.robot.subsystems.OmnidriveSubsystem;

/**
 * Translada o robo em linha reta numa direcao fixa do campo, sem girar, ate
 * percorrer a distancia pedida. A distancia e medida pelos encoders das rodas
 * (malha fechada), entao nao depende de tempo nem do nivel da bateria.
 *
 * <p>A frente do robo continua apontando para o mesmo lado durante todo o
 * percurso: e o ganho de ter 3 rodas omni.
 */
public class DriveDistanceCommand extends CommandBase 
{
    private final OmnidriveSubsystem drive;

    /** Direcao do deslocamento no referencial do campo, ja normalizada. */
    private final double dirX;
    private final double dirY;

    private final double targetDistance;

    /** Rumo no instante em que o comando comeca, usado como referencia da correcao. */
    private double startHeading;

    /**
     * @param drive  subsistema de tracao
     * @param dirX   componente lateral da direcao (+1 = direita)
     * @param dirY   componente longitudinal da direcao (+1 = frente)
     * @param meters distancia a percorrer, em metros
     */
    public DriveDistanceCommand(OmnidriveSubsystem drive, double dirX, double dirY, double meters) 
    {
        this.drive = drive;

        double norm = Math.hypot(dirX, dirY);
        if (norm < 1e-6) 
        {
            norm = 1.0; // direcao nula: evita divisao por zero
        }
        this.dirX = dirX / norm;
        this.dirY = dirY / norm;

        this.targetDistance = Math.abs(meters);

        addRequirements(drive);
    }

    @Override
    public void initialize() 
    {
        drive.resetEncoders();
        startHeading = drive.getHeading();
    }

    @Override
    public void execute() 
    {
        double remaining = targetDistance - traveledDistance();

        // Rampa de desaceleracao nos ultimos centimetros, para nao passar do ponto
        double speed = AutoConstants.kDriveSpeed;
        if (remaining < AutoConstants.kRampDownDistance) 
        {
            speed = AutoConstants.kDriveSpeed * (remaining / AutoConstants.kRampDownDistance);
        }
        speed = Math.max(speed, AutoConstants.kMinDriveSpeed);

        // Correcao proporcional para o robo nao "abrir" o rumo enquanto anda de lado
        double headingError = startHeading - drive.getHeading();
        double turn = headingError * AutoConstants.kHeadingP;
        turn = Math.max(-AutoConstants.kMaxTurnOutput,
               Math.min(AutoConstants.kMaxTurnOutput, turn));

        drive.driveFieldOriented(dirX * speed, dirY * speed, turn);
    }

    @Override
    public boolean isFinished() 
    {
        return traveledDistance() >= targetDistance - AutoConstants.kDistanceTolerance;
    }

    @Override
    public void end(boolean interrupted) 
    {
        drive.stop();
    }

    /**
     * Distancia em linha reta ja percorrida nesta perna do trajeto.
     *
     * @return metros percorridos desde o initialize()
     */
    private double traveledDistance() 
    {
        double[] displacement = drive.getRobotDisplacement();
        return Math.hypot(displacement[0], displacement[1]);
    }
}
