# -*- coding: utf-8 -*-
"""Lab 9 - OmniDrive: quem define a frente do robo (e o que a IMU define).

Terceiro deck da serie, irmao de build.py e build_rotacao.py: mesma paleta,
mesmos helpers (deckbase, diagrams), mesma convencao de desenho.

    python build_frente.py [saida.pptx]
"""
import math, sys
from deckbase import *
from diagrams import diag_wheels
from pptx.util import Inches, Pt
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR

SEC1 = u"Parte 1 — A frente nasce nas rodas"
SEC2 = u"Parte 2 — O giroscópio e o campo"

NOTES = []


def nota(txt):
    """Nota do apresentador do slide que acabou de ser criado."""
    NOTES.append(txt)


def mini_robot(s, cx, cy, heading_deg, R=0.62, color=CY, chassis=True):
    """Chassi pequeno visto de cima; heading_deg = 0 aponta para o alto."""
    if chassis:
        rect(s, cx - R, cy - R, 2 * R, 2 * R, fill=PANEL2, line=MUTED, lw=1.2,
             shape=MSO_SHAPE.OVAL)
    a = math.radians(heading_deg)
    vec(s, cx, cy, cx + (R - 0.08) * math.sin(a), cy - (R - 0.08) * math.cos(a),
        color=color, lw=2.0, head=0.2)


# =================================================================== CAPA
s = new_slide(sec=SEC1, number=False)
rect(s, 0, 0, W, 0.14, fill=AM, shape=MSO_SHAPE.RECTANGLE)
diag_wheels(s, 10.5, 4.05, R=1.3, show_axes=False, show_radius=False,
            labels=False, show_drive=False)
vec(s, 10.5, 4.05, 10.5, 2.25, color=AM, lw=2.4, head=0.24)
tb(s, 9.3, 1.88, 2.4, 0.3, u"frente", size=12.5, color=AM, bold=True,
   align=PP_ALIGN.CENTER)

tb(s, 1.0, 1.5, 8.6, 0.4, u"ROBÓTICA MÓVEL  ·  WORLDSKILLS  ·  LAB 9", size=14,
   color=AM, bold=True)
tb(s, 1.0, 2.1, 8.6, 1.9, u"Quem define\na frente do robô", size=48,
   color=WHITE, bold=True, spacing=1.08)
rect(s, 1.0, 4.42, 1.9, 0.05, fill=CY, shape=MSO_SHAPE.RECTANGLE)
tb(s, 1.0, 4.82, 8.0, 1.0,
   u"Não é o giroscópio — e entender a diferença resolve\nmetade dos problemas de direção na arena.",
   size=18, color=MUTED, spacing=1.25)
tb(s, 1.0, 6.3, 8.6, 0.4,
   u"Base holonômica de 3 rodas a 120°  ·  Titan + navX  ·  WPILib / Java",
   size=12.5, color=DIM)
nota(u"Este deck nasceu de uma pergunta de aluno: 'o que determina a frente do\n"
     u"robo e a posicao do giroscopio?'.\n"
     u"A resposta curta e nao — e a resposta longa separa dois conceitos que a\n"
     u"turma costuma embolar: a frente do ROBO e a frente do CAMPO.\n"
     u"Vale abrir perguntando isso para a turma e deixar votarem antes.")

# =================================================================== A PERGUNTA
s = new_slide(u"A pergunta", u"O giroscópio determina a frente do robô?")
rect(s, 0.75, 2.3, 11.8, 1.25, fill=PANEL2, line=AM, lw=1.4, adj=0.07)
tb(s, 1.2, 2.58, 10.9, 0.7,
   u"Não. O giroscópio mede quanto o robô girou — nunca o que é a frente dele.",
   size=22, color=WHITE, bold=True, spacing=1.15)

card(s, 0.75, 3.95, 5.75, 2.0, u"A frente do ROBÔ",
     u"Nasce da montagem das rodas e da fiação dos motores. É geometria e "
     u"cabo — está decidida antes de a IMU ser ligada, e não muda enquanto o "
     u"robô anda.", accent=AM, hsize=16, bsize=14)
card(s, 6.8, 3.95, 5.75, 2.0, u"A frente do CAMPO",
     u"Essa sim vem da IMU: o zero dela decide para onde aponta o +x da arena. "
     u"É o que o field-oriented e a odometria usam como referência.",
     accent=CY, hsize=16, bsize=14)
