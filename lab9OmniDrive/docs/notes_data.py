# -*- coding: utf-8 -*-
"""Notas do apresentador — uma entrada por slide, na ordem de criacao."""

NOTES = []

# ------------------------------------------------------------------ 01 capa
NOTES.append(u"""⏱ 2 min  ·  ABERTURA

Enquanto a turma senta, deixe o robô na mesa e o projeto aberto no VS Code ao lado dos slides.

Abra assim: "Este robô tem três rodas que não apontam para onde ele anda, um código em que ninguém chama o motor diretamente, e um arquivo de 200 linhas que sabe onde o robô está sem nenhum GPS. Hoje a gente desmonta as três coisas."

PERGUNTE (levantar a mão): "Quem já escreveu código de robô que funcionou na bancada e falhou na arena?" — quase todos levantam. Use isso: a maior parte das falhas vem de estrutura, não de matemática.

CONTRATO DA AULA: ~2h40 de conteúdo com 2 intervalos. Se você tiver 90 min, corte os slides marcados [OPCIONAL] nas notas e faça a Parte 3 inteira — ela é a que menos se aprende sozinho.""")

# ------------------------------------------------------------------ 02 agenda
NOTES.append(u"""⏱ 2 min  ·  ROTEIRO

Venda as três partes como uma escada, não como três assuntos soltos:
  1) Command-Based = ONDE o código mora.
  2) OmniDrive = COMO o robô obedece.
  3) Odometria = COMO o robô sabe onde está — e é literalmente a parte 2 lida de trás para frente.

Diga em voz alta: "A matemática da parte 3 é a mesma da parte 2. Se você entender bem a 2, a 3 sai quase de graça." Isso tira o medo do bloco de odometria.

REGRA DA AULA: tudo que está no slide existe no projeto, com o mesmo nome. Peça que abram o repositório agora e deixem aberto — eles vão navegar junto.""")

# ------------------------------------------------------------------ 03 hardware
NOTES.append(u"""⏱ 3 min  ·  HARDWARE

Vá rápido nos cinco primeiros cartões — é contexto, não conteúdo. Gaste o tempo no rodapé.

O PONTO DA AULA está no aviso amarelo: ticks por volta, diâmetro da roda e raio do chassi são medidas FÍSICAS. O software não tem como descobrir sozinho. Se alguém trocar a roda por uma de outro diâmetro e não mexer no Constants, a odometria passa a mentir — e não dá erro, não trava, não avisa. Só erra.

PERGUNTE: "Qual desses três números, se estiver 5% errado, estraga mais a odometria?" (Resposta: o diâmetro — erro proporcional em toda distância. O raio só afeta o giro estimado pelas rodas, que aqui nem é usado para a pose.)

Guarde a resposta completa: ela volta no slide "Fontes de erro".""")

# ------------------------------------------------------------------ 04 mapa
NOTES.append(u"""⏱ 3 min  ·  MAPA DO PROJETO

Navegue a árvore no VS Code enquanto fala, na mesma ordem do slide. Eles precisam sair da aula sabendo abrir o arquivo certo sem procurar.

Três frases que valem a pena repetir:
 • Robot.java nunca cresce. Se ele estiver crescendo, alguma coisa está no lugar errado.
 • RobotContainer é o mapa: é lá que você liga botão a comando.
 • Odometry.java não importa nada de hardware — é por isso que existe uma pasta src/test. Essa separação é escolha de projeto, não acidente.

PERGUNTE: "Por que Odometry é uma classe separada, e não um método dentro do OmnidriveSubsystem?" Deixe responderem; a resposta (testabilidade) só fecha no slide 49, mas plantar a pergunta aqui faz o slide 49 render muito mais.""")

# ------------------------------------------------------------------ 05 divisória parte 1
NOTES.append(u"""⏱ 1 min  ·  DIVISÓRIA

Respire, troque o tom. Avise: "Os próximos 50 minutos não têm nenhuma conta. É só organização de código — e é a parte que mais salva competição."

Se a turma já conhece Command-Based, não pule a Parte 1: vá rápido até o slide 15 (addRequirements) e gaste o tempo lá. É o conceito que a maioria usa sem entender.""")

# ------------------------------------------------------------------ 06 motivação
NOTES.append(u"""⏱ 3 min  ·  MOTIVAÇÃO

Comece pela coluna da esquerda e seja honesto: "Todo mundo já escreveu isso. Eu já escrevi isso." Não transforme em deboche — eles precisam reconhecer o próprio código sem se sentir atacados.

O problema real não é o método ficar grande. É que ninguém consegue responder "quem está controlando o motor agora?". Com dois trechos escrevendo no mesmo motor, o motor obedece ao último — e o bug aparece uma vez a cada dez partidas.

PERGUNTE: "O que acontece se o autônomo não terminar e o teleop começar?" (Sem framework: os dois rodam. Com framework: o scheduler resolve.)

Feche com a palavra-chave DECLARATIVO: você descreve o robô e as regras; quem executa é o scheduler.""")

# ------------------------------------------------------------------ 07 quatro peças
NOTES.append(u"""⏱ 4 min  ·  AS QUATRO PEÇAS

Dê o paralelo que costuma funcionar: Subsystem é um recurso com dono (como um arquivo aberto em modo exclusivo); Command é uma tarefa com começo, meio e fim; Scheduler é o sistema operacional; Trigger é o atalho de teclado.

Mostre no código, um de cada vez — abra OmnidriveSubsystem.java, DriveDistanceCommand.java, Robot.java (a linha do run()) e RobotContainer.java (o JoystickButton). Quatro arquivos, quatro peças. Leva 60 segundos e vale mais que o slide.

REGRA DE OURO (leia o rodapé em voz alta): um recurso = um subsystem. Dois comandos nunca usam o mesmo subsystem ao mesmo tempo.

PERGUNTE: "Nesse projeto, quantos subsystems existem?" (Um. E isso está certo — o robô só tem a base.)""")

# ------------------------------------------------------------------ 08 loop 20 ms
NOTES.append(u"""⏱ 4 min  ·  O LOOP DE 20 ms

Este é o slide mais importante da Parte 1. Percorra as cinco caixas com o dedo, na ordem, e diga em voz alta: "isso acontece 50 vezes por segundo, para sempre, em todos os modos".

Dois detalhes que ninguém conta:
 • Os comandos rodam ANTES dos periodic(). Logo, o periodic() já enxerga o efeito do comando deste ciclo.
 • Nada pode bloquear. Um Thread.sleep(500) dentro de um execute() congela o robô inteiro por meio segundo: joystick, odometria, segurança, tudo.

PERGUNTE: "O que acontece se eu puser um while(sensor == false) dentro do execute()?" (O robô trava. O watchdog reclama. A odometria perde 25 ciclos.)

Se der tempo, mostre o aviso de loop overrun no console — eles vão encontrar isso na competição.""")

