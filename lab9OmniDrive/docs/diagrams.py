# -*- coding: utf-8 -*-
"""Diagramas desenhados com shapes nativos do PowerPoint."""
import math
from deckbase import *
from pptx.util import Inches, Pt
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import PP_ALIGN


# ---------------------------------------------------------------- scheduler
def diag_scheduler(s, x, y, w):
    """Ciclo do CommandScheduler.run() a cada 20 ms."""
    steps = [
        ("1. Lê triggers", "botões / condições", CY),
        ("2. Agenda", "comandos novos", VI),
        ("3. execute()", "dos comandos ativos", AM),
        ("4. isFinished()", "terminou? end(false)", GR),
        ("5. periodic()", "de cada subsystem", PK),
    ]
    bw = (w - 0.34 * (len(steps) - 1)) / len(steps)
    for i, (t1, t2, col) in enumerate(steps):
        xx = x + i * (bw + 0.34)
        rect(s, xx, y, bw, 1.12, fill=PANEL, line=col, lw=1.3, adj=0.1)
        tb(s, xx + 0.12, y + 0.2, bw - 0.24, 0.3, t1, size=13.5, bold=True,
           color=col, align=PP_ALIGN.CENTER)
        tb(s, xx + 0.12, y + 0.56, bw - 0.24, 0.5, t2, size=11.5, color=MUTED,
           align=PP_ALIGN.CENTER, spacing=1.1)
        if i < len(steps) - 1:
            arrow_r(s, xx + bw + 0.045, y + 0.47, 0.25, 0.22, color=DIM)
    # retorno
    yb = y + 1.12 + 0.46
    line(s, x + w - bw / 2, y + 1.12, x + w - bw / 2, yb, color=DIM)
    line(s, x + w - bw / 2, yb, x + bw / 2, yb, color=DIM)
    vec(s, x + bw / 2, yb, x + bw / 2, y + 1.17, color=DIM, lw=1.25, head=0.2)
    tag = rect(s, x + w / 2 - 1.1, yb - 0.17, 2.2, 0.36, fill=BG, line=DIM, adj=0.45)
    shape_text(tag, u"repete a cada 20 ms", size=11.5, color=MUTED)


# ---------------------------------------------------------------- ciclo de vida
def diag_command_life(s, x, y, w):
    bw = 2.25
    gap = (w - 3 * bw) / 2.0
    boxes = [
        ("initialize()", u"1x, ao ser agendado\nzera encoders, guarda rumo", CY),
        ("execute()", u"a cada 20 ms\ncalcula e manda potência", AM),
        ("end(interrupted)", u"1x, ao sair\npara os motores", PK),
    ]
    for i, (t, d, col) in enumerate(boxes):
        xx = x + i * (bw + gap)
        rect(s, xx, y, bw, 1.0, fill=PANEL, line=col, lw=1.3, adj=0.1)
        tb(s, xx + 0.1, y + 0.14, bw - 0.2, 0.3, t, size=14, bold=True, color=col,
           align=PP_ALIGN.CENTER, font=MONO)
        tb(s, xx + 0.1, y + 0.48, bw - 0.2, 0.5, d, size=11, color=MUTED,
           align=PP_ALIGN.CENTER, spacing=1.12)
        if i < 2:
            arrow_r(s, xx + bw + gap / 2 - 0.16, y + 0.37, 0.32, 0.24, color=DIM)
    # laco do execute
    cx = x + bw + gap + bw / 2
    line(s, cx - 0.85, y + 1.0, cx - 0.85, y + 1.38, color=AM)
    line(s, cx - 0.85, y + 1.38, cx + 0.85, y + 1.38, color=AM)
    vec(s, cx + 0.85, y + 1.38, cx + 0.85, y + 1.04, color=AM, lw=1.25, head=0.18)
    tb(s, cx - 1.6, y + 1.44, 3.2, 0.3, u"isFinished() == false", size=10.5,
       color=AM, font=MONO, align=PP_ALIGN.CENTER)
    # ramo isFinished true
    x3 = x + 2 * (bw + gap)
    tb(s, x + bw + gap + bw + 0.08, y - 0.42, gap - 0.1, 0.34,
       u"isFinished()\n== true", size=10, color=GR, align=PP_ALIGN.CENTER, spacing=1.05)
    # interrupcao
    yi = y + 1.95
    ib = rect(s, x, yi, bw * 1.55, 0.6, fill=PANEL2, line=PK, lw=1.2, adj=0.2)
    shape_text(ib, u"outro comando exige o mesmo subsystem", size=11, color=TXT)
    line(s, x + bw * 1.55, yi + 0.3, x3 + bw / 2, yi + 0.3, color=PK, dash=True)
    vec(s, x3 + bw / 2, yi + 0.3, x3 + bw / 2, y + 1.05, color=PK, lw=1.4, head=0.2)
    tb(s, x3 + bw / 2 + 0.12, yi - 0.34, 2.6, 0.3, u"end(true)", size=11.5,
       color=PK, font=MONO, bold=True)