nota(u"Deixar a distincao no quadro o deck inteiro: FRENTE DO ROBO x FRENTE DO\n"
     u"CAMPO.\n"
     u"Analogia que funciona: a proa de um barco e a proa, independente da\n"
     u"bussola. A bussola so diz para que lado do mundo a proa esta apontando.\n"
     u"Trocar a bussola de lugar nao transforma a popa em proa.")

# =================================================================== PARTE 1
section(u"1", u"A frente nasce nas rodas", u"Geometria e fiação, antes de qualquer sensor",
        accent=AM, topics=(u"as três equações", u"a roda 1", u"canais do Titan",
                           u"setInverted"))
nota(u"Parte 1: onde a frente e decidida de fato. Tudo aqui e fisico —\n"
     u"nenhuma linha depende de sensor.")

# ----------------------------------------------------- as tres equacoes
s = new_slide(u"Onde a frente é decidida", u"No sinal da primeira equação")
code(s, 0.75, 2.2, 11.8, 2.1, u"""public void driveRobotOriented(double vx, double vy, double wz)
{
    // Cinematica holonomica de 3 rodas a 120 graus
    double targetPercent1 = -vx + wz;
    double targetPercent2 = 0.5 * vx + (Math.sqrt(3) / 2.0) * vy + wz;
    double targetPercent3 = 0.5 * vx - (Math.sqrt(3) / 2.0) * vy + wz;""",
     title=u"OmnidriveSubsystem.java", size=13)

tb(s, 0.75, 4.5, 11.8, 0.85,
   u"Com vx positivo, a roda 1 gira ao contrário das rodas 2 e 3. A direção física em que o "
   u"chassi sai andando quando isso acontece É a frente — por definição. Não há outra "
   u"definição no projeto inteiro.", size=15, color=TXT, spacing=1.3)

card(s, 0.75, 5.5, 5.75, 0.85, u"vx = “para a frente”",
     u"é só um nome. O que o nome significa, quem decide é o hardware.",
     accent=AM, hsize=14, bsize=12.5)
card(s, 6.8, 5.5, 5.75, 0.85, u"A mesma conta na odometria",
     u"forwardKinematics() inverte estas três linhas. Mesma convenção.",
     accent=GR, hsize=14, bsize=12.5)
nota(u"Ler a primeira equacao em voz alta: 'roda 1 recebe MENOS vx'.\n"
     u"Perguntar: por que menos? Porque a roda 1 esta do lado oposto ao\n"
     u"movimento — ela e a traseira. Se as tres recebessem +vx, o robo giraria\n"
     u"no lugar em vez de andar.\n"
     u"Ponto central do deck: a frente e consequencia desse sinal, nao de um\n"
     u"sensor.")

# ----------------------------------------------------- diagrama do chassi
s = new_slide(u"A frente é o lado oposto à roda 1", u"Olhando o chassi de cima")
diag_wheels(s, 4.5, 4.4, R=1.3, show_axes=True, show_drive=True,
            show_radius=False, labels=True)
bullets(s, 8.1, 2.4, 4.45, [
    (u"Roda 1 atrás: ", u"é a única que recebe vx com sinal negativo. Ela empurra "
     u"o chassi para o lado oposto a ela."),
    (u"Rodas 2 e 3 na frente: ", u"recebem metade de vx cada uma, somando o mesmo "
     u"empurrão para a frente."),
    (u"As setas brancas ", u"são o sentido em que cada roda traciona quando se "
     u"pede vx positivo."),
], size=14, gap=0.16)
note(s, 8.1, 5.4, 4.45,
     u"Gire este desenho 120° e nada muda na matemática — muda só qual lado você "
     u"chama de frente.", color=AM, size=12.5, h=1.3)
nota(u"Mostrar com a mao: as duas rodas da frente empurram para cima, a traseira\n"
     u"puxa no sentido contrario, e a soma vetorial da os 100% para a frente.\n"
     u"Se a turma tiver o robo na mesa, girar o chassi 120 graus e repetir a\n"
     u"pergunta: e agora, qual e a frente? Resposta: o lado oposto a roda que\n"
     u"esta no canal 0.")

