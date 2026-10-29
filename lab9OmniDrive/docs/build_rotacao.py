# -*- coding: utf-8 -*-
"""Lab 9 - OmniDrive: a matriz de rotacao e o trajeto por coordenadas da arena.

Deck irmao do build.py: reusa a mesma paleta e os mesmos helpers (deckbase,
diagrams), entao as duas apresentacoes podem ser mostradas na sequencia.

    python build_rotacao.py [saida.pptx]

Convencao de desenho, a mesma do deck principal: olhando a arena de cima,
+x (frente) aponta para o alto da folha e +y (esquerda) para a esquerda.
"""
import math, sys
from deckbase import *
from diagrams import diag_wheels, pipe
from pptx.util import Inches, Pt
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR

SEC1 = u"Parte 1 — A matriz de rotação"
SEC2 = u"Parte 2 — Trajeto por coordenadas"

NOTES = []


def nota(txt):
    """Nota do apresentador do slide que acabou de ser criado."""
    NOTES.append(txt)


# =================================================================== helpers
def field_map(s, ox, oy, scale=1.6, show_path=True, show_pause=True):
    """Mapa da arena visto de cima: +x para o alto, +y para a esquerda.

    ox, oy = posicao em polegadas da origem do campo (ponto de partida).
    """
    def P(xm, ym):
        return (ox - ym * scale, oy - xm * scale)

    # eixos do campo
    vec(s, ox, oy, ox, oy - 3.6 * 1.0, color=MUTED, lw=1.4, head=0.2)
    tb(s, ox - 0.9, oy - 3.95, 1.8, 0.3, u"+x · frente", size=11, color=MUTED,
       align=PP_ALIGN.CENTER)
    vec(s, ox, oy, ox - 2.6, oy, color=MUTED, lw=1.4, head=0.2)
    tb(s, ox - 4.0, oy - 0.36, 1.3, 0.3, u"+y · esquerda", size=11, color=MUTED,
       align=PP_ALIGN.RIGHT)

    # grade de meio metro
    for k in (0.5, 1.0, 1.5, 2.0):
        yy = oy - k * scale
        if yy > oy - 3.6:
            line(s, ox - 2.4, yy, ox + 0.12, yy, color=DIM, lw=0.75, dash=True)
    for k in (0.5, 1.0):
        xx = ox - k * scale
        line(s, xx, oy - 3.5, xx, oy + 0.12, color=DIM, lw=0.75, dash=True)

    pts = [(0.0, 0.0), (1.0, 0.0), (1.0, 0.8), (1.8, 0.8)]
    xy = [P(a, b) for a, b in pts]

    if show_path:
        for i in range(3):
            x1, y1 = xy[i]
            x2, y2 = xy[i + 1]
            d = math.hypot(x2 - x1, y2 - y1)
            k1, k2 = 0.30 / d, (d - 0.30) / d
            vec(s, x1 + (x2 - x1) * k1, y1 + (y2 - y1) * k1,
                x1 + (x2 - x1) * k2, y1 + (y2 - y1) * k2, color=CY, lw=2.4, head=0.22)

    # robo em cada ponto: a frente aponta sempre para +x (para o alto)
    for i, (cx, cy) in enumerate(xy):
        r = 0.27
        rect(s, cx - r, cy - r, 2 * r, 2 * r, fill=PANEL if i else PANEL2,
             line=PANEL2 if i else MUTED, lw=1.2, shape=MSO_SHAPE.OVAL)
        vec(s, cx, cy, cx, cy - 0.24, color=CY, lw=1.8, head=0.16)
        if show_pause and i:
            rect(s, cx - 0.44, cy - 0.44, 0.88, 0.88, fill=None, line=AM, lw=1.1,
                 shape=MSO_SHAPE.OVAL)

    # rotulos
    # Rotulos fora do anel de pausa (raio 0.44): a direita no inicio e no P1,
    # a esquerda no P2 e no P3, para nenhum deles cruzar o trajeto.
    rot = [(u"início (0,00 · 0,00)", -2.32, -0.62, PP_ALIGN.RIGHT),
           (u"P1 (1,00 · 0,00)", 0.62, -0.16, PP_ALIGN.LEFT),
           (u"P2 (1,00 · 0,80)", -2.32, -0.16, PP_ALIGN.RIGHT),
           (u"P3 (1,80 · 0,80)", -2.32, -0.16, PP_ALIGN.RIGHT)]
    for (txt, dx, dy, al), (cx, cy) in zip(rot, xy):
        tb(s, cx + dx, cy + dy, 1.7, 0.6, txt,
           size=10.5, color=TXT, font=MONO, align=al, spacing=1.15)

    if show_pause:
        tb(s, ox - 5.3, oy + 0.46, 3.6, 0.3, u"○  pausa de 1 s em cada ponto",
           size=11, color=AM, align=PP_ALIGN.RIGHT)
    return xy


