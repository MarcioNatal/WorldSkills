# -*- coding: utf-8 -*-
"""Lab 10 - Curva S: controle de movimento com jerk limitado no WPILib.

Irmao dos decks do lab 9: mesma paleta, mesmos helpers (deckbase), mesma
convencao de desenho. O que muda e que aqui os graficos sao o conteudo, nao
ilustracao - por isso o plots.py no lugar do diagrams.py.

    python build_curvas.py [saida.pptx]
"""
import math, sys
from deckbase import *
from plots import Plot, legend, scurve_profile, scurve_time, trapezoid_profile
from pptx.util import Inches, Pt
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR

SEC1 = u"Parte 1 — O que o WPILib tem (e o que falta)"
SEC2 = u"Parte 2 — A curva S"
SEC3 = u"Parte 3 — No robô"

NOTES = []


def nota(txt):
    """Nota do apresentador do slide que acabou de ser criado."""
    NOTES.append(txt)


# ------------------------------------------------------------------ numeros
# Os limites do lab (Constants.AtiradorConstants).
VMAX, AMAX, JMAX = 300.0, 600.0, 3000.0

# Alvo dos botoes: 100 mm. Com 100 mm o trapezio nem chega ao cruzeiro (fica
# triangular), por isso os graficos usam 300 mm - la o trapezio aparece
# inteiro, com as tres fases.
D_LAB = 100.0
D_PLOT = 300.0

TS_LAB = scurve_time(D_LAB, VMAX, AMAX, JMAX)
T_VEL = 1.875 * D_LAB / VMAX
T_ACC = math.sqrt(10.0 / math.sqrt(3.0) * D_LAB / AMAX)
T_JRK = (60.0 * D_LAB / JMAX) ** (1.0 / 3.0)

TRP_S, TRP_V, TRP_A, T_TRP, VPK_TRP, TRAMP, TCRUISE = \
    trapezoid_profile(D_PLOT, VMAX, AMAX)
TS_PLOT = scurve_time(D_PLOT, VMAX, AMAX, JMAX)
SC_S, SC_V, SC_A, SC_J = scurve_profile(D_PLOT, TS_PLOT)

SC_VPK = 1.875 * D_PLOT / TS_PLOT
SC_APK = 10.0 / math.sqrt(3.0) * D_PLOT / (TS_PLOT ** 2)
SC_JPK = 60.0 * D_PLOT / (TS_PLOT ** 3)

TMAX = TS_PLOT * 1.04


# =================================================================== CAPA
s = new_slide(sec=SEC1, number=False)
rect(s, 0, 0, W, 0.14, fill=AM, shape=MSO_SHAPE.RECTANGLE)

_p = Plot(s, 7.9, 2.35, 4.9, 3.0, TMAX, -620, 620, xlabel=None, pad_l=0.3,
          pad_b=0.3)
_p.steps([(0, 0), (0, AMAX), (TRAMP, AMAX), (TRAMP, 0),
          (TRAMP + TCRUISE, 0), (TRAMP + TCRUISE, -AMAX),
          (T_TRP, -AMAX), (T_TRP, 0), (TMAX, 0)], color=DIM, lw=1.6, dash=True)
_p.curve(SC_A, color=AM, lw=2.6)
tb(s, 7.9, 5.42, 4.9, 0.3, u"aceleração:  trapézio  ·  curva S", size=11.5,
   color=DIM, align=PP_ALIGN.CENTER, font=MONO)

tb(s, 1.0, 1.5, 7.0, 0.4, u"ROBÓTICA MÓVEL  ·  WORLDSKILLS  ·  LAB 10", size=14,
   color=AM, bold=True)
tb(s, 1.0, 2.1, 7.0, 1.9, u"Curva S:\njerk limitado", size=48, color=WHITE,
   bold=True, spacing=1.08)
rect(s, 1.0, 4.42, 1.9, 0.05, fill=CY, shape=MSO_SHAPE.RECTANGLE)
tb(s, 1.0, 4.82, 6.6, 1.2,
   u"O WPILib não tem essa classe. Dá para construir\nela em 30 linhas — e vale entender por quê.",
   size=18, color=MUTED, spacing=1.25)
tb(s, 1.0, 6.3, 7.0, 0.4,
   u"TitanQuad M3  ·  PID em V/mm  ·  WPILib 2020 / Java",
   size=12.5, color=DIM)
nota(u"Este deck responde a uma pergunta direta: da para fazer curva S com as\n"
     u"ferramentas do WPILib?\n"
     u"A resposta e sim, mas nao existe classe pronta - e o caminho para\n"
     u"construi-la ensina mais do que a classe em si.\n"
     u"Na capa ja esta o grafico que o deck inteiro persegue: a aceleracao do\n"
     u"trapezio (pontilhada, quadrada) contra a da curva S (ambar, em rampa).")

# =================================================================== A RESPOSTA
s = new_slide(u"A pergunta", u"Dá para limitar o jerk com as ferramentas do WPILib?")
rect(s, 0.75, 2.3, 11.8, 1.25, fill=PANEL2, line=AM, lw=1.4, adj=0.07)
tb(s, 1.2, 2.58, 10.9, 0.7,
   u"Dá — mas nenhuma versão do WPILib tem perfil com limite de jerk.",
   size=22, color=WHITE, bold=True, spacing=1.15)

card(s, 0.75, 3.95, 3.75, 2.0, u"O que falta",
     u"Uma classe de perfil que trate o jerk como o TrapezoidProfile trata a "
     u"aceleração. Não existe. Nem em 2020, nem em 2026.",
     accent=PK, hsize=16, bsize=13.5)