# ----------------------------------------------------- as tres coisas fisicas
s = new_slide(u"O que realmente decide", u"Três coisas, nenhuma delas um sensor")
for i, (n, head, body, col) in enumerate([
    (u"01", u"A montagem das rodas",
     u"Onde cada roda está parafusada no chassi, a 120° uma da outra.", AM),
    (u"02", u"Qual roda está em qual canal",
     u"O canal 0 é a traseira. Trocar os cabos de dois motores move a frente "
     u"do robô sem mudar uma linha de código.", CY),
    (u"03", u"Os setInverted",
     u"Inverter um motor inverte o sentido daquela roda e desmonta a "
     u"geometria das três.", PK),
]):
    y = 2.25 + i * 1.12
    rect(s, 0.75, y, 6.6, 0.98, fill=PANEL, line=PANEL2, adj=0.1)
    rect(s, 0.75, y + 0.11, 0.055, 0.76, fill=col, shape=MSO_SHAPE.RECTANGLE)
    tb(s, 1.05, y + 0.14, 0.6, 0.3, n, size=13, color=col, bold=True, font=MONO)
    tb(s, 1.75, y + 0.11, 5.4, 0.3, head, size=15, color=WHITE, bold=True)
    tb(s, 1.75, y + 0.44, 5.4, 0.5, body, size=12, color=MUTED, spacing=1.15)

code(s, 7.65, 2.25, 4.9, 1.65, u"""// Constants.java
kMotor1Channel = 0; // Traseira
kMotor2Channel = 1; // Frontal Dir.
kMotor3Channel = 2; // Frontal Esq.""", size=12)
code(s, 7.65, 4.1, 4.9, 1.5, u"""// motorSettings()
motor1.setInverted(false);
motor2.setInverted(false);
motor3.setInverted(false);""", size=12)
note(s, 0.75, 5.75, 11.8,
     u"Os comentários “Traseira / Frontal Dir. / Frontal Esq.” não configuram nada: "
     u"eles descrevem o que já foi decidido no parafuso e no conector. Se o cabo não "
     u"bate com o comentário, quem está errado é o comentário.", color=AM)
nota(u"Insistir no terceiro item: setInverted nao e 'ajuste fino'. Inverter uma\n"
     u"roda so, das tres, quebra a geometria — o robo passa a girar quando\n"
     u"deveria andar reto.\n"
     u"E insistir no aviso de baixo: comentario nao e configuracao. Ja perdi\n"
     u"tarde inteira procurando bug de software que era cabo trocado.")

# ----------------------------------------------------- experimento
s = new_slide(u"Prova", u"Troque dois cabos e a frente muda de lugar")
for i, (tit, col, rot, desc) in enumerate([
    (u"Como está hoje", AM, 0.0,
     u"Canal 0 = roda de trás.\nA frente é o lado entre as rodas 2 e 3."),
    (u"Trocando os cabos 0 e 1", PK, 120.0,
     u"Canal 0 passou a ser outra roda.\nA frente girou 120°, sem recompilar nada."),
]):
    bx = 0.75 + i * 6.05
    rect(s, bx, 2.25, 5.75, 3.85, fill=PANEL, line=PANEL2, adj=0.05)
    rect(s, bx, 2.39, 0.055, 3.57, fill=col, shape=MSO_SHAPE.RECTANGLE)
    tb(s, bx + 0.35, 2.48, 5.1, 0.35, tit, size=15, color=col, bold=True)

    # Raio pequeno de proposito: o desenho inteiro (rodas, numeros e a seta
    # da frente) tem que caber entre o titulo e a legenda do painel.
    cx, cy = bx + 2.87, 4.18
    R = 0.62
    rect(s, cx - R, cy - R, 2 * R, 2 * R, fill=PANEL2, line=MUTED, lw=1.3,
         shape=MSO_SHAPE.OVAL)
    for phi, num, wcol in ((180.0 + rot, u"1", PK), (-60.0 + rot, u"2", CY),
                           (60.0 + rot, u"3", GR)):
        ra = math.radians(phi)
        px = cx - R * math.sin(ra)
        py = cy - R * math.cos(ra)
        rect(s, px - 0.21, py - 0.07, 0.42, 0.14, fill=wcol, adj=0.45,
             rot=math.degrees(math.atan2(math.sin(ra), -math.cos(ra))))
        lx = cx - 0.95 * math.sin(ra)
        ly = cy - 0.95 * math.cos(ra)
        tb(s, lx - 0.25, ly - 0.14, 0.5, 0.3, num, size=11.5, color=wcol,
           bold=True, align=PP_ALIGN.CENTER, font=MONO)
    a = math.radians(rot)
    vec(s, cx, cy, cx + 1.02 * math.sin(a), cy - 1.02 * math.cos(a),
        color=col, lw=2.4, head=0.22)
    tb(s, cx + 1.55 * math.sin(a) - 0.6, cy - 1.55 * math.cos(a) - 0.15, 1.2, 0.3,
       u"frente", size=12, color=col, bold=True, align=PP_ALIGN.CENTER)

    tb(s, bx + 0.35, 5.38, 5.1, 0.6, desc, size=12.5, color=MUTED, spacing=1.2)