# =================================================================== CAPA
s = new_slide(sec=SEC1, number=False)
rect(s, 0, 0, W, 0.14, fill=CY, shape=MSO_SHAPE.RECTANGLE)

# ilustracao da capa: vetor unico, lido nos dois referenciais
cx, cy = 10.4, 4.3
vec(s, cx, cy, cx, cy - 2.0, color=DIM, lw=1.6, head=0.2)
vec(s, cx, cy, cx - 2.0, cy, color=DIM, lw=1.6, head=0.2)
ang = math.radians(34.0)
vx, vy = 2.3 * math.sin(ang), 2.3 * math.cos(ang)
vec(s, cx, cy, cx - vx, cy - vy, color=CY, lw=2.6, head=0.26)
line(s, cx - vx, cy - vy, cx - vx, cy, color=AM, lw=1.1, dash=True)
line(s, cx - vx, cy - vy, cx, cy - vy, color=AM, lw=1.1, dash=True)
tb(s, cx - 3.3, cy + 0.34, 2.6, 0.3, u"um vetor, dois nomes", size=11,
   color=CY, bold=True, align=PP_ALIGN.RIGHT)

tb(s, 1.0, 1.5, 8.6, 0.4, u"ROBÓTICA MÓVEL  ·  WORLDSKILLS  ·  LAB 9", size=14,
   color=CY, bold=True)
tb(s, 1.0, 2.1, 9.4, 1.9, u"A matriz de rotação\ne o trajeto pela arena", size=48,
   color=WHITE, bold=True, spacing=1.08)
rect(s, 1.0, 4.42, 1.9, 0.05, fill=AM, shape=MSO_SHAPE.RECTANGLE)
tb(s, 1.0, 4.82, 8.0, 1.0,
   u"Dois referenciais, uma matriz 2×2 — e como ela leva o\nrobô até uma coordenada da arena.",
   size=18, color=MUTED, spacing=1.25)
tb(s, 1.0, 6.3, 8.6, 0.4,
   u"Base holonômica de 3 rodas a 120°  ·  odometria + navX  ·  WPILib / Java",
   size=12.5, color=DIM)
nota(u"Deck curto, para rodar depois do Lab 9 principal.\n"
     u"A ideia central: o robo mede e atua no referencial dele, a arena e\n"
     u"descrita em outro referencial, e a unica coisa que liga os dois e uma\n"
     u"matriz 2x2. Quem entende essa matriz entende field-oriented, odometria\n"
     u"e navegacao por coordenadas de uma vez.")

# =================================================================== ROTEIRO
s = new_slide(u"Roteiro", u"Duas partes, um conceito só")
items = [
    (u"1", u"A matriz de rotação", u"Por que existem dois referenciais, de onde sai a\n"
     u"matriz 2×2 e os dois sentidos em que ela é usada.", CY),
    (u"2", u"Trajeto por coordenadas", u"Como transformar “vá para (1,80 · 0,80)” em\n"
     u"velocidade para as três rodas, ciclo a ciclo.", AM),
]
for i, (n, tit, desc, col) in enumerate(items):
    y = 2.3 + i * 1.9
    rect(s, 0.75, y, 11.8, 1.55, fill=PANEL, line=PANEL2, adj=0.07)
    rect(s, 0.75, y + 0.16, 0.06, 1.23, fill=col, shape=MSO_SHAPE.RECTANGLE)
    tb(s, 1.15, y + 0.22, 0.8, 1.0, n, size=42, color=col, bold=True)
    tb(s, 2.15, y + 0.26, 4.0, 0.5, tit, size=21, color=WHITE, bold=True)
    tb(s, 2.15, y + 0.78, 9.6, 0.7, desc, size=14, color=MUTED, spacing=1.2)
note(s, 0.75, 6.1, 11.8,
     u"Pré-requisito: a odometria tem que estar com escala e sinal conferidos. "
     u"Matriz certa com escala errada leva o robô, com toda a convicção, para o lugar errado.",
     color=AM)
nota(u"Combinar com a turma: a parte 1 e matematica no quadro, a parte 2 e\n"
     u"codigo. Se o tempo apertar, a parte 1 e a que nao pode ser cortada —\n"
     u"a parte 2 e aplicacao direta dela.")

# =================================================================== PARTE 1
section(u"1", u"A matriz de rotação", u"Dois referenciais e a ponte entre eles",
        accent=CY, topics=(u"referencial do robô", u"referencial do campo",
                           u"R(θ) e R(-θ)", u"onde está no código"))