# ------------------------------------------------------------------ 09 Robot.java
NOTES.append(u"""⏱ 2 min  ·  Robot.java

Slide rápido. A única linha que importa está destacada: CommandScheduler.getInstance().run().

Diga: "Se essa linha sumir, nada do Command-Based funciona. Nenhum comando roda, nenhum periodic() é chamado, a odometria congela — e o robô não dá erro nenhum. Ele só fica parado."

Note que robotPeriodic roda em TODOS os modos, inclusive desabilitado. Guarde: é por isso que você vai poder empurrar o robô na mão e ver a pose mudar (volta no slide 11 e no checklist do slide 51).

Chame atenção para teleopPeriodic() VAZIO — e diga que é proposital. Isso incomoda quem vem de código procedural, e o slide 16 resolve.""")

# ------------------------------------------------------------------ 10 subsystem
NOTES.append(u"""⏱ 3 min  ·  SUBSYSTEM

O ponto é encapsulamento, não sintaxe. Pergunte: "Quem pode chamar motor1.set() neste projeto?" Resposta: só o próprio subsystem. Nenhum comando toca no motor — comandos chamam métodos públicos.

Mostre a lista de métodos públicos no rodapé e caracterize o estilo: VERBOS curtos e não bloqueantes. driveFieldOriented() calcula e retorna; ela não espera o robô chegar em lugar nenhum. Quem espera é o comando, ciclo a ciclo.

PERGUNTE: "Por que o construtor chama resetEncoders() e resetHeading()?" (Para o robô começar num estado conhecido. Sem isso a primeira leitura é lixo acumulado da energização anterior.)

Observação honesta: extends SubsystemBase já registra o subsystem no scheduler — é o que faz o periodic() ser chamado sozinho.""")

# ------------------------------------------------------------------ 11 periodic
NOTES.append(u"""⏱ 3 min  ·  periodic()

A pergunta que organiza o slide: o que é do subsystem e o que é do comando?
 • Subsystem: medir, estimar, publicar, proteger. Acontece sempre.
 • Comando: decidir para onde ir. Acontece enquanto aquele comportamento está ativo.

Se o periodic() mandar potência no motor, ele vira um segundo dono — e briga com o comando ativo. Essa é a regra prática.

Leia o comentário do código em voz alta ("precisa rodar TODO ciclo"): a odometria integra deltas; pular ciclos aumenta o passo de integração e piora a aproximação do arco. Isso volta no slide 44.

DEMONSTRE se o robô estiver na mesa: deixe desabilitado, empurre com a mão e mostre os números mudando no dashboard. É o momento "aaah" da aula inteira — vale os 30 segundos.""")

# ------------------------------------------------------------------ 12 ciclo de vida
NOTES.append(u"""⏱ 4 min  ·  CICLO DE VIDA DO COMMAND

Percorra o diagrama contando uma história: o comando é agendado → initialize() uma vez → execute() repete a cada 20 ms enquanto isFinished() devolver false → end() uma vez.

As duas setas de saída são o que diferencia quem entendeu de quem decorou:
 • isFinished() == true → end(false) — terminou sozinho.
 • outro comando exige o mesmo subsystem → end(true) — foi interrompido.
Em ambos os casos end() roda. É por isso que parar o motor no end() é seguro.

PERGUNTE: "Por que zerar o encoder no initialize() e não no construtor?" (O construtor roda uma vez, na inicialização do robô. O comando pode ser agendado várias vezes. Zerar no construtor mede a distância desde que o robô ligou.) Esse erro aparece todo ano.

A tabela de baixo é material de consulta — não leia linha por linha.""")

# ------------------------------------------------------------------ 13 DriveDistanceCommand
NOTES.append(u"""⏱ 3 min  ·  DriveDistanceCommand (estrutura)

Leia o código na ordem do ciclo de vida, não de cima para baixo: construtor → initialize → isFinished → end.

Destaques:
 • A direção é normalizada no construtor (Math.hypot). Por isso "Auto 1" com (1, −1) anda na diagonal com a mesma velocidade de um lado reto, e não com √2 vezes.
 • A guarda norm < 1e-6 evita divisão por zero se alguém passar (0,0).
 • addRequirements(drive) é a linha mais importante do arquivo — volta no slide 15.
 • isFinished() mede distância REAL por encoder. Não depende de tempo nem de nível de bateria. Compare com a alternativa ingênua ("ande por 2 segundos"), que muda de resultado com a bateria meia.

PERGUNTE: "Esse comando termina sozinho ou precisa ser cancelado?" (Termina sozinho — tem isFinished de verdade.)""")

# ------------------------------------------------------------------ 14 execute
NOTES.append(u"""⏱ 3 min  ·  execute(): rampa + correção

Três ideias, uma de cada vez:

1) RAMPA DE FRENAGEM — nos últimos 15 cm a velocidade cai proporcional ao que falta. Sem isso o robô passa do ponto por inércia.
2) PISO (kMinDriveSpeed) — a rampa levaria a velocidade a zero antes de chegar; abaixo de certo valor o motor não vence o atrito estático e o robô para antes do alvo. O piso resolve.
3) CORREÇÃO DE RUMO — um controlador P escrito à mão: erro de ângulo × ganho, com teto para não virar rotação.

AVISO PRÁTICO (está no aviso lateral): na primeira vez testem com kHeadingP = 0.0. Se o robô girar em vez de corrigir, o sinal está invertido — usem −0.010. Isso economiza uma tarde.

PERGUNTE: "Por que o teto (kMaxTurnOutput)?" (Um erro grande de rumo geraria uma correção que domina o movimento: o robô giraria em vez de andar.)""")

# ------------------------------------------------------------------ 15 addRequirements
NOTES.append(u"""⏱ 4 min  ·  addRequirements()

Pare aqui. Se a turma só levar um conceito da Parte 1, que seja este.

Conte a regra com o diagrama: o subsystem só tem um dono por vez. Quando B é agendado, o scheduler chama A.end(true) e passa a rodar B. Quem chega depois ganha.

O cenário do bug: você esquece addRequirements. Os dois comandos rodam juntos, os dois chamam motor.set(), e o motor obedece ao último que escreveu naquele ciclo. O robô anda "quase certo", de forma intermitente, e você perde horas procurando no lugar errado.

PERGUNTE: "O default command do drive e o SquareAutoCommand podem rodar juntos?" (Não — os dois exigem o drive. E é exatamente por isso que o joystick para de funcionar sozinho durante o autônomo, sem você escrever uma linha para isso.)

Mencione: em grupos, as exigências se somam — o grupo exige tudo que seus filhos exigem.""")