nota(u"Se der, fazer ao vivo: desligar, trocar dois conectores do Titan, ligar\n"
     u"e mandar o robo 'para a frente'. Ele sai andando para outro lado.\n"
     u"Nenhuma linha de codigo mudou e o giroscopio e o mesmo — o que mudou foi\n"
     u"qual motor atende pelo nome de roda 1.")

# =================================================================== PARTE 2
section(u"2", u"O giroscópio e o campo", u"O que a IMU realmente define",
        accent=CY, topics=(u"θ", u"o zero da IMU", u"montagem da placa",
                           u"kGyroReversed"))
nota(u"Parte 2: agora sim entra a IMU — mas para definir outra coisa.")

# ----------------------------------------------------- o que a IMU mede
s = new_slide(u"O que a IMU mede", u"O ângulo entre a frente do robô e o +x do campo")
cx, cy = 4.3, 4.4
vec(s, cx, cy, cx, cy - 2.0, color=MUTED, lw=1.5, head=0.2)
tb(s, cx - 1.0, cy - 2.38, 2.0, 0.3, u"+x do campo", size=11.5, color=MUTED,
   align=PP_ALIGN.CENTER)
vec(s, cx, cy, cx - 2.0, cy, color=MUTED, lw=1.5, head=0.2)
tb(s, cx - 3.3, cy - 0.34, 1.2, 0.3, u"+y", size=11.5, color=MUTED,
   align=PP_ALIGN.RIGHT)
th = 42.0
rect(s, cx - 0.95, cy - 0.95, 1.9, 1.9, fill=PANEL, line=PANEL2, lw=1.4,
     shape=MSO_SHAPE.OVAL)
a = math.radians(-th)
vec(s, cx, cy, cx + 1.95 * math.sin(a), cy - 1.95 * math.cos(a), color=AM, lw=2.4,
    head=0.24)
tb(s, cx + 2.1 * math.sin(a) - 1.85, cy - 2.1 * math.cos(a) - 0.16, 1.7, 0.3,
   u"frente do robô", size=12, color=AM, bold=True, align=PP_ALIGN.RIGHT)
tb(s, cx - 0.92, cy - 1.42, 0.7, 0.35, u"θ", size=18, color=CY, bold=True,
   italic=True)

bullets(s, 7.4, 2.4, 5.15, [
    (u"A frente do robô (âmbar) ", u"está presa ao chassi. Ela não depende da IMU."),
    (u"O +x do campo (cinza) ", u"está preso à arena. Ele não gira com o robô."),
    (u"θ é só o ângulo entre os dois. ", u"É a única coisa que a IMU entrega — e é "
     u"exatamente o que falta para converter um referencial no outro."),
], size=14, gap=0.18)
code(s, 7.4, 5.3, 5.15, 1.0, u"""Rotation2d.fromDegrees(
    kGyroReversed ? -getHeading() : getHeading())""",
     title=u"getRotation2d()", size=11.5)
nota(u"Mostrar com as duas maos: uma mao e a frente do robo, a outra e o +x do\n"
     u"campo. Gire so a mao do robo — o angulo entre elas muda, mas nenhuma das\n"
     u"duas 'virou' a outra.\n"
     u"theta e o unico numero que a IMU contribui para toda a navegacao. Todo o\n"
     u"resto (escala, direcao, cinematica) vem das rodas.")

# ----------------------------------------------------- o zero da IMU
s = new_slide(u"O zero da IMU", u"Ele decide para onde aponta o +x do campo")
tb(s, 0.75, 2.15, 11.8, 0.4,
   u"Mesmo robô, mesma frente, nos dois quadros. O que muda é quando a IMU foi zerada.",
   size=14.5, color=MUTED)