card(s, 4.78, 3.95, 3.75, 2.0, u"O que sobra",
     u"Matemática de forma fechada: um polinômio quíntico resolve o caso "
     u"ponto-a-ponto em ~30 linhas, sem iteração.",
     accent=AM, hsize=16, bsize=13.5)
card(s, 8.8, 3.95, 3.75, 2.0, u"O que já serve",
     u"PIDController, SimpleMotorFeedforward e SlewRateLimiter continuam "
     u"sendo as peças. Muda só quem calcula o setpoint.",
     accent=GR, hsize=16, bsize=13.5)
nota(u"Abrir deixando a turma responder antes. A resposta mais comum e 'usa o\n"
     u"TrapezoidProfile' - e e justamente a que o deck vai desmontar.\n"
     u"A segunda mais comum e 'usa o ExponentialProfile, que tem curva' - essa\n"
     u"tem um slide proprio mais adiante.\n"
     u"Deixar claro desde agora: a malha de controle NAO muda. O que muda e o\n"
     u"setpoint que entra nela.")

# =================================================================== PARTE 1
section(u"1", u"O que o WPILib tem (e o que falta)",
        u"Derivadas, trapézio e o jerk que ninguém limita", accent=CY,
        topics=(u"s, v, a, j", u"TrapezoidProfile", u"jerk infinito",
                u"o mito do exponencial"))
nota(u"Parte 1 e diagnostico: entender o problema antes de escrever a solucao.\n"
     u"Se a turma sair daqui sabendo desenhar a aceleracao de um trapezio, a\n"
     u"parte 2 fica facil.")

# ----------------------------------------------------- as quatro derivadas
s = new_slide(u"Vocabulário", u"Quatro derivadas, quatro nomes")
formula(s, 0.75, 2.2, 11.8, 0.95,
        u"posição  s(t)   →   velocidade  v = ds/dt   →   "
        u"aceleração  a = dv/dt   →   jerk  j = da/dt",
        size=18)

rows = [[u"grandeza", u"unidade (mm)", u"no robô é...", u"quem limita"],
        [u"posição", u"mm", u"onde o eixo está", u"o alvo do comando"],
        [u"velocidade", u"mm/s", u"quão rápido anda", u"TrapezoidProfile"],
        [u"aceleração", u"mm/s²", u"a força no mecanismo", u"TrapezoidProfile"],
        [u"jerk", u"mm/s³", u"o TRANCO", u"ninguém"]]
table(s, 0.75, 3.3, 11.8, rows, [2.4, 2.3, 3.6, 3.5], size=14, rowh=0.52,
      mono_body=False)

note(s, 0.75, 6.0, 11.8,
     u"Aceleração é força (F = m·a): é o que o mecanismo sente. Jerk é a "
     u"VARIAÇÃO dessa força — é o que a folga, a correia e o parafuso sentem.",
     color=AM)
nota(u"Insistir na ultima linha da tabela: o jerk e a unica das quatro que\n"
     u"nenhuma classe do WPILib limita.\n"
     u"Analogia de elevador: aceleracao constante voce quase nao percebe; o que\n"
     u"embola o estomago e o instante em que ela LIGA e DESLIGA. Esse instante\n"
     u"e o jerk.\n"
     u"Unidade mm/s3 assusta, mas e so 'mm por segundo ao cubo' - quantos\n"
     u"mm/s2 de aceleracao entram por segundo.")

# ----------------------------------------------------- o que existe no WPILib
s = new_slide(u"O inventário", u"O que o WPILib realmente oferece")
rows = [[u"classe", u"pacote", u"limita", u"jerk"],
        [u"TrapezoidProfile", u"wpilibj.trajectory", u"velocidade, aceleração",
         u"INFINITO"],
        [u"ProfiledPIDController", u"wpilibj.controller", u"idem (embrulha o acima)",
         u"INFINITO"],
        [u"ExponentialProfile", u"math.trajectory (2024+)", u"tensão, não jerk",
         u"infinito em t=0"],
        [u"SlewRateLimiter", u"wpilibj (2020)", u"taxa de 1 sinal",
         u"é peça, não perfil"],
        [u"LinearFilter", u"wpilibj", u"suaviza (atrasa)", u"não garante nada"]]
table(s, 0.75, 2.25, 11.8, rows, [3.1, 3.2, 3.2, 2.3], size=13, rowh=0.5,
      mono_body=False)

note(s, 0.75, 5.5, 11.8,
     u"Atenção ao pacote: no WPILib 2020 é edu.wpi.first.wpilibj.trajectory. "
     u"Exemplo copiado da documentação atual diz edu.wpi.first.math.trajectory "
     u"e não compila.", color=AM)
nota(u"Esta tabela e a resposta factual a pergunta da capa. Vale projetar e\n"
     u"deixar a turma conferir no VS Code: abrir o autocomplete e ver que nao\n"
     u"existe nenhum JerkProfile.\n"
     u"O aviso do pacote economiza uma aula inteira de erro de compilacao: a\n"
     u"documentacao oficial hoje mostra math.trajectory, que so existe de 2023\n"
     u"em diante.")

# ----------------------------------------------------- o trapezio
s = new_slide(u"O trapézio", u"Limita aceleração — e por isso mesmo quebra o jerk")

p = Plot(s, 0.75, 2.15, 11.8, 2.25, TMAX, -720, 720,
         ylabel=u"a (mm/s²)", xlabel=None)
p.ytick(AMAX, u"+600")
p.ytick(0, u"0")
p.ytick(-AMAX, u"-600")
p.steps([(0, 0), (0, AMAX), (TRAMP, AMAX), (TRAMP, 0),
         (TRAMP + TCRUISE, 0), (TRAMP + TCRUISE, -AMAX),
         (T_TRP, -AMAX), (T_TRP, 0), (TMAX, 0)], color=CY, lw=2.6)