nota(u"Parte 1. Objetivo: sair daqui sabendo escrever a matriz de cabeca e\n"
     u"saber dizer, olhando uma linha de codigo, em que sentido ela esta\n"
     u"sendo aplicada.")

# ----------------------------------------------------- dois referenciais
s = new_slide(u"O problema", u"O robô mede num referencial, a arena vive em outro")
bullets(s, 0.75, 2.2, 5.7, [
    (u"Referencial do robô: ", u"é onde as rodas e os encoders vivem. "
     u"“vx” é sempre para a frente do robô, “vy” é sempre para a esquerda dele."),
    (u"Referencial do campo: ", u"é onde a arena vive. Uma coordenada só faz "
     u"sentido aqui — o ponto (1,80 · 0,80) não se move quando o robô gira."),
    (u"O conflito: ", u"gire o chassi 90° e “para a frente do robô” passou a "
     u"apontar para outro lado da arena. A medida é a mesma, o significado mudou."),
], size=15.5, gap=0.17)

# diagrama: campo fixo + chassi girado com os eixos proprios
cx, cy = 9.5, 4.35
vec(s, cx, cy, cx, cy - 2.25, color=MUTED, lw=1.5, head=0.2)
tb(s, cx - 1.0, cy - 2.62, 2.0, 0.3, u"+x campo", size=11.5, color=MUTED,
   align=PP_ALIGN.CENTER)
vec(s, cx, cy, cx - 2.25, cy, color=MUTED, lw=1.5, head=0.2)
tb(s, cx - 3.5, cy - 0.36, 1.2, 0.3, u"+y campo", size=11.5, color=MUTED,
   align=PP_ALIGN.RIGHT)
th = math.radians(40.0)
R = 1.0
rect(s, cx - R, cy - R, 2 * R, 2 * R, fill=PANEL, line=PANEL2, lw=1.4,
     shape=MSO_SHAPE.OVAL)
vec(s, cx, cy, cx - 1.9 * math.sin(th), cy - 1.9 * math.cos(th), color=AM, lw=2.2, head=0.22)
tb(s, cx - 1.9 * math.sin(th) - 1.65, cy - 1.9 * math.cos(th) - 0.1, 1.5, 0.3,
   u"frente do robô", size=11.5, color=AM, bold=True, align=PP_ALIGN.RIGHT)
vec(s, cx, cy, cx - 1.5 * math.cos(th), cy + 1.5 * math.sin(th), color=VI, lw=2.2, head=0.22)
tb(s, cx - 1.5 * math.cos(th) - 1.55, cy + 1.5 * math.sin(th) - 0.06, 1.4, 0.3,
   u"lado do robô", size=11.5, color=VI, bold=True, align=PP_ALIGN.RIGHT)
tb(s, cx - 0.78, cy - 1.42, 0.7, 0.3, u"θ", size=17, color=CY, bold=True, italic=True)
nota(u"Perguntar para a turma: se eu mando “vx = 1” com o robo girado 40 graus,\n"
     u"para onde ele vai? Resposta: para a frente DELE, que nao e mais o +x do\n"
     u"campo. Esse e o slide em que a ficha cai.\n"
     u"theta e o angulo do campo para o robo, medido anti-horario.")

# ----------------------------------------------------- de onde sai a matriz
s = new_slide(u"De onde sai a matriz", u"Projetar os eixos do robô nos eixos do campo")
tb(s, 0.75, 2.15, 11.8, 0.6,
   u"Um vetor (vx, vy) escrito no referencial do robô significa: ande vx ao longo da frente do "
   u"robô, e vy ao longo do lado dele. Para saber o que isso dá no campo, basta somar a "
   u"contribuição de cada um nos eixos do campo.", size=14.5, color=MUTED, spacing=1.25)

formula(s, 0.75, 3.0, 5.6, 1.5,
        u"x_campo = vx·cos θ - vy·sin θ\ny_campo = vx·sin θ + vy·cos θ",
        size=17, color=TXT)
tb(s, 0.75, 4.62, 5.6, 0.3, u"as mesmas duas linhas, em forma de matriz", size=11.5,
   color=DIM)
formula(s, 0.75, 4.95, 5.6, 1.35,
        u"[ x_c ]   [ cos θ  -sin θ ] [ vx ]\n[ y_c ] = [ sin θ   cos θ ] [ vy ]",
        size=15, color=CY)

card(s, 6.8, 3.0, 5.75, 1.5, u"É só uma troca de base",
     u"As colunas da matriz são os eixos do robô escritos em coordenadas de "
     u"campo. Nada é esticado: a matriz só gira, então o comprimento do vetor "
     u"não muda.", accent=GR, bsize=13)
card(s, 6.8, 4.7, 5.75, 1.6, u"Dois casos para decorar",
     u"θ = 0° → a matriz é a identidade: robô e campo coincidem.\n"
     u"θ = 90° → a frente do robô virou o +y do campo: (1, 0) vira (0, 1).",
     accent=AM, bsize=13)