# ---------------------------------------------------------------- 3 rodas
WHEELS = [
    (180.0, "1", u"traseira", PK),
    (-60.0, "2", u"frontal dir.", CY),
    (60.0,  "3", u"frontal esq.", GR),
]


def diag_wheels(s, cx, cy, R=1.35, show_axes=True, show_drive=True,
                show_radius=True, labels=True, wl=0.78, wt=0.235):
    """Chassi circular com 3 rodas omni a 120 graus. Frente do robo = para cima."""
    # chassi
    rect(s, cx - R - 0.1, cy - R - 0.1, 2 * (R + 0.1), 2 * (R + 0.1),
         fill=PANEL, line=PANEL2, lw=1.4, shape=MSO_SHAPE.OVAL)
    if show_axes:
        vec(s, cx, cy, cx, cy - R - 0.62, color=AM, lw=1.8, head=0.2)
        tb(s, cx + 0.1, cy - R - 0.84, 2.0, 0.3, u"frente  (+)", size=11.5,
           color=AM, bold=True)
        vec(s, cx, cy, cx - R - 0.62, cy, color=VI, lw=1.8, head=0.2)
        tb(s, cx - R - 1.62, cy - 0.32, 1.0, 0.3, u"lado (+)", size=11.5,
           color=VI, bold=True, align=PP_ALIGN.RIGHT)
    # centro
    rect(s, cx - 0.06, cy - 0.06, 0.12, 0.12, fill=WHITE, shape=MSO_SHAPE.OVAL)
    for phi, num, name, col in WHEELS:
        ra = math.radians(phi)
        px = cx - R * math.sin(ra)
        py = cy - R * math.cos(ra)
        dx, dy = -math.cos(ra), math.sin(ra)
        rot = math.degrees(math.atan2(dy, dx))
        rect(s, px - wl / 2, py - wt / 2, wl, wt, fill=col, line=None,
             adj=0.45, rot=rot)
        if labels:
            lx = cx - (R + 0.70) * math.sin(ra)
            ly = cy - (R + 0.70) * math.cos(ra)
            tb(s, lx - 0.85, ly - 0.26, 1.7, 0.56,
               u"roda %s\n%s" % (num, name), size=11.5, color=col, bold=True,
               align=PP_ALIGN.CENTER, spacing=1.05)
        if show_drive:
            vec(s, px, py, px + dx * 0.62, py + dy * 0.62, color=WHITE, lw=1.5, head=0.17)
    if show_radius:
        ra = math.radians(-60.0)
        px = cx - R * math.sin(ra)
        py = cy - R * math.cos(ra)
        line(s, cx, cy, px, py, color=MUTED, lw=1.1, dash=True)
        tb(s, cx + 0.42, cy - 0.62, 1.2, 0.3, u"R", size=14, color=MUTED,
           bold=True, italic=True)