p.at(TRAMP * 0.5, AMAX + 90, u"acelera", color=MUTED, size=11)
p.at(TRAMP + TCRUISE * 0.5, 90, u"cruzeiro", color=MUTED, size=11)
p.at(TRAMP + TCRUISE + 0.08, -AMAX + 130, u"freia", color=MUTED, size=11)

p2 = Plot(s, 0.75, 4.45, 11.8, 1.5, TMAX, -1.25, 1.25, ylabel=u"j (mm/s³)")
p2.xtick(0.0, u"0")
p2.xtick(TRAMP, u"%.2f" % TRAMP)
p2.xtick(T_TRP, u"%.2f" % T_TRP)
for _t, _up in ((0.0, True), (TRAMP, False), (TRAMP + TCRUISE, False),
                (T_TRP, True)):
    p2.spike(_t, up=_up, color=PK, lw=2.2, label=(u"+∞" if _up else u"−∞"))

note(s, 0.75, 6.05, 11.8,
     u"Quatro degraus na aceleração = quatro impulsos de jerk. É exatamente "
     u"onde o mecanismo dá o tranco: na partida, na entrada do cruzeiro, no "
     u"começo da frenagem e na parada.", color=AM)
nota(u"O slide central da parte 1. Pedir para a turma olhar o grafico de cima e\n"
     u"responder onde a aceleracao MUDA - e marcar junto no grafico de baixo.\n"
     u"O trapezio nao esta errado: ele cumpre exatamente o que promete, limitar\n"
     u"velocidade e aceleracao. O problema e que, para cumprir isso no menor\n"
     u"tempo possivel, ele usa degraus - e degrau em aceleracao e jerk\n"
     u"infinito.\n"
     u"Numeros do grafico: 300 mm, vmax 300 mm/s, amax 600 mm/s2, T = 1.5 s.")

# ----------------------------------------------------- o mito do exponencial
s = new_slide(u"O mito", u"“Mas existe o ExponentialProfile, que já é curvo”")
card(s, 0.75, 2.2, 5.75, 2.3, u"O que ele realmente faz",
     u"Modela o motor como V = kV·v + kA·a e responde: qual o perfil mais "
     u"rápido respeitando 12 V? A aceleração decai sozinha porque a tensão "
     u"disponível acaba — não porque alguém limitou o jerk.",
     accent=VI, hsize=16, bsize=13.5)
card(s, 6.8, 2.2, 5.75, 2.3, u"Por que não resolve",
     u"Em t = 0 a tensão salta de 0 para o máximo: a aceleração salta com ela. "
     u"O jerk é infinito ali e na troca para frenagem — os dois pontos onde o "
     u"tranco mais aparece.",
     accent=PK, hsize=16, bsize=13.5)

formula(s, 0.75, 4.75, 11.8, 0.8,
        u"perfil exponencial  ≠  curva S          "
        u"limite de TENSÃO  ≠  limite de JERK", size=18, color=AM)

note(s, 0.75, 5.85, 11.8,
     u"E, no caso deste lab, a discussão é teórica: ExponentialProfile só "
     u"existe a partir do WPILib 2024. O projeto roda em 2020.", color=AM)
nota(u"Esta confusao e muito comum porque o nome 'exponencial' sugere suavidade\n"
     u"e o grafico de velocidade dele realmente parece um S.\n"
     u"Mostrar a diferenca de intencao: o exponencial e um perfil OTIMO (o mais\n"
     u"rapido que a bateria permite); a curva S e um perfil CONFORTAVEL (o mais\n"
     u"macio que o prazo permite). Objetivos opostos.\n"
     u"Se alguem perguntar se vale migrar de versao so por isso: nao, porque\n"
     u"nem assim se ganha limite de jerk.")

# ----------------------------------------------------- o preco do jerk
s = new_slide(u"Por que isso importa", u"O que o tranco faz no mecanismo")
card(s, 0.75, 2.2, 5.75, 1.6, u"Folga bate",
     u"Toda folga de engrenagem e correia é percorrida de uma vez no degrau. "
     u"É o clack na partida.", accent=PK, hsize=15, bsize=13)
card(s, 6.8, 2.2, 5.75, 1.6, u"Estrutura oscila",
     u"O degrau excita as frequências naturais do braço. O eixo chega no alvo "
     u"e continua tremendo.", accent=OR, hsize=15, bsize=13)
card(s, 0.75, 4.0, 5.75, 1.6, u"Pico de corrente",
     u"Aceleração instantânea pede corrente instantânea. Breaker abre, tensão "
     u"cai, a odometria perde passo.", accent=AM, hsize=15, bsize=13)
card(s, 6.8, 4.0, 5.75, 1.6, u"Roda derrapa",
     u"Na base, o degrau passa do limite de atrito. O encoder conta giro que o "
     u"robô não andou.", accent=VI, hsize=15, bsize=13)

tb(s, 0.75, 5.85, 11.8, 0.7,
   u"Nenhum desses problemas é de ganho de PID. Ajustar kP não conserta um "
   u"setpoint que pede o impossível.", size=16, color=WHITE, bold=True,
   spacing=1.2)
nota(u"Fechar a parte 1 ligando o grafico a bancada. A frase do rodape e a que\n"
     u"importa: a turma tende a tratar qualquer oscilacao como problema de\n"
     u"ganho, e gasta a aula mexendo em kP e kD.\n"
     u"O quarto card e o que mais pega em prova de arena: derrapagem corrompe\n"
     u"a odometria, e dai todo o autonomo vai embora.\n"
     u"Pedir exemplos que eles ja viram na propria bancada antes de virar o\n"
     u"slide.")

