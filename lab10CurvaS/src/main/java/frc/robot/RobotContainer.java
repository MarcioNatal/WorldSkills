/*----------------------------------------------------------------------------*/
/* WorldSkills - Lab 10: controle de movimento com curva S (jerk limitado)    */
/*----------------------------------------------------------------------------*/

package frc.robot;

//WPI imports
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

//Vendor imports
import com.studica.frc.Titan;

//Command imports
import frc.robot.commands.AtiradorSCurveCmd;
import frc.robot.commands.AtiradorStepCmd;
import frc.robot.commands.AtiradorTrapezoidCmd;

//Subsystem imports
import frc.robot.Constants.AtiradorConstants;
import frc.robot.subsystems.AtiradorSubsystem;

/**
 * Lab 10: comparacao das tres formas de ir do ponto A ao ponto B no mesmo eixo.
 * <p>
 * Botoes do Xbox:
 * <ul>
 *   <li><b>A</b> - curva S (jerk limitado)</li>
 *   <li><b>B</b> - perfil trapezoidal do WPILib</li>
 *   <li><b>X</b> - degrau direto no PID, sem perfil</li>
 *   <li><b>Y</b> - volta para a origem em curva S</li>
 * </ul>
 * Rode os tres com o grafico aberto em "Atirador/Setpoint Acel mmps2" e
 * "Atirador/Posicao mm".
 */
public class RobotContainer {

  // Um unico objeto Titan para o TitanQuad: um CAN ID = um objeto Titan.
  private final Titan titan = new Titan(Constants.TITAN_ID);

  private final AtiradorSubsystem atiradorSubsystem;

  private final XboxController joystick = new XboxController(0);

  private final SendableChooser<Command> autoChooser = new SendableChooser<>();

  public RobotContainer() {

    atiradorSubsystem = new AtiradorSubsystem(titan);

    // De proposito sem default command: qualquer comando que escrevesse no
    // motor a cada ciclo apagaria o hold de posicao que o periodic() mantem.

    configureButtonBindings();
  }

  private void configureButtonBindings() {

    double alvo = AtiradorConstants.kTargetMM;

    new JoystickButton(joystick, XboxController.Button.kA.value)
        .whenPressed(new AtiradorSCurveCmd(atiradorSubsystem, alvo));

    new JoystickButton(joystick, XboxController.Button.kB.value)
        .whenPressed(new AtiradorTrapezoidCmd(atiradorSubsystem, alvo));

    new JoystickButton(joystick, XboxController.Button.kX.value)
        .whenPressed(new AtiradorStepCmd(atiradorSubsystem, alvo));

    new JoystickButton(joystick, XboxController.Button.kY.value)
        .whenPressed(new AtiradorSCurveCmd(atiradorSubsystem, 0.0));

    // Os mesmos tres no autonomo, para rodar sem joystick na bancada.
    autoChooser.setDefaultOption("None", new InstantCommand());
    autoChooser.addOption("Curva S " + alvo + " mm", new AtiradorSCurveCmd(atiradorSubsystem, alvo));
    autoChooser.addOption("Trapezoidal " + alvo + " mm", new AtiradorTrapezoidCmd(atiradorSubsystem, alvo));
    autoChooser.addOption("Degrau " + alvo + " mm", new AtiradorStepCmd(atiradorSubsystem, alvo));
    SmartDashboard.putData(autoChooser);
  }

  /**
   * @return comando selecionado no chooser do autonomo
   */
  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