def diag_omni_wheel(s, cx, cy, R=0.95):
    """Roda omni vista de frente: aro + rolos passivos."""
    rect(s, cx - R, cy - R, 2 * R, 2 * R, fill=PANEL2, line=CY, lw=1.6, shape=MSO_SHAPE.OVAL)
    rect(s, cx - R * 0.46, cy - R * 0.46, 2 * R * 0.46, 2 * R * 0.46,
         fill=PANEL, line=PANEL2, lw=1.2, shape=MSO_SHAPE.OVAL)
    n = 10
    for i in range(n):
        a = 2 * math.pi * i / n
        px = cx + R * 0.76 * math.cos(a)
        py = cy + R * 0.76 * math.sin(a)
        rect(s, px - 0.165, py - 0.072, 0.33, 0.144, fill=AM, adj=0.5,
             rot=math.degrees(a))
    rect(s, cx - 0.05, cy - 0.05, 0.1, 0.1, fill=WHITE, shape=MSO_SHAPE.OVAL)


# ---------------------------------------------------------------- field oriented
def diag_field_oriented(s, cx, cy, theta_deg=38.0, R=1.05):
    """Campo fixo + robo girado theta; vetor de joystick no frame do campo."""
    # eixos do campo
    vec(s, cx, cy, cx, cy - 1.9, color=MUTED, lw=1.4, head=0.17)
    tb(s, cx - 1.0, cy - 2.26, 2.0, 0.3, u"X campo", size=10.5, color=MUTED,
       align=PP_ALIGN.CENTER)
    vec(s, cx, cy, cx - 1.9, cy, color=MUTED, lw=1.4, head=0.17)
    tb(s, cx - 2.9, cy + 0.1, 0.9, 0.3, u"Y campo", size=10.5, color=MUTED,
       align=PP_ALIGN.RIGHT)
    # chassi girado
    rect(s, cx - R, cy - R, 2 * R, 2 * R, fill=PANEL, line=PANEL2, lw=1.3,
         shape=MSO_SHAPE.OVAL)
    # arco theta (entre o X do campo e a frente do robo)
    arc = rect(s, cx - 0.66, cy - 0.66, 1.32, 1.32, fill=None, line=GR, lw=1.4,
               shape=MSO_SHAPE.ARC)
    arc.rotation = 270.0
    tb(s, cx - 0.72, cy - 1.02, 0.5, 0.3, u"θ", size=17, color=GR, bold=True,
       italic=True, align=PP_ALIGN.CENTER)
    # frente do robo
    th = math.radians(theta_deg)
    fx, fy = -math.sin(th), -math.cos(th)
    vec(s, cx, cy, cx + fx * 1.5, cy + fy * 1.5, color=AM, lw=2.1, head=0.2)
    tb(s, cx - 3.05, cy + fy * 1.62 - 0.16, 2.0, 0.32, u"frente do robô",
       size=10.5, color=AM, bold=True, align=PP_ALIGN.RIGHT)
    # eixo lateral do robo
    lx, ly = -math.cos(th), math.sin(th)
    vec(s, cx, cy, cx + lx * 1.2, cy + ly * 1.2, color=VI, lw=1.5, head=0.17)
    # joystick: vetor no frame do campo, separado da frente do robo
    vec(s, cx, cy, cx + 0.64, cy - 1.72, color=CY, lw=2.4, head=0.22)
    tb(s, cx + 0.82, cy - 1.98, 2.1, 0.56, u"comando do\njoystick (campo)",
       size=10.5, color=CY, bold=True, spacing=1.05)


# ---------------------------------------------------------------- pipelines
def pipe(s, x, y, w, items, h=0.72, size=12.5, gap=0.42, arrow_color=CY):
    """Cadeia horizontal de caixas com setas."""
    n = len(items)
    bw = (w - gap * (n - 1)) / n
    out = []
    for i, it in enumerate(items):
        label, col = (it if isinstance(it, tuple) else (it, CY))
        xx = x + i * (bw + gap)
        b = rect(s, xx, y, bw, h, fill=PANEL, line=col, lw=1.25, adj=0.14)
        shape_text(b, label, size=size, color=TXT, font=MONO)
        out.append((xx, bw))
        if i < n - 1:
            arrow_r(s, xx + bw + 0.06, y + h / 2 - 0.12, gap - 0.12, 0.24, color=arrow_color)
    return out