# =================================================================== PARTE 2
section(u"2", u"A curva S", u"Aceleração em rampa, jerk finito, forma fechada",
        accent=AM, topics=(u"o quíntico", u"os três picos", u"de onde vem T",
                           u"o preço"))
nota(u"Parte 2 e a matematica. Avisar que nao tem iteracao nem tabela: e uma\n"
     u"formula que cabe em um slide.")

# ----------------------------------------------------- a ideia
s = new_slide(u"A ideia", u"Se o degrau é o problema, troque o degrau por uma rampa")

p = Plot(s, 0.75, 2.15, 11.8, 3.15, TMAX, -720, 720, ylabel=u"a (mm/s²)",
         xlabel=None)
p.ytick(AMAX, u"+600")
p.ytick(0, u"0")
p.ytick(-AMAX, u"-600")
p.xtick(T_TRP, u"%.2f s" % T_TRP)
p.xtick(TS_PLOT, u"%.2f s" % TS_PLOT)
p.steps([(0, 0), (0, AMAX), (TRAMP, AMAX), (TRAMP, 0),
         (TRAMP + TCRUISE, 0), (TRAMP + TCRUISE, -AMAX),
         (T_TRP, -AMAX), (T_TRP, 0), (TMAX, 0)], color=DIM, lw=1.8, dash=True)
p.curve(SC_A, color=AM, lw=2.8, tmax=TMAX)

legend(s, 0.75, 5.48, [(DIM, u"trapézio  (jerk infinito nos cantos)"),
                       (AM, u"curva S  (jerk = inclinação desta rampa)")])

note(s, 0.75, 6.05, 11.8,
     u"A inclinação da rampa âmbar É o jerk. Limitar jerk é a mesma coisa que "
     u"dizer “a aceleração não pode mudar mais rápido que isso”.", color=AM)
nota(u"Este e o slide que da o nome: a aceleracao entra e sai em rampa, e a\n"
     u"VELOCIDADE resultante tem a forma de S.\n"
     u"Insistir na equivalencia: limitar jerk = limitar a inclinacao da\n"
     u"aceleracao. Quem entendeu isso ja entendeu o perfil.\n"
     u"A curva ambar passa de 600? Nao - o pico dela aqui e %d mm/s2, dentro\n"
     u"do limite. Quem decidiu a duracao neste caso foi a velocidade." %
     int(round(SC_APK)))

# ----------------------------------------------------- o quintico
s = new_slide(u"O quíntico de jerk mínimo",
              u"Uma função, e as outras três saem por derivada")
formula(s, 0.75, 2.2, 11.8, 1.0,
        u"u = t / T                 s(u) = d · (10u³ − 15u⁴ + 6u⁵)", size=21)

formula(s, 0.75, 3.45, 11.8, 2.0,
        u"v(u) = 30 · d/T   · u² · (1−u)²\n"
        u"a(u) = 60 · d/T²  · u · (1−u) · (1−2u)\n"
        u"j(u) = 60 · d/T³  · (1 − 6u + 6u²)",
        size=19, align=PP_ALIGN.CENTER, fill=PANEL)

card(s, 0.75, 5.7, 5.75, 1.15, u"Nas duas pontas: v = 0 E a = 0",
     u"O motor não recebe degrau nem na partida nem na chegada.",
     accent=GR, hsize=14.5, bsize=13)
card(s, 6.8, 5.7, 5.75, 1.15, u"Só d e T entram",
     u"Distância e duração. Todo o resto do perfil é consequência.",
     accent=CY, hsize=14.5, bsize=13)
nota(u"O nome 'jerk minimo' vem de otimizacao: entre todas as trajetorias que\n"
     u"saem de a e chegam em b no tempo T com as pontas paradas, esta e a que\n"
     u"minimiza a integral do jerk ao quadrado.\n"
     u"Nao precisa provar na aula. O que vale notar: cada linha e a derivada da\n"
     u"linha de cima - da para pedir que a turma derive v para achar a, no\n"
     u"quadro.\n"
     u"Notar que u vai de 0 a 1 sempre: o perfil e o MESMO para qualquer\n"
     u"distancia, so muda a escala.")

# ----------------------------------------------------- as quatro curvas
s = new_slide(u"As quatro curvas", u"O mesmo movimento, visto nas quatro derivadas")

q1 = Plot(s, 0.75, 2.15, 5.75, 2.1, TS_PLOT, 0, D_PLOT * 1.1,
          ylabel=u"s (mm)", xlabel=None, pad_l=0.66, pad_b=0.3, pad_t=0.42)
q1.curve(SC_S, color=CY, lw=2.4)
q1.ytick(D_PLOT, u"300")

q2 = Plot(s, 6.8, 2.15, 5.75, 2.1, TS_PLOT, 0, VMAX * 1.15,
          ylabel=u"v (mm/s)", xlabel=None, pad_l=0.66, pad_b=0.3, pad_t=0.42)
q2.curve(SC_V, color=GR, lw=2.4)
q2.ytick(VMAX, u"300")
q2.at(TS_PLOT * 0.5, VMAX * 0.74, u"pico = 1.875·d/T", color=MUTED, size=10.5,
      w=2.1, align=PP_ALIGN.CENTER, dx=-1.05)

q3 = Plot(s, 0.75, 4.45, 5.75, 2.1, TS_PLOT, -620, 620,
          ylabel=u"a (mm/s²)", pad_l=0.66, pad_b=0.36, pad_t=0.42)
