package frc.robot.commands;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import frc.robot.Constants.AutoConstants;
import frc.robot.subsystems.OmnidriveSubsystem;

/**
 * Autonomo de exemplo: passa por tres coordenadas da arena, parando
 * {@link AutoConstants#kWaypointPauseSeconds} segundo em cada uma.
 *
 * <p>As coordenadas sao HIPOTETICAS - troque os valores de {@link #WAYPOINTS}
 * pelos pontos reais da arena. Elas sao medidas a partir do ponto onde o robo
 * comeca o autonomo, que o primeiro comando do grupo declara como a origem
 * (0, 0) com a frente do robo apontando para +x:
 *
 * <pre>
 *   Visto de cima, na convencao do deck do Lab 9: +x (frente) para o alto
 *   da folha e +y (esquerda) para a esquerda.
 *
 *   P3 (1.80, 0.80) *                           +x
 *                   |                            ^
 *                   |                            |
 *   P2 (1.00, 0.80) *----------* P1 (1.00, 0.00) |
 *                              |         +y &lt;----+
 *                              |
 *                              * inicio (0, 0)
 * </pre>
 *
 * <p>O robo nao gira em nenhum momento: ele anda de lado entre os pontos, que e
 * o ganho de ter 3 rodas omni. Cada perna tem timeout de seguranca - se o robo
 * travar contra alguma coisa, o grupo segue para o ponto seguinte em vez de
 * ficar empurrando.
 */
public class WaypointAutoCommand extends SequentialCommandGroup
{
    /**
     * Pontos do trajeto, em metros, no referencial do campo
     * (+x = frente, +y = esquerda). Valores de exemplo: ajuste para a arena.
     */
    private static final Translation2d[] WAYPOINTS =
    {
        new Translation2d(1.00, 0.00),  // P1: 1 m para a frente
        new Translation2d(1.00, 0.80),  // P2: 80 cm para a esquerda
        new Translation2d(1.80, 0.80)   // P3: mais 80 cm para a frente
    };

    public WaypointAutoCommand(OmnidriveSubsystem drive)
    {
        // Ancora a odometria: a posicao atual passa a ser a origem do campo e o
        // rumo atual passa a ser 0 grau. Sem isso as coordenadas abaixo seriam
        // medidas a partir de onde o robo foi ligado, nao de onde ele esta
        // agora. Usa resetOdometry (nao resetHeading) de proposito: mexer na
        // pose nao exige zerar a IMU, o offset interno cuida disso.
        addCommands(new InstantCommand(drive::resetOdometry));

        for (int i = 0; i < WAYPOINTS.length; i++)
        {
            addCommands(leg(drive, WAYPOINTS[i]));

            // Pausa em cada ponto, inclusive no ultimo: da tempo de ler a pose
            // no dashboard e conferir se o robo chegou onde devia
            addCommands(pause(drive));
        }

        addCommands(new InstantCommand(drive::stop, drive));
    }

    /**
     * Parada em cima do ponto. Precisa exigir o subsystem (segundo argumento do
     * RunCommand): um WaitCommand puro nao "segura" a tracao, e durante a pausa
     * o default command do drive - o joystick - voltaria a mandar nas rodas.
     */
    private static Command pause(OmnidriveSubsystem drive)
    {
        return new RunCommand(drive::stop, drive)
                .withTimeout(AutoConstants.kWaypointPauseSeconds);
    }

    /**
     * Uma perna do trajeto, com a rede de seguranca de tempo.
     */
    private static Command leg(OmnidriveSubsystem drive, Translation2d point)
    {
        return new DriveToPointCommand(drive, point.getX(), point.getY())
                .withTimeout(AutoConstants.kWaypointTimeoutSeconds);
    }
}
