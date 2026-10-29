# -*- coding: utf-8 -*-
"""Graficos cartesianos para o deck do Lab 10 (curva S).

Mesma convencao do diagrams.py do lab 9: funcoes que recebem o slide e
coordenadas em polegadas e desenham direto. A diferenca e que aqui as curvas
sao a propria mensagem do deck, entao vale ter um eixo de verdade.

As curvas usam build_freeform: cada curva e UMA forma, nao 40 segmentos.
"""
import math
from deckbase import (rect, tb, line, PANEL, PANEL2, TXT, MUTED, DIM, CY, AM,
                      GR, PK, VI, WHITE, UI, MONO)
from pptx.util import Inches, Pt
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import PP_ALIGN
from pptx.enum.dml import MSO_LINE_DASH_STYLE

EMU = 914400


def freecurve(slide, pts, color=CY, lw=2.0, dash=False):
    """Polilinha como forma unica. pts em polegadas."""
    xs = [int(round(p[0] * EMU)) for p in pts]
    ys = [int(round(p[1] * EMU)) for p in pts]
    builder = slide.shapes.build_freeform(xs[0], ys[0], scale=1.0)
    builder.add_line_segments(list(zip(xs[1:], ys[1:])), close=False)
    sh = builder.convert_to_shape()
    sh.shadow.inherit = False
    sh.fill.background()
    sh.line.color.rgb = color
    sh.line.width = Pt(lw)
    if dash:
        sh.line.dash_style = MSO_LINE_DASH_STYLE.DASH
    return sh


class Plot(object):
    """Area de plotagem com eixos em polegadas e dados em unidades reais."""

    def __init__(self, slide, x, y, w, h, tmax, ymin, ymax,
                 ylabel=None, xlabel=u"t (s)", pad_l=0.72, pad_b=0.42,
                 pad_t=0.44, pad_r=0.22, zero_line=True):
        self.s = slide
        self.tmax = float(tmax)
        self.ymin = float(ymin)
        self.ymax = float(ymax)

        rect(slide, x, y, w, h, fill=PANEL, line=PANEL2, lw=1.0, adj=0.05)

        self.x0 = x + pad_l
        self.x1 = x + w - pad_r
        self.y0 = y + h - pad_b
        self.y1 = y + pad_t

        line(slide, self.x0, self.y1, self.x0, self.y0, color=DIM, lw=1.1)
        line(slide, self.x0, self.y0, self.x1, self.y0, color=DIM, lw=1.1)

        if zero_line and self.ymin < 0.0 < self.ymax:
            yz = self.py(0.0)
            line(slide, self.x0, yz, self.x1, yz, color=DIM, lw=1.0, dash=True)

        if ylabel:
            tb(slide, x + 0.1, y + 0.06, 2.2, 0.26, ylabel,
               size=11, color=MUTED, bold=True, font=MONO)
        if xlabel:
            tb(slide, self.x1 - 1.1, self.y0 + 0.08, 1.1, 0.24, xlabel,
               size=10, color=DIM, align=PP_ALIGN.RIGHT, font=MONO)

    def px(self, t):
        return self.x0 + (self.x1 - self.x0) * (t / self.tmax)

    def py(self, v):
        span = self.ymax - self.ymin
        return self.y0 - (self.y0 - self.y1) * ((v - self.ymin) / span)

    def curve(self, fn, color=CY, lw=2.0, n=140, tmin=0.0, tmax=None, dash=False):
        tmax = self.tmax if tmax is None else tmax
        pts = []
        for i in range(n + 1):
            t = tmin + (tmax - tmin) * i / float(n)
            pts.append((self.px(t), self.py(fn(t))))
        return freecurve(self.s, pts, color=color, lw=lw, dash=dash)

    def steps(self, pts_data, color=CY, lw=2.0, dash=False):
        """Polilinha em unidades reais, para ondas quadradas."""
        pts = [(self.px(t), self.py(v)) for t, v in pts_data]
        return freecurve(self.s, pts, color=color, lw=lw, dash=dash)

    def vline(self, t, color=MUTED, lw=1.0, dash=True):
        line(self.s, self.px(t), self.y1, self.px(t), self.y0,
             color=color, lw=lw, dash=dash)

    def hline(self, v, color=MUTED, lw=1.0, dash=True):
        line(self.s, self.x0, self.py(v), self.x1, self.py(v),
             color=color, lw=lw, dash=dash)

    def at(self, t, v, text, color=TXT, size=11, bold=False, dx=0.06, dy=-0.1,
           w=1.9, align=PP_ALIGN.LEFT, font=UI):
        tb(self.s, self.px(t) + dx, self.py(v) + dy, w, 0.24, text,
           size=size, color=color, bold=bold, align=align, font=font)

    def ytick(self, v, text, color=DIM, size=9.5):
        line(self.s, self.x0 - 0.07, self.py(v), self.x0, self.py(v),
             color=DIM, lw=1.0)
        tb(self.s, self.x0 - 0.72, self.py(v) - 0.09, 0.62, 0.22, text,
           size=size, color=color, align=PP_ALIGN.RIGHT, font=MONO)

    def xtick(self, t, text, color=DIM, size=9.5):
        line(self.s, self.px(t), self.y0, self.px(t), self.y0 + 0.07,
             color=DIM, lw=1.0)
        tb(self.s, self.px(t) - 0.35, self.y0 + 0.09, 0.7, 0.22, text,
           size=size, color=color, align=PP_ALIGN.CENTER, font=MONO)

    def spike(self, t, up=True, color=PK, lw=2.0, label=None):
        """Impulso de jerk: o que o trapezio faz nas bordas."""
        xx = self.px(t)
        yz = self.py(0.0)
        yy = self.y1 + 0.06 if up else self.y0 - 0.06
        line(self.s, xx, yz, xx, yy, color=color, lw=lw)
        rect(self.s, xx - 0.09, yy - (0.0 if up else 0.18), 0.18, 0.18,
             fill=color, shape=MSO_SHAPE.ISOSCELES_TRIANGLE,
             rot=0 if up else 180)
        if label:
            tb(self.s, xx - 0.5, yy + (-0.32 if up else -0.48), 1.0, 0.26, label,
               size=12.5, color=color, bold=True, align=PP_ALIGN.CENTER)