q3.curve(SC_A, color=AM, lw=2.4)
q3.ytick(SC_APK, u"%d" % int(round(SC_APK)))

q4 = Plot(s, 6.8, 4.45, 5.75, 2.1, TS_PLOT, -JMAX * 1.15, JMAX * 1.15,
          ylabel=u"j (mm/s³)", pad_l=0.66, pad_b=0.36, pad_t=0.42)
q4.curve(SC_J, color=PK, lw=2.4)
q4.ytick(SC_JPK, u"%d" % int(round(SC_JPK)))
q4.at(TS_PLOT * 0.42, SC_JPK * 0.5, u"finito!", color=PK, size=11.5, bold=True,
      w=1.2)
nota(u"Ler os quatro na ordem, da esquerda para a direita e de cima para baixo:\n"
     u"posicao em S, velocidade em sino, aceleracao em onda suave, jerk em\n"
     u"reta.\n"
     u"O grafico do jerk e o troco do deck: no trapezio ele era impulso\n"
     u"infinito, aqui e uma reta que cruza o zero no meio do movimento.\n"
     u"Perguntar: por que o jerk e maximo no inicio e no fim? Porque e la que a\n"
     u"aceleracao esta mudando mais rapido.")

# ----------------------------------------------------- de onde vem T
s = new_slide(u"De onde vem T", u"Inverta cada pico e fique com a maior duração")
formula(s, 0.75, 2.15, 11.8, 1.35,
        u"|v|max = 1.8750·d/T   →   T ≥ 1.8750·d / vmax\n"
        u"|a|max = 5.7735·d/T²  →   T ≥ √(5.7735·d / amax)\n"
        u"|j|max = 60.000·d/T³  →   T ≥ ∛(60·d / jmax)", size=17,
        align=PP_ALIGN.LEFT, fill=PANEL)

rows = [[u"limite", u"valor no lab", u"T exigido (d = 100 mm)"],
        [u"velocidade", u"%.0f mm/s" % VMAX, u"%.3f s" % T_VEL],
        [u"aceleração", u"%.0f mm/s²" % AMAX, u"%.3f s" % T_ACC],
        [u"jerk", u"%.0f mm/s³" % JMAX, u"%.3f s   <-  manda" % T_JRK]]
table(s, 0.75, 3.6, 11.8, rows, [3.4, 3.6, 4.8], size=14, rowh=0.48)

code(s, 0.75, 5.85, 11.8, 1.0,
     u"""this.totalTime = Math.max(timeFromVelocity,
                          Math.max(timeFromAcceleration, timeFromJerk));""",
     title=u"SCurveProfile.java", size=13)
nota(u"Aqui a turma costuma perguntar por que a MAIOR e nao a menor. Porque\n"
     u"cada linha e um T MINIMO: respeitar os tres ao mesmo tempo e respeitar\n"
     u"o mais exigente.\n"
     u"No lab, com 100 mm, quem manda e o jerk - %.3f s contra %.3f s da\n"
     u"velocidade. Ou seja: o parametro novo e o que decide o movimento.\n"
     u"Exercicio rapido: subir kMaxJerk para 20000 e perguntar quem passa a\n"
     u"mandar. Resposta: a aceleracao, com %.3f s." % (T_JRK, T_VEL, T_ACC))

# ----------------------------------------------------- o codigo
s = new_slide(u"O código", u"Forma fechada: nenhuma iteração, nenhuma tabela")
code(s, 0.75, 2.2, 11.8, 2.5,
     u"""public State calculate(double t)
{
    if (t <= 0.0)        return new State(startPosition, 0.0, 0.0);
    if (t >= totalTime)  return new State(startPosition + delta, 0.0, 0.0);

    double u = t / totalTime;
    double oneMinusU = 1.0 - u;

    double position     = startPosition + delta * u*u*u * (10.0 - 15.0*u + 6.0*u*u);
    double velocity     = 30.0 * delta / totalTime * u*u * oneMinusU*oneMinusU;
    double acceleration = 60.0 * delta / (totalTime*totalTime) * u * oneMinusU * (1.0 - 2.0*u);

    return new State(position, velocity, acceleration);
}""", title=u"SCurveProfile.java", size=12.5)

card(s, 0.75, 4.95, 5.75, 1.3, u"Mesma interface do TrapezoidProfile",
     u"Constraints, State, calculate(t), isFinished(t). Trocar um pelo outro no "
     u"comando são duas linhas.", accent=GR, hsize=14.5, bsize=13)
card(s, 6.8, 4.95, 5.75, 1.3, u"State carrega a aceleração",
     u"O TrapezoidProfile não devolve. E é ela que alimenta o termo ka do "
     u"feedforward.", accent=CY, hsize=14.5, bsize=13)
nota(u"Chamar atencao para as duas primeiras linhas: fora do intervalo o perfil\n"
     u"devolve as pontas PARADAS. Com isso, segurar o comando depois do fim nao\n"
     u"gera comando estranho nenhum - detalhe que evita um bug chato.\n"
     u"A escolha de imitar a interface do TrapezoidProfile e deliberada: o lab\n"
     u"quer que trocar de perfil seja trivial, para a comparacao ser honesta.\n"
     u"Nada de Math.pow: multiplicacao explicita e mais rapida e mais legivel.")

# ----------------------------------------------------- o preco
s = new_slide(u"O preço", u"Maciez custa tempo — e é bom saber quanto")

p = Plot(s, 0.75, 2.15, 11.8, 2.95, TMAX, 0, VMAX * 1.2, ylabel=u"v (mm/s)",
         xlabel=None)
