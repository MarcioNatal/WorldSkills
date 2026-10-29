package frc.robot.commands;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.Constants.AutoConstants;
import frc.robot.subsystems.OmnidriveSubsystem;

/**
 * Leva o robo ate uma coordenada (x, y) do campo usando a odometria como
 * realimentacao, sem girar o chassi.
 *
 * <p>Diferenca em relacao ao {@link DriveDistanceCommand}: lá o alvo e uma
 * distancia relativa ("ande 0,5 m para a frente"), aqui o alvo e um ponto
 * absoluto da arena ("va para x=1,2 / y=0,8"). Como o erro e recalculado a cada
 * ciclo a partir da pose, se o robo escorregar ou for empurrado ele se corrige
 * sozinho em vez de errar o ponto final.
 *
 * <p>Convencao de eixos, a mesma da odometria e do {@code driveFieldOriented}:
 * +x para frente do campo, +y para a esquerda do campo, em metros.
 *
 * <p>A velocidade e proporcional a distancia que falta ({@code kTranslationP}),
 * o que ja da a desaceleracao no fim do trajeto de graca - nao precisa da rampa
 * manual que o DriveDistanceCommand usa.
 */
public class DriveToPointCommand extends CommandBase
{
    private final OmnidriveSubsystem drive;

    /** Ponto de destino no referencial do campo, em metros. */
    private final Translation2d target;

    /** Rumo a manter durante o trajeto, capturado no initialize(). */
    private Rotation2d targetHeading = new Rotation2d();

    /**
     * @param drive  subsistema de tracao
     * @param xMeters coordenada x do destino no campo (+ = frente do campo)
     * @param yMeters coordenada y do destino no campo (+ = esquerda do campo)
     */
    public DriveToPointCommand(OmnidriveSubsystem drive, double xMeters, double yMeters)
    {
        this.drive = drive;
        this.target = new Translation2d(xMeters, yMeters);

        addRequirements(drive);
    }

    @Override
    public void initialize()
    {
        // Mantem o rumo que o robo tinha ao receber o comando. Nao zera
        // encoder nem pose: o alvo e absoluto e depende da odometria continuar
        // valendo entre um ponto e o proximo.
        targetHeading = drive.getPose().getRotation();
    }

    @Override
    public void execute()
    {
        Translation2d error = target.minus(drive.getPose().getTranslation());
        double distance = error.getNorm();

        if (distance < 1e-6)
        {
            drive.driveFieldOriented(0.0, 0.0, 0.0);
            return;
        }

        // Velocidade proporcional ao que falta, limitada entre o piso que vence
        // o atrito e a velocidade de cruzeiro
        double speed = Math.min(distance * AutoConstants.kTranslationP, AutoConstants.kDriveSpeed);
        speed = Math.max(speed, AutoConstants.kMinDriveSpeed);

        // Direcao unitaria do erro, ja em coordenadas de campo
        double dirX = error.getX() / distance;
        double dirY = error.getY() / distance;

        // Correcao de rumo: o robo translada, nao gira
        double headingError = targetHeading.minus(drive.getPose().getRotation()).getDegrees();
        double turn = headingError * AutoConstants.kHeadingP;
        turn = Math.max(-AutoConstants.kMaxTurnOutput,
               Math.min(AutoConstants.kMaxTurnOutput, turn));

        drive.driveFieldOriented(dirX * speed, dirY * speed, turn);
    }

    @Override
    public boolean isFinished()
    {
        return remainingDistance() <= AutoConstants.kPositionTolerance;
    }

    @Override
    public void end(boolean interrupted)
    {
        drive.stop();
    }

    /**
     * @return distancia em linha reta que ainda falta para o alvo, em metros
     */
    private double remainingDistance()
    {
        return target.minus(drive.getPose().getTranslation()).getNorm();
    }
}