# ------------------------------------------------------------------ 16 default command
NOTES.append(u"""⏱ 3 min  ·  DEFAULT COMMAND

Aqui você fecha a pergunta que ficou aberta no slide 9: por que teleopPeriodic() está vazio.

O teleop é um comando como qualquer outro — só que é o comando padrão do subsystem. Enquanto ninguém mais exigir o drive, ele roda. Quando o autônomo termina ou é cancelado, ele reassume sozinho. Você não escreve nenhuma linha de "voltar para o controle manual".

Dois detalhes técnicos:
 • RunCommand nunca termina (isFinished sempre false) — é exatamente o que se quer num default.
 • O lambda () -> ... é lido A CADA CICLO. Por isso lê o joystick sempre atualizado. Se fosse um valor calculado uma vez, o robô ficaria preso na primeira leitura.

O MENOS óbvio e mais cobrado: o sinal negativo em −joystick.getY(). O eixo Y do joystick é positivo para BAIXO. Erro clássico de lab — vale escrever no quadro.""")

# ------------------------------------------------------------------ 17 comandos prontos
NOTES.append(u"""⏱ 2 min  ·  COMANDOS PRONTOS  [OPCIONAL se o tempo apertar]

Slide de referência. Não leia a tabela: aponte a coluna isFinished() e diga que ela é o que distingue as classes.

A mensagem é econômica: a maioria dos comportamentos não precisa de uma classe nova. InstantCommand para uma ação pontual, RunCommand para algo contínuo, WaitCommand para pausa.

Mostre o exemplo de baixo e conecte: o botão de reset do campo neste projeto é literalmente isso — um InstantCommand com uma referência de método.

NOTA DE VERSÃO (leia o aviso): este projeto é WPILib 2020. Hoje .whenPressed virou .onTrue e a geometria mudou de pacote. Avise agora para eles não se perderem lendo a documentação atual — vão achar nomes diferentes.""")

# ------------------------------------------------------------------ 18 composições
NOTES.append(u"""⏱ 3 min  ·  COMPOSIÇÕES

Os quatro cartões em 60 segundos — o que importa é a diferença entre Race e Deadline (quem decide o fim).

Gaste o tempo no bloco de decorators: você compõe comportamento sem criar classe nova.

INSISTA no withTimeout. É um ParallelRace com um WaitCommand por baixo. Diga a frase: "Todo autônomo sério tem timeout em cada etapa. Sem isso, um robô travado contra a parede fica empurrando até o fim da partida — e o resto da rotina nunca acontece."

PERGUNTE: "Se a etapa 1 trava e tem timeout de 6 s, o que acontece com as etapas 2, 3 e 4?" (Rodam normalmente. O grupo segue em frente. Esse é o ponto.)""")

# ------------------------------------------------------------------ 19 SquareAutoCommand
NOTES.append(u"""⏱ 2 min  ·  SquareAutoCommand

A sacada do slide: não há nenhuma lógica nova aqui. É DriveDistanceCommand quatro vezes, com pausas, dentro de um SequentialCommandGroup.

Diga: "Isto é o retorno do investimento da Parte 1. Você escreveu um comando bem feito e agora monta trajetos de graça."

Três detalhes:
 • O resetHeading() no início faz o rumo em que o robô foi colocado no tapete virar o 0° daquele trajeto.
 • O robô NÃO gira em nenhum momento — a frente aponta para o mesmo lado o trajeto inteiro. É o ganho concreto de ter 3 rodas omni, e é o gancho para a Parte 2.
 • pause() existe por física: a inércia e a folga da transmissão precisam assentar antes de inverter a direção.

PERGUNTE: "Como você faria um triângulo?" (Trocando os vetores de direção. Nenhuma linha nova de controle.) É o exercício 4 do slide 53.""")

# ------------------------------------------------------------------ 20 RobotContainer
NOTES.append(u"""⏱ 2 min  ·  RobotContainer

Mostre que o arquivo inteiro é declaração: o que existe, quem responde a qual botão, quais autônomos estão disponíveis.

Pontos:
 • Um único subsystem, instanciado aqui e passado por construtor a quem precisar. Nunca static, nunca singleton improvisado.
 • SendableChooser deixa escolher o autônomo no dashboard, sem recompilar. Na competição isso vale ouro — você troca a estratégia entre partidas.
 • O default "Autonomous None" é um InstantCommand vazio. Robô parado é o padrão seguro: se ninguém escolher nada, nada acontece.

PERGUNTE: "Auto 1 é (1, −1) com 0.2 m. Quanto o robô anda?" (0,2 m — a direção foi normalizada no construtor. Se não fosse, andaria 0,2·√2 ≈ 0,28 m.)""")

# ------------------------------------------------------------------ 21 constants
NOTES.append(u"""⏱ 2 min  ·  CONSTANTS  [OPCIONAL se o tempo apertar]

Rápido. Três blocos: DriveConstants (hardware), AutoConstants (trajeto), OIConstants (controle).

Dois hábitos que vale ensinar:
 • Constantes DERIVADAS são calculadas, não digitadas. kEncoderDistancePerTick sai de π·diâmetro/ticks. Se alguém trocar a roda, muda um número e o resto acompanha.
 • O comentário "CONFERIR NO HARDWARE" marca o que depende de medir o robô físico. É uma convenção simples que evita que alguém confie num número que nunca foi verificado.

DESTAQUE o rodapé: kGyroReversed vale para a odometria E para o field-oriented ao mesmo tempo. Uma flag, duas funções — assim as duas nunca divergem de convenção. Se fossem dois sinais separados, um dia alguém corrigiria um e esqueceria o outro, e o robô ficaria com o campo girado só na odometria.""")

# ------------------------------------------------------------------ 22 deadband
NOTES.append(u"""⏱ 2 min  ·  ZONA MORTA

Comece pelo sintoma, não pela solução: "Você solta o joystick e o robô anda sozinho devagar. Por quê?" — o eixo parado lê 0.01, 0.02, nunca 0.000.

Zona morta simples resolve a deriva mas cria um degrau: ao cruzar os 5%, a saída pula de 0 para 0,05 de uma vez. O piloto sente como um solavanco.

O reescalonamento estica o que sobrou de volta para 0…1 — é o que o gráfico mostra: sem degrau, sem deriva, e o alcance total preservado.

Math.copySign(deadband, value) é o truque que faz funcionar igual nos dois sentidos. Vale mostrar o que aconteceria sem ele (assimetria entre frente e ré).

PERGUNTE: "5% é muito ou pouco?" (Depende do joystick. Se o robô ainda derivar, aumente; se o controle ficar insensível, diminua. É calibração, não dogma.)""")