p.ytick(VMAX, u"300")
p.xtick(T_TRP, u"%.2f s" % T_TRP)
p.xtick(TS_PLOT, u"%.2f s" % TS_PLOT)
p.hline(VMAX, color=DIM)
p.steps([(0, 0), (TRAMP, VPK_TRP), (TRAMP + TCRUISE, VPK_TRP), (T_TRP, 0),
         (TMAX, 0)], color=DIM, lw=1.8, dash=True)
p.curve(SC_V, color=AM, lw=2.8, tmax=TMAX)
p.at(TRAMP + TCRUISE * 0.25, VMAX * 1.15, u"cruzeiro: o trapézio fica no teto",
     color=DIM, size=11.5, w=3.8)
p.at(TS_PLOT * 0.5, VMAX * 0.5, u"o quíntico só TOCA o teto", color=AM,
     size=11.5, bold=True, w=2.9, dx=-1.45, align=PP_ALIGN.CENTER)

legend(s, 0.75, 5.28, [(DIM, u"trapézio  %.2f s" % T_TRP),
                       (AM, u"curva S  %.2f s  (+%.0f%%)"
                        % (TS_PLOT, 100.0 * (TS_PLOT / T_TRP - 1.0)))])

note(s, 0.75, 5.85, 11.8,
     u"O quíntico não tem fase de velocidade constante: a média é 53% do pico. "
     u"Curso curto (atirador, garra, elevador) não se nota; atravessar o campo, "
     u"nota.", color=AM)
nota(u"Slide de honestidade. A curva S nao e de graca, e o aluno tem que saber\n"
     u"escolher.\n"
     u"300 mm, mesmos limites: trapezio %.2f s, curva S %.2f s. O trapezio\n"
     u"passa um tempo inteiro no teto de velocidade; o quintico so toca o teto\n"
     u"por um instante.\n"
     u"Se o tempo pesar mais que a maciez, o caminho e o perfil de 7 fases\n"
     u"(jerk trapezoidal), que tem cruzeiro - mais ~120 linhas, com os casos\n"
     u"degenerados. Nao entra neste lab." % (T_TRP, TS_PLOT))

# =================================================================== PARTE 3
section(u"3", u"No robô", u"Quem calcula o perfil, quem fecha a malha",
        accent=GR, topics=(u"arquitetura", u"feedforward", u"os três botões",
                           u"o que plotar"))
nota(u"Parte 3 e o lab10CurvaS rodando. A partir daqui e bancada.")

# ----------------------------------------------------- arquitetura
s = new_slide(u"Arquitetura", u"O comando planeja, o subsystem executa")

rect(s, 0.75, 2.2, 5.75, 2.5, fill=PANEL, line=PANEL2, adj=0.06)
rect(s, 0.75, 2.34, 0.055, 2.22, fill=AM, shape=MSO_SHAPE.RECTANGLE)
tb(s, 1.05, 2.4, 5.2, 0.32, u"COMANDO  —  dono do plano", size=14.5, bold=True,
   color=AM)
bullets(s, 1.05, 2.85, 5.2,
        [u"monta o perfil no initialize()",
         u"conta o tempo com um Timer",
         u"a cada ciclo: calculate(t)",
         u"entrega s, v, a ao subsystem"], size=13.5, gap=0.07)

rect(s, 6.8, 2.2, 5.75, 2.5, fill=PANEL, line=PANEL2, adj=0.06)
rect(s, 6.8, 2.34, 0.055, 2.22, fill=GR, shape=MSO_SHAPE.RECTANGLE)
tb(s, 7.1, 2.4, 5.2, 0.32, u"SUBSYSTEM  —  dono da malha", size=14.5, bold=True,
   color=GR)
bullets(s, 7.1, 2.85, 5.2,
        [u"feedforward + PID → volts",
         u"setVoltage() compensa a bateria",
         u"clamp em kPositionMaxVolts",
         u"segura a posição no periodic()"], size=13.5, gap=0.07)

arrow_r(s, 6.33, 3.35, 0.42, h=0.3, color=CY)

formula(s, 0.75, 5.0, 11.8, 0.75,
        u"perfil (s, v, a)  →  feedforward.calculate(v, a) + pid.calculate(medido, s)  →  volts",
        size=15)

note(s, 0.75, 6.1, 11.8,
     u"Trocar degrau por trapézio por curva S não mexe uma linha no subsystem. "
     u"É o teste de que a separação está certa.", color=AM)
nota(u"Esta divisao e a razao de o lab ter tres comandos e um subsystem, e nao\n"
     u"tres subsystems.\n"
     u"Perguntar: por que o hold de posicao fica no periodic() e nao no\n"
     u"execute()? Porque o periodic roda mesmo sem comando agendado - e isso\n"
     u"que faz o eixo voltar se alguem empurrar depois que o comando terminou.\n"
     u"Lembrar tambem do default command: neste lab NAO existe, de proposito.\n"
     u"Qualquer comando que escrevesse no motor todo ciclo apagaria o hold.")

# ----------------------------------------------------- feedforward
s = new_slide(u"Feedforward + PID", u"O perfil só rende se alguém antecipar")
code(s, 0.75, 2.2, 11.8, 1.5,
     u"""public void followSetpoint(double positionMM, double velocityMMps, double accelMMps2)
{
    driveVolts(feedforward.calculate(velocityMMps, accelMMps2)
               + pidController.calculate(getPositionMM(), positionMM));
}""", title=u"AtiradorSubsystem.java", size=12.5)