nota(u"Deduzir no quadro com theta = 90 graus antes de mostrar o caso geral:\n"
     u"cos 90 = 0, sin 90 = 1, entao (vx, vy) = (1, 0) vira (0, 1).\n"
     u"Insistir que a matriz nao muda o comprimento do vetor — e rotacao pura,\n"
     u"nao escala. Erro de escala nunca e culpa da matriz.")

# ----------------------------------------------------- os dois sentidos
s = new_slide(u"Os dois sentidos", u"A mesma matriz, usada de ida e de volta")
tb(s, 0.75, 2.12, 11.8, 0.4,
   u"Rotação é fácil de inverter: girar de -θ desfaz girar de +θ. Na forma de matriz, "
   u"a inversa é a transposta — trocar o sinal dos dois senos.", size=14.5, color=MUTED)

rect(s, 0.75, 2.75, 5.75, 3.0, fill=PANEL, line=GR, lw=1.3, adj=0.05)
rect(s, 0.87, 2.75, 5.51, 0.07, fill=GR, shape=MSO_SHAPE.RECTANGLE)
tb(s, 1.05, 2.98, 5.2, 0.35, u"ROBÔ  →  CAMPO     R(θ)", size=14, color=GR, bold=True)
tb(s, 1.05, 3.4, 5.2, 0.5,
   u"Usado na odometria: as rodas mediram deslocamento no\nreferencial do robô.",
   size=12.5, color=MUTED, spacing=1.15)
formula(s, 1.05, 4.05, 5.15, 1.05,
        u"x_c = vx·cos θ - vy·sin θ\ny_c = vx·sin θ + vy·cos θ", size=13.5, color=TXT)
tb(s, 1.05, 5.25, 5.2, 0.4, u"Pose2d.exp()  ·  Translation2d.rotateBy(+θ)",
   size=12, color=GR, font=MONO)

rect(s, 6.8, 2.75, 5.75, 3.0, fill=PANEL, line=AM, lw=1.3, adj=0.05)
rect(s, 6.92, 2.75, 5.51, 0.07, fill=AM, shape=MSO_SHAPE.RECTANGLE)
tb(s, 7.1, 2.98, 5.2, 0.35, u"CAMPO  →  ROBÔ     R(-θ)", size=14, color=AM, bold=True)
tb(s, 7.1, 3.4, 5.2, 0.5,
   u"Usado para acionar: o alvo veio em coordenadas de\narena, as rodas não entendem isso.",
   size=12.5, color=MUTED, spacing=1.15)
formula(s, 7.1, 4.05, 5.15, 1.05,
        u"vx =  x_c·cos θ + y_c·sin θ\nvy = -x_c·sin θ + y_c·cos θ", size=13.5, color=TXT)
tb(s, 7.1, 5.25, 5.2, 0.4, u"driveFieldOriented()", size=12, color=AM, font=MONO)

note(s, 0.75, 6.0, 11.8,
     u"Os dois sentidos leem o mesmo rumo (getRotation2d()), com a mesma flag kGyroReversed. "
     u"É por isso que odometria e field-oriented nunca divergem de convenção neste projeto.",
     color=AM)
nota(u"Truque para a turma lembrar o sentido: olhe de quem e o dado de entrada.\n"
     u"Dado que veio das RODAS -> R(+theta), porque ele nasceu no referencial do\n"
     u"robo e precisa ir para o campo. Dado que veio de um ALVO ou do joystick\n"
     u"em campo -> R(-theta), porque ele precisa descer para as rodas.")

# ----------------------------------------------------- codigo: campo -> robo
s = new_slide(u"No código · campo → robô", u"É literalmente a matriz, em duas linhas")
code(s, 0.75, 2.2, 11.8, 2.5, u"""public void driveFieldOriented(double fieldVx, double fieldVy, double wz)
{
    // Rotacao campo -> robo: gira o vetor de -theta. Mesma fonte de rumo
    // que a odometria usa, para as duas nao divergirem de convencao.
    Rotation2d angle = getRotation2d();
    double robotVx =  smoothVx * angle.getCos() + smoothVy * angle.getSin();
    double robotVy = -smoothVx * angle.getSin() + smoothVy * angle.getCos();

    driveRobotOriented(robotVx, robotVy, smoothWz);
}""", title=u"OmnidriveSubsystem.java", size=13)

card(s, 0.75, 5.05, 5.75, 1.3, u"O sinal é a única pegadinha",
     u"Os dois senos trocados de sinal em relação a R(θ) são o que fazem a "
     u"rotação ser de -θ. Inverter isso faz o robô andar espelhado.",
     accent=PK, bsize=12.5)