# ------------------------------------------------------------------ 23 fecha parte 1
NOTES.append(u"""⏱ 2 min  ·  CHECKLIST DA PARTE 1

Não leia os seis. Passe o olho e peça que a turma responda em voz alta "sim" ou "não" para cada um pensando no próprio código da equipe.

É aqui que você mede se a Parte 1 pegou. Se alguém hesitar no item "Todo comando que move declara?", volte 8 slides e refaça o diagrama do addRequirements — vale mais do que seguir em frente.

SUGESTÃO: este é um bom ponto de intervalo de 10 minutos. A Parte 2 começa com geometria e pede a turma descansada.""")

# ------------------------------------------------------------------ 24 divisória parte 2
NOTES.append(u"""⏱ 1 min  ·  DIVISÓRIA

Troque o tom de novo: "Agora tem conta. Não é conta difícil — é projeção de vetor, que vocês já fizeram em física — mas precisa de papel."

Peça que peguem papel e caneta AGORA. Os slides 28 e 29 funcionam muito melhor se eles refizerem as contas junto, em vez de olharem.""")

# ------------------------------------------------------------------ 25 holonômico
NOTES.append(u"""⏱ 3 min  ·  HOLONÔMICO

Demonstre em vez de explicar. Pegue qualquer objeto em cima da mesa:
 • Base diferencial (um carrinho): para ir para o lado, tem que girar, andar e desgirar.
 • Base holonômica: vai para o lado direto, sem girar.

A frase precisa: 3 graus de liberdade no plano (x, y, θ). A diferença é quantos deles você consegue ATUAR de forma independente. Diferencial: 2. Omni com 3 rodas: 3.

Conecte com o que eles já viram: é por isso que o SquareAutoCommand faz um quadrado sem nunca girar.

SEJA HONESTO sobre o preço: rolos passivos escorregam. A odometria por roda numa base omni é menos confiável que numa diferencial. Esse é o motivo de a Parte 3 usar a IMU para o ângulo — plante aqui, colha no slide 42.""")

# ------------------------------------------------------------------ 26 roda omni
NOTES.append(u"""⏱ 3 min  ·  A RODA OMNI

Se tiver uma roda omni solta, passe pela turma. Vale mais que o slide inteiro. Peça que empurrem no eixo de tração e depois de lado.

A ideia: a roda só tem força numa direção — a do próprio giro. Na perpendicular, os rolos giram e ela escorrega sem resistir.

As duas consequências do slide são as que importam:
 • Para o código: cada roda contribui apenas com a COMPONENTE do movimento do chassi projetada na sua direção de tração. Essa frase é literalmente a cinemática inversa do próximo slide — anuncie isso.
 • Para a odometria: o escorregamento dos rolos é ruído. Distância sai razoável, ângulo sai ruim.

PERGUNTE: "Se as três rodas travassem os rolos, o que aconteceria?" (O robô não conseguiria andar — as três direções de tração brigariam entre si. O deslizamento livre é o que torna a base possível.)""")

# ------------------------------------------------------------------ 27 geometria
NOTES.append(u"""⏱ 4 min  ·  GEOMETRIA DO CHASSI

Use o diagrama fisicamente: aponte cada roda no slide E no robô real, dizendo o número. Roda 1 traseira, roda 2 frontal direita, roda 3 frontal esquerda. Essa ordem é a ordem dos vetores em TODO o código — se eles trocarem, nada funciona.

Dois fatos geométricos que sustentam a conta do próximo slide:
 • As três direções de tração, separadas por 120°, cobrem o plano inteiro. Qualquer vetor de velocidade pode ser decomposto nelas.
 • Como as três rodas estão no mesmo raio R, um giro puro faz as três andarem o MESMO arco R·Δθ, no mesmo sentido. Guarde isso — é a chave da cinemática direta no slide 40.

As setas brancas são o sentido positivo de cada roda. Diga: "Mudar a montagem física ou um setInverted() muda o sinal das equações. A matemática não sabe como vocês parafusaram o robô."

PERGUNTE: "Por que a roda traseira é a que empurra o robô de lado?" (Porque ela é tangente ao círculo, e no ponto traseiro a tangente é lateral.) Essa resposta é a base do slide 37 — não pule.""")

# ------------------------------------------------------------------ 28 cinemática inversa
NOTES.append(u"""⏱ 5 min  ·  CINEMÁTICA INVERSA

NÃO apresente as equações como fórmula pronta. Derive uma delas no quadro com a turma e deixe as outras duas por analogia.

Roteiro da derivação (roda traseira, a mais fácil):
 1) A roda só tem força na direção de tração dela.
 2) Logo, o que ela precisa girar é a projeção do movimento do chassi nessa direção — produto escalar.
 3) Some o arco do giro: numa rotação pura de Δθ, toda roda a raio R percorre R·Δθ.
 4) Pronto: v1 = −vx + ω.

Faça questão de dizer que o sistema é LINEAR. Por isso as mesmas três equações valem para porcentagem, para m/s e para metros percorridos. É isso que permite reusá-las na odometria — anuncie: "Daqui a 12 slides vamos ler essas mesmas três linhas de trás para frente."

O ω aparece igual nas três justamente porque o raio é o mesmo para todas.""")

# ------------------------------------------------------------------ 29 conferindo
NOTES.append(u"""⏱ 4 min  ·  CONFERINDO NA MÃO

Trabalhe a tabela linha a linha, com a turma fazendo a conta. Não entregue as respostas de imediato — tape a coluna e pergunte.

 • Translação no eixo 1: a roda 1 vai contra, as outras duas a meia força. Pergunte por que "meia": é o cos(60°).
 • Translação no eixo 2: a roda 1 não faz nada (só desliza) e as outras duas vão em sentidos opostos. É a rotação pura do par.
 • GIRO PURO é o caso mais útil da aula. As três recebem o mesmo valor. Leia o aviso em voz alta: se as três recebem o mesmo valor e o robô TRANSLADA em vez de girar, tem roda montada ou invertida errada. Esse é o primeiro teste de bancada do checklist do slide 51.
 • Diagonal: |v2| = 1.37 > 1. Deixe o desconforto no ar e emende direto no próximo slide.

PERGUNTE antes de virar: "O que o motor faz quando você manda 1.37?" (Satura em 1. E a direção do movimento muda — é o problema que a normalização resolve.)""")