rows = [[u"ganho", u"unidade", u"o que faz"],
        [u"ks", u"V", u"tira o eixo da inércia (atrito seco)"],
        [u"kv", u"V / (mm/s)", u"paga a velocidade pedida"],
        [u"ka", u"V / (mm/s²)",
         u"ANTECIPA a aceleração — é o que faz a curva S render"]]
table(s, 0.75, 3.95, 11.8, rows, [1.9, 3.0, 6.9], size=13.5, rowh=0.5,
      mono_body=False)

note(s, 0.75, 6.05, 11.8,
     u"No projeto ks = kv = ka = 0: o perfil é seguido só pelo feedback. "
     u"Funciona, mas o eixo fica sempre atrasado. Caracterizar o mecanismo é o "
     u"exercício seguinte.", color=AM)
nota(u"Ponto que separa quem entendeu de quem copiou: sem feedforward, o PID so\n"
     u"reage ao erro, entao o eixo sempre persegue o perfil de longe.\n"
     u"Com ka, o motor recebe tensao PORQUE vai acelerar, nao porque atrasou.\n"
     u"Roteiro de caracterizacao na bancada: ks por rampa manual ate o eixo\n"
     u"mexer; kv por tensao fixa dividida pela velocidade medida em regime; ka\n"
     u"por tentativa, subindo de pouco a partir de zero.\n"
     u"Deixar em zero na primeira rodada e proposital: a turma ve o atraso e\n"
     u"entende para que serve o termo.")

# ----------------------------------------------------- os tres botoes
s = new_slide(u"Na bancada", u"Três botões, o mesmo eixo, o mesmo alvo")
rows = [[u"botão", u"comando", u"perfil", u"limita"],
        [u"A", u"AtiradorSCurveCmd", u"curva S", u"v, a e j"],
        [u"B", u"AtiradorTrapezoidCmd", u"TrapezoidProfile", u"v e a"],
        [u"X", u"AtiradorStepCmd", u"degrau (nenhum)", u"nada"],
        [u"Y", u"AtiradorSCurveCmd", u"volta à origem", u"v, a e j"]]
table(s, 0.75, 2.15, 11.8, rows, [1.3, 4.6, 3.3, 2.6], size=13.5, rowh=0.52)

card(s, 0.75, 4.9, 5.75, 1.15, u"Também no autônomo",
     u"Os três estão no SendableChooser, para rodar sem joystick.",
     accent=CY, hsize=14.5, bsize=13)
card(s, 6.8, 4.9, 5.75, 1.15, u"Alvo em um só lugar",
     u"AtiradorConstants.kTargetMM = 100 mm. Mudou ali, mudou nos quatro.",
     accent=GR, hsize=14.5, bsize=13)

note(s, 0.75, 6.15, 11.8,
     u"Comece pelo X (degrau), depois B, depois A: a sequência ensina mais "
     u"que a explicação.", color=AM)
nota(u"Ordem X -> B -> A e proposital: cada um corrige o anterior.\n"
     u"No X, o PID comeca com erro maximo e satura a tensao; a forma do\n"
     u"movimento e decidida pela saturacao, nao por um plano.\n"
     u"No B aparece o controle de velocidade e aceleracao, mas o tranco da\n"
     u"partida continua.\n"
     u"No A o tranco sai, e o movimento fica visivelmente mais lento - deixar a\n"
     u"turma reclamar do tempo ANTES de mostrar o slide do preco.")

# ----------------------------------------------------- o que plotar
s = new_slide(u"O que plotar", u"O gráfico é a aula")
formula(s, 0.75, 2.2, 11.8, 0.8, u"Atirador/Setpoint Acel mmps2", size=20,
        color=AM)

rows = [[u"canal", u"degrau", u"trapézio", u"curva S"],
        [u"Setpoint Acel mmps2", u"sempre 0", u"onda quadrada", u"trapézio suave"],
        [u"Setpoint Vel mmps", u"sempre 0", u"trapézio", u"sino"],
        [u"Posicao mm", u"sobe saturado", u"segue o perfil", u"segue o perfil"],
        [u"Erro mm", u"salta para 100", u"pequeno", u"pequeno"],
        [u"Tensao V", u"satura em 6 V", u"pode saturar", u"sobra margem"]]
table(s, 0.75, 3.2, 11.8, rows, [3.6, 2.6, 2.8, 2.8], size=13, rowh=0.46,
      mono_body=False)

note(s, 0.75, 6.02, 11.8,
     u"No degrau, velocidade e aceleração desejadas valem zero: não existe "
     u"plano nenhum. É isso que o gráfico vazio está dizendo.", color=AM)
nota(u"Abrir o grafico no Glass ou Shuffleboard ANTES de apertar qualquer\n"
     u"botao, com os tres canais de setpoint ja na tela.\n"
     u"A linha do Erro mm no degrau e a mais didatica: ela salta para o valor\n"
     u"do alvo inteiro no primeiro ciclo. Nos perfis, o erro fica pequeno o\n"
     u"tempo todo porque o setpoint caminha junto com o eixo.\n"
     u"Tensao sobrando na curva S e um bonus de seguranca: menos saturacao,\n"
     u"menos corrente de pico.")

# ----------------------------------------------------- plano B
s = new_slide(u"Plano B", u"Jerk limitado sem classe nova, com SlewRateLimiter")
code(s, 0.75, 2.2, 11.8, 2.05,
     u"""// Limita o JERK da aceleracao e integra para achar a velocidade.
private final SlewRateLimiter limiteDeJerk = new SlewRateLimiter(kMaxJerk);
private double velocidadeSuave = 0.0;

// a cada ciclo de 20 ms:
double aceleracaoBruta = (velocidadeDoTrapezio - velocidadeSuave) / kDt;
double aceleracaoSuave = limiteDeJerk.calculate(aceleracaoBruta);
velocidadeSuave += aceleracaoSuave * kDt;""",
     title=u"edu.wpi.first.wpilibj.SlewRateLimiter", size=12.5)