card(s, 6.8, 5.05, 5.75, 1.3, u"Rotation2d já traz cos e sin",
     u"getCos() e getSin() vêm pré-calculados no objeto: não há chamada "
     u"trigonométrica por ciclo nessa conta.", accent=CY, bsize=12.5)
nota(u"Mostrar linha por linha ao lado da formula do slide anterior.\n"
     u"Se alguem perguntar dos smoothVx: e o SlewRateLimiter, que suaviza a\n"
     u"rampa de velocidade antes da rotacao. Nao muda a matematica da rotacao.")

# ----------------------------------------------------- codigo: robo -> campo
s = new_slide(u"No código · robô → campo", u"Na odometria ela está escondida no exp()")
code(s, 0.75, 2.2, 11.8, 2.2, u"""double[] robotDelta = forwardKinematics(delta1, delta2, delta3);

Rotation2d angle = gyroAngle.plus(gyroOffset);
double dTheta = angle.minus(previousAngle).getRadians();

// exp() gira o deslocamento local para o campo E integra o arco de
// verdade, em vez de assumir que o robo andou reto dentro do ciclo
Pose2d newPose = pose.exp(new Twist2d(robotDelta[0], robotDelta[1], dTheta));""",
     title=u"Odometry.java · updateWithTime()", size=13)

tb(s, 0.75, 4.65, 11.8, 0.4,
   u"Quando o dado é velocidade e não deslocamento, a rotação aparece sem disfarce:", size=14,
   color=MUTED)
code(s, 0.75, 5.1, 7.3, 1.25, u"""return new Translation2d(vxMetersPerSecond, vyMetersPerSecond)
           .rotateBy(pose.getRotation());""",
     title=u"Odometry.java · getFieldVelocity()", size=12.5)
card(s, 8.4, 5.1, 4.15, 1.25, u"Por que exp() e não só a matriz",
     u"Com dθ ≠ 0 o robô andou em arco. O exp() aplica a rotação e corrige o "
     u"arco; com dθ = 0 ele vira a matriz pura.", accent=GR, bsize=12)
nota(u"Ponto importante: na odometria a matriz existe, mas quem a aplica e o\n"
     u"Pose2d.exp(). Com dTheta = 0 o exp degenera exatamente na matriz 2x2.\n"
     u"Com dTheta diferente de zero ele ainda corrige o arco percorrido dentro\n"
     u"dos 20 ms — por isso vale mais que uma rotacao simples.")

# ----------------------------------------------------- prova numerica
s = new_slide(u"Prova numérica", u"Robô girado 90°, andando para a frente")
tb(s, 0.75, 2.15, 6.4, 0.45,
   u"Melhor caso para conferir sinal: com θ = 90° os cossenos somem e sobra só a troca de eixos.",
   size=14.5, color=MUTED, spacing=1.2)
y = table(s, 0.75, 2.8, 6.4, [
    [u"Grandeza", u"Robô", u"Campo"],
    [u"velocidade", u"(1 · 0) m/s", u"(0 · 1) m/s"],
    [u"significado", u"para a frente", u"para a esquerda"],
], [2.3, 2.05, 2.05], size=12.5, rowh=0.46)
tb(s, 0.75, y + 0.36, 6.4, 0.3, u"substituindo cos 90° = 0  e  sin 90° = 1",
   size=12, color=DIM)
formula(s, 0.75, y + 0.76, 6.4, 1.1,
        u"x_c = 1·0 - 0·1 = 0\ny_c = 1·1 + 0·0 = 1", size=15, color=CY)

code(s, 7.6, 2.8, 4.95, 2.85, u"""odometry.resetPose(
    new Pose2d(0, 0, Rotation2d.fromDegrees(90)),
    new Rotation2d());

// no referencial do robo: para a frente
assertEquals(1.0, odometry
    .getVxMetersPerSecond(), 1e-6);

// no campo: isso aponta para +y
assertEquals(0.0, odometry
    .getFieldVelocity().getX(), 1e-6);
assertEquals(1.0, odometry
    .getFieldVelocity().getY(), 1e-6);""",
     title=u"OdometryTest.java", size=11.5)
tb(s, 7.6, 5.8, 4.95, 0.5, u"./gradlew test  ·  roda no PC, sem robô", size=12,
   color=GR, font=MONO)
nota(u"Fechar a parte 1 aqui: a conta no quadro e o teste automatizado dizem a\n"
     u"mesma coisa. Rodar ./gradlew test na frente da turma se der tempo —\n"
     u"13 testes, dois deles so dessa rotacao.\n"
     u"Se o sinal estivesse trocado, o teste de 90 graus daria -1 em vez de +1.")