# ------------------------------------------------------------------ 30 normalização
NOTES.append(u"""⏱ 3 min  ·  NORMALIZAÇÃO

Use o gráfico: o vetor rosa é o pedido, o verde é o enviado. Mesma direção, módulo menor.

A ideia inteira cabe numa frase: divida as TRÊS pelo mesmo fator. O vetor encurta, mas aponta exatamente para o mesmo lado.

Mostre o contraexemplo, porque é o erro que as pessoas cometem: cortar individualmente (clamp em cada roda). Só a roda que estourou seria reduzida; as outras ficariam no valor original e o robô sairia torto. Vale desenhar os dois vetores no quadro lado a lado.

PERGUNTE: "Por que o robô fica mais lento na diagonal do que na reta?" (Porque a diagonal exige mais de alguma roda, e a normalização reduz tudo. Não é bug — é limite físico. Se eles tentarem "corrigir" isso, vão quebrar a direção.)""")

# ------------------------------------------------------------------ 31 malha fechada
NOTES.append(u"""⏱ 3 min  ·  MALHA FECHADA

Percorra o pipeline do slide com o dedo — ele resume a Parte 2 inteira. Diga: "Cada etapa é independente. Dá para depurar uma de cada vez."

O contraste que vende a ideia: malha aberta manda % direto no motor. Bateria cheia o robô voa; bateria meia ele arrasta. Mesma rotina, resultados diferentes. Malha fechada mede o RPM real e corrige — o robô anda igual nas duas situações.

Um PID por roda, porque cada roda tem seu próprio atrito, sua própria carga e às vezes um motor um pouco diferente.

ARMADILHA (leia o aviso): a assinatura antiga do WPILib é pid.calculate(medida, setpoint) — a MEDIDA vem primeiro. Trocar a ordem inverte o sinal do erro e o robô foge do alvo em vez de convergir. Vale escrever no quadro.""")

# ------------------------------------------------------------------ 32 PID
NOTES.append(u"""⏱ 3 min  ·  PID POR RODA

Não dê aula de teoria de controle — dê o modelo mental de cada termo:
 • P reage ao erro de AGORA. Alto demais: oscila, a roda "chia". Baixo demais: nunca chega.
 • I acumula erro pequeno e persistente — é o que vence o atrito que o P sozinho não vence. Alto demais: sobressinal e windup.
 • D reage à VARIAÇÃO do erro. Aqui está desligado de propósito: encoder quantizado gera ruído, e o D amplifica ruído.

Faça a conta do P em voz alta: erro de 100 RPM × 0.005 = saída 0.5. Isso aterra o número para eles.

O RODAPÉ é o detalhe de engenharia que vale a aula: stop() chama pid.reset(). Sem isso, o termo I guarda o erro acumulado da manobra anterior e o próximo comando começa com um empurrão fantasma. Pergunte: "Em que momento do SquareAutoCommand isso apareceria?" (Na troca de lado, depois do pause.)""")

# ------------------------------------------------------------------ 33 slew rate
NOTES.append(u"""⏱ 2 min  ·  SlewRateLimiter

Leia o gráfico: rosa é o degrau que o piloto pede; verde é o que o robô recebe.

A unidade é variação máxima por segundo. 2.0 significa ir de 0 a 1 em meio segundo. Aterre assim.

Por que importa: degrau de potência faz a roda patinar. Patinar estraga duas coisas ao mesmo tempo — a tração e a odometria, porque o encoder conta giro que não virou deslocamento. Conecte explicitamente com o slide 50.

O giro usa 3.0 (mais rápido) porque girar envolve menos inércia do que arrancar o robô inteiro.

Note a ordem: a rampa é aplicada ANTES da rotação campo→robô e antes da cinemática. Volte ao pipeline do slide 31 se alguém se perder.""")

# ------------------------------------------------------------------ 34 field oriented
NOTES.append(u"""⏱ 4 min  ·  FIELD-ORIENTED

Faça a demonstração corporal: fique de frente para a turma, aponte "para frente". Agora gire 90° e aponte "para frente" de novo. Pergunte: "Para frente de quem?"

 • Robot-oriented: frente é do ROBÔ. Se ele girou, o piloto se perde — especialmente quando o robô vem na direção dele.
 • Field-oriented: frente é do CAMPO. O joystick aponta para o fundo da arena, não importa como o robô está girado.

A matemática é uma rotação de −θ: leva o vetor de comando do referencial do campo para o do robô, antes da cinemática. Mostre o diagrama e as duas linhas da fórmula.

PONTO DE PROJETO: θ vem da IMU — a MESMA fonte que a odometria usa. Isso é de propósito, para as duas nunca divergirem de convenção. Volte a mencionar o kGyroReversed do slide 21.""")

# ------------------------------------------------------------------ 35 field oriented código
NOTES.append(u"""⏱ 3 min  ·  FIELD-ORIENTED NO CÓDIGO

Mostre que são literalmente seis linhas. A experiência do piloto muda completamente por causa disso.

Ordem das etapas (repita, é a terceira vez e é de propósito): rampa → rotação → cinemática → normalização → PID.

Detalhe que costuma gerar pergunta: ω NÃO é girado. Velocidade angular é a mesma nos dois referenciais — girar o referencial não muda a taxa de giro.

getRotation2d() é a ÚNICA porta de entrada do ângulo no código. Aplica kGyroReversed e entrega na convenção da WPILib (anti-horário positivo). Toda vez que alguém precisar do rumo, chama essa função — nunca ahrs.getAngle() direto. Essa disciplina é o que evita que metade do código use uma convenção e a outra metade outra.

TESTE DE BANCADA (aviso lateral): levante o robô, gire 90° na mão, empurre o joystick para frente. As rodas devem tentar levar o robô na mesma direção do campo de antes. Se der 90° errado, inverta kGyroReversed.""")

# ------------------------------------------------------------------ 36 resetHeading
NOTES.append(u"""⏱ 2 min  ·  resetHeading()

O que o piloto sente: aperta o gatilho e o "para frente" do joystick passa a ser a direção em que o robô está agora. É o botão de se reorientar quando se perdeu.

O que o código faz: zera a IMU E zera a pose, juntas.

INSISTA no "juntas", porque é a parte não óbvia. O eixo de referência girou. Se você mantivesse o x,y antigo com um θ novo, a posição estaria num referencial e o ângulo em outro — a pose viraria lixo silenciosamente, sem erro nenhum.

Diferencie de resetPose() (slide 46): resetHeading joga tudo para a origem; resetPose declara uma pose conhecida SEM mexer na IMU.

Conecte: é a primeira linha do SquareAutoCommand. O rumo em que o robô foi colocado no tapete vira o 0° daquele trajeto.""")

