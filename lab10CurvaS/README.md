# Lab 10 - Curva S (controle de movimento com jerk limitado)

Projeto independente, mesma base dos outros labs (GradleRIO 2020.3.2.30 / VMX-pi,
WPILib 2020.3.2). Um unico eixo - o atirador no **M3** do TitanQuad - executa o
mesmo deslocamento de tres formas diferentes, para a comparacao ser direta.

## O problema

O WPILib **nao tem** perfil de movimento com limite de jerk:

| Classe | Limita | Jerk |
| --- | --- | --- |
| `TrapezoidProfile` (`edu.wpi.first.wpilibj.trajectory`) | velocidade, aceleracao | **infinito** nos cantos |
| `ExponentialProfile` | tensao (modelo `kV*v + kA*a`), nao jerk | so existe a partir do WPILib 2024 |
| `SlewRateLimiter`, `LinearFilter` | taxa de variacao de um sinal | sao pecas, nao perfil |

O `ExponentialProfile` costuma ser confundido com curva S porque a velocidade
dele sobe parecido. Ele nao e um limitador de jerk: responde "qual o perfil
mais rapido respeitando 12 V". E nao esta disponivel nesta versao.

## A solucao usada aqui

`frc/robot/controllers/SCurveProfile.java` - polinomio quintico de jerk minimo,
forma fechada, sem iteracao:

```
s(u) = d * (10u^3 - 15u^4 + 6u^5),  u = t/T
```

Velocidade **e** aceleracao valem zero nas duas pontas, ou seja, o motor nao
recebe degrau nem na partida nem na chegada. Os picos saem das derivadas:

```
|v|max = 1.8750 * d / T
|a|max = 5.7735 * d / T^2
|j|max = 60.0   * d / T^3
```

O construtor inverte as tres e fica com a maior duracao `T`, a unica que
respeita todos os limites ao mesmo tempo.

**Preco:** o quintico nao tem fase de velocidade constante. A velocidade media
e 53% do pico, entao um curso longo demora ~1.9x mais que um trapezio com o
mesmo `maxVelocity`. Para mecanismo de curso curto nao se nota; para atravessar
o campo, nota.

## Como rodar

Botoes do Xbox (alvo em `AtiradorConstants.kTargetMM`, 100 mm):

| Botao | Comando | O que faz |
| --- | --- | --- |
| A | `AtiradorSCurveCmd` | curva S, jerk limitado |
| B | `AtiradorTrapezoidCmd` | `TrapezoidProfile` do WPILib |
| X | `AtiradorStepCmd` | degrau direto no PID, sem perfil (o que o lab 1 faz) |
| Y | `AtiradorSCurveCmd` | volta para a origem em curva S |

Os mesmos tres tambem aparecem no chooser do autonomo, para rodar na bancada
sem joystick.

Abra o grafico em **`Atirador/Setpoint Acel mmps2`**. E ai que a aula acontece:

- degrau: nao existe plano, a forma vem da saturacao de tensao;
- trapezio: onda quadrada (+a_max, 0, -a_max) - cada borda e um jerk infinito;
- curva S: trapezio suave, subindo e descendo em rampa.

Outros canais uteis: `Atirador/Posicao mm`, `Atirador/Setpoint Vel mmps`,
`Atirador/Erro mm`, `Atirador/Tensao V`, `Atirador/Setpoint Jerk mmps3`.

## Arquitetura

- **comando** = dono do perfil e do relogio; diz a cada ciclo qual
  posicao/velocidade/aceleracao o eixo deveria ter agora;
- **subsystem** = dono da malha; converte o pedido em volts
  (`SimpleMotorFeedforward` + `PIDController`, saida por `setVoltage`) e segura
  a posicao final no `periodic()` depois que o comando termina.

Trocar o tipo de perfil nao mexe no subsystem.

## Ajustes

- `ks`, `kv`, `ka` em `AtiradorConstants` comecam em **zero**. Assim o perfil e
  seguido apenas pelo feedback: funciona, mas o eixo fica sempre um pouco
  atrasado. O termo `ka` e o que faz o motor antecipar a aceleracao em vez de
  reagir a ela - e so com ele que a curva S rende o maximo.
- `kMaxJerkMMps3` e o parametro novo. Baixe para sentir o movimento ficar mais
  macio e mais lento; suba muito e a curva S converge para o trapezio, trancos
  inclusive.
- Ganhos do PID estao em **V/mm** porque o erro e medido em mm e a saida e
  tensao. Mantenha tudo em mm, mm/s, mm/s^2.

## Testes

```
./gradlew test
```

`SCurveProfileTest` roda sem robo e verifica pontas paradas, os tres limites
respeitados, qual restricao fica ativa, a integral da velocidade, sentido
negativo e curso zero.

## Slides

`docs/Lab10-CurvaS-JerkLimitado.pptx` — 23 slides, com notas do apresentador.
Gerado por `docs/build_curvas.py` (python-pptx), nos mesmos moldes dos decks do
lab 9: `deckbase.py` traz paleta e helpers, `plots.py` traz os gráficos
cartesianos (as curvas são freeform, uma forma por curva).

```
cd docs
python build_curvas.py
```

Todos os números dos slides — durações, picos, o +25% da comparação — são
calculados no próprio script a partir dos limites em `AtiradorConstants`, então
mudar uma constante e regerar mantém o deck coerente com o código.