# =================================================================== PARTE 2
section(u"2", u"Trajeto por coordenadas", u"De “vá para (1,80 · 0,80)” até RPM nas rodas",
        accent=AM, topics=(u"erro no campo", u"malha fechada", u"três pontos",
                           u"ajuste em campo"))
nota(u"Parte 2. Agora a matriz vira ferramenta: ela e um dos elos da corrente\n"
     u"que transforma uma coordenada da arena em rotacao de roda.")

# ----------------------------------------------------- a ideia
s = new_slide(u"A ideia", u"Erro no campo, direção unitária, velocidade proporcional")
bullets(s, 0.75, 2.2, 6.0, [
    (u"1. O erro é um vetor de campo: ", u"alvo - pose. Ele já aponta para onde o "
     u"robô tem que ir, em coordenadas de arena."),
    (u"2. Normalizar dá a direção: ", u"dividir pelo comprimento separa “para onde” "
     u"de “com que velocidade”."),
    (u"3. A velocidade sai da distância: ", u"erro × kTranslationP, limitado entre o "
     u"piso que vence o atrito e a velocidade de cruzeiro."),
    (u"4. A matriz faz o resto: ", u"driveFieldOriented recebe esse vetor de campo e "
     u"desce para as rodas."),
], size=15, gap=0.18)

cx, cy = 10.1, 4.0
vec(s, cx, cy, cx, cy - 2.0, color=MUTED, lw=1.3, head=0.18)
vec(s, cx, cy, cx - 2.0, cy, color=MUTED, lw=1.3, head=0.18)
tb(s, cx - 1.0, cy - 2.36, 2.0, 0.3, u"+x campo", size=11, color=MUTED,
   align=PP_ALIGN.CENTER)
tb(s, cx - 3.3, cy - 0.34, 1.2, 0.3, u"+y campo", size=11, color=MUTED,
   align=PP_ALIGN.RIGHT)
px, py = cx - 0.55, cy - 0.5
tx, ty = cx - 1.75, cy - 1.72
rect(s, px - 0.1, py - 0.1, 0.2, 0.2, fill=WHITE, shape=MSO_SHAPE.OVAL)
tb(s, px + 0.16, py - 0.1, 1.3, 0.3, u"pose", size=11.5, color=WHITE, font=MONO)
rect(s, tx - 0.11, ty - 0.11, 0.22, 0.22, fill=AM, shape=MSO_SHAPE.OVAL)
tb(s, tx - 1.5, ty - 0.34, 1.4, 0.3, u"alvo", size=11.5, color=AM, font=MONO,
   align=PP_ALIGN.RIGHT)
# a ponta para antes do alvo, senao a seta cobre o ponto
_d = math.hypot(tx - px, ty - py)
_k = (_d - 0.17) / _d
vec(s, px, py, px + (tx - px) * _k, py + (ty - py) * _k, color=CY, lw=2.4, head=0.22)
tb(s, px - 3.05, py - 0.72, 1.9, 0.5, u"erro =\nalvo - pose", size=11.5, color=CY,
   bold=True, align=PP_ALIGN.RIGHT, spacing=1.1)
formula(s, 7.35, 5.3, 5.2, 1.1,
        u"dir   = erro / |erro|\nspeed = |erro| × kTranslationP", size=13, color=TXT)
nota(u"Insistir no passo 2: normalizar. Quem esquece de dividir pela distancia\n"
     u"acaba com um controlador cuja velocidade depende do tamanho da arena.\n"
     u"O passo 3 e o que da a desaceleracao de graca: perto do alvo o erro e\n"
     u"pequeno, logo a velocidade e pequena. Nao precisa de rampa manual.")

# ----------------------------------------------------- malha fechada
s = new_slide(u"A malha fechada", u"Onde a matriz entra, e onde ela sai")
pipe(s, 0.75, 2.5, 11.8, [
    (u"alvo\n(x, y) do campo", AM),
    (u"erro\nalvo - pose", AM),
    (u"direção × speed\nvetor de campo", CY),
    (u"R(-θ)\ncampo → robô", PK),
    (u"cinemática\n3 rodas + PID", GR),
    (u"encoders\n+ IMU", GR),
    (u"R(+θ)\npose no campo", PK),
], h=1.15, size=11, gap=0.26)

# realimentacao: da pose de volta para o erro
line(s, 12.1, 3.78, 12.1, 4.5, color=PK, lw=1.3, dash=True)
line(s, 12.1, 4.5, 2.6, 4.5, color=PK, lw=1.3, dash=True)
vec(s, 2.6, 4.5, 2.6, 3.82, color=PK, lw=1.4, head=0.2)
tb(s, 4.0, 4.56, 6.0, 0.3, u"a pose estimada volta como referência a cada 20 ms",
   size=12, color=PK, align=PP_ALIGN.CENTER)