card(s, 0.75, 4.5, 5.75, 1.5, u"A favor",
     u"Cinco linhas, nenhuma classe nova, e funciona em teleop com joystick — "
     u"onde não existe perfil para calcular.", accent=GR, hsize=15, bsize=13)
card(s, 6.8, 4.5, 5.75, 1.5, u"Contra",
     u"O filtro atrasa. A distância de frenagem do trapézio fica curta e "
     u"aparece um overshoot pequeno, que o PID limpa.", accent=PK, hsize=15,
     bsize=13)

note(s, 0.75, 6.25, 11.8,
     u"Ponto-a-ponto, o quíntico é melhor: chega sem overshoot e com o tempo "
     u"conhecido antes de o motor girar.", color=AM)
nota(u"Vale mostrar porque e a resposta certa para teleop: com joystick nao ha\n"
     u"alvo, logo nao ha perfil - e o limitador em serie e a unica opcao.\n"
     u"Explicar o overshoot: o trapezio calculou onde comecar a frear supondo\n"
     u"aceleracao instantanea. O filtro atrasa a frenagem, entao o eixo passa\n"
     u"um pouco do alvo.\n"
     u"Cuidado com um erro comum: dois SlewRateLimiter em serie na VELOCIDADE\n"
     u"nao limitam jerk - limitam aceleracao duas vezes. O limitador tem que\n"
     u"agir na ACELERACAO, como no codigo.")

# ----------------------------------------------------- testes
s = new_slide(u"Testes", u"A matemática dá para conferir sem robô")
code(s, 0.75, 2.2, 11.8, 1.6,
     u"""// ./gradlew test  -  roda em 4 s, sem roboRIO e sem HAL.
for (int i = 0; i <= 2000; i++) {
    double t = profile.totalTime() * i / 2000.0;
    picoJerk = Math.max(picoJerk, Math.abs(profile.jerk(t)));
}
assertTrue(picoJerk <= kMaxJerk * 1.001);""",
     title=u"SCurveProfileTest.java", size=12.5)

rows = [[u"teste", u"o que garante"],
        [u"comecaEterminaParado", u"v = 0 E a = 0 nas duas pontas"],
        [u"respeitaOsTresLimites", u"picos de v, a e j dentro do pedido"],
        [u"quemMandaNaDuracao...", u"T sai do limite mais apertado"],
        [u"integralDaVelocidadeDaOCurso", u"a área sob v(t) é a distância"],
        [u"funcionaNoSentidoNegativo", u"alvo atrás do ponto de partida"],
        [u"cursoZeroNaoTravaOComando", u"T = 0 e isFinished(0) = true"]]
table(s, 0.75, 3.95, 11.8, rows, [5.2, 6.6], size=13, rowh=0.42,
      mono_body=False)
nota(u"Perfil e matematica pura: nao toca hardware, logo da para testar no\n"
     u"desktop. Bom argumento para a turma: o que da para testar sem robo,\n"
     u"teste sem robo.\n"
     u"O teste da integral da velocidade e o mais bonito: ele confere o\n"
     u"polinomio por um caminho totalmente diferente (somar v*dt) e tem que dar\n"
     u"a mesma distancia.\n"
     u"A tolerancia de 1.001 existe porque a amostragem e finita, nao porque a\n"
     u"formula erra.")

# =================================================================== FECHO
s = new_slide(u"Resumo", u"Três frases para levar")
tb(s, 0.75, 2.2, 11.8, 1.5,
   u"O WPILib limita aceleração. Quem limita o tranco é você.",
   size=26, color=WHITE, bold=True, spacing=1.2)

card(s, 0.75, 3.9, 3.75, 1.7, u"Degrau em aceleração",
     u"= jerk infinito. É o tranco, a folga batendo e a corrente de pico.",
     accent=PK, bsize=12.5)
card(s, 4.78, 3.9, 3.75, 1.7, u"Rampa em aceleração",
     u"= curva S. Um quíntico de 30 linhas resolve o caso ponto-a-ponto.",
     accent=AM, bsize=12.5)
card(s, 8.8, 3.9, 3.75, 1.7, u"Maciez custa tempo",
     u"+%.0f%% no exemplo de 300 mm. Saber o preço é parte de escolher."
     % (100.0 * (TS_PLOT / T_TRP - 1.0)), accent=GR, bsize=12.5)

tb(s, 0.75, 5.9, 11.8, 0.4,
   u"Projeto:  lab10CurvaS  ·  Decks irmãos:  Lab9-OmniDrive  ·  Lab9-MatrizRotacao-Trajetoria",
   size=12, color=DIM, font=MONO)
nota(u"Fechar voltando a pergunta da capa. Se a turma responder 'da, mas tem que\n"
     u"escrever o perfil - o WPILib so limita aceleracao', o deck cumpriu.\n"
     u"Deixar duas tarefas: (1) caracterizar ks/kv/ka e repetir a comparacao;\n"
     u"(2) baixar kMaxJerk ate o movimento ficar lento demais, e explicar por\n"
     u"que.\n"
     u"Quem quiser ir alem: perfil de 7 fases com jerk trapezoidal, que junta a\n"
     u"maciez da curva S com a fase de cruzeiro do trapezio.")

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
out = sys.argv[1] if len(sys.argv) > 1 else "Lab10-CurvaS-JerkLimitado.pptx"
prs.save(out)
print("slides:", len(_slides))
print("saved:", out)
