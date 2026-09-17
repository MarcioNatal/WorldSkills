package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.AutoConstants;
import frc.robot.subsystems.OmnidriveSubsystem;

/**
 * Autonomo: percorre um quadrado de {@link AutoConstants#kSquareSideMeters} de
 * lado por translacao pura, sem girar em nenhum momento.
 *
 * <p>O robo anda frente, direita, tras e esquerda (sentido horario visto de
 * cima) e a frente dele continua apontando para o mesmo lado o trajeto inteiro.
 * Cada lado tem um timeout de seguranca: se o robo travar contra alguma coisa,
 * o grupo segue para o proximo lado em vez de ficar empurrando.
 */
public class SquareAutoCommand extends SequentialCommandGroup 
{
    public SquareAutoCommand(OmnidriveSubsystem drive) 
    {
        double side = AutoConstants.kSquareSideMeters;

        addCommands(
            // O rumo atual passa a ser o "zero" do campo para este trajeto
            new InstantCommand(drive::resetHeading),

            leg(drive,  0.0,  1.0, side),   // frente
            pause(),
            leg(drive,  1.0,  0.0, side),   // direita
            pause(),
            leg(drive,  0.0, -1.0, side),   // tras
            pause(),
            leg(drive, -1.0,  0.0, side),   // esquerda

            new InstantCommand(drive::stop, drive)
        );
    }

    /**
     * Um lado do quadrado, com a rede de seguranca de tempo.
     */
    private static edu.wpi.first.wpilibj2.command.Command leg(
            OmnidriveSubsystem drive, double dirX, double dirY, double meters) 
    {
        return new DriveDistanceCommand(drive, dirX, dirY, meters)
                .withTimeout(AutoConstants.kLegTimeoutSeconds);
    }

    private static WaitCommand pause() 
    {
        return new WaitCommand(AutoConstants.kPauseSeconds);
    }
}