# ------------------------------------------------------------------ 37 cuidado eixos
NOTES.append(u"""⏱ 4 min  ·  CONVENÇÃO DE EIXOS

ATENÇÃO: este slide é sobre o projeto REAL, não um exercício hipotético. Trate com seriedade.

O fato: o Javadoc de Odometry.forwardKinematics chama a primeira saída de "para frente"; o de DriveDistanceCommand chama dirX de "componente lateral". São nomes diferentes para a mesma posição do vetor.

Quem decide quem está certo é a montagem física. Pela descrição em Constants (motor 1 = traseira) e pela geometria do slide 27, a roda traseira empurra o robô de LADO — e é ela que aparece sozinha em v1 = −vx. Logo vx é o eixo lateral.

Consequência prática: a pose publicada pode estar girada 90° em relação ao que o nome sugere. O quadrado continua fechando; ele só é percorrido girado. É um bug de NOME, não de fórmula.

DEIXE CLARO: nenhuma equação aqui está errada. O que precisa casar é nome × montagem física. E deixe o método: empurre o robô 1 m para a frente, desabilitado, e veja qual número do dashboard se move. 30 segundos resolvem.

Se a turma for fazer o lab hoje, este é o primeiro item do roteiro deles.""")

# ------------------------------------------------------------------ 38 divisória parte 3
NOTES.append(u"""⏱ 1 min  ·  DIVISÓRIA

Prepare o terreno: "A Parte 2 respondeu 'como faço o robô andar'. A Parte 3 responde 'onde ele está'. E a resposta é: lendo as mesmas três equações ao contrário."

Se o tempo estiver curto, esta é a parte que NÃO se corta. Command-Based e cinemática inversa se aprendem lendo tutorial; odometria bem feita, não.

SUGESTÃO: segundo intervalo de 10 min antes de começar, se a Parte 2 foi longa.""")

# ------------------------------------------------------------------ 39 pose
NOTES.append(u"""⏱ 3 min  ·  O QUE É POSE

Três números que respondem "onde eu estou": x, y, θ — sempre no referencial do CAMPO, nunca do robô.

Para que serve, concretamente: autônomo que vai até um ponto, retorno a uma posição conhecida, correção por visão, gráfico da trajetória no dashboard para depurar.

De onde vem: encoders (quanto cada roda andou) + IMU (para onde aponta). Sem GPS, sem tempo, sem mágica.

Convenção da WPILib: x para a frente do campo, y para a ESQUERDA, θ anti-horário positivo. Ângulos sempre em Rotation2d, nunca em double solto — Rotation2d cuida do wrap de 360° sozinho, e esse é um bug clássico que vocês não vão ter.

SEJA HONESTO sobre a limitação: é estimativa ACUMULADA. O erro só cresce, nunca diminui sozinho. Boa por metros e segundos; não por uma partida inteira sem correção externa. Isso prepara o slide 50.""")

# ------------------------------------------------------------------ 40 cinemática direta
NOTES.append(u"""⏱ 6 min  ·  CINEMÁTICA DIRETA  ·  slide mais importante da Parte 3

Faça no quadro, com a turma. Vale mais que qualquer outra coisa nesta aula.

Partimos das mesmas três equações do slide 28, agora em METROS percorridos (o sistema é linear — relembre).

A MÁGICA: some as três equações.
 • Os coeficientes de dx: −1 + 0.5 + 0.5 = 0. Cancela.
 • Os de dy: 0 + k − k = 0. Cancela.
 • Sobra: d1 + d2 + d3 = 3·R·dθ.
Ou seja: a soma das três rodas só carrega informação de ROTAÇÃO. Pare, deixe isso assentar, e pergunte por que faz sentido fisicamente. (Resposta: num giro puro as três andam o mesmo arco — é exatamente o que o slide 27 estabeleceu.)

Com o arco isolado, dx e dy saem por substituição direta. Mostre as três linhas do resultado.

Repare na assinatura: forwardKinematics é STATIC. Sem hardware, sem estado, sem relógio. É essa decisão que permite os testes do slide 49 — anuncie.""")

# ------------------------------------------------------------------ 41 tick ao metro
NOTES.append(u"""⏱ 3 min  ·  DO TICK AO METRO

Conta simples, consequências grandes: metros por tick = π·diâmetro / ticks por volta. Aqui dá 0,21 mm.

Diga: "A resolução não é o gargalo. 0,21 mm é muito melhor do que a precisão com que o robô realmente anda. O gargalo é o escorregamento."

O que É o gargalo: o diâmetro. Erro de 5% no diâmetro = erro de 5% em TODA distância medida, sempre no mesmo sentido, acumulando. Não é ruído, é viés.

Note que o Titan já devolve metros, porque recebeu o fator na criação do encoder. Nenhuma conversão espalhada pelo código.

CALIBRAÇÃO PRÁTICA (rodapé): marque 2,00 m no tapete, mande o robô andar 2,00 m, meça o real. Andou 1,90? Multiplique kWheelDiameterMeters por 2,00/1,90. Mande fazer isso hoje, com fita métrica — é a melhoria de maior retorno por minuto investido.

Dica extra: meça o diâmetro com o robô NO CHÃO, com a roda achatada pelo peso. O diâmetro livre é maior que o efetivo.""")

# ------------------------------------------------------------------ 42 rodas vs IMU
NOTES.append(u"""⏱ 4 min  ·  θ DAS RODAS × θ DA IMU

Apresente como DECISÃO DE PROJETO, não como detalhe de implementação. É a escolha mais importante desta odometria.

Pelas rodas: dθ = arco / R. Funciona perfeitamente na teoria. Na prática, rolos passivos escorregam e R nunca é exato.

Por que o erro de ÂNGULO é mais grave que o de distância: ele é integrado. Um ângulo errado contamina x e y em todos os ciclos seguintes, e o erro cresce com a distância percorrida depois do desvio. Um erro de distância fica local; um erro de ângulo se espalha.

Pela IMU: deriva lenta (graus por minuto), independente de contato com o chão, sem acoplamento com escorregamento.

A DECISÃO: dx e dy vêm das rodas; θ vem da IMU. Escreva no quadro.

Detalhe fino: forwardKinematics ainda devolve dθ, mas o updateWithTime o ignora para a pose. Ele só serve para estimar velocidade angular. Vale mencionar para quem for ler o código depois e estranhar.""")