for i, (tit, zero_deg, desc, col) in enumerate([
    (u"Zerou com o robô assim", 0.0,
     u"A frente do robô vira o +x do campo.\nθ = 0°.", GR),
    (u"Zerou com o robô girado", -40.0,
     u"O +x do campo ficou em outro lugar.\nAgora o mesmo robô marca θ = 40°.", PK),
]):
    bx = 0.75 + i * 6.05
    rect(s, bx, 2.62, 5.75, 3.58, fill=PANEL, line=PANEL2, adj=0.05)
    rect(s, bx, 2.76, 0.055, 3.3, fill=col, shape=MSO_SHAPE.RECTANGLE)
    tb(s, bx + 0.35, 2.80, 5.1, 0.35, tit, size=14.5, color=col, bold=True)

    cx, cy = bx + 2.87, 4.75
    rect(s, cx - 0.55, cy - 0.55, 1.1, 1.1, fill=PANEL2, line=MUTED, lw=1.2,
         shape=MSO_SHAPE.OVAL)
    vec(s, cx, cy, cx, cy - 0.95, color=AM, lw=2.2, head=0.22)

    if zero_deg == 0.0:
        # Os dois vetores coincidem: em vez de desenhar um por cima do outro,
        # o rotulo diz que sao o mesmo.
        tb(s, cx + 0.18, cy - 1.13, 1.9, 0.3, u"frente do robô", size=11.5,
           color=AM, bold=True)
        tb(s, cx + 0.18, cy - 0.87, 1.9, 0.3, u"= +x do campo", size=11.5,
           color=MUTED)
    else:
        az = math.radians(zero_deg)
        vec(s, cx, cy, cx + 1.35 * math.sin(az), cy - 1.35 * math.cos(az),
            color=MUTED, lw=1.5, head=0.2)
        tb(s, cx - 1.92, cy - 1.41, 1.6, 0.3, u"+x campo", size=11, color=MUTED,
           align=PP_ALIGN.RIGHT)
        tb(s, cx + 0.18, cy - 1.00, 1.9, 0.3, u"frente do robô", size=11.5,
           color=AM, bold=True)
        tb(s, cx - 0.63, cy - 0.80, 0.5, 0.3, u"θ", size=15, color=CY, bold=True,
           italic=True, align=PP_ALIGN.CENTER)

    tb(s, bx + 0.35, 5.55, 5.1, 0.6, desc, size=12.5, color=MUTED, spacing=1.2)
nota(u"Este e o slide que mata a confusao. As duas setas ambar sao identicas —\n"
     u"a frente do robo nao mudou. A cinza e que mudou de lugar.\n"
     u"resetHeading() faz exatamente isso: congela o campo na direcao em que a\n"
     u"frente do robo estava apontando naquele instante. Por isso o gatilho do\n"
     u"joystick 'realinha o campo', nao o robo.")

# ----------------------------------------------------- montagem da placa
s = new_slide(u"E a placa, montada torta?", u"Yaw é rotação em torno do eixo vertical")
card(s, 0.75, 2.3, 3.9, 2.25, u"Girada na horizontal",
     u"A navX parafusada 90° torta no chassi.\n\nSoma uma constante a θ. A frente "
     u"do robô não se mexe — o campo é que nasce girado.", accent=GR, bsize=13)
card(s, 4.95, 2.3, 3.9, 2.25, u"De cabeça para baixo",
     u"O yaw troca de sinal: girar para a esquerda passa a contar como direita.\n\n"
     u"É isso que kGyroReversed conserta.", accent=AM, bsize=13)
card(s, 9.15, 2.3, 3.9, 2.25, u"Deitada de lado",
     u"Aí sim quebra: o que era yaw virou pitch ou roll, e getAngle() deixa de "
     u"medir a rotação no plano do chão.", accent=PK, bsize=13)

formula(s, 0.75, 4.95, 12.3, 0.95,
        u"placa torta na horizontal  →  θ + constante        placa invertida  →  θ com sinal trocado",
        size=14, color=TXT)
note(s, 0.75, 6.05, 12.3,
     u"Em nenhum dos três casos a frente do robô muda. A placa só mede rotação; "
     u"ela não tem como saber onde ficam as rodas.", color=AM)
nota(u"Pegar a placa (ou um livro) e girar na mao para mostrar os tres casos.\n"
     u"O caso 'deitada de lado' e o unico que quebra de verdade, e o sintoma e\n"
     u"caracteristico: o angulo muda quando voce INCLINA o robo, nao quando\n"
     u"gira. Se aparecer isso na bancada, e montagem fisica, nao flag.")