card(s, 0.75, 5.05, 3.75, 1.3, u"Duas vezes, sentidos opostos",
     u"R(-θ) para acionar, R(+θ) para medir. A mesma leitura de rumo nas duas.",
     accent=PK, bsize=12)
card(s, 4.78, 5.05, 3.75, 1.3, u"Nada de cronômetro",
     u"O erro vem da pose, não do tempo. Bateria fraca ou tapete diferente não "
     u"deslocam o ponto final.", accent=GR, bsize=12)
card(s, 8.8, 5.05, 3.75, 1.3, u"Escorregão não acumula",
     u"Empurre o robô no meio do trajeto: no ciclo seguinte o erro já é outro e "
     u"ele volta.", accent=CY, bsize=12)
nota(u"Percorrer a corrente com o dedo, da esquerda para a direita, e depois a\n"
     u"linha tracejada de volta. Os dois blocos rosa sao a mesma matriz, em\n"
     u"sentidos opostos — e o resumo visual do deck inteiro.")

# ----------------------------------------------------- os tres pontos
s = new_slide(u"O trajeto de exemplo", u"Três coordenadas, 1 s de parada em cada")
field_map(s, 10.7, 5.95, scale=1.6)
bullets(s, 0.75, 2.25, 5.6, [
    (u"A origem é onde o autônomo começou: ", u"o grupo chama resetOdometry() antes "
     u"da primeira perna, então as coordenadas saem do ponto de partida."),
    (u"O robô não gira em nenhum momento: ", u"a seta ciano dentro do chassi aponta "
     u"para +x nos quatro pontos. Na segunda perna ele anda de lado."),
    (u"Cada perna tem timeout: ", u"se o robô travar contra alguma coisa, o grupo "
     u"segue para o ponto seguinte em vez de ficar empurrando."),
], size=14.5, gap=0.16)
nota(u"Marcar no tapete, com fita, o ponto de partida antes da demonstracao —\n"
     u"ele e a origem de tudo.\n"
     u"Perguntar para a turma: na segunda perna, qual roda gira mais? Resposta:\n"
     u"depende do rumo; e justamente a matriz que distribui isso.")

# ----------------------------------------------------- codigo do comando
s = new_slide(u"O comando de um ponto", u"Três contas e uma chamada, por ciclo")
code(s, 0.75, 2.2, 11.8, 2.75, u"""Translation2d error = target.minus(drive.getPose().getTranslation());
double distance = error.getNorm();

// velocidade proporcional ao que falta: a desaceleracao sai de graca
double speed = Math.min(distance * AutoConstants.kTranslationP, AutoConstants.kDriveSpeed);
speed = Math.max(speed, AutoConstants.kMinDriveSpeed);

// direcao unitaria do erro, ja em coordenadas de campo
double dirX = error.getX() / distance;
double dirY = error.getY() / distance;

drive.driveFieldOriented(dirX * speed, dirY * speed, turn);""",
     title=u"DriveToPointCommand.java · execute()", size=12.5)

card(s, 0.75, 5.3, 5.75, 1.05, u"isFinished()",
     u"Termina quando a distância cai abaixo de kPositionTolerance (5 cm).",
     accent=CY, bsize=12.5)
card(s, 6.8, 5.3, 5.75, 1.05, u"Sem resetEncoders()",
     u"O alvo é absoluto: a pose precisa continuar valendo de um ponto ao outro.",
     accent=PK, bsize=12.5)
nota(u"Comparar com o DriveDistanceCommand do deck anterior: lá o alvo e uma\n"
     u"distancia relativa e o comando zera os encoders no initialize. Aqui o\n"
     u"alvo e absoluto e zerar encoder seria perder a referencia.\n"
     u"O 'turn' que aparece na ultima linha e a correcao de rumo, calculada\n"
     u"acima com kHeadingP — o robo translada, nao gira.")

# ----------------------------------------------------- codigo do grupo
s = new_slide(u"O grupo dos três pontos", u"Ancorar, depois alternar perna e pausa")
code(s, 0.75, 2.2, 7.3, 3.15, u"""private static final Translation2d[] WAYPOINTS =
{
    new Translation2d(1.00, 0.00),  // P1
    new Translation2d(1.00, 0.80),  // P2
    new Translation2d(1.80, 0.80)   // P3
};

addCommands(new InstantCommand(drive::resetOdometry));

for (int i = 0; i < WAYPOINTS.length; i++)
{
    addCommands(leg(drive, WAYPOINTS[i]));
    addCommands(pause(drive));
}""", title=u"WaypointAutoCommand.java", size=12.5)

card(s, 8.4, 2.2, 4.15, 1.5, u"Trocar o trajeto",
     u"Só o array muda. Acrescentar um quarto ponto não mexe em mais nada do "
     u"grupo.", accent=CY, bsize=12.5)