# ------------------------------------------------------------------ 43 ciclo odometria
NOTES.append(u"""⏱ 3 min  ·  O CICLO DA ODOMETRIA

Percorra o pipeline da esquerda para a direita, com o dedo, uma caixa por vez. Depois mostre a IMU entrando por baixo e a pose anterior realimentando.

Três ideias por trás:
 1) DELTA, não absoluto. A odometria guarda a leitura anterior de cada encoder e trabalha com a diferença. Assim ela nunca depende do zero do encoder — e é isso que torna o resetWheelBaseline do slide 47 possível.
 2) RUMO ABSOLUTO: θ = IMU + gyroOffset; dθ = θ − θ_anterior.
 3) INTEGRAÇÃO: a pose nova sai da pose anterior mais o deslocamento do ciclo. Nada é recalculado do zero.

PERGUNTE: "Isso roda onde?" (No periodic() do subsystem — slide 11. Se alguém não lembrar, volte: é o encadeamento mais importante entre as Partes 1 e 3.)""")

# ------------------------------------------------------------------ 44 exp
NOTES.append(u"""⏱ 4 min  ·  pose.exp()

Desenhe no quadro antes de mostrar o slide: um robô que translada E gira ao mesmo tempo durante 20 ms não anda em linha reta. Anda num arco.

Somar o deslocamento como reta corta o canto. Por ciclo o erro é minúsculo; 50 vezes por segundo, numa curva longa, não é.

Twist2d(dx, dy, dθ) descreve esse movimento no referencial do próprio robô: "ande tanto para frente, tanto para o lado, girando tanto". pose.exp(twist) aplica o mapa exponencial e integra o arco de verdade.

TRANQUILIZE a turma: ninguém precisa derivar o mapa exponencial hoje. O que precisa saber é QUANDO importa (translação + rotação simultâneas) e que a WPILib já resolveu.

Detalhe elegante: quando dθ ≈ 0, exp() degenera na soma simples. Você não paga nada em linha reta e ganha precisão nas curvas.

PERGUNTE: "No SquareAutoCommand, em que momento o exp() faz diferença?" (Quase nenhum — o robô não gira. Mas na correção de rumo do execute(), que gira um pouquinho enquanto anda, faz.)""")

# ------------------------------------------------------------------ 45 updateWithTime
NOTES.append(u"""⏱ 5 min  ·  updateWithTime() LINHA POR LINHA

Leia o método seguindo os números dos comentários. Vá devagar — é o coração do arquivo.

1) Delta de cada roda.
2) Memoriza a leitura para o próximo ciclo.
3) forwardKinematics converte rodas → chassi.
4) Rumo absoluto da IMU + offset; dθ é a diferença para o ciclo anterior.
5) exp() integra o arco.

O PASSO 5 É A CHAVE e quase sempre passa batido: a TRANSLAÇÃO vem do exp(), mas θ é SOBRESCRITO pela IMU. Mostre a linha:
    pose = new Pose2d(newPose.getTranslation(), angle);
Pergunte: "Por que não usar o ângulo que o exp() devolveu?" (Porque esse veio das rodas — e nós decidimos no slide 42 que não confiamos nelas para ângulo.) Se eles responderem essa, a Parte 3 pegou.

previousAngle só existe para extrair o dθ do ciclo.

E repare: updateWithTime recebe o tempo POR FORA. É o que permite testar no PC, sem o relógio da FPGA. Decisão de projeto, não capricho — fecha no slide 49.""")

# ------------------------------------------------------------------ 46 resetPose
NOTES.append(u"""⏱ 3 min  ·  resetPose()

Cenário: início do autônomo. Você sabe onde colocou o robô na arena — quer DECLARAR isso, não zerar nada.

O truque é o gyroOffset: em vez de zerar o giroscópio, guarda-se a diferença entre o rumo que você declarou e o rumo cru da IMU. Daí em diante, θ_pose = θ_IMU + gyroOffset.

Por que não zerar a IMU: zerar perde a referência acumulada e, em alguns giroscópios, leva tempo e pode pegar o robô em movimento. O offset é instantâneo e exato.

Mostre o teste ao lado: resetPose declara 90°, a IMU anda 10°, a pose tem que ir para 100°. Esse teste de duas linhas é o que garante que o offset está certo.

Outros usos: correção por visão (uma tag te dá a pose verdadeira), encostar numa parede de posição conhecida, marcação no tapete.

No subsystem: resetPose(pose) repassa getRotation2d() como rumo cru, e resetOdometry() é só resetPose(new Pose2d()).""")

# ------------------------------------------------------------------ 47 resetWheelBaseline
NOTES.append(u"""⏱ 3 min  ·  resetWheelBaseline()  ·  o bug clássico

Conte como história de bug, com a turma tentando adivinhar o final.

"Os encoders leem 1,00 m. O comando chama resetEncoders(). A leitura vai para 0. No próximo ciclo a odometria calcula delta = 0 − 1,00 = −1,00 m." Pare. Pergunte: "O que o robô acha que aconteceu?" (Que pulou 1 metro para trás, instantaneamente.)

E o pior: não dá erro. A pose simplesmente fica errada a partir dali, e você só descobre quando o autônomo termina no lugar errado.

A defesa: quem zera os encoders AVISA a odometria que a nova referência é zero. O delta do próximo ciclo volta a ser o movimento real, e a pose não se move nem um milímetro no reset.

Mostre que resetEncoders() no subsystem já faz isso — as duas coisas andam juntas, por construção. E mostre o teste que trava esse comportamento para sempre.

GENERALIZE: sempre que uma leitura absoluta puder "pular", quem consome deltas precisa ser reancorado. Vale para encoder, para a IMU, para qualquer coisa.""")

# ------------------------------------------------------------------ 48 velocidade
NOTES.append(u"""⏱ 2 min  ·  VELOCIDADE E TELEMETRIA  [OPCIONAL se o tempo apertar]

O mesmo delta que virou pose vira velocidade: basta dividir pelo tempo.

Dois detalhes de implementação que valem comentário:
 • Usa o período REAL (now − lastTimestamp), não 0.02 fixo. O scheduler não é um relógio perfeito; sob carga os ciclos atrasam. Assumir 20 ms exatos introduz erro justamente quando o robô está sob mais esforço.
 • A guarda period > 1e-6 evita divisão por zero no primeiro ciclo.

A telemetria é o osciloscópio deles. Diga: "Velocidade medida é ouro para depurar — compare com o que vocês pediram. Se você mandou 0.35 e está vendo metade, ou a bateria caiu, ou está patinando, ou o PID não está fechando."

E lembre: a telemetria é publicada no periodic(), logo roda mesmo desabilitado.""")