def diag_odom_pipeline(s, x, y, w):
    boxes = pipe(s, x, y, w, [
        (u"3 encoders\n(metros)", GR),
        (u"Δ do ciclo\nd - d_anterior", GR),
        (u"forwardKinematics\n→ dx, dy, dθ", CY),
        (u"Twist2d\n+ θ da IMU", AM),
        (u"pose.exp()\n→ Pose2d", PK),
    ], h=0.95, size=11.5, gap=0.4)
    # IMU entrando
    ximu = boxes[3][0]
    b = rect(s, ximu - 0.55, y + 1.65, 2.2, 0.55, fill=PANEL2, line=AM, lw=1.2, adj=0.2)
    shape_text(b, u"navX IMU  →  θ", size=11.5, color=AM, font=MONO)
    vec(s, ximu + 0.55, y + 1.65, ximu + 0.55, y + 1.02, color=AM, lw=1.5, head=0.2)
    # realimentacao da pose: sai pela direita, volta pelo topo
    xr = boxes[-1][0] + boxes[-1][1]
    xmid = boxes[-1][0] + boxes[-1][1] / 2
    yb = y - 0.5
    line(s, xr, y + 0.2, xr + 0.3, y + 0.2, color=PK, dash=True)
    line(s, xr + 0.3, y + 0.2, xr + 0.3, yb, color=PK, dash=True)
    line(s, xr + 0.3, yb, xmid, yb, color=PK, dash=True)
    vec(s, xmid, yb, xmid, y - 0.04, color=PK, lw=1.3, head=0.18)
    tb(s, xmid - 3.2, yb - 0.34, 3.0, 0.3, u"pose anterior realimentada", size=10.5,
       color=PK, align=PP_ALIGN.RIGHT)


def diag_drive_pipeline(s, x, y, w):
    pipe(s, x, y, w, [
        (u"joystick\nX, Y, Z", CY),
        (u"deadband\n+ rescale", CY),
        (u"SlewRate\nLimiter", VI),
        (u"campo → robô\nrotação de -θ", AM),
        (u"cinemática\ninversa", GR),
        (u"normaliza\n|max| ≤ 1", GR),
        (u"× kMaxRPM\nPID por roda", PK),
    ], h=0.95, size=11, gap=0.26)


# ---------------------------------------------------------------- arco vs corda
def diag_arc(s, x, y):
    """Comparacao: integrar reta (corda) x integrar arco (exp)."""
    for i, (tit, col, desc) in enumerate([
        (u"Integração em reta (errado)", PK, u"assume que o robô andou reto\ndentro dos 20 ms"),
        (u"pose.exp() — arco (certo)", GR, u"integra a trajetória curva\nde verdade"),
    ]):
        bx = x + i * 5.6
        rect(s, bx, y, 5.1, 2.5, fill=PANEL, line=PANEL2, adj=0.06)
        tb(s, bx + 0.26, y + 0.18, 4.6, 0.3, tit, size=13.5, bold=True, color=col)
        tb(s, bx + 0.26, y + 1.92, 4.6, 0.5, desc, size=11, color=MUTED, spacing=1.1)
        px, py = bx + 0.9, y + 1.62
        qx, qy = bx + 4.1, y + 0.78
        rect(s, px - 0.07, py - 0.07, 0.14, 0.14, fill=WHITE, shape=MSO_SHAPE.OVAL)
        rect(s, qx - 0.07, qy - 0.07, 0.14, 0.14, fill=col, shape=MSO_SHAPE.OVAL)
        tb(s, px - 0.5, py + 0.1, 1.0, 0.3, u"t", size=11, color=MUTED, italic=True)
        tb(s, qx - 0.1, qy - 0.42, 1.4, 0.3, u"t + 20 ms", size=11, color=MUTED, italic=True)
        if i == 0:
            vec(s, px, py, qx, qy, color=col, lw=2.0, head=0.2)
        else:
            steps = 10
            prev = (px, py)
            for k in range(1, steps + 1):
                f = k / float(steps)
                mx = px + (qx - px) * f
                my = py + (qy - py) * f - 0.62 * math.sin(math.pi * f)
                line(s, prev[0], prev[1], mx, my, color=col, lw=2.0)
                prev = (mx, my)
            line(s, px, py, qx, qy, color=DIM, lw=1.0, dash=True)