card(s, 8.4, 3.85, 4.15, 1.5, u"resetOdometry, não resetHeading",
     u"resetHeading chama ahrs.reset(), que no navX não é instantâneo: o rumo "
     u"zerado com atraso entra como salto na pose.", accent=AM, bsize=12)
note(s, 0.75, 5.6, 11.8,
     u"A pausa é um RunCommand(drive::stop, drive) com timeout, não um WaitCommand: "
     u"WaitCommand não declara addRequirements(drive), e durante o 1 s o default command "
     u"do subsystem — o joystick — reassume as rodas.", color=AM)
nota(u"O detalhe da pausa e o tipo de erro que so aparece na arena, com o\n"
     u"joystick ligado e fora do centro. Vale contar como armadilha classica\n"
     u"do Command-Based: quem nao exige o subsystem, nao manda nele.")

# ----------------------------------------------------- ajuste
s = new_slide(u"Ajuste em campo", u"Escala e sinal primeiro; ganho depois")
table(s, 0.75, 2.25, 11.8, [
    [u"Constante", u"Início", u"O que faz", u"Quando mexer"],
    [u"kTranslationP", u"0.8", u"velocidade por metro de erro", u"passa do ponto: suba"],
    [u"kPositionTolerance", u"0.05 m", u"raio em que o ponto conta", u"oscila no alvo: solte"],
    [u"kMinDriveSpeed", u"0.12", u"piso que vence o atrito", u"para antes e morre: suba"],
    [u"kDriveSpeed", u"0.35", u"teto de velocidade", u"por último, sempre"],
    [u"kHeadingP", u"0.010", u"correção de rumo por grau", u"gira em vez de corrigir: -0.010"],
], [2.85, 1.25, 3.55, 4.15], size=12, rowh=0.47)

tb(s, 0.75, 5.2, 11.8, 0.35, u"ORDEM DE AJUSTE", size=12, color=CY, bold=True)
pipe(s, 0.75, 5.55, 11.8, [
    (u"escala da odometria", GR), (u"sinal dos eixos", GR), (u"kHeadingP", AM),
    (u"kMinDriveSpeed", AM), (u"kTranslationP", CY), (u"kDriveSpeed", CY),
], h=0.52, size=11, gap=0.2)
nota(u"Mexer em ganho antes de fechar escala e sinal e perder a tarde: o\n"
     u"controlador vai perseguir um alvo que a odometria esta informando errado.\n"
     u"Teste de escala: empurrar o robo 1 m com trena ao lado e comparar com\n"
     u"Odom X. Diferenca maior que 5 cm e escala, nao ganho.")

# ----------------------------------------------------- fecho
s = new_slide(u"Em uma frase", u"O que levar do laboratório")
rect(s, 0.75, 2.3, 11.8, 1.5, fill=PANEL2, line=CY, lw=1.4, adj=0.06)
tb(s, 1.2, 2.62, 10.9, 0.95,
   u"A matriz de rotação é a única coisa que liga o que o robô sente ao que a "
   u"arena significa — e ela aparece duas vezes, em sentidos opostos.",
   size=20, color=WHITE, bold=True, spacing=1.2)

card(s, 0.75, 4.1, 3.75, 1.45, u"R(+θ) · medir",
     u"Deslocamento das rodas → pose no campo. Dentro de Pose2d.exp() e de "
     u"getFieldVelocity().", accent=GR, bsize=12.5)
card(s, 4.78, 4.1, 3.75, 1.45, u"R(-θ) · acionar",
     u"Vetor de campo → velocidade das rodas. Dentro de driveFieldOriented().",
     accent=AM, bsize=12.5)
card(s, 8.8, 4.1, 3.75, 1.45, u"Entre as duas",
     u"O erro: alvo menos pose, recalculado a cada 20 ms. É o autônomo inteiro.",
     accent=CY, bsize=12.5)
tb(s, 0.75, 5.95, 11.8, 0.4,
   u"Guia de implementação passo a passo:  docs/autonomo-waypoints.html",
   size=13, color=DIM, font=MONO)
nota(u"Fechar pedindo para cada dupla responder, sem olhar o codigo: nesta\n"
     u"linha, a rotacao e de +theta ou de -theta? Se a turma acerta isso, o\n"
     u"objetivo do deck foi cumprido.\n"
     u"Mandar o guia HTML junto com os slides — ele tem o checklist de campo e\n"
     u"a tabela de diagnostico.")

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
out = sys.argv[1] if len(sys.argv) > 1 else "Lab9-MatrizRotacao-Trajetoria.pptx"
prs.save(out)
print("slides:", len(_slides))
print("saved:", out)