# ------------------------------------------------------------------ 49 testes
NOTES.append(u"""⏱ 4 min  ·  TESTES SEM ROBÔ

Aqui fecha a pergunta plantada no slide 4: por que Odometry é uma classe separada sem hardware.

A ideia do teste, em uma frase: simular os encoders. A partir de um movimento conhecido do chassi, calcular quanto cada roda teria girado (cinemática INVERSA) e alimentar a odometria com isso. Se a pose estimada bate com o movimento imposto, a matemática fechou.

É um teste de ida e volta: inversa → direta → compara. Note que a função wheels() do teste é literalmente a mesma equação do driveRobotOriented — se alguém mudar uma e não a outra, o teste quebra. Isso é de propósito.

RODE AO VIVO: ./gradlew test, com o projetor ligado. Ver 11 testes verdes em 3 segundos, sem robô, sem bateria, sem cabo, convence mais que qualquer argumento.

Depois quebre um de propósito — troque o √3 por 2 — e mostre a falha. Essa demonstração costuma ser o momento em que a turma passa a escrever testes.

PERGUNTE: "O que esses testes NÃO pegam?" (Escorregamento, roda invertida no hardware, diâmetro errado. Teste valida matemática, não física.)""")

# ------------------------------------------------------------------ 50 fontes de erro
NOTES.append(u"""⏱ 3 min  ·  FONTES DE ERRO  [OPCIONAL se o tempo apertar]

A lista está em ordem de impacto — diga isso, é a informação mais útil do slide. Quando a pose estiver errada, eles devem investigar nessa ordem.

 1) ESCORREGAMENTO é o campeão. Mitigação: o SlewRateLimiter do slide 33 e velocidade moderada. Acelerar devagar literalmente melhora a odometria.
 2) DIÂMETRO: erro proporcional em toda distância. Calibração do slide 41.
 3) RAIO DO CHASSI: aqui afeta pouco, porque θ vem da IMU. Numa odometria que confiasse nas rodas, seria crítico.
 4) DERIVA DA IMU: lenta. resetHeading entre manobras ou correção por referência externa.
 5) CICLOS PERDIDOS: código bloqueante no periodic. Volte ao slide 8 se precisar.

PERGUNTE: "Qual desses o teste unitário pega?" (Nenhum. Todos são físicos. Por isso o checklist do próximo slide existe.)""")

# ------------------------------------------------------------------ 51 checklist tapete
NOTES.append(u"""⏱ 4 min  ·  CHECKLIST NO TAPETE

Este é o slide que eles vão fotografar. Avise: "Tirem foto deste. É o roteiro da próxima vez que vocês montarem um robô."

A ORDEM IMPORTA e vale explicar por quê: cada passo depende do anterior estar certo. Não adianta calibrar escala se uma roda está invertida; não adianta testar field-oriented se o sinal da IMU está trocado.

 1) Rodas no sentido certo — giro puro, as três no mesmo sentido (slide 29).
 2) IMU no sentido certo — girar anti-horário deve AUMENTAR o θ.
 3) Eixos da odometria — empurrar 1 m e ver qual número se move (slide 37).
 4) Escala da distância — 2,00 m medidos (slide 41).
 5) Field-oriented — girar 90° e empurrar o joystick (slide 35).
 6) Fechamento do quadrado — rode o Auto 2, marque a partida com fita, meça o erro de volta.

O passo 6 é a métrica final de qualidade. Peça que anotem o número hoje e tentem melhorá-lo. Vira competição saudável dentro da turma.""")

# ------------------------------------------------------------------ 52 fechando o ciclo
NOTES.append(u"""⏱ 3 min  ·  FECHANDO O CICLO

Slide de síntese. Percorra de cima para baixo, devagar, sem acrescentar informação nova.

Comando desce: joystick → deadband → rampa → rotação → cinemática inversa → normalização → PID.
Robô físico no meio: as rodas giram no tapete.
Estimativa sobe: encoders → delta → cinemática direta → Twist2d + θ da IMU → exp() → pose no campo.

A FRASE DE FECHO, que é o argumento da aula inteira: a cinemática inversa e a direta são a MESMA matemática, lida nos dois sentidos. Um lado diz ao robô o que fazer; o outro pergunta ao robô o que ele fez.

Se alguém só entendeu metade da aula, este slide costuma ser onde a outra metade encaixa. Dê 20 segundos de silêncio depois de falar.""")

# ------------------------------------------------------------------ 53 exercícios
NOTES.append(u"""⏱ 3 min  ·  EXERCÍCIOS

Não leia os seis. Aponte os níveis e diga o critério: os fáceis são para hoje, os médios para a semana, os difíceis para quem quer puxar.

Comentários rápidos:
 • Fácil 1 e 2 são de leitura e verificação — garantem que a convenção de eixos ficou clara.
 • Médio 3 (TurnToAngleCommand) junta a Parte 1 com a Parte 3: é o primeiro comando deles que usa getPose() para decidir quando parar.
 • Médio 4 (triângulo) prova que a composição funciona: se eles precisarem tocar no DriveDistanceCommand, a Parte 1 não pegou.
 • Difícil 5 (DriveToPoseCommand) é o salto conceitual: sair de deslocamento RELATIVO para posição ABSOLUTA no campo. É o que times competitivos usam.
 • Difícil 6 (simular escorregamento no teste) é o mais formativo: eles vão medir o impacto do problema nº 1 do slide 50 sem precisar do robô.

Se houver tempo de lab hoje, comece pelo checklist do slide 51 e só depois pelos exercícios.""")

# ------------------------------------------------------------------ 54 resumo
NOTES.append(u"""⏱ 3 min  ·  RESUMO

Não leia as dez. Peça que a turma leia em silêncio por 30 segundos e escolha a que foi mais surpreendente para ela. Ouça três ou quatro respostas — isso te diz o que pegou e o que não pegou, e vale como avaliação informal.

Se você só tiver fôlego para repetir três:
 • addRequirements() — a linha que impede dois donos no mesmo motor.
 • Normalize dividindo as três pelo mesmo fator, nunca corte uma só.
 • dx e dy das rodas, θ da IMU. Nunca confie no ângulo das rodas omni.

FECHE COM AÇÃO, não com resumo: "Rodem ./gradlew test hoje. Façam o checklist do tapete amanhã. Me tragam o erro de fechamento do quadrado na próxima aula."

Deixe os slides disponíveis e lembre que o PPTX está em docs/ no repositório, junto com os scripts que o geraram.""")