# ----------------------------------------------------- diagnostico
s = new_slide(u"Diagnóstico", u"O sintoma diz se é cabo ou se é IMU")
table(s, 0.75, 2.3, 11.8, [
    [u"Sintoma", u"O que é", u"Onde mexer"],
    [u"Anda para o lado errado com o campo zerado certo",
     u"fiação ou montagem", u"canais em Constants"],
    [u"Gira no lugar quando devia andar reto",
     u"uma roda invertida das três", u"setInverted()"],
    [u"Robô-relativo certo, field-oriented girado",
     u"zero da IMU", u"resetHeading no início"],
    [u"Field-oriented espelhado: corrige para o lado errado",
     u"sinal do yaw", u"kGyroReversed"],
    [u"θ muda quando você inclina o robô, não quando gira",
     u"placa deitada", u"montagem da navX"],
], [5.3, 3.1, 3.4], size=12, rowh=0.56, mono_body=False)
note(s, 0.75, 5.7, 11.8,
     u"Regra de bolso: se o erro aparece no modo robô, é hardware. Se só aparece no "
     u"modo campo, é IMU. Testar nessa ordem economiza a tarde.", color=CY)
nota(u"Pedir para a turma classificar cada sintoma ANTES de mostrar a coluna do\n"
     u"meio — funciona bem como exercicio rapido.\n"
     u"A regra de bolso do rodape e o que eles levam para a arena: erro no modo\n"
     u"robo = hardware; erro so no modo campo = IMU.")

# ----------------------------------------------------- o teste
s = new_slide(u"O teste de 30 segundos", u"Qual lado é a frente, sem discussão")
bullets(s, 0.75, 2.3, 6.3, [
    (u"1. ", u"Robô no chão, espaço livre à frente, bateria carregada."),
    (u"2. ", u"Segure o botão e mande vx positivo, sem rotação e sem campo."),
    (u"3. ", u"O lado para onde ele sai andando é a frente. Fim da discussão."),
    (u"4. ", u"Não é o lado que você queria? Então é cabo ou parafuso — "
     u"não mexa na IMU."),
], size=15, gap=0.2)
code(s, 7.35, 2.3, 5.2, 2.2, u"""// RobotContainer, so para o teste
new JoystickButton(joystick, 2).whileHeld(
    new RunCommand(
        () -> driveSubsystem
            .driveRobotOriented(0.4, 0.0, 0.0),
        driveSubsystem));""", size=11.5)
note(s, 7.35, 4.75, 5.2,
     u"driveRobotOriented, não driveFieldOriented: o teste tem que ficar fora do "
     u"alcance da IMU.", color=AM, size=12.5)
nota(u"Ponto fino importante: o teste TEM que ser com driveRobotOriented. Se\n"
     u"usarem o field-oriented, a IMU entra na conta e o teste deixa de isolar\n"
     u"o que a gente quer medir.\n"
     u"0.4 e deliberadamente devagar: da para acompanhar andando ao lado.")

# ----------------------------------------------------- fecho
s = new_slide(u"Em uma frase", u"O que levar do laboratório")
rect(s, 0.75, 2.3, 11.8, 1.45, fill=PANEL2, line=AM, lw=1.4, adj=0.06)
tb(s, 1.2, 2.6, 10.9, 0.95,
   u"A frente do robô é decidida por parafuso e cabo. O giroscópio só diz para "
   u"onde essa frente está apontando no campo.",
   size=20, color=WHITE, bold=True, spacing=1.2)

card(s, 0.75, 4.05, 3.75, 1.5, u"Mude a fiação",
     u"A frente do robô muda de lugar — e o código nem fica sabendo.",
     accent=AM, bsize=12.5)
card(s, 4.78, 4.05, 3.75, 1.5, u"Zere a IMU",
     u"A frente do campo muda de lugar. O robô continua igual.",
     accent=CY, bsize=12.5)
card(s, 8.8, 4.05, 3.75, 1.5, u"Nunca o contrário",
     u"Nenhuma flag de software move uma roda; nenhum parafuso move o norte.",
     accent=GR, bsize=12.5)
tb(s, 0.75, 5.9, 11.8, 0.4,
   u"Decks irmãos:  Lab9-OmniDrive-CommandBased-Odometria  ·  Lab9-MatrizRotacao-Trajetoria",
   size=12, color=DIM, font=MONO)
nota(u"Fechar com a pergunta de abertura: 'o que determina a frente do robo e a\n"
     u"posicao do giroscopio?'. Se a turma responder 'nao, e a roda que esta no\n"
     u"canal 0', o deck cumpriu o objetivo.\n"
     u"Emendar com o deck da matriz de rotacao: agora que sabemos o que e theta,\n"
     u"da para ver o que se faz com ele.")

# =================================================================== NOTAS
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
out = sys.argv[1] if len(sys.argv) > 1 else "Lab9-FrenteDoRobo.pptx"
prs.save(out)
print("slides:", len(_slides))
print("saved:", out)
