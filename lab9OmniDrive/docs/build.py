# -*- coding: utf-8 -*-
"""Lab 9 - OmniDrive: Command-Based, cinematica holonomica e odometria."""
import math, os, sys
from deckbase import *
from diagrams import *
from pptx.util import Inches, Pt
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR

SEC0 = u"Lab 9 — OmniDrive"
SEC1 = u"Parte 1 — Command-Based"
SEC2 = u"Parte 2 — OmniDrive"
SEC3 = u"Parte 3 — Odometria"

# =================================================================== CAPA
s = new_slide(sec=SEC0, number=False)
rect(s, 0, 0, W, 0.14, fill=CY, shape=MSO_SHAPE.RECTANGLE)
diag_wheels(s, 10.5, 4.1, R=1.45, show_axes=False, show_radius=False, labels=False,
            show_drive=False)
tb(s, 1.0, 1.55, 8.6, 0.4, u"ROBÓTICA MÓVEL  ·  WORLDSKILLS  ·  LAB 9", size=14,
   color=CY, bold=True)
tb(s, 1.0, 2.05, 9.0, 2.6, u"OmniDrive,\nCommand-Based\ne Odometria", size=50,
   color=WHITE, bold=True, spacing=1.05)
rect(s, 1.0, 4.72, 1.9, 0.05, fill=AM, shape=MSO_SHAPE.RECTANGLE)
tb(s, 1.0, 5.08, 8.2, 1.0,
   u"Da estrutura do framework à matemática que diz\nonde o robô está no campo.",
   size=18, color=MUTED, spacing=1.25)
tb(s, 1.0, 6.3, 8.6, 0.4,
   u"Base holonômica de 3 rodas a 120°  ·  TitanQuad + navX  ·  WPILib / Java",
   size=12.5, color=DIM)

# =================================================================== AGENDA
s = new_slide(u"Roteiro", u"O que vamos construir na sua cabeça hoje")
items = [
    (u"1", u"Command-Based", u"Como o framework organiza o robô: subsystems,\n"
     u"commands, scheduler e o loop de 20 ms.", CY),
    (u"2", u"OmniDrive", u"Por que 3 rodas a 120° andam para qualquer lado,\n"
     u"a cinemática inversa, PID e field-oriented.", AM),
    (u"3", u"Odometria", u"Invertendo a cinemática para estimar a pose\n"
     u"(x, y, θ) do robô no campo, ciclo a ciclo.", GR),
]
for i, (n, t, d, col) in enumerate(items):
    yy = 2.25 + i * 1.38
    rect(s, 0.95, yy, 11.45, 1.2, fill=PANEL, line=PANEL2, adj=0.09)
    nb = rect(s, 1.18, yy + 0.23, 0.74, 0.74, fill=col, adj=0.3)
    shape_text(nb, n, size=25, color=BG, bold=True)
    tb(s, 2.25, yy + 0.22, 3.0, 0.4, t, size=19.5, bold=True, color=col)
    tb(s, 5.3, yy + 0.24, 6.8, 0.9, d, size=13.5, color=MUTED, spacing=1.2)
note(s, 0.95, 6.5, 11.45,
     u"Fio condutor: tudo que está nos slides existe no projeto lab9OmniDrive. "
     u"Abra o código ao lado — os nomes são os mesmos.", color=AM, icon=u"★")