def legend(slide, x, y, items, size=11.5, gap=0.2, swatch=0.26):
    """items = lista de (cor, texto) numa linha horizontal."""
    xx = x
    for color, text in items:
        rect(slide, xx, y + 0.055, swatch, 0.075, fill=color,
             shape=MSO_SHAPE.RECTANGLE)
        wd = 0.088 * len(text) + 0.12
        tb(slide, xx + swatch + 0.09, y, wd, 0.24, text, size=size, color=MUTED)
        xx += swatch + 0.09 + wd + gap
    return xx


def scurve_profile(d, T):
    """Quintico de jerk minimo. Devolve as funcoes (s, v, a, j) de t."""
    def u_of(t):
        return min(1.0, max(0.0, t / T))

    def s(t):
        u = u_of(t)
        return d * u ** 3 * (10.0 - 15.0 * u + 6.0 * u * u)

    def v(t):
        u = u_of(t)
        return 30.0 * d / T * u * u * (1.0 - u) ** 2

    def a(t):
        u = u_of(t)
        return 60.0 * d / (T * T) * u * (1.0 - u) * (1.0 - 2.0 * u)

    def j(t):
        if t < 0.0 or t > T:
            return 0.0
        u = u_of(t)
        return 60.0 * d / (T ** 3) * (1.0 - 6.0 * u + 6.0 * u * u)

    return s, v, a, j


def scurve_time(d, vmax, amax, jmax):
    """Duracao minima que respeita os tres limites ao mesmo tempo."""
    d = abs(d)
    return max(1.875 * d / vmax,
               math.sqrt(10.0 / math.sqrt(3.0) * d / amax),
               (60.0 * d / jmax) ** (1.0 / 3.0))


def trapezoid_profile(d, vmax, amax):
    """Trapezio classico. Devolve (s, v, a, T, vpeak, tramp, tcruise)."""
    d_ramp = vmax * vmax / (2.0 * amax)
    if d >= 2.0 * d_ramp:
        vpeak = vmax
        tramp = vmax / amax
        tcruise = (d - 2.0 * d_ramp) / vmax
    else:
        vpeak = math.sqrt(d * amax)
        tramp = vpeak / amax
        tcruise = 0.0
    T = 2.0 * tramp + tcruise
    ramp_dist = 0.5 * amax * tramp * tramp

    def v(t):
        if t <= 0.0:
            return 0.0
        if t < tramp:
            return amax * t
        if t < tramp + tcruise:
            return vpeak
        if t < T:
            return vpeak - amax * (t - (tramp + tcruise))
        return 0.0

    def a(t):
        if 0.0 < t < tramp:
            return amax
        if tramp + tcruise < t < T:
            return -amax
        return 0.0

    def s(t):
        if t <= 0.0:
            return 0.0
        if t >= T:
            return d
        if t < tramp:
            return 0.5 * amax * t * t
        if t < tramp + tcruise:
            return ramp_dist + vpeak * (t - tramp)
        td = T - t
        return d - 0.5 * amax * td * td

    return s, v, a, T, vpeak, tramp, tcruise