# =================================================================== HARDWARE
s = new_slide(u"O robô deste lab", u"O hardware que o software precisa conhecer")
hw = [
    (u"TitanQuad (CAN id 42)", u"Controladora Studica. Dá os 3 motores\n"
     u"(canais 0, 1, 2) e os 3 encoders nos\nmesmos índices.", CY),
    (u"3 motores Maverick", u"Com caixa de redução. Limite nominal\n"
     u"kMaxRPM = 150 RPM — é a escala que\nconverte “%” em RPM alvo.", VI),
    (u"3 encoders em quadratura", u"1464 ticks por volta da roda.\n"
     u"Roda omni de 100 mm de diâmetro.", GR),
    (u"navX IMU (SPI / MXP)", u"Dá o rumo acumulado (yaw).\n"
     u"É a fonte de θ para o field-oriented\nE para a odometria.", AM),
    (u"Joystick (porta 0)", u"Eixos X, Y, Z = andar de lado, andar\n"
     u"para frente e girar. Trigger zera o campo.", PK),
    (u"VMX-pi / SmartDashboard", u"Roda o código e publica a telemetria\n"
     u"(pose, velocidade) para você conferir.", OR),
]
for i, (t, d, col) in enumerate(hw):
    card(s, 0.95 + (i % 3) * 3.88, 2.2 + (i // 3) * 1.95, 3.6, 1.72, t, d,
         accent=col, hsize=14, bsize=12)
note(s, 0.95, 6.35, 11.45,
     u"Três números vêm do hardware e TÊM de ser medidos: ticks por volta, "
     u"diâmetro da roda e raio do chassi (centro → centro da roda). "
     u"Erro em qualquer um deles aparece como erro de odometria.", color=AM)

# =================================================================== ARVORE
s = new_slide(u"Mapa do projeto", u"Onde cada responsabilidade mora")
tree = u"""src/main/java/frc/robot/
│
├── Main.java                   // ponto de entrada da JVM — nao mexer
├── Robot.java                  // TimedRobot: so chama o scheduler
├── RobotContainer.java         // declara TUDO: subsystems, commands, botoes
├── Constants.java              // numeros do robo, em um lugar so
│
├── subsystems/
│   ├── OmnidriveSubsystem.java // hardware + cinematica + PID + field-oriented
│   └── Odometry.java           // matematica pura da pose (sem hardware)
│
└── commands/
    ├── DriveDistanceCommand.java  // anda N metros numa direcao
    └── SquareAutoCommand.java     // compoe 4 lados = quadrado

src/test/java/frc/robot/subsystems/
└── OdometryTest.java           // roda no PC: ./gradlew test"""
code(s, 0.95, 2.2, 7.3, 4.25, tree, size=11.6, title=u"estrutura")
cards = [
    (u"Robot.java", u"Nunca cresce. Em Command-Based ele\nquase não tem lógica.", CY),
    (u"RobotContainer", u"O “mapa” do robô. É aqui que você liga\nbotão → comando.", AM),
    (u"Odometry.java", u"Sem hardware dentro → dá para testar\nno PC, sem robô.", GR),
]
for i, (t, d, col) in enumerate(cards):
    card(s, 8.6, 2.2 + i * 1.45, 3.78, 1.25, t, d, accent=col, hsize=13.5, bsize=11.5)

# =================================================================== PARTE 1
section(u"1", u"Command-Based", u"Como o framework organiza o robô — e por que isso evita bagunça",
        accent=CY, topics=[u"Subsystem", u"Command", u"Scheduler", u"Trigger",
                           u"Default command", u"Composições"])
STATE["sec"] = SEC1

# ---- o problema
s = new_slide(u"Motivação", u"O problema que o Command-Based resolve")
rect(s, 0.95, 2.2, 5.55, 3.4, fill=PANEL, line=PK, lw=1.2, adj=0.06)
tb(s, 1.2, 2.38, 5.0, 0.35, u"Sem framework", size=16, bold=True, color=PK)
code_bad = u"""public void teleopPeriodic() {
  // joystick
  // ... e o autonomo?
  // ... e se dois trechos mandarem
  //     potencia pro mesmo motor?
  // ... e timer, flags, estados...
  if (etapa == 2 && !travado) {
     ...
  }
}"""
code(s, 1.2, 2.85, 5.05, 2.45, code_bad, size=11.5)
bullets(s, 1.2, 5.52, 5.05, [
    u"Um método gigante que faz tudo",
    u"Ninguém sabe quem controla o motor",
    u"Autônomo e teleop viram cópias",
], size=12.5, gap=0.05)
rect(s, 6.85, 2.2, 5.55, 3.4, fill=PANEL, line=GR, lw=1.2, adj=0.06)
tb(s, 7.1, 2.38, 5.0, 0.35, u"Com Command-Based", size=16, bold=True, color=GR)
code_good = u"""// RobotContainer: so declara
drive.setDefaultCommand(teleop);
autoChooser.addOption("Auto 2",
    new SquareAutoCommand(drive));

// Robot: so roda o scheduler
CommandScheduler.getInstance().run();"""
code(s, 7.1, 2.85, 5.05, 2.45, code_good, size=11.5)
bullets(s, 7.1, 5.52, 5.05, [
    u"Cada recurso tem um dono (subsystem)",
    u"Cada comportamento é um objeto reusável",
    u"O scheduler resolve os conflitos",
], size=12.5, gap=0.05)
note(s, 0.95, 6.62, 11.45,
     u"Command-Based é declarativo: você descreve o robô e as regras; "
     u"quem executa é o CommandScheduler.", color=AM, icon=u"★")

# ---- 4 pecas
s = new_slide(u"Conceitos", u"As quatro peças — e só essas quatro")
pieces = [
    (u"SUBSYSTEM", u"Subsystem",
     u"Um recurso físico do robô. Dono exclusivo de um conjunto de motores e sensores.",
     u"OmnidriveSubsystem", CY),
    (u"COMMAND", u"Command",
     u"Um comportamento com começo, meio e fim. Pede os subsystems que vai usar.",
     u"DriveDistanceCommand", AM),
    (u"SCHEDULER", u"CommandScheduler",
     u"O único executor. Roda os comandos, resolve conflitos e chama os periodic().",
     u"CommandScheduler.run()", VI),
    (u"TRIGGER", u"Trigger / Button",
     u"A condição que agenda um comando: botão, sensor, combinação lógica.",
     u"new JoystickButton(...)", GR),
]
for i, (kick, t, d, ex, col) in enumerate(pieces):
    x = 0.95 + i * 2.93
    rect(s, x, 2.2, 2.68, 3.55, fill=PANEL, line=PANEL2, adj=0.08)
    rect(s, x, 2.2, 2.68, 0.1, fill=col, shape=MSO_SHAPE.RECTANGLE)
    tb(s, x + 0.22, 2.48, 2.3, 0.3, kick, size=10.5, color=col, bold=True)
    tb(s, x + 0.22, 2.78, 2.3, 0.4, t, size=16.5, bold=True, color=WHITE)
    tb(s, x + 0.22, 3.3, 2.3, 1.4, d, size=12.5, color=MUTED, spacing=1.22)
    rect(s, x + 0.2, 4.95, 2.3, 0.62, fill=PANEL2, adj=0.2)
    tb(s, x + 0.3, 5.05, 2.1, 0.45, u"no lab:\n" + ex, size=10, color=col, font=MONO, spacing=1.2)
note(s, 0.95, 6.05, 11.45,
     u"Regra de ouro: um recurso = um subsystem. Dois comandos nunca usam o mesmo "
     u"subsystem ao mesmo tempo — o scheduler garante isso, e é esse detalhe que "
     u"elimina a classe de bug “dois trechos brigando pelo mesmo motor”.",
     color=AM, icon=u"★")

# ---- loop 20ms
s = new_slide(u"O coração do framework", u"O que acontece a cada 20 ms")
diag_scheduler(s, 0.95, 2.25, 11.45)
bullets(s, 0.95, 4.42, 11.45, [
    (u"20 ms = 50 Hz. ", u"É o período do TimedRobot. Todo execute() e todo periodic() roda nesse ritmo."),
    (u"Ordem importa: ", u"os commands rodam ANTES dos periodic() dos subsystems. "
     u"Logo, o periodic() já vê o efeito do comando deste ciclo."),
    (u"Nada bloqueia: ", u"nunca use sleep, while(true) ou espera ocupada dentro de um comando — "
     u"você travaria o robô inteiro, inclusive a odometria."),
], size=15, gap=0.16)
note(s, 0.95, 6.35, 11.45,
     u"Se CommandScheduler.getInstance().run() não for chamado, NADA do Command-Based "
     u"funciona: nenhum comando roda, nenhum periodic() é chamado, a odometria congela.")

# ---- Robot.java
s = new_slide(u"Robot.java", u"O arquivo que você quase não edita")
src = u"""public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;

  @Override public void robotInit() {
    m_robotContainer = new RobotContainer();     // cria tudo e liga os botoes
  }

  @Override public void robotPeriodic() {
    CommandScheduler.getInstance().run();        // <<< roda em TODOS os modos
  }

  @Override public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    if (m_autonomousCommand != null) m_autonomousCommand.schedule();
  }

  @Override public void teleopInit() {
    if (m_autonomousCommand != null) m_autonomousCommand.cancel();
  }
}"""
code(s, 0.95, 2.2, 8.1, 4.55, src, size=11.8, title=u"Robot.java")
bullets(s, 9.3, 2.3, 3.1, [
    (u"robotPeriodic ", u"roda em disabled, autônomo, teleop e test."),
    (u"autonomousInit ", u"só agenda o comando que o chooser selecionou."),
    (u"teleopInit ", u"cancela o autônomo — senão os dois disputariam o drive."),
    (u"Zero lógica ", u"de robô aqui. Tudo mora em RobotContainer."),
], size=13, gap=0.18)
note(s, 9.3, 4.9, 3.1,
     u"teleopPeriodic() está VAZIO e isso é proposital: o joystick é um default "
     u"command do subsystem, não código solto no periodic.", color=AM, icon=u"★", h=1.8)

# ---- Subsystem
s = new_slide(u"Subsystem", u"O dono exclusivo do hardware")
src = u"""public class OmnidriveSubsystem extends SubsystemBase {
  private final Titan titan0;                 // hardware so aqui dentro
  private final Titan.Motor motor1, motor2, motor3;
  private final Titan.Encoder encoder1, encoder2, encoder3;
  private final AHRS ahrs;                    // navX
  private final Odometry odometry = new Odometry();

  public OmnidriveSubsystem() {
    titan0   = new Titan(DriveConstants.kTitanCanId);
    motor1   = titan0.getMotor(DriveConstants.kMotor1Channel);
    encoder1 = titan0.getEncoder(DriveConstants.kEncoder1Channel,
                                 DriveConstants.kEncoderDistancePerTick);
    motorSettings();   resetEncoders();        // ... idem motor2/3 e encoder2/3
    ahrs = new AHRS(SPI.Port.kMXP);   resetHeading();
  }
}"""
code(s, 0.95, 2.2, 7.55, 3.7, src, size=11.6, title=u"OmnidriveSubsystem.java")
bullets(s, 8.8, 2.3, 3.6, [
    (u"extends SubsystemBase ", u"já registra o subsystem no scheduler."),
    (u"private final ", u"nenhuma outra classe toca no motor. Quem quer mover o robô chama um método público."),
    (u"Construtor ", u"= configurar e deixar o hardware em estado conhecido."),
], size=13, gap=0.2)
note(s, 0.95, 6.1, 11.45,
     u"Métodos públicos do subsystem são VERBOS curtos e não bloqueantes: "
     u"driveRobotOriented(), driveFieldOriented(), stop(), resetEncoders(), getPose().",
     color=AM, icon=u"★")

# ---- periodic
s = new_slide(u"periodic()", u"O relógio particular do subsystem")
src = u"""@Override
public void periodic() {
  // Odometria: precisa rodar TODO ciclo, senao o deslocamento entre
  // duas atualizacoes vira uma reta e a curva e perdida.
  double[] d = getEncoderDistances();
  odometry.update(getRotation2d(), d[0], d[1], d[2]);

  publishTelemetry();
}"""
code(s, 0.95, 2.2, 6.9, 2.1, src, size=12, title=u"periodic()")
card(s, 0.95, 4.5, 6.9, 1.5, u"O que ENTRA no periodic()",
     u"Leitura de sensores, atualização de estimadores de estado (odometria!), "
     u"telemetria, verificações de segurança. Tudo que precisa acontecer sempre, "
     u"independente do comando ativo.", accent=GR, bsize=12.5)
card(s, 8.1, 2.2, 4.28, 2.1, u"O que NÃO entra",
     u"Decidir para onde o robô vai. Essa decisão é do comando. Se o periodic() "
     u"mandar potência no motor, ele briga com o comando ativo.",
     accent=PK, bsize=12.5)
card(s, 8.1, 4.5, 4.28, 1.5, u"Por que todo ciclo",
     u"A odometria integra deltas. Se você pular ciclos, cada passo fica maior e a "
     u"aproximação do arco piora — o erro cresce nas curvas.", accent=AM, bsize=12.5)
note(s, 0.95, 6.62, 11.45,
     u"periodic() roda mesmo com o robô desabilitado. É por isso que você pode empurrar "
     u"o robô na mão e ver a pose mudar no dashboard — excelente teste.", color=AM, icon=u"★")

# ---- ciclo de vida do command
s = new_slide(u"Command", u"Ciclo de vida: quatro métodos, nada mais")
diag_command_life(s, 1.35, 2.35, 10.6)
tbl = [
    [u"Método", u"Quando roda", u"Para quê"],
    [u"initialize()", u"1x, ao ser agendado", u"preparar: zerar encoders, guardar referência"],
    [u"execute()", u"a cada 20 ms", u"calcular e aplicar a ação"],
    [u"isFinished()", u"a cada 20 ms, após execute", u"responder: já acabou?"],
    [u"end(interrupted)", u"1x, ao sair", u"limpar: parar motores, resetar filtros"],
]
table(s, 0.95, 4.95, 11.45, tbl, [2.5, 3.5, 5.45], size=12.5, rowh=0.4, mono_body=False)

# ---- command na pratica
s = new_slide(u"Command na prática", u"DriveDistanceCommand: andar N metros sem girar")
src = u"""public class DriveDistanceCommand extends CommandBase {
  public DriveDistanceCommand(OmnidriveSubsystem drive,
                              double dirX, double dirY, double meters) {
    this.drive = drive;
    double norm = Math.hypot(dirX, dirY);          // normaliza a direcao
    if (norm < 1e-6) norm = 1.0;                   // evita divisao por zero
    this.dirX = dirX / norm;
    this.dirY = dirY / norm;
    this.targetDistance = Math.abs(meters);
    addRequirements(drive);                        // <<< reserva o subsystem
  }
  @Override public void initialize() {
    drive.resetEncoders();                         // zera a regua
    startHeading = drive.getHeading();             // rumo de referencia
  }
  @Override public boolean isFinished() {
    return traveledDistance() >= targetDistance - kDistanceTolerance;
  }
  @Override public void end(boolean interrupted) { drive.stop(); }
}"""
code(s, 0.95, 2.2, 7.9, 4.4, src, size=11.4, title=u"DriveDistanceCommand.java")
bullets(s, 9.15, 2.3, 3.25, [
    (u"Construtor ", u"só guarda parâmetros — o robô ainda não existe em movimento."),
    (u"initialize() ", u"é o lugar de zerar a medição. Fazer isso no construtor seria um bug: o comando pode ser reusado."),
    (u"isFinished() ", u"mede distância real por encoder — não depende de tempo nem de bateria."),
    (u"end() ", u"sempre para o motor, inclusive quando interrompido."),
], size=12.5, gap=0.2)

# ---- execute do comando
s = new_slide(u"Command na prática", u"execute(): rampa de frenagem + correção de rumo")
src = u"""@Override
public void execute() {
  double remaining = targetDistance - traveledDistance();

  // Rampa de desaceleracao nos ultimos centimetros, para nao passar do ponto
  double speed = AutoConstants.kDriveSpeed;
  if (remaining < AutoConstants.kRampDownDistance) {
    speed *= remaining / AutoConstants.kRampDownDistance;
  }
  speed = Math.max(speed, AutoConstants.kMinDriveSpeed);  // piso: vence o atrito

  // Correcao proporcional para o robo nao "abrir" o rumo enquanto anda
  double headingError = startHeading - drive.getHeading();
  double turn = headingError * AutoConstants.kHeadingP;
  turn = Math.max(-kMaxTurnOutput, Math.min(kMaxTurnOutput, turn));  // teto

  drive.driveFieldOriented(dirX * speed, dirY * speed, turn);
}"""
code(s, 0.95, 2.2, 8.1, 4.3, src, size=11.4, title=u"execute()")
bullets(s, 9.3, 2.3, 3.1, [
    (u"Rampa ", u"evita passar do alvo: perto do fim, velocidade cai proporcionalmente."),
    (u"Piso ", u"kMinDriveSpeed: abaixo disso o robô não vence o atrito e para antes."),
    (u"Correção P ", u"de rumo: o giro residual é corrigido, com teto para não virar rotação."),
], size=12.5, gap=0.2)
note(s, 9.3, 4.85, 3.1,
     u"Controlador proporcional (P) escrito à mão. Se o robô girar em vez de corrigir, "
     u"o sinal está invertido — use -0.010.",
     color=AM, h=1.85, size=12)

# ---- requirements
s = new_slide(u"addRequirements()", u"A regra que impede dois comandos de brigar")
rect(s, 0.95, 2.22, 11.45, 1.78, fill=PANEL, line=PANEL2, adj=0.06)
b1 = rect(s, 1.3, 2.46, 2.9, 1.25, fill=PANEL2, line=AM, lw=1.3, adj=0.12)
shape_text(b1, u"Comando A\n(default: joystick)", size=12.5, color=AM)
b2 = rect(s, 8.95, 2.46, 2.9, 1.25, fill=PANEL2, line=CY, lw=1.3, adj=0.12)
shape_text(b2, u"Comando B\n(SquareAutoCommand)", size=12.5, color=CY)
b3 = rect(s, 5.0, 2.56, 3.0, 1.05, fill=BG, line=GR, lw=1.5, adj=0.14)
shape_text(b3, u"OmnidriveSubsystem", size=13, color=GR, bold=True, font=MONO)
arrow_r(s, 4.35, 2.95, 0.55, 0.28, color=AM)
arrow_l(s, 8.15, 2.95, 0.55, 0.28, color=CY)
tb(s, 4.95, 3.66, 3.1, 0.3, u"só UM pode usar por vez", size=11.5, color=GR,
   align=PP_ALIGN.CENTER, bold=True)
bullets(s, 0.95, 4.25, 11.45, [
    (u"Quem chega depois ganha. ", u"Ao agendar B, o scheduler interrompe A: chama A.end(true) e passa a rodar B."),
    (u"Esqueceu o addRequirements? ", u"Os dois rodam juntos, os dois escrevem no motor, "
     u"e o motor obedece ao último — bug intermitente, difícil de achar."),
    (u"Comandos que não movem nada ", u"podem não exigir subsystem (ex.: um InstantCommand que só lê um valor)."),
    (u"Em grupos, ", u"as exigências se somam: o grupo exige tudo que seus filhos exigem."),
], size=13.5, gap=0.14)
note(s, 0.95, 6.5, 11.45,
     u"addRequirements(drive) é a linha mais importante de um comando que mexe no robô. "
     u"Decore isso.", color=AM, icon=u"★")

# ---- default command
s = new_slide(u"Default Command", u"O que o subsystem faz quando ninguém manda nada")
src = u"""private void configureDefaultCommands() {
  driveSubsystem.setDefaultCommand(
      new RunCommand(
          () -> driveSubsystem.driveFieldOriented(
                    applyDeadband(joystick.getX(), OIConstants.kDeadband),
                    applyDeadband(-joystick.getY(), OIConstants.kDeadband),
                    applyDeadband(joystick.getZ(), OIConstants.kDeadband)),
          driveSubsystem));          // <<< o subsystem exigido
}"""
code(s, 0.95, 2.2, 7.5, 2.4, src, size=11.8, title=u"RobotContainer.configureDefaultCommands()")
bullets(s, 0.95, 4.9, 7.5, [
    (u"É o teleop. ", u"Enquanto nenhum outro comando exigir o drive, este roda."),
    (u"Volta sozinho. ", u"Quando o autônomo termina ou é cancelado, o default reassume."),
    (u"Nunca termina: ", u"RunCommand tem isFinished() == false para sempre."),
], size=14, gap=0.14)
card(s, 8.75, 2.2, 3.63, 2.4, u"-joystick.getY()",
     u"O eixo Y do joystick é positivo para BAIXO. O sinal negativo faz "
     u"“empurrar para frente” virar valor positivo. Erro clássico de lab.",
     accent=PK, bsize=12)
card(s, 8.75, 4.9, 3.63, 1.5, u"Lambda  () -> ...",
     u"RunCommand recebe um pedaço de código. É lido a cada ciclo — logo, "
     u"lê o joystick sempre atualizado.", accent=VI, bsize=12)

# ---- comandos prontos
s = new_slide(u"Comandos prontos", u"Você escreve menos classes do que imagina")
rows = [
    [u"Classe", u"isFinished()", u"Para que serve", u"No lab"],
    [u"InstantCommand", u"na hora (1 ciclo)", u"disparar uma ação pontual", u"drive::resetHeading"],
    [u"RunCommand", u"nunca", u"rodar um lambda sem fim", u"teleop (default command)"],
    [u"WaitCommand", u"após N segundos", u"pausa entre etapas", u"pause() do quadrado"],
    [u"StartEndCommand", u"nunca", u"liga ao entrar, desliga ao sair", u"—"],
    [u"FunctionalCommand", u"você define", u"os 4 métodos como lambdas", u"—"],
]
table(s, 0.95, 2.25, 11.45, rows, [2.9, 2.5, 3.6, 2.45], size=12.5, rowh=0.43)
src = u"""// Uma acao pontual num botao
new JoystickButton(joystick, OIConstants.kResetGyroButton)
    .whenPressed(new InstantCommand(driveSubsystem::resetHeading));"""
code(s, 0.95, 5.0, 11.45, 1.0, src, size=12, title=u"InstantCommand + botao")
note(s, 0.95, 6.2, 11.45,
     u"Este projeto usa WPILib 2020: o binding é .whenPressed(...) e a geometria vem de "
     u"edu.wpi.first.wpilibj.geometry. Em versões novas isso virou .onTrue(...) e "
     u"edu.wpi.first.math.geometry.", color=AM)

# ---- composicoes
s = new_slide(u"Composições", u"Comandos que contêm comandos")
comps = [
    (u"Sequential", u"um depois do outro", u"A → B → C", GR),
    (u"Parallel", u"todos juntos; acaba\nquando o último acaba", u"A ∥ B ∥ C", CY),
    (u"ParallelRace", u"todos juntos; acaba\nquando o PRIMEIRO acaba", u"A ∥ B → 1º vence", AM),
    (u"ParallelDeadline", u"acaba quando o\n“líder” acaba", u"A(líder) ∥ B", VI),
]
for i, (t, d, g, col) in enumerate(comps):
    x = 0.95 + i * 2.93
    rect(s, x, 2.2, 2.68, 2.1, fill=PANEL, line=col, lw=1.2, adj=0.1)
    tb(s, x + 0.2, 2.38, 2.3, 0.3, t, size=14.5, bold=True, color=col)
    tb(s, x + 0.2, 2.75, 2.3, 0.8, d, size=11.5, color=MUTED, spacing=1.15)
    rect(s, x + 0.2, 3.6, 2.28, 0.52, fill=PANEL2, adj=0.2)
    tb(s, x + 0.3, 3.73, 2.1, 0.3, g, size=11.5, color=TXT, font=MONO,
       align=PP_ALIGN.CENTER)
src = u"""// Decorators: compoem sem criar classe nova
new DriveDistanceCommand(drive, 0, 1, 0.5)
    .withTimeout(6.0)                      // rede de seguranca: desiste em 6 s
    .andThen(new WaitCommand(0.25))        // emenda outro comando no fim
    .alongWith(new InstantCommand(() -> SmartDashboard.putBoolean("indo", true)));"""
code(s, 0.95, 4.6, 11.45, 1.35, src, size=12, title=u"decorators")
note(s, 0.95, 6.1, 11.45,
     u"withTimeout() é um ParallelRace com um WaitCommand por baixo. "
     u"Todo autônomo sério tem timeout em cada etapa — senão um robô travado "
     u"empurra a parede até o fim da partida.", color=AM)

# ---- SquareAuto
s = new_slide(u"Composição real", u"SquareAutoCommand: o quadrado é só uma sequência")
src = u"""public class SquareAutoCommand extends SequentialCommandGroup {
  public SquareAutoCommand(OmnidriveSubsystem drive) {
    double side = AutoConstants.kSquareSideMeters;
    addCommands(
        new InstantCommand(drive::resetHeading),  // o rumo atual vira o zero
        leg(drive,  0.0,  1.0, side),   // lado 1
        pause(),
        leg(drive,  1.0,  0.0, side),   // lado 2
        pause(),
        leg(drive,  0.0, -1.0, side),   // lado 3
        pause(),
        leg(drive, -1.0,  0.0, side),   // lado 4
        new InstantCommand(drive::stop, drive));
  }
  private static Command leg(OmnidriveSubsystem drive, double dirX,
                             double dirY, double meters) {
    return new DriveDistanceCommand(drive, dirX, dirY, meters)
               .withTimeout(AutoConstants.kLegTimeoutSeconds);
  }
  // pause() -> new WaitCommand(AutoConstants.kPauseSeconds)
}"""
code(s, 0.95, 2.2, 7.6, 4.5, src, size=11.2, title=u"SquareAutoCommand.java")
bullets(s, 8.85, 2.3, 3.55, [
    (u"Zero lógica nova. ", u"Só reusa DriveDistanceCommand quatro vezes."),
    (u"Sem girar. ", u"A frente do robô aponta para o mesmo lado o trajeto inteiro — é o ganho do omni."),
    (u"pause() ", u"deixa o robô assentar antes de trocar de direção (inércia + folga)."),
    (u"leg() e pause() ", u"são fábricas privadas: o addCommands fica legível."),
], size=12.5, gap=0.2)

# ---- RobotContainer
s = new_slide(u"RobotContainer", u"O lugar onde o robô é declarado")
src = u"""public class RobotContainer {
  private final OmnidriveSubsystem driveSubsystem = new OmnidriveSubsystem();
  private final Joystick joystick = new Joystick(OIConstants.kJoystickPort);
  private final SendableChooser<Command> autoChooser;

  public RobotContainer() {
    autoChooser = new SendableChooser<>();
    configureButtonBindings();
    configureDefaultCommands();
  }
  private void configureButtonBindings() {
    SmartDashboard.putData("Autonomous", autoChooser);
    autoChooser.setDefaultOption("Autonomous None", new InstantCommand());
    new JoystickButton(joystick, OIConstants.kResetGyroButton)
        .whenPressed(new InstantCommand(driveSubsystem::resetHeading));
    autoChooser.addOption("Auto 1",
        new DriveDistanceCommand(driveSubsystem, 1, -1, 0.2));
    autoChooser.addOption("Auto 2", new SquareAutoCommand(driveSubsystem));
  }
  public Command getAutonomousCommand() { return autoChooser.getSelected(); }
}"""
code(s, 0.95, 2.2, 8.15, 4.5, src, size=11.1, title=u"RobotContainer.java")
bullets(s, 9.35, 2.3, 3.05, [
    (u"Um subsystem só. ", u"Instanciado aqui e passado por construtor a quem precisar."),
    (u"SendableChooser ", u"deixa você escolher o autônomo no dashboard, sem recompilar."),
    (u"Default “None” ", u"= InstantCommand vazio. Robô paradão é o padrão seguro."),
    (u"Auto 1 ", u"anda 0,2 m na diagonal (1, -1) — já normalizada no construtor."),
], size=12.5, gap=0.2)

# ---- Constants
s = new_slide(u"Constants", u"Nenhum número mágico espalhado pelo código")
src = u"""public static final class DriveConstants {
  public static final int kTitanCanId = 42;
  public static final int kMotor1Channel = 0;   // Traseira
  public static final int kMotor2Channel = 1;   // Frontal Direita
  public static final int kMotor3Channel = 2;   // Frontal Esquerda

  // CONFERIR NO HARDWARE
  public static final double kEncoderTicksPerRevolution = 1464.0;
  public static final double kWheelDiameterMeters = 0.100;
  public static final double kEncoderDistancePerTick =
      (Math.PI * kWheelDiameterMeters) / kEncoderTicksPerRevolution;
  public static final double kRobotRadiusMeters = 0.150;
  public static final boolean kGyroReversed = false;
  public static final double kMaxRPM = 150.0;
  public static final double kP = 0.005, kI = 0.001, kD = 0.000;
  public static final double kSlewRateX = 2.0, kSlewRateY = 2.0, kSlewRateZ = 3.0;
}"""
code(s, 0.95, 2.2, 8.0, 3.8, src, size=11.3, title=u"Constants.java")
bullets(s, 9.2, 2.3, 3.2, [
    (u"Três blocos: ", u"DriveConstants (hardware), AutoConstants (trajeto), OIConstants (controle)."),
    (u"Derivadas na classe. ", u"kEncoderDistancePerTick é calculada, não digitada."),
    (u"Comentário “CONFERIR” ", u"marca o que depende de medir o robô."),
], size=12.5, gap=0.2)
note(s, 0.95, 6.1, 11.45,
     u"kGyroReversed vale para a odometria E para o field-oriented ao mesmo tempo. "
     u"Uma flag, duas funções — assim as duas nunca divergem de convenção.",
     color=AM, icon=u"★")

# ---- deadband
s = new_slide(u"Tratando o joystick", u"Zona morta com reescalonamento")
src = u"""private static double applyDeadband(double value, double deadband) {
  if (Math.abs(value) > deadband) {
    return (value - Math.copySign(deadband, value)) / (1.0 - deadband);
  }
  return 0.0;
}"""
code(s, 0.95, 2.2, 6.9, 1.65, src, size=12.3, title=u"applyDeadband()")
bullets(s, 0.95, 4.05, 6.9, [
    (u"O problema: ", u"joystick parado raramente lê 0.000 — lê 0.01, 0.02. O robô “anda sozinho”."),
    (u"Zona morta simples ", u"resolve isso mas cria um degrau: ao passar do limiar, a saída salta."),
    (u"Reescalonamento ", u"estica o que sobrou de volta para 0…1. Sem degrau, sem deriva."),
], size=13.5, gap=0.14)
# grafico
gx, gy, gw, gh = 8.35, 2.2, 4.05, 3.45
rect(s, gx, gy, gw, gh, fill=PANEL, line=PANEL2, adj=0.06)
ox, oy = gx + 0.72, gy + gh - 0.98
line(s, ox, oy, ox + 2.9, oy, color=MUTED, lw=1.1)
line(s, ox, oy, ox, oy - 1.78, color=MUTED, lw=1.1)
tb(s, ox + 1.85, oy + 0.08, 1.1, 0.3, u"entrada", size=10, color=MUTED)
tb(s, gx + 0.16, gy + 0.2, 1.4, 0.3, u"saída", size=10, color=MUTED)
dbx = ox + 0.42
line(s, ox, oy, dbx, oy, color=PK, lw=2.6)
line(s, dbx, oy, ox + 2.8, oy - 1.72, color=GR, lw=2.6)
line(s, ox, oy, ox + 2.8, oy - 1.72, color=DIM, lw=1.1, dash=True)
line(s, dbx, oy, dbx, oy - 1.8, color=DIM, lw=0.9, dash=True)
tb(s, dbx - 0.3, oy + 0.08, 0.6, 0.3, u"5%", size=10, color=PK, bold=True)
tb(s, gx + 0.25, gy + gh - 0.62, 3.6, 0.52,
   u"vermelho = zona morta  ·  verde = reescalonado\npontilhado = sem filtro",
   size=10, color=MUTED, spacing=1.18)
note(s, 0.95, 6.05, 11.45,
     u"Math.copySign(deadband, value) tira a zona morta respeitando o sinal: "
     u"funciona igual empurrando para frente ou para trás.", color=AM, icon=u"★")

# ---- resumo parte 1
s = new_slide(u"Fecha a Parte 1", u"Checklist mental do Command-Based")
chk = [
    (u"O scheduler é chamado?", u"CommandScheduler.getInstance().run() em robotPeriodic().", CY),
    (u"Todo comando que move declara?", u"addRequirements(subsystem) no construtor.", AM),
    (u"O hardware está encapsulado?", u"Motores private final dentro do subsystem.", GR),
    (u"Nada bloqueia?", u"Sem sleep, sem while de espera, sem loop longo.", PK),
    (u"O teleop é um default command?", u"setDefaultCommand(), não código no teleopPeriodic().", VI),
    (u"Toda etapa de autônomo tem timeout?", u".withTimeout(segundos) em cada perna.", OR),
]
for i, (q, a, col) in enumerate(chk):
    x = 0.95 + (i % 2) * 5.78
    y = 2.25 + (i // 2) * 1.42
    rect(s, x, y, 5.5, 1.2, fill=PANEL, line=PANEL2, adj=0.1)
    bx = rect(s, x + 0.22, y + 0.33, 0.52, 0.52, fill=col, adj=0.3)
    shape_text(bx, u"✓", size=18, color=BG, bold=True)
    tb(s, x + 0.95, y + 0.25, 4.4, 0.35, q, size=14, bold=True, color=WHITE)
    tb(s, x + 0.95, y + 0.63, 4.4, 0.5, a, size=11.5, color=MUTED, font=MONO, spacing=1.15)

# =================================================================== PARTE 2
section(u"2", u"OmniDrive", u"Três rodas a 120°: andar para qualquer lado sem precisar girar",
        accent=AM, topics=[u"Holonômico", u"Roda omni", u"Cinemática inversa",
                           u"Normalização", u"PID", u"Field-oriented"])
STATE["sec"] = SEC2

# ---- holonomico
s = new_slide(u"Conceito", u"Holonômico vs. não-holonômico")
card(s, 0.95, 2.2, 5.55, 2.0, u"Base diferencial (2 rodas) — NÃO holonômica",
     u"Controla 2 coisas: velocidade linear e giro. Para ir para o lado, tem que "
     u"girar, andar e girar de volta. 3 graus de liberdade no plano, mas só 2 atuáveis.",
     accent=PK, bsize=12.5)
card(s, 6.85, 2.2, 5.55, 2.0, u"Base omni (3 rodas a 120°) — HOLONÔMICA",
     u"Controla 3 coisas ao mesmo tempo: andar para frente, andar para o lado e "
     u"girar — de forma independente. 3 graus de liberdade, 3 atuadores.",
     accent=GR, bsize=12.5)
formula(s, 0.95, 4.4, 11.45, 0.95,
        u"3 motores   →   3 graus de liberdade:   v_frente ,  v_lado ,  ω",
        size=20, color=AM)
bullets(s, 0.95, 5.55, 11.45, [
    (u"Na prática: ", u"o robô pode transladar na diagonal mantendo a frente apontada "
     u"para o alvo — é exatamente o que o SquareAutoCommand faz."),
    (u"Preço a pagar: ", u"os rolos passivos escorregam; a odometria por roda é menos "
     u"confiável que numa base diferencial. Daí usar a IMU para o ângulo."),
], size=13.5, gap=0.14)

# ---- roda omni
s = new_slide(u"A roda omni", u"Tração num eixo, deslizamento livre no outro")
diag_omni_wheel(s, 3.1, 4.35, R=1.5)
tb(s, 1.3, 6.15, 3.6, 0.3, u"vista de frente da roda", size=11, color=MUTED,
   align=PP_ALIGN.CENTER)
vec(s, 3.1, 4.35, 3.1, 2.55, color=GR, lw=2.2, head=0.22)
tb(s, 3.3, 2.5, 2.4, 0.3, u"tração (motor)", size=11.5, color=GR, bold=True)
vec(s, 3.1, 4.35, 5.0, 4.35, color=PK, lw=2.2, head=0.22)
tb(s, 3.6, 4.48, 1.9, 0.3, u"desliza livre", size=11.5, color=PK, bold=True)
bullets(s, 6.1, 2.35, 6.3, [
    (u"Aro com rolos passivos ", u"montados a 90° do eixo principal."),
    (u"Direção de tração: ", u"a roda empurra o robô na direção do seu próprio giro — "
     u"só nessa direção ela tem força."),
    (u"Direção perpendicular: ", u"os rolos giram e a roda escorrega sem resistir. "
     u"É isso que permite às outras duas rodas arrastarem o robô de lado."),
    (u"Consequência para o código: ", u"cada roda contribui apenas com a componente "
     u"do movimento do chassi projetada na sua direção de tração."),
    (u"Consequência para a odometria: ", u"o escorregamento dos rolos é ruído. "
     u"Distância sai razoável, ângulo sai ruim."),
], size=14, gap=0.16)

# ---- geometria
s = new_slide(u"Geometria do chassi", u"Três rodas, 120° entre elas")
diag_wheels(s, 3.5, 4.0, R=1.25)
bullets(s, 7.25, 2.25, 5.15, [
    (u"Rodas tangenciais: ", u"cada roda é montada tangente ao círculo de raio R "
     u"centrado no robô."),
    (u"120° de separação ", u"= as três direções de tração cobrem o plano inteiro. "
     u"Qualquer vetor de velocidade pode ser decomposto nelas."),
    (u"Mesmo R para as três ", u"→ num giro puro, as três rodas andam o mesmo arco "
     u"R·Δθ, no mesmo sentido."),
    (u"Setas brancas ", u"= sentido positivo de cada roda. Mudar a montagem física "
     u"ou o setInverted() muda o sinal das equações."),
    (u"kRobotRadiusMeters = 0.150", u" — é a única constante geométrica que a "
     u"odometria usa para converter arco em radianos."),
], size=13, gap=0.13)
note(s, 0.95, 6.3, 11.45,
     u"Constants nomeia as rodas como traseira (motor 1), frontal direita (motor 2) e "
     u"frontal esquerda (motor 3) — essa é a ordem dos vetores em todo o código.",
     color=AM, icon=u"★")

# ---- cinematica inversa
s = new_slide(u"Cinemática inversa", u"Do movimento desejado para a velocidade de cada roda")
tb(s, 0.95, 2.2, 11.45, 0.4,
   u"Projetamos o movimento do chassi na direção de tração de cada roda e somamos o arco do giro:",
   size=15, color=MUTED)
formula(s, 0.95, 2.75, 11.45, 2.05,
        u"v1  =  −vx                      +  ω\n"
        u"v2  =  0.5·vx  +  (√3/2)·vy     +  ω\n"
        u"v3  =  0.5·vx  −  (√3/2)·vy     +  ω",
        size=22, color=WHITE, align=PP_ALIGN.CENTER)
src = u"""// Cinematica holonomica de 3 rodas a 120 graus
double targetPercent1 = -vx + wz;
double targetPercent2 = 0.5 * vx + (Math.sqrt(3) / 2.0) * vy + wz;
double targetPercent3 = 0.5 * vx - (Math.sqrt(3) / 2.0) * vy + wz;"""
code(s, 0.95, 5.1, 7.55, 1.35, src, size=12.2, title=u"driveRobotOriented()")
bullets(s, 8.75, 5.05, 3.65, [
    u"Três equações, três incógnitas",
    u"Sistema LINEAR → vale para % , m/s ou metros",
    u"ω aparece igual nas três (mesmo R)",
], size=12.5, gap=0.1)

# ---- conferindo as equacoes
s = new_slide(u"Conferindo na mão", u"Três casos que você deve saber de cabeça")
rows = [
    [u"Movimento", u"vx", u"vy", u"ω", u"v1", u"v2", u"v3", u"Leitura"],
    [u"translação eixo 1", u"1", u"0", u"0", u"−1", u"+0.5", u"+0.5", u"roda 1 contra, 2 e 3 a meia força"],
    [u"translação eixo 2", u"0", u"1", u"0", u"0", u"+0.87", u"−0.87", u"roda 1 só desliza, 2 e 3 opostas"],
    [u"giro puro", u"0", u"0", u"1", u"+1", u"+1", u"+1", u"as três iguais → gira no lugar"],
    [u"diagonal", u"1", u"1", u"0", u"−1", u"+1.37", u"−0.37", u"passou de 1 → precisa normalizar"],
]
table(s, 0.95, 2.25, 11.45, rows, [2.6, 0.75, 0.75, 0.75, 0.95, 0.95, 0.95, 3.75],
      size=12.5, rowh=0.5)
note(s, 0.95, 5.0, 11.45,
     u"O caso do giro puro é o teste mais útil: se as três rodas recebem o MESMO valor "
     u"e o robô translada em vez de girar, uma roda está montada ou invertida errado.",
     color=AM, icon=u"★")
bullets(s, 0.95, 5.95, 11.45, [
    (u"Linha 4 é o motivo da normalização: ", u"o somatório estourou o alcance do motor "
     u"(|v2| = 1.37 > 1). Sem tratar, o motor satura e a DIREÇÃO do movimento muda."),
], size=14.5, gap=0.12)

# ---- normalizacao
s = new_slide(u"Normalização", u"Saturar sem distorcer a direção")
src = u"""// Normalizacao de velocidade
double maxSpeed = Math.max(Math.abs(targetPercent1),
                  Math.max(Math.abs(targetPercent2), Math.abs(targetPercent3)));
if (maxSpeed > 1.0) {
  targetPercent1 /= maxSpeed;
  targetPercent2 /= maxSpeed;
  targetPercent3 /= maxSpeed;
}"""
code(s, 0.95, 2.2, 7.2, 2.1, src, size=12.2, title=u"driveRobotOriented()")
bullets(s, 0.95, 4.6, 7.2, [
    (u"Divide as TRÊS pelo mesmo fator. ", u"O vetor fica mais curto, mas aponta "
     u"exatamente para o mesmo lado."),
    (u"Cortar individualmente (clamp) seria o erro: ", u"só a roda que estourou seria "
     u"reduzida, e o robô sairia torto."),
    (u"Efeito prático: ", u"na diagonal o robô fica mais lento que na reta — e isso é "
     u"fisicamente inevitável."),
], size=14, gap=0.16)
# ilustracao
bx, by = 8.4, 2.3
rect(s, bx, by, 3.98, 3.65, fill=PANEL, line=PANEL2, adj=0.06)
cx0, cy0 = bx + 1.1, by + 2.6
line(s, cx0 - 0.2, cy0, cx0 + 2.6, cy0, color=MUTED, lw=1.0)
line(s, cx0, cy0 + 0.2, cx0, cy0 - 2.1, color=MUTED, lw=1.0)
vec(s, cx0, cy0, cx0 + 2.35, cy0 - 1.85, color=PK, lw=2.2, head=0.2)
vec(s, cx0, cy0, cx0 + 1.6, cy0 - 1.26, color=GR, lw=2.6, head=0.22)
tb(s, bx + 0.25, by + 0.2, 3.5, 0.3, u"pedido vs. normalizado", size=12,
   color=WHITE, bold=True)
tb(s, bx + 0.25, by + 3.05, 3.5, 0.5,
   u"rosa = pedido (estourou)   ·   verde = enviado\nmesma direção, módulo menor",
   size=10.5, color=MUTED, spacing=1.15)

# ---- malha fechada
s = new_slide(u"Malha fechada", u"De “porcentagem” a RPM controlado")
diag_drive_pipeline(s, 0.95, 2.3, 11.45)
src = u"""// Converte em RPM Alvo
double targetRPM1 = targetPercent1 * DriveConstants.kMaxRPM;   // ... 2 e 3 idem

// Leitura real dos encoders
double currentRPM1 = motor1.getRPM();

// Correcao PID
double output1 = pid1.calculate(currentRPM1, targetRPM1);
motor1.set(output1);"""
code(s, 0.95, 3.85, 7.2, 2.15, src, size=12.2, title=u"driveRobotOriented()")
bullets(s, 8.4, 3.9, 4.0, [
    (u"Malha aberta ", u"= mandar % direto no motor. Bateria baixa ou tapete pesado → robô mais lento."),
    (u"Malha fechada ", u"= medir o RPM e corrigir. O robô anda igual com bateria cheia ou meia."),
    (u"Um PID por roda ", u"porque cada roda tem seu próprio atrito e carga."),
], size=12.5, gap=0.16)
note(s, 0.95, 6.35, 11.45,
     u"Atenção à assinatura antiga do WPILib: pid.calculate(medida, setpoint) — "
     u"a MEDIDA vem primeiro. Trocar a ordem inverte o sinal do erro e o robô foge do alvo.",
     color=AM)

# ---- PID
s = new_slide(u"PID por roda", u"O que cada ganho faz na sua base")
pid = [
    (u"P — Proporcional", u"kP = 0.005", u"Reage ao erro de agora. Erro de 100 RPM → saída 0.5. "
     u"Muito alto: oscila e a roda “chia”. Muito baixo: nunca chega no alvo.", CY),
    (u"I — Integral", u"kI = 0.001", u"Acumula erro pequeno e persistente — vence o atrito que o P "
     u"sozinho não vence. Muito alto: sobressinal e windup.", AM),
    (u"D — Derivativo", u"kD = 0.000", u"Reage à variação do erro, freia a oscilação. "
     u"Desligado aqui: encoder quantizado gera ruído e o D amplifica ruído.", VI),
]
for i, (t, g, d, col) in enumerate(pid):
    y = 2.25 + i * 1.26
    rect(s, 0.95, y, 11.45, 1.1, fill=PANEL, line=PANEL2, adj=0.1)
    rect(s, 0.95, y + 0.11, 0.055, 0.88, fill=col, shape=MSO_SHAPE.RECTANGLE)
    tb(s, 1.25, y + 0.17, 3.0, 0.35, t, size=15, bold=True, color=col)
    gb = rect(s, 1.25, y + 0.56, 1.55, 0.4, fill=PANEL2, adj=0.2)
    shape_text(gb, g, size=12, color=WHITE, font=MONO)
    tb(s, 3.2, y + 0.17, 9.0, 0.82, d, size=13, color=TXT, spacing=1.22)
src = u"""public void stop() {
  motor1.set(0); motor2.set(0); motor3.set(0);
  pid1.reset(); pid2.reset(); pid3.reset();      // zera o acumulador do I
  xLimiter.reset(0.0); yLimiter.reset(0.0); zLimiter.reset(0.0);
}"""
note(s, 0.95, 6.6, 11.45,
     u"stop() chama pid.reset() de propósito: sem isso o termo I guarda o erro da "
     u"manobra anterior e o próximo comando começa com um empurrão fantasma.",
     color=AM, icon=u"★")

# ---- slew rate
s = new_slide(u"SlewRateLimiter", u"Rampa de aceleração: proteger o robô de você mesmo")
src = u"""private final SlewRateLimiter xLimiter = new SlewRateLimiter(DriveConstants.kSlewRateX);
private final SlewRateLimiter yLimiter = new SlewRateLimiter(DriveConstants.kSlewRateY);
private final SlewRateLimiter zLimiter = new SlewRateLimiter(DriveConstants.kSlewRateZ);

double smoothVx = xLimiter.calculate(fieldVx);   // kSlewRateX = 2.0  -> 0 a 100% em 0,5 s
double smoothVy = yLimiter.calculate(fieldVy);   // kSlewRateY = 2.0
double smoothWz = zLimiter.calculate(wz);        // kSlewRateZ = 3.0  -> giro responde mais rapido"""
code(s, 0.95, 2.2, 11.45, 1.85, src, size=11.8, title=u"OmnidriveSubsystem")
# grafico rampa
gx, gy, gw, gh = 0.95, 4.35, 5.6, 2.25
rect(s, gx, gy, gw, gh, fill=PANEL, line=PANEL2, adj=0.06)
ox, oy = gx + 0.6, gy + gh - 0.5
line(s, ox, oy, ox + 4.5, oy, color=MUTED, lw=1.0)
line(s, ox, oy, ox, oy - 1.4, color=MUTED, lw=1.0)
line(s, ox, oy - 1.3, ox + 0.001, oy - 1.3, color=MUTED)
line(s, ox, oy, ox + 0.85, oy, color=PK, lw=2.2)
line(s, ox + 0.85, oy, ox + 0.85, oy - 1.3, color=PK, lw=2.2)
line(s, ox + 0.85, oy - 1.3, ox + 4.4, oy - 1.3, color=PK, lw=2.2)
line(s, ox, oy, ox + 0.85, oy, color=GR, lw=2.2)
line(s, ox + 0.85, oy, ox + 2.4, oy - 1.3, color=GR, lw=2.4)
line(s, ox + 2.4, oy - 1.3, ox + 4.4, oy - 1.3, color=GR, lw=2.4)
tb(s, gx + 0.25, gy + 0.14, 5.0, 0.3, u"degrau (rosa) vs. rampa (verde)", size=11.5,
   color=WHITE, bold=True)
tb(s, ox + 2.6, oy + 0.1, 2.0, 0.3, u"tempo", size=10.5, color=MUTED)
bullets(s, 7.0, 4.3, 5.4, [
    (u"Unidade: ", u"variação máxima por segundo. 2.0 → vai de 0 a 1 em 0,5 s."),
    (u"Por que importa: ", u"degrau de potência faz a roda “cantar”, escorregar e "
     u"estragar a odometria."),
    (u"Giro mais rápido (3.0) ", u"porque girar envolve menos inércia do que arrancar."),
    (u"Aplicado ANTES ", u"da rotação campo→robô e da cinemática."),
], size=13, gap=0.14)

# ---- field oriented conceito
s = new_slide(u"Field-oriented", u"“Para frente” é do campo, não do robô")
diag_field_oriented(s, 10.0, 4.4, theta_deg=38.0, R=1.05)
bullets(s, 0.95, 2.3, 5.4, [
    (u"Robot-oriented: ", u"empurrar o joystick para frente move o robô na direção "
     u"que ELE está apontando. Se o robô girou, o piloto se perde."),
    (u"Field-oriented: ", u"empurrar para frente move o robô para o fundo do campo, "
     u"não importa como ele está girado."),
    (u"Como: ", u"girar o vetor de comando de −θ, levando-o do referencial do campo "
     u"para o do robô, antes da cinemática."),
    (u"θ vem da IMU ", u"— a mesma fonte que a odometria usa, de propósito."),
], size=13.5, gap=0.16)
formula(s, 0.95, 5.5, 5.4, 1.15,
        u"vx_robô =  vx·cosθ + vy·senθ\nvy_robô = −vx·senθ + vy·cosθ",
        size=15.5, color=WHITE)

# ---- field oriented codigo
s = new_slide(u"Field-oriented no código", u"Seis linhas que mudam a experiência do piloto")
src = u"""public void driveFieldOriented(double fieldVx, double fieldVy, double wz) {
  double smoothVx = xLimiter.calculate(fieldVx);
  double smoothVy = yLimiter.calculate(fieldVy);
  double smoothWz = zLimiter.calculate(wz);

  // Rotacao campo -> robo: gira o vetor de -theta. Mesma fonte de rumo
  // que a odometria usa, para as duas nao divergirem de convencao.
  Rotation2d angle = getRotation2d();
  double robotVx =  smoothVx * angle.getCos() + smoothVy * angle.getSin();
  double robotVy = -smoothVx * angle.getSin() + smoothVy * angle.getCos();

  driveRobotOriented(robotVx, robotVy, smoothWz);
}"""
code(s, 0.95, 2.2, 7.8, 3.15, src, size=11.5, title=u"driveFieldOriented()")
code(s, 0.95, 5.55, 7.8, 1.3, u"""public Rotation2d getRotation2d() {   // unica porta de entrada do angulo
  return Rotation2d.fromDegrees(
      DriveConstants.kGyroReversed ? -getHeading() : getHeading());
}""", size=11.5, title=u"getRotation2d()")
bullets(s, 9.0, 2.3, 3.4, [
    (u"Ordem: ", u"rampa → rotação → cinemática → normalização → PID. Cada etapa é independente."),
    (u"ω não gira. ", u"Velocidade angular é a mesma nos dois referenciais."),
    (u"getRotation2d() ", u"é a ÚNICA porta de entrada do ângulo: aplica kGyroReversed e entrega na convenção WPILib (anti-horário positivo)."),
], size=12.5, gap=0.2)
note(s, 9.0, 4.95, 3.4,
     u"Teste de bancada: gire o robô 90° na mão e empurre o joystick para frente. "
     u"Ele deve ir para a mesma direção do campo de antes. Se andar 90° errado, "
     u"inverta kGyroReversed.", color=AM, icon=u"★", h=1.9)

# ---- reset heading
s = new_slide(u"resetHeading()", u"Reancorar o campo — e por que a pose vai junto")
src = u"""public void resetHeading() {
  ahrs.reset();                                     // IMU: rumo atual passa a ser 0
  odometry.resetPose(new Pose2d(), getRotation2d()); // pose volta para a origem
}"""
code(s, 0.95, 2.2, 11.45, 1.15, src, size=12.3, title=u"OmnidriveSubsystem")
bullets(s, 0.95, 3.75, 11.45, [
    (u"O que o botão faz: ", u"a posição e o rumo atuais passam a ser a origem (0, 0) "
     u"apontando para 0°. O piloto usa isso quando perde a referência."),
    (u"Por que zerar a pose também: ", u"o eixo de referência girou. Manter o x,y antigo "
     u"com um θ novo deixaria a posição num referencial e o ângulo em outro — a pose "
     u"viraria lixo silenciosamente."),
    (u"Onde fica o binding: ", u"RobotContainer, no trigger (botão 1) do joystick, via "
     u"InstantCommand(driveSubsystem::resetHeading)."),
    (u"Diferente de resetPose(): ", u"resetHeading joga tudo para a origem; resetPose "
     u"declara uma pose conhecida SEM mexer na IMU."),
], size=14.5, gap=0.18)
note(s, 0.95, 6.5, 11.45,
     u"SquareAutoCommand começa com resetHeading(): o rumo em que o robô foi colocado "
     u"no tapete passa a ser o 0° daquele trajeto.", color=AM, icon=u"★")

# ---- convencao de eixos
s = new_slide(u"Cuidado", u"Convenção de eixos: o erro mais comum deste lab")
bullets(s, 0.95, 2.2, 11.45, [
    (u"O mesmo par de números tem dois nomes no projeto. ", u"O Javadoc de "
     u"Odometry.forwardKinematics chama a primeira saída de “para frente”; o de "
     u"DriveDistanceCommand chama dirX de “componente lateral”."),
    (u"Pela montagem de Constants ", u"(1 traseira, 2 frontal direita, 3 frontal "
     u"esquerda), a roda traseira é a que empurra o robô de LADO — e é ela que aparece "
     u"sozinha na primeira equação."),
], size=13.5, gap=0.14)
formula(s, 0.95, 3.72, 11.45, 0.8,
        u"v1 = −vx   →   a roda traseira só responde a vx   →   vx é o eixo LATERAL",
        size=17, color=AM)
bullets(s, 0.95, 4.72, 11.45, [
    (u"Consequência: ", u"a pose publicada (Odom X / Odom Y) pode estar girada 90° em "
     u"relação ao que o nome sugere. O quadrado continua fechando — só é percorrido girado."),
    (u"Como verificar em 30 segundos: ", u"robô desabilitado no tapete, empurre-o para a "
     u"frente 1 metro na mão. Se “Odom Y” mudou e “Odom X” não, o nome está trocado."),
], size=13.5, gap=0.14)
note(s, 0.95, 6.5, 11.45,
     u"Nenhuma fórmula aqui está errada — o que precisa casar é NOME × MONTAGEM FÍSICA. "
     u"Meça no robô e corrija os nomes (ou os sinais) antes de confiar na odometria.",
     color=AM)

# =================================================================== PARTE 3
section(u"3", u"Odometria", u"Invertendo a cinemática para saber onde o robô está",
        accent=GR, topics=[u"Pose2d", u"Cinemática direta", u"Twist2d", u"exp()",
                           u"Resets", u"Testes"])
STATE["sec"] = SEC3

# ---- pose
s = new_slide(u"O que é pose", u"Três números que respondem “onde eu estou?”")
formula(s, 0.95, 2.2, 11.45, 1.15,
        u"Pose2d = ( x [m],  y [m],  θ )     no referencial do CAMPO",
        size=18, color=WHITE)
cards3 = [
    (u"Para que serve", u"Autônomo que vai a um ponto, retorno a uma\n"
     u"posição conhecida, correção por visão, gráfico\n"
     u"da trajetória no dashboard.", CY),
    (u"De onde vem", u"Encoders (quanto cada roda andou) +\n"
     u"IMU (para onde o robô aponta).\nNada de GPS, nada de tempo.", GR),
    (u"Convenção WPILib", u"x para a frente do campo, y para a esquerda,\n"
     u"θ positivo no sentido anti-horário.\nÂngulos em Rotation2d, nunca em double solto.", AM),
    (u"Limitação", u"É estimativa acumulada: o erro só cresce.\n"
     u"Boa por metros e segundos, não por\n"
     u"uma partida inteira sem correção.", PK),
]
for i, (t, d, col) in enumerate(cards3):
    card(s, 0.95 + (i % 2) * 5.78, 3.7 + (i // 2) * 1.6, 5.5, 1.4, t, d,
         accent=col, hsize=14, bsize=12)

# ---- cinematica direta
s = new_slide(u"Cinemática direta", u"Invertendo o sistema: das rodas para o chassi")
tb(s, 0.95, 2.18, 11.45, 0.35,
   u"Partimos das três equações das rodas — agora em METROS percorridos — e isolamos o movimento do chassi:",
   size=14, color=MUTED)
formula(s, 0.95, 2.6, 5.55, 1.45,
        u"d1 = −dx            + R·dθ\n"
        u"d2 = 0.5dx + k·dy   + R·dθ\n"
        u"d3 = 0.5dx − k·dy   + R·dθ\n"
        u"           k = √3/2",
        size=15.5, color=MUTED, align=PP_ALIGN.LEFT)
arrow_r(s, 6.72, 3.2, 0.5, 0.3, color=AM)
formula(s, 7.45, 2.6, 4.95, 1.45,
        u"arco = (d1 + d2 + d3) / 3\n"
        u"dx   = arco − d1\n"
        u"dy   = (d2 − d3) / √3\n"
        u"dθ   = arco / R",
        size=15.5, color=WHITE, align=PP_ALIGN.LEFT)
note(s, 0.95, 4.12, 11.45,
     u"A mágica: somando as três equações, dx e dy se cancelam (−1+0.5+0.5 = 0 e +k−k = 0) "
     u"e sobra d1+d2+d3 = 3·R·dθ. Esse é o arco comum de rotação. O resto sai por substituição.",
     color=AM, icon=u"★", h=0.72)
src = u"""public static double[] forwardKinematics(double d1, double d2, double d3) {
  // Componente comum as tres rodas: o arco percorrido na rotacao pura
  double rotationArc = (d1 + d2 + d3) / 3.0;
  double dx     = rotationArc - d1;
  double dy     = (d2 - d3) / Math.sqrt(3.0);
  double dTheta = rotationArc / DriveConstants.kRobotRadiusMeters;
  return new double[] { dx, dy, dTheta };
}"""
code(s, 0.95, 5.05, 8.3, 1.85, src, size=11.8, title=u"Odometry.forwardKinematics()")
bullets(s, 9.6, 5.15, 2.8, [
    u"static: sem hardware, sem estado",
    u"Vale para metros ou m/s",
    u"Testável no PC",
], size=11.5, gap=0.1)

# ---- encoder ticks
s = new_slide(u"Do tick ao metro", u"A régua do robô")
formula(s, 0.95, 2.2, 11.45, 1.35,
        u"metros por tick  =  (π · diâmetro da roda) / ticks por volta\n"
        u"               =  (π · 0.100) / 1464  ≈  0.0002146 m  ≈  0.21 mm",
        size=19, color=WHITE)
src = u"""public static final double kEncoderDistancePerTick =
    (Math.PI * kWheelDiameterMeters) / kEncoderTicksPerRevolution;

// O Titan já devolve METROS porque recebeu o fator na criacao do encoder:
encoder1 = titan0.getEncoder(DriveConstants.kEncoder1Channel,
                             DriveConstants.kEncoderDistancePerTick);"""
code(s, 0.95, 3.9, 7.1, 1.85, src, size=11.8, title=u"Constants + OmnidriveSubsystem")
bullets(s, 8.3, 3.95, 4.1, [
    (u"Resolução de 0,21 mm ", u"é ótima — o gargalo é o escorregamento, não o encoder."),
    (u"Erro proporcional: ", u"diâmetro 5% errado → toda distância 5% errada."),
    (u"Meça o diâmetro ", u"com o robô no chão, com a roda achatada pelo peso."),
], size=12.5, gap=0.16)
note(s, 0.95, 6.1, 11.45,
     u"Calibração prática: marque 2,00 m no tapete, mande o robô andar 2,00 m e meça o real. "
     u"Se andou 1,90 m, multiplique kWheelDiameterMeters por 2,00/1,90.", color=AM, icon=u"★")

# ---- rodas vs IMU
s = new_slide(u"θ das rodas × θ da IMU", u"A decisão de projeto mais importante da odometria")
card(s, 0.95, 2.25, 5.55, 2.55, u"θ estimado pelas rodas",
     u"dθ = arco / R, com arco = (d1+d2+d3)/3.\n\n"
     u"Funciona na teoria. Na prática: rolos\n"
     u"passivos escorregam, R nunca é exato e o erro\n"
     u"de ângulo é INTEGRADO — ele contamina x e y\n"
     u"em todos os ciclos seguintes.", accent=PK, bsize=12.5)
card(s, 6.85, 2.25, 5.55, 2.55, u"θ medido pela IMU (navX)",
     u"Giroscópio integrado, independente de contato\n"
     u"com o chão.\n\n"
     u"Deriva lenta (graus por minuto), sem acoplamento\n"
     u"com escorregamento. É de longe a melhor fonte\n"
     u"de rumo numa base omni.", accent=GR, bsize=12.5)
formula(s, 0.95, 5.1, 11.45, 0.95,
        u"Decisão:  dx, dy  vêm das RODAS   ·   θ  vem da IMU",
        size=21, color=AM)
bullets(s, 0.95, 6.25, 11.45, [
    (u"forwardKinematics ainda devolve dθ ", u"— mas updateWithTime o ignora para a pose "
     u"e usa o delta da IMU. O dθ das rodas serve só para estimar velocidade angular."),
], size=13.5, gap=0.1)

# ---- pipeline odometria
s = new_slide(u"O ciclo da odometria", u"O que acontece a cada 20 ms no periodic()")
diag_odom_pipeline(s, 0.95, 3.0, 11.45)
bullets(s, 0.95, 5.6, 11.45, [
    (u"1. Delta, não absoluto: ", u"a odometria guarda a leitura anterior de cada encoder "
     u"e trabalha com a diferença. Assim ela nunca depende do zero do encoder."),
    (u"2. Rumo absoluto: ", u"θ = IMU + gyroOffset; dθ = θ − θ_anterior."),
    (u"3. Integração: ", u"a pose nova sai da pose anterior mais o deslocamento do ciclo."),
], size=14, gap=0.14)

# ---- exp
s = new_slide(u"pose.exp()", u"Integrar o arco, não a corda")
diag_arc(s, 0.95, 2.2)
bullets(s, 0.95, 4.92, 11.45, [
    (u"O problema: ", u"se o robô translada E gira no mesmo ciclo, a trajetória dentro "
     u"dos 20 ms é um arco. Somar o deslocamento como uma reta corta o canto."),
    (u"Twist2d(dx, dy, dθ) ", u"descreve esse movimento no referencial do próprio robô: "
     u"“ande tanto para frente, tanto para o lado, girando tanto”."),
    (u"pose.exp(twist) ", u"aplica o mapa exponencial — integra o arco de verdade e "
     u"devolve a pose nova no campo."),
    (u"Quando dθ ≈ 0, ", u"exp() cai na soma simples. Você não paga nada em linha reta "
     u"e ganha precisão nas curvas."),
], size=13.5, gap=0.12)

# ---- updateWithTime
s = new_slide(u"Odometry.updateWithTime()", u"O coração, linha por linha")
src = u"""public Pose2d updateWithTime(double now, Rotation2d gyroAngle,
                             double distance1, double distance2, double distance3) {
  double delta1 = distance1 - lastDistance1;      // 1. delta de cada roda
  double delta2 = distance2 - lastDistance2;
  double delta3 = distance3 - lastDistance3;
  lastDistance1 = distance1;                      // 2. memoriza p/ o proximo ciclo
  lastDistance2 = distance2;
  lastDistance3 = distance3;

  double[] robotDelta = forwardKinematics(delta1, delta2, delta3);  // 3. rodas -> chassi

  Rotation2d angle = gyroAngle.plus(gyroOffset);        // 4. rumo absoluto
  double dTheta = angle.minus(previousAngle).getRadians();

  // exp() integra o arco de verdade (trajetoria curva), em vez de
  // assumir que o robo andou em linha reta dentro do ciclo
  Pose2d newPose = pose.exp(new Twist2d(robotDelta[0], robotDelta[1], dTheta));

  pose = new Pose2d(newPose.getTranslation(), angle);   // 5. theta SEMPRE da IMU
  previousAngle = angle;
  updateVelocities(now, robotDelta[0], robotDelta[1], dTheta);
  return pose;
}"""
code(s, 0.95, 2.2, 8.5, 4.65, src, size=11.0, title=u"Odometry.java")
bullets(s, 9.7, 2.3, 2.7, [
    (u"Passo 5 ", u"é a chave: a translação vem do exp(), mas θ é sobrescrito pela IMU."),
    (u"previousAngle ", u"só existe para extrair dθ do ciclo."),
    (u"Retorna a pose ", u"por conveniência — o estado fica guardado no objeto."),
    (u"updateWithTime ", u"recebe o tempo por fora: é o que permite testar no PC."),
], size=12, gap=0.18)

# ---- resetPose
s = new_slide(u"resetPose()", u"Teleportar o robô sem mexer na IMU")
src = u"""public void resetPose(Pose2d newPose, Rotation2d gyroAngle) {
  pose = newPose;
  previousAngle = newPose.getRotation();
  gyroOffset = newPose.getRotation().minus(gyroAngle);   // guarda a diferenca
}"""
code(s, 0.95, 2.2, 7.2, 1.45, src, size=12.2, title=u"Odometry.java")
formula(s, 0.95, 4.0, 7.2, 0.85,
        u"θ_pose  =  θ_IMU  +  gyroOffset",
        size=19, color=AM)
bullets(s, 0.95, 4.95, 7.2, [
    (u"Para que serve: ", u"início do autônomo (“estou no ponto A da arena”), correção "
     u"por visão, referência de parede ou marcação do tapete."),
    (u"Por que offset e não reset da IMU: ", u"zerar o giroscópio perde a referência "
     u"acumulada. O offset é instantâneo e exato."),
], size=13, gap=0.12)
code(s, 8.4, 2.2, 3.98, 2.95, u"""@Test
public void resetPoseMantemORumoDaImu() {
  Rotation2d gyro = fromDegrees(30.0);
  odometry.resetPose(
      new Pose2d(2.0, 1.0,
          fromDegrees(90.0)), gyro);

  // IMU anda 10 graus ->
  // a pose anda os mesmos 10
  odometry.updateWithTime(0.02,
      fromDegrees(40.0), 0, 0, 0);
  assertEquals(100.0, ...);
}""", size=10.3, title=u"OdometryTest")
note(s, 0.95, 6.5, 11.45,
     u"No subsystem: resetPose(pose) repassa getRotation2d() como rumo cru, e "
     u"resetOdometry() é só resetPose(new Pose2d()).", color=AM, icon=u"★")

# ---- resetWheelBaseline
s = new_slide(u"resetWheelBaseline()", u"O bug clássico — e a defesa contra ele")
rect(s, 0.95, 2.2, 5.55, 2.35, fill=PANEL, line=PK, lw=1.3, adj=0.07)
tb(s, 1.22, 2.4, 5.0, 0.35, u"Sem reancorar", size=15, bold=True, color=PK)
tb(s, 1.22, 2.85, 5.05, 1.6,
   u"1. Encoders leem 1,00 m\n"
   u"2. Comando chama resetEncoders() → leitura vai a 0\n"
   u"3. Próximo ciclo: delta = 0 − 1,00 = −1,00 m\n"
   u"4. A odometria “vê” o robô pular 1 metro para trás",
   size=12.5, color=TXT, spacing=1.25)
rect(s, 6.85, 2.2, 5.55, 2.35, fill=PANEL, line=GR, lw=1.3, adj=0.07)
tb(s, 7.12, 2.4, 5.0, 0.35, u"Com reancoragem", size=15, bold=True, color=GR)
tb(s, 7.12, 2.85, 5.05, 1.6,
   u"resetEncoders() avisa a odometria que a nova\n"
   u"leitura de referência é 0. O delta do próximo\n"
   u"ciclo volta a ser o movimento real, e a pose\n"
   u"não se move nem um milímetro no reset.",
   size=12.5, color=TXT, spacing=1.25)
src = u"""public void resetEncoders() {
  encoder1.reset(); encoder2.reset(); encoder3.reset();

  // Sem isso o salto da leitura (valor atual -> zero) entraria na
  // odometria como um deslocamento gigante para tras.
  odometry.resetWheelBaseline(0.0, 0.0, 0.0);
}"""
code(s, 0.95, 4.95, 6.9, 1.85, src, size=11.8, title=u"OmnidriveSubsystem")
code(s, 8.1, 4.95, 4.28, 1.85, u"""@Test
public void zerarEncodersNaoMoveAPose() {
  // ... anda 1 m, zera, atualiza
  assertEquals(1.0, pose.getX(), 1e-6);
}""", size=11, title=u"OdometryTest")

# ---- velocidade e telemetria
s = new_slide(u"Velocidade e telemetria", u"O mesmo delta serve para duas coisas")
src = u"""private void updateVelocities(double now, double dx, double dy, double dTheta) {
  double period = (lastTimestamp >= 0.0) ? (now - lastTimestamp) : 0.0;
  lastTimestamp = now;

  if (period > 1e-6) {                 // periodo real: nao assume 20 ms exatos
    vxMetersPerSecond = dx / period;
    vyMetersPerSecond = dy / period;
    omegaRadPerSecond = dTheta / period;
  }
}"""
code(s, 0.95, 2.2, 7.1, 2.45, src, size=11.7, title=u"Odometry.updateVelocities()")
src2 = u"""private void publishTelemetry() {
  Pose2d pose = getPose();
  SmartDashboard.putNumber("Odom X (m)", pose.getTranslation().getX());
  SmartDashboard.putNumber("Odom Y (m)", pose.getTranslation().getY());
  SmartDashboard.putNumber("Odom Theta (deg)", pose.getRotation().getDegrees());
  SmartDashboard.putNumber("Odom Vx (m/s)", odometry.getVxMetersPerSecond());
  SmartDashboard.putNumber("Odom Vy (m/s)", odometry.getVyMetersPerSecond());
}"""
code(s, 0.95, 4.95, 7.1, 1.95, src2, size=11.3, title=u"publishTelemetry()")
bullets(s, 8.3, 2.3, 4.1, [
    (u"Período real ", u"e não 0.02 fixo: o scheduler não é um relógio perfeito."),
    (u"Guarda o 1e-6 ", u"para não dividir por zero no primeiro ciclo."),
    (u"Velocidade medida ", u"é ouro para depurar: compare com o que você pediu."),
    (u"Telemetria sempre, ", u"inclusive desabilitado. É o seu osciloscópio."),
], size=12.5, gap=0.18)

# ---- testes
s = new_slide(u"Testes sem robô", u"A matemática fecha antes de pisar no tapete")
src = u"""/** Cinematica inversa: quanto cada roda gira para o chassi se deslocar. */
private static double[] wheels(double dx, double dy, double dThetaRad) {
  double arc = R * dThetaRad;
  return new double[] { -dx + arc,
                         0.5 * dx + (Math.sqrt(3.0) / 2.0) * dy + arc,
                         0.5 * dx - (Math.sqrt(3.0) / 2.0) * dy + arc };
}
@Test
public void quadradoFechaNaOrigem() {
  WheelSim sim = new WheelSim();
  double side = 0.5;
  sim.drive( side,   0.0, 0.0, new Rotation2d(), 25);
  sim.drive(  0.0,  side, 0.0, new Rotation2d(), 25);
  sim.drive(-side,   0.0, 0.0, new Rotation2d(), 25);
  sim.drive(  0.0, -side, 0.0, new Rotation2d(), 25);
  assertEquals(0.0, sim.pose().getTranslation().getX(), 1e-6);
  assertEquals(0.0, sim.pose().getTranslation().getY(), 1e-6);
}"""
code(s, 0.95, 2.2, 7.9, 3.85, src, size=11.1, title=u"OdometryTest.java")
bullets(s, 9.15, 2.3, 3.25, [
    (u"A ideia: ", u"simular os encoders. Do movimento conhecido do chassi, calcular quanto cada roda giraria, e alimentar a odometria."),
    (u"Ida e volta: ", u"se a pose estimada bate com o movimento imposto, a matemática fechou."),
    (u"Sem HAL, sem robô. ", u"É por isso que Odometry não toca em hardware."),
], size=12.5, gap=0.2)
rect(s, 9.15, 5.42, 3.25, 0.6, fill=PANEL2, line=GR, lw=1.2, adj=0.2)
tb(s, 9.35, 5.56, 2.9, 0.35, u"./gradlew test", size=14, color=GR, font=MONO, bold=True)
note(s, 0.95, 6.15, 11.45,
     u"Os 11 testes cobrem: as três cinemáticas diretas, andar reto, andar de lado, girar "
     u"no lugar, o acoplamento ângulo×eixo, o quadrado, os dois resets e a velocidade.",
     color=AM, icon=u"★")

# ---- fontes de erro
s = new_slide(u"Fontes de erro", u"Por que a pose “foge” — em ordem de impacto")
errs = [
    (u"1", u"Escorregamento dos rolos", u"Aceleração brusca, colisão, rampa. A roda gira e "
     u"o robô não anda. Mitigação: SlewRateLimiter e velocidade moderada.", PK),
    (u"2", u"Diâmetro da roda errado", u"Erro proporcional em TODA distância. Mitigação: "
     u"calibrar andando 2 m medidos.", OR),
    (u"3", u"Raio do chassi errado", u"Só afeta o dθ estimado pelas rodas — aqui, apenas "
     u"a velocidade angular, já que θ vem da IMU.", AM),
    (u"4", u"Deriva da IMU", u"Graus por minuto. Mitigação: resetHeading entre manobras, "
     u"ou correção por visão/parede.", VI),
    (u"5", u"Ciclos perdidos", u"Código bloqueante no periodic faz o passo de integração "
     u"crescer e a aproximação piorar.", CY),
]
for i, (n, t, d, col) in enumerate(errs):
    y = 2.2 + i * 0.93
    rect(s, 0.95, y, 11.45, 0.8, fill=PANEL if i % 2 == 0 else BG, line=PANEL2, adj=0.14)
    nb = rect(s, 1.15, y + 0.17, 0.46, 0.46, fill=col, adj=0.3)
    shape_text(nb, n, size=15, color=BG, bold=True)
    tb(s, 1.82, y + 0.24, 3.3, 0.35, t, size=14, bold=True, color=col)
    tb(s, 5.2, y + 0.11, 7.0, 0.62, d, size=12, color=TXT, spacing=1.18,
       anchor=MSO_ANCHOR.MIDDLE)

# ---- checklist de calibracao
s = new_slide(u"Checklist no tapete", u"A ordem em que você deve conferir o robô")
steps = [
    (u"Rodas na direção certa", u"Robô suspenso. Mande giro puro (vx=vy=0, ω=1): as três "
     u"rodas devem girar no MESMO sentido. Se não, ajuste setInverted().", CY),
    (u"IMU no sentido certo", u"Gire o robô no sentido anti-horário na mão: "
     u"Odom Theta deve AUMENTAR. Se diminuir, inverta kGyroReversed.", VI),
    (u"Eixos da odometria", u"Empurre 1 m para a frente, desabilitado. Veja qual número "
     u"(Odom X ou Odom Y) se move e confirme a convenção de nomes.", AM),
    (u"Escala da distância", u"Marque 2,00 m. Se o robô reporta 1,90 m, corrija "
     u"kWheelDiameterMeters na proporção 2,00/1,90.", GR),
    (u"Field-oriented", u"Gire o robô 90° e empurre o joystick para frente: ele deve ir "
     u"para a mesma direção do campo de antes.", PK),
    (u"Fechamento do quadrado", u"Rode o Auto 2 e marque o ponto de partida com fita. "
     u"O erro de volta ao ponto é a sua métrica de qualidade.", OR),
]
for i, (t, d, col) in enumerate(steps):
    x = 0.95 + (i % 2) * 5.78
    y = 2.2 + (i // 2) * 1.5
    rect(s, x, y, 5.5, 1.3, fill=PANEL, line=PANEL2, adj=0.09)
    rect(s, x, y + 0.14, 0.055, 1.02, fill=col, shape=MSO_SHAPE.RECTANGLE)
    bx2 = rect(s, x + 0.24, y + 0.2, 0.44, 0.44, fill=col, adj=0.3)
    shape_text(bx2, str(i + 1), size=14, color=BG, bold=True)
    tb(s, x + 0.82, y + 0.24, 4.4, 0.35, t, size=13.5, bold=True, color=col)
    tb(s, x + 0.82, y + 0.62, 4.5, 0.62, d, size=11.5, color=MUTED, spacing=1.18)

# ---- fechando o ciclo
s = new_slide(u"Fechando o ciclo", u"Do joystick ao campo, e de volta")
diag_drive_pipeline(s, 0.95, 2.4, 11.45)
tb(s, 0.95, 3.5, 11.45, 0.35, u"COMANDO  ·  o que o robô deve fazer", size=12,
   color=CY, bold=True, align=PP_ALIGN.CENTER)
arrow_d(s, 6.6, 3.9, 0.45, 0.3, color=AM)
bx3 = rect(s, 4.3, 4.45, 4.75, 0.7, fill=PANEL2, line=AM, lw=1.4, adj=0.2)
shape_text(bx3, u"ROBÔ FÍSICO  ·  rodas giram no tapete", size=13, color=AM, bold=True)
arrow_d(s, 6.6, 5.25, 0.45, 0.3, color=GR)
pipe(s, 0.95, 5.85, 11.45, [
    (u"3 encoders", GR), (u"Δ do ciclo", GR), (u"cinemática direta", GR),
    (u"Twist2d + θ IMU", AM), (u"pose.exp()", PK), (u"Pose2d no campo", PK),
], h=0.62, size=11.5, gap=0.3, arrow_color=GR)
tb(s, 0.95, 6.62, 11.45, 0.35, u"ESTIMATIVA  ·  onde o robô realmente está", size=12,
   color=GR, bold=True, align=PP_ALIGN.CENTER)
tb(s, 0.95, 2.05, 11.45, 0.3,
   u"A cinemática inversa e a direta são a MESMA matemática, lida nos dois sentidos.",
   size=13.5, color=MUTED, align=PP_ALIGN.CENTER)

# ---- exercicios
s = new_slide(u"Mão na massa", u"Exercícios em ordem crescente de dificuldade")
ex = [
    (u"Fácil", u"Publique no dashboard a distância de cada encoder e confirme, "
     u"empurrando o robô na mão, os sinais das três rodas.", GR),
    (u"Fácil", u"Crie um botão que chame resetOdometry() sem zerar a IMU, e explique "
     u"a diferença em relação ao resetHeading().", GR),
    (u"Médio", u"Escreva um TurnToAngleCommand: gira no lugar até getPose() reportar "
     u"o ângulo alvo, com controlador P e timeout.", AM),
    (u"Médio", u"Troque o quadrado por um triângulo equilátero de 0,6 m usando só "
     u"composição de comandos — sem tocar no DriveDistanceCommand.", AM),
    (u"Difícil", u"Escreva um DriveToPoseCommand que usa getPose() em malha fechada "
     u"para ir a (x, y) do campo, em vez de medir deslocamento relativo.", PK),
    (u"Difícil", u"Acrescente um teste em OdometryTest que simule escorregamento "
     u"(uma roda andando 10% menos) e meça o erro final de pose.", PK),
]
for i, (lvl, d, col) in enumerate(ex):
    y = 2.2 + i * 0.79
    rect(s, 0.95, y, 11.45, 0.68, fill=PANEL if i % 2 == 0 else BG, line=PANEL2, adj=0.16)
    lb = rect(s, 1.12, y + 0.14, 1.0, 0.4, fill=col, adj=0.4)
    shape_text(lb, lvl, size=11, color=BG, bold=True)
    tb(s, 2.3, y + 0.04, 9.9, 0.6, d, size=12.5, color=TXT, spacing=1.16,
       anchor=MSO_ANCHOR.MIDDLE)

# ---- resumo final
s = new_slide(u"Resumo", u"As dez frases que você deve levar embora")
final = [
    u"O scheduler roda a cada 20 ms: triggers, comandos, depois os periodic().",
    u"Um recurso = um subsystem; addRequirements() evita dois donos no mesmo motor.",
    u"Teleop é um default command, não código solto no teleopPeriodic().",
    u"Três rodas a 120° dão 3 graus de liberdade: frente, lado e giro, independentes.",
    u"Cinemática inversa = projetar o movimento do chassi na direção de cada roda.",
    u"Normalize dividindo as três pelo mesmo fator — nunca corte uma só.",
    u"Field-oriented é girar o vetor de comando de −θ antes da cinemática.",
    u"Cinemática direta é a mesma matemática invertida: somar as três isola o giro.",
    u"dx e dy vêm das rodas; θ vem da IMU. Nunca confie no ângulo das rodas omni.",
    u"pose.exp() integra o arco; resetWheelBaseline evita o salto fantasma no reset.",
]
for i, t in enumerate(final):
    x = 0.95 + (i % 2) * 5.78
    y = 2.2 + (i // 2) * 0.88
    nb = rect(s, x, y, 0.42, 0.42, fill=PANEL2, adj=0.3)
    shape_text(nb, str(i + 1), size=12, color=CY, bold=True, font=MONO)
    tb(s, x + 0.6, y - 0.02, 4.9, 0.78, t, size=12.5, color=TXT, spacing=1.2,
       anchor=MSO_ANCHOR.MIDDLE)
rect(s, 0.95, 6.6, 11.45, 0.05, fill=AM, shape=MSO_SHAPE.RECTANGLE)
tb(s, 0.95, 6.76, 11.45, 0.35,
   u"lab9OmniDrive  ·  WPILib / Java  ·  ./gradlew test para validar a matemática",
   size=11.5, color=DIM, align=PP_ALIGN.CENTER)

# =================================================================== NOTAS
from notes_data import NOTES

_slides = list(prs.slides)
if len(NOTES) != len(_slides):
    raise SystemExit("ERRO: %d notas para %d slides" % (len(NOTES), len(_slides)))

for _sl, _txt in zip(_slides, NOTES):
    _tf = _sl.notes_slide.notes_text_frame
    _tf.text = ""
    for _i, _ln in enumerate(_txt.split("\n")):
        _p = _tf.paragraphs[0] if _i == 0 else _tf.add_paragraph()
        _r = _p.add_run()
        _r.text = _ln
        _r.font.name = UI
        _r.font.size = Pt(12)

# =================================================================== SAVE
out = sys.argv[1] if len(sys.argv) > 1 else "Lab9-OmniDrive.pptx"
prs.save(out)
print("slides:", len(prs.slides.__iter__.__self__._sldIdLst))
print("saved:", out)
