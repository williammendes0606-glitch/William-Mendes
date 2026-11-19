package com.example.data.curriculum

import com.example.model.EditalTopic
import com.example.model.FlashcardTemplate
import com.example.model.KnowledgeArea
import com.example.model.LessonContent
import com.example.model.QuestionDifficulty
import com.example.model.QuestionItem
import com.example.model.QuestionOrigin
import com.example.model.SubjectDefinition
import com.example.model.WorkedExample

object MathCurriculum {
    val subject = SubjectDefinition(
        id = "matematica",
        name = "Matemática",
        shortName = "Matemática",
        area = KnowledgeArea.MATEMATICA,
        description = "Explicada do zero até o nível de prova: porcentagem, funções, PA/PG, combinatória, probabilidade, geometria e trigonometria.",
        editalStatusNote = "Matriz curricular baseada no programa de Matemática do PAES/UEMA (Ensino Médio completo).",
        topics = listOf(
            EditalTopic(
                id = "mat_aritmetica_fin",
                subjectId = "matematica",
                title = "Razão, Proporção, Porcentagem e Matemática Financeira",
                subtopics = listOf("Regra de três simples e composta", "Aumentos e descontos sucessivos", "Juros simples e compostos"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "mat_funcoes",
                subjectId = "matematica",
                title = "Conjuntos e Funções (1º e 2º Graus, Exponencial e Logaritmo)",
                subtopics = listOf("Diagramas de Venn", "Função afim e coeficiente angular", "Função quadrática, vértice e máximo/mínimo", "Equações exponenciais e propriedades de log"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "mat_progressoes",
                subjectId = "matematica",
                title = "Sequências, PA e PG",
                subtopics = listOf("Termo geral e soma da PA", "Termo geral, soma finita e infinita da PG"),
                priorityWeight = 2
            ),
            EditalTopic(
                id = "mat_combinatoria_prob",
                subjectId = "matematica",
                title = "Análise Combinatória e Probabilidade",
                subtopics = listOf("Princípio Fundamental da Contagem", "Permutação, Arranjo e Combinação", "Probabilidade da união, interseção e condicional"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "mat_geometria",
                subjectId = "matematica",
                title = "Geometria Plana, Espacial e Analítica",
                subtopics = listOf("Semelhança, Pitágoras e áreas", "Prismas, pirâmides, cilindros, cones e esferas", "Distância entre pontos e equação da reta/circunferência"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "mat_trig_matriz",
                subjectId = "matematica",
                title = "Trigonometria, Matrizes, Sistemas e Estatística",
                subtopics = listOf("Seno, cosseno, tangente e ciclo trigonométrico", "Determinantes e sistemas lineares", "Média, mediana, moda e desvio padrão"),
                priorityWeight = 2
            )
        )
    )

    val lessons = listOf(
        LessonContent(
            id = "lesson_mat_porcentagem",
            subjectId = "matematica",
            topicId = "mat_aritmetica_fin",
            title = "Porcentagem, Regra de Três e Juros do Zero",
            estimatedMinutes = 25,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "Porcentagem significa 'dividir por 100'. Por exemplo, 25% quer dizer 25 em cada 100 (ou 25/100 = 0,25). Se você domina o FATOR DE MULTIPLICAÇÃO, resolve aumentos, descontos e juros compostos em segundos sem precisar fazer várias regras de três!",
            deepExplanation = "1) FATOR DE MULTIPLICAÇÃO (o atalho profissional):\n• Aumento de i%: multiplique por `(1 + i/100)`. Ex: aumentar 15% = multiplicar por `1,15`.\n• Desconto de i%: multiplique por `(1 - i/100)`. Ex: descontar 20% = multiplicar por `0,80`.\n• Aumentos/descontos SUCESSIVOS nunca se somam diretamente! Multiplique os fatores. Ex: aumento de 10% seguido de desconto de 10% = `1,10 × 0,90 = 0,99` -> ou seja, redução líquida de 1%!\n\n2) JUROS SIMPLES vs COMPOSTOS:\n• Juros Simples: os juros incidem sempre sobre o capital inicial (`J = C · i · t` e `M = C · (1 + i·t)`). Cresce em Progressão Aritmética (linear).\n• Juros Compostos: juros sobre juros (`M = C · (1 + i)^t`). Cresce em Progressão Geométrica (exponencial).\n\n3) REGRA DE TRÊS COMPOSTA (Método Causa × Efeito):\n• Separe o que é PROCESSO/CAUSA (trabalhadores, máquinas, dias, horas/dia) do que é PRODUTO/EFEITO (peças produzidas, metros construídos, páginas lidas).\n• Multiplique as causas entre si e iguale à proporção do efeito, mantendo o produto cruzado apenas na fronteira Causa × Efeito!",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — Desconto e Aumento Sucessivos (Clássico de Prova)",
                    problem = "Um curso custava R$ 1.000,00. Teve um aumento de 20% e, no mês seguinte, um desconto de 20%. Qual o preço final?",
                    stepByStep = listOf(
                        "1. Cuidado! Não volta para R$ 1.000,00 porque o 2º percentual incide sobre o valor já aumentado.",
                        "2. Fator de aumento de 20%: `1 + 0,20 = 1,20`.",
                        "3. Fator de desconto de 20%: `1 - 0,20 = 0,80`.",
                        "4. Multiplique tudo: `1000 × 1,20 × 0,80 = 1000 × 0,96 = 960`."
                    ),
                    answer = "Preço final: R$ 960,00 (queda líquida de 4%)."
                ),
                WorkedExample(
                    title = "Exemplo 2 — Encontrando o Valor Original Antes do Desconto",
                    problem = "Após 30% de desconto, um tênis de corrida saiu por R$ 280,00. Quanto ele custava originalmente?",
                    stepByStep = listOf(
                        "1. Se houve 30% de desconto, quem pagou pagou `70%` (`0,70`) do preço original P.",
                        "2. Monte a equação direta: `0,70 · P = 280`.",
                        "3. Divida: `P = 280 / 0,70 = 2800 / 7 = 400`."
                    ),
                    answer = "Preço original: R$ 400,00."
                ),
                WorkedExample(
                    title = "Exemplo 3 — Juros Compostos Passo a Passo",
                    problem = "Um capital de R$ 2.000,00 foi aplicado a 10% ao mês em juros compostos por 2 meses. Qual o montante?",
                    stepByStep = listOf(
                        "1. Fórmula: `M = C · (1 + i)^t`.",
                        "2. Substitua: `C = 2000`, `i = 0,10`, `t = 2`.",
                        "3. `M = 2000 · (1,10)^2 = 2000 · 1,21 = 2420`."
                    ),
                    answer = "Montante final: R$ 2.420,00 (juros de R$ 420,00)."
                )
            ),
            examTricks = listOf(
                "Para calcular 10% de cabeça, volte a vírgula 1 casa para a esquerda. Para 1%, volte 2 casas. Ex: 15% de 240 = 10% (24) + metade (12) = 36!",
                "Nunca some porcentagens sucessivas! Aumento de +a% e desconto de -a% SEMPRE gera perda igual a `(a² / 100)%`.",
                "Em regra de três: se duas grandezas são inversamente proporcionais (mais velocidade -> menos tempo), inverta a fração antes de multiplicar."
            ),
            memoryTechniques = listOf(
                "Juros Simples = 'J = C·i·t' (Juro = Cita). Juros Compostos = 'M = C·(1+i)^t' (Montante com expoente no tempo).",
                "Fator: +20% = 1,20; -20% = 0,80 (pense em R$ 1,00 ganhando ou perdendo centavos)."
            ),
            structuredSummary = listOf(
                "Fator de aumento: `1 + i/100`; fator de desconto: `1 - i/100`.",
                "Variações sucessivas multiplicam os fatores entre si.",
                "Para descobrir o valor anterior ao desconto/aumento, DIVIDA pelo fator.",
                "Juros compostos acumulam exponencialmente: `M = C · (1 + i)^t`."
            ),
            practiceQuestionIds = listOf("q_mat_01", "q_mat_02", "q_mat_03"),
            finalExamQuestionIds = listOf("q_mat_04", "q_mat_05")
        ),
        LessonContent(
            id = "lesson_mat_funcoes",
            subjectId = "matematica",
            topicId = "mat_funcoes",
            title = "Funções do 1º e 2º Graus (Gráfico, Raízes e Vértice)",
            estimatedMinutes = 30,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "Uma função é uma regra que transforma um valor de entrada `x` em um único valor de saída `y = f(x)`. Na Função Afim (1º grau), o gráfico é uma RETA (`f(x) = ax + b`). Na Função Quadrática (2º grau), o gráfico é uma PARÁBOLA (`f(x) = ax² + bx + c`).",
            deepExplanation = "1) FUNÇÃO AFIM `f(x) = ax + b`:\n• `a` é o coeficiente angular (taxa de variação/inclinação): se `a > 0` a reta sobe (crescente); se `a < 0` desce (decrescente). Calculamos `a = (y₂ - y₁) / (x₂ - x₁)`.\n• `b` é o coeficiente linear: é exatamente o ponto onde a reta corta o eixo Y `(0, b)`.\n• Raiz (zero da função): valor de `x` onde `y = 0`, ou seja, `x = -b / a`.\n\n2) FUNÇÃO QUADRÁTICA `f(x) = ax² + bx + c`:\n• Concavidade: se `a > 0`, parábola sorri (∪) e tem ponto de MÍNIMO; se `a < 0`, parábola fica triste (∩) e tem ponto de MÁXIMO.\n• `c` é onde a curva corta o eixo Y `(0, c)`.\n• Discriminante `Δ = b² - 4ac`: `Δ > 0` (2 raízes reais), `Δ = 0` (1 raiz real / tangencia eixo X), `Δ < 0` (não corta eixo X).\n• Vértice `(Xv, Yv)` — o campeão de questões contextualizadas da UEMA:\n  `Xv = -b / (2a)` (ou a média das duas raízes: `(x₁ + x₂) / 2`!)\n  `Yv = -Δ / (4a)` (ou simplesmente substitua `Xv` na função: `f(Xv)`!).",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — Encontrando a Lei da Reta pelo Gráfico",
                    problem = "Uma reta passa pelos pontos `(0, 4)` e `(2, 10)`. Qual a função `f(x)`?",
                    stepByStep = listOf(
                        "1. O ponto `(0, 4)` corta o eixo Y, portanto já sabemos na hora: `b = 4`.",
                        "2. Calcule a inclinação `a = (10 - 4) / (2 - 0) = 6 / 2 = 3`.",
                        "3. Monte a função: `f(x) = 3x + 4`."
                    ),
                    answer = "`f(x) = 3x + 4`."
                ),
                WorkedExample(
                    title = "Exemplo 2 — Lucro Máximo com Vértice",
                    problem = "O rendimento diário `R(x) = -x² + 12x - 20` depende da quantidade `x` de horas de treino intenso. Qual o rendimento máximo e com quantas horas ocorre?",
                    stepByStep = listOf(
                        "1. Como `a = -1 < 0`, a parábola tem concavidade para baixo -> existe MÁXIMO.",
                        "2. Calcule o `Xv` (horas para máximo): `Xv = -b / (2a) = -12 / (2 · (-1)) = 6` horas.",
                        "3. Substitua `x = 6` na fórmula (mais rápido que decorar `-Δ/4a`!): `R(6) = -(6)² + 12(6) - 20 = -36 + 72 - 20 = 16`."
                    ),
                    answer = "Rendimento máximo de 16 pontos com `x = 6` horas."
                )
            ),
            examTricks = listOf(
                "Não perca tempo calculando `-Δ / (4a)` se você já achou `Xv`: basta substituir `Xv` na equação para achar `Yv`!",
                "Soma e Produto para achar raízes de `x² + bx + c = 0` de cabeça: Soma `S = -b/a` e Produto `P = c/a`.",
                "Em questões de 'valor mínimo/máximo' no enunciado, 95% das vezes é Função do 2º grau pedindo `Xv` ou `Yv`."
            ),
            memoryTechniques = listOf(
                "Reta: `a` = Aclive (inclinação); `b` = Base no eixo Y.",
                "Parábola: `a > 0` = carinha feliz (tem fundo = mínimo). `a < 0` = carinha triste (tem topo = máximo)."
            ),
            structuredSummary = listOf(
                "1º Grau `f(x) = ax + b`: `a` = taxa de variação; `b` = corte em Y; raiz = `-b/a`.",
                "2º Grau `f(x) = ax² + bx + c`: `a` define concavidade; `c` = corte em Y.",
                "Vértice: `Xv = -b/(2a)` (quando pede a variável x) e `Yv = f(Xv)` (quando pede o valor máximo/mínimo)."
            ),
            practiceQuestionIds = listOf("q_mat_06", "q_mat_07"),
            finalExamQuestionIds = listOf("q_mat_08", "q_mat_09")
        ),
        LessonContent(
            id = "lesson_mat_combinatoria_prob",
            subjectId = "matematica",
            topicId = "mat_combinatoria_prob",
            title = "Análise Combinatória, Probabilidade e Geometria Essencial",
            estimatedMinutes = 25,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "Contar possibilidades e calcular chances não exige decorar dezenas de fórmulas! Tudo nasce da pergunta: 'A ORDEM dos elementos escolhidos faz diferença?'. Se a ordem importa, usamos Princípio Multiplicativo / Arranjo; se a ordem NÃO importa, usamos Combinação.",
            deepExplanation = "1) ARRANJO vs COMBINAÇÃO:\n• Se escolher {João, Maria} para Presidente e Vice é diferente de {Maria, João}, a ordem IMPORTA -> use os tracinhos (Princípio Fundamental da Contagem).\n• Se escolher {João, Maria} para uma comissão de 2 guardas é a MESMA dupla que {Maria, João}, a ordem NÃO importa -> COMBINAÇÃO: `C(n, p) = n! / [p! · (n - p)!]`.\n\n2) PROBABILIDADE:\n• `P(A) = Casos Favoráveis / Casos Possíveis` (sempre entre 0 e 1, ou 0% a 100%).\n• Probabilidade Complementar (salva-vidas em provas!): `P(pelo menos 1) = 1 - P(nenhum)`.\n• Eventos independentes consecutivos ('e'): MULTIPLICA. Eventos mutuamente excludentes ('ou'): SOMA.\n\n3) ÁREAS E VOLUMES NO PAES/UEMA:\n• Triângulo: `(b · h) / 2` (Equilátero: `L²√3 / 4`).\n• Círculo: `Área = πr²` e `Comprimento = 2πr`.\n• Prismas e Cilindros (têm duas bases iguais): `Volume = Área da Base × Altura`.\n• Pirâmides e Cones (têm ponta): `Volume = (Área da Base × Altura) / 3`.",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — Escolha de Comissão (Ordem Não Importa)",
                    problem = "De um pelotão com 6 cadetes, quantas equipes distintas de 2 cadetes podem ser formadas para uma ronda?",
                    stepByStep = listOf(
                        "1. Teste a ordem: a dupla {Ana, Bruno} é igual à dupla {Bruno, Ana}? SIM! Ordem não importa -> Combinação.",
                        "2. `C(6, 2) = (6 × 5) / (2 × 1) = 30 / 2 = 15`."
                    ),
                    answer = "15 equipes distintas."
                ),
                WorkedExample(
                    title = "Exemplo 2 — Volume do Cilindro vs Cone",
                    problem = "Um reservatório cilíndrico de raio 2 m e altura 3 m comporta quantos m³? (Use π = 3,14).",
                    stepByStep = listOf(
                        "1. Base circular: `Ab = π · r² = 3,14 · 2² = 3,14 · 4 = 12,56 m²`.",
                        "2. Volume do cilindro = `Ab · h = 12,56 · 3 = 37,68 m³`.",
                        "3. Dica extra: se fosse um cone com as mesmas medidas, bastaria dividir por 3 (`12,56 m³`)."
                    ),
                    answer = "37,68 m³."
                )
            ),
            examTricks = listOf(
                "No cálculo rápido de Combinação `C(n, p)`: coloque no numerador `p` fatores descendo a partir de `n`, e divida por `p!`. Ex: `C(8, 3) = (8×7×6) / (3×2×1) = 56`.",
                "Sólidos com PONTA (pirâmide e cone) sempre dividem o volume por 3!",
                "Viu 'pelo menos um' em Probabilidade? Calcule `100% - P(zero)`!"
            ),
            memoryTechniques = listOf(
                "Ordem Importa = Arranjo/Tracinhos; Ordem Descartada = Combinação (Divide pelo fatorial da turma).",
                "'E' = Multiplica (×); 'OU' = Soma (+)."
            ),
            structuredSummary = listOf(
                "Ordem importa -> PFC / Permutação (`n!`). Ordem não importa -> Combinação.",
                "Probabilidade = Favoráveis / Total; complementar `P(A') = 1 - P(A)`.",
                "Cilindro/Prisma: `V = Ab · h`. Cone/Pirâmide: `V = (Ab · h)/3`."
            ),
            practiceQuestionIds = listOf("q_mat_10", "q_mat_11"),
            finalExamQuestionIds = listOf("q_mat_12", "q_mat_07")
        )
    )

    val questions = listOf(
        QuestionItem(
            id = "q_mat_01",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_aritmetica_fin",
            topicName = "Porcentagem",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral de fixação — PAES/UEMA.",
            statement = "Um edital previa inicialmente 40 vagas para determinado quadro. Após revisão orçamentária, houve um acréscimo de 15% no número de vagas. Quantas vagas passaram a ser ofertadas?",
            options = listOf("44 vagas", "45 vagas", "46 vagas", "48 vagas"),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: 44 corresponderia a 10%.",
                "Incorreta: 45 corresponderia a 12,5%.",
                "Correta: 15% de 40 = 10% (4) + 5% (2) = 6. Logo, 40 + 6 = 46 vagas.",
                "Incorreta: 48 corresponderia a 20%."
            ),
            generalExplanation = "Calculamos 40 × 1,15 = 46, ou decompondo: 10% de 40 = 4; metade (5%) = 2; total de acréscimo = 6.",
            examTrick = "Sempre quebre porcentagens em 10% + 5% para fazer a conta em 3 segundos!"
        ),
        QuestionItem(
            id = "q_mat_02",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_aritmetica_fin",
            topicName = "Variações Sucessivas",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Um equipamento tático sofreu dois descontos sucessivos: primeiro de 20% e depois de 10%. Qual foi o desconto único equivalente acumulado?",
            options = listOf("30%", "28%", "26%", "25%"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: erro clássico de somar diretamente 20% + 10%.",
                "Correta: 0,80 × 0,90 = 0,72 (paga 72%, logo desconto de 100% - 72% = 28%).",
                "Incorreta: erro de cálculo nos fatores.",
                "Incorreta: não corresponde ao produto dos fatores."
            ),
            generalExplanation = "Usando fatores de desconto: (1 - 0,20) × (1 - 0,10) = 0,80 × 0,90 = 0,72. Como o preço final equivale a 72% do original, o desconto acumulado foi de 28%.",
            examTrick = "Pegadinha clássica: descontos sucessivos NUNCA se somam! Multiplique os fatores restantes."
        ),
        QuestionItem(
            id = "q_mat_03",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_aritmetica_fin",
            topicName = "Regra de Três Composta",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Se 4 instrutores corrigem 120 redações em 3 dias trabalhando no mesmo ritmo, quantos dias serão necessários para que 6 instrutores corrijam 240 redações?",
            options = listOf("2 dias", "3 dias", "4 dias", "5 dias"),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: 2 dias seria se o volume de redações não tivesse dobrado.",
                "Incorreta: não compensa o aumento proporcional de redações.",
                "Correta: Processo (4 instrutores × 3 dias) / Produto (120 redações) = Processo (6 × d) / Produto (240). Assim, 12/120 = 6d/240 -> 0,1 = 6d/240 -> 6d = 24 -> d = 4 dias.",
                "Incorreta: cálculo acima do necessário."
            ),
            generalExplanation = "Pelo método Causa/Efeito: (4 · 3) / 120 = (6 · d) / 240. Simplificando: 12/120 = 1/10, e 6d/240 = d/40. Logo, d/40 = 1/10 -> d = 4 dias.",
            examTrick = "Causa em cima, Efeito embaixo! Nunca falha em regra de três composta."
        ),
        QuestionItem(
            id = "q_mat_04",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_aritmetica_fin",
            topicName = "Juros Compostos",
            difficulty = QuestionDifficulty.DIFICIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Um capital de R$ 5.000,00 foi aplicado sob regime de juros compostos à taxa de 4% ao mês durante 2 meses. Qual foi o total de JUROS obtido ao final desse período?",
            options = listOf("R$ 400,00", "R$ 408,00", "R$ 416,00", "R$ 5.408,00"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: R$ 400,00 seria no regime de juros simples.",
                "Correta: M = 5000 × (1,04)² = 5000 × 1,0816 = 5408. Juros = M - C = 5408 - 5000 = R$ 408,00.",
                "Incorreta: valor superestimado.",
                "Incorreta: R$ 5.408,00 é o MONTANTE, mas a pergunta pediu apenas os JUROS!"
            ),
            generalExplanation = "Calculamos M = 5000 × 1,04 × 1,04 = 5408. Como o enunciado pediu apenas o total de JUROS, subtraímos o capital inicial: 5408 - 5000 = R$ 408,00.",
            examTrick = "Atenção ao comando final da questão: pediu MONTANTE ou JUROS?"
        ),
        QuestionItem(
            id = "q_mat_05",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_progressoes",
            topicName = "Progressão Aritmética (PA)",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Um candidato começa resolvendo 12 questões no 1º dia e aumenta em 3 questões por dia de forma constante durante 10 dias. Quantas questões ele resolverá ao todo nesses 10 dias?",
            options = listOf("240 questões", "255 questões", "270 questões", "300 questões"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: erro ao calcular o 10º termo.",
                "Correta: a₁ = 12, r = 3, n = 10. a₁₀ = 12 + 9×3 = 39. Soma = (12 + 39) × 10 / 2 = 51 × 5 = 255.",
                "Incorreta: soma excessiva.",
                "Incorreta: corresponderia a uma média de 30 por dia."
            ),
            generalExplanation = "Termo geral da PA: a₁₀ = a₁ + (10 - 1)·r = 12 + 27 = 39. Soma da PA: S₁₀ = (a₁ + a₁₀)·n / 2 = (12 + 39)·10 / 2 = 51 × 5 = 255.",
            examTrick = "Soma da PA: (Primeiro + Último) × Quantidade / 2."
        ),
        QuestionItem(
            id = "q_mat_06",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_funcoes",
            topicName = "Função Afim",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Em uma corrida de táxi, cobra-se uma bandeirada fixa de R$ 6,00 mais R$ 2,50 por quilômetro rodado. Qual a distância percorrida em uma viagem que custou R$ 31,00?",
            options = listOf("8 km", "10 km", "12 km", "14 km"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: 8 km custariam R$ 26,00.",
                "Correta: 2,50x + 6 = 31 -> 2,50x = 25 -> x = 10 km.",
                "Incorreta: 12 km custariam R$ 36,00.",
                "Incorreta: 14 km custariam R$ 41,00."
            ),
            generalExplanation = "Modelando como função afim f(x) = 2,5x + 6 = 31, isolamos x: 2,5x = 25 -> x = 10.",
            examTrick = "Valor fixo = 'b'; valor por unidade = 'a'."
        ),
        QuestionItem(
            id = "q_mat_07",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_funcoes",
            topicName = "Função Quadrática (Vértice)",
            difficulty = QuestionDifficulty.DIFICIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Um projétil descreve trajetória parabólica dada por h(t) = -5t² + 30t, onde h é a altura em metros e t o tempo em segundos. Qual é a altura MÁXIMA atingida pelo projétil?",
            options = listOf("3 metros", "30 metros", "45 metros", "60 metros"),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: t = 3 s é o TEMPO em que ocorre a altura máxima (Xv), não a altura!",
                "Incorreta: valor sem substituição completa.",
                "Correta: tv = -30 / (2·(-5)) = 3 s. Substituindo na função: h(3) = -5(3)² + 30(3) = -45 + 90 = 45 metros.",
                "Incorreta: não atinge 60 m."
            ),
            generalExplanation = "Primeiro encontramos o instante do vértice: tv = -b/(2a) = -30 / -10 = 3 s. Em seguida calculamos h(3) = -5(9) + 90 = 45 metros.",
            examTrick = "Cuidado com a pegadinha: a letra A (3) é Xv (tempo), mas a pergunta pediu Yv (altura máxima)!"
        ),
        QuestionItem(
            id = "q_mat_08",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_funcoes",
            topicName = "Conjuntos (Diagrama de Venn)",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Em uma turma de 50 candidatos, 30 revisaram Português, 25 revisaram Matemática e 10 revisaram ambas as disciplinas. Quantos não revisaram nenhuma das duas?",
            options = listOf("5 candidatos", "10 candidatos", "15 candidatos", "Zero candidatos"),
            correctIndex = 0,
            optionExplanations = listOf(
                "Correta: Só Port = 30 - 10 = 20. Só Mat = 25 - 10 = 15. Ambas = 10. Total que revisou = 20 + 15 + 10 = 45. Ficaram de fora: 50 - 45 = 5.",
                "Incorreta: 10 é a interseção.",
                "Incorreta: 15 é quem revisou apenas Matemática.",
                "Incorreta: a soma sem retirar a interseção engana."
            ),
            generalExplanation = "Comece sempre preenchendo a INTERSEÇÃO (10), subtraia dos totais individuais (20 e 15), some as três regiões (45) e compare com o total (50 - 45 = 5).",
            examTrick = "Diagrama de Venn sempre começa de DENTRO PARA FORA (pela interseção)!"
        ),
        QuestionItem(
            id = "q_mat_09",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_funcoes",
            topicName = "Exponencial e Logaritmo",
            difficulty = QuestionDifficulty.DIFICIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Sabendo que log₁₀(2) ≈ 0,30 e log₁₀(3) ≈ 0,48, qual o valor aproximado de log₁₀(12)?",
            options = listOf("0,78", "1,08", "1,26", "1,44"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: 0,78 seria log(6) = log(2) + log(3).",
                "Correta: 12 = 2² × 3. Logo, log(12) = 2·log(2) + log(3) = 2(0,30) + 0,48 = 0,60 + 0,48 = 1,08.",
                "Incorreta: erro na multiplicação.",
                "Incorreta: valor de log(24) seria 1,38."
            ),
            generalExplanation = "Fatorando 12 = 2² · 3 e aplicando a regra do produto e da potência de logaritmos: log(2²·3) = 2·log(2) + log(3) = 0,60 + 0,48 = 1,08.",
            examTrick = "Fatore o número em primos e lembre: log de produto vira SOMA!"
        ),
        QuestionItem(
            id = "q_mat_10",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_combinatoria_prob",
            topicName = "Análise Combinatória",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "De quantas maneiras distintas uma comissão de 3 oficiais pode ser escolhida entre 7 oficiais disponíveis?",
            options = listOf("21 maneiras", "35 maneiras", "42 maneiras", "210 maneiras"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: 21 seria C(7,2).",
                "Correta: Ordem não importa -> C(7,3) = (7 × 6 × 5) / (3 × 2 × 1) = 35.",
                "Incorreta: faltou dividir por 3! completo.",
                "Incorreta: 210 é o Arranjo A(7,3), usado apenas se houvesse cargos diferentes."
            ),
            generalExplanation = "Como os 3 membros formam uma comissão sem distinção de cargo, a ordem de escolha não altera a comissão -> Combinação simples C(7,3) = 35.",
            examTrick = "'Comissão, grupo, equipe sem cargos' = Combinação!"
        ),
        QuestionItem(
            id = "q_mat_11",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_combinatoria_prob",
            topicName = "Probabilidade",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Em uma urna há 4 cartões verdes, 3 azuis e 3 bege. Retirando-se 1 cartão ao acaso, qual a probabilidade de ele NÃO ser bege?",
            options = listOf("30%", "40%", "60%", "70%"),
            correctIndex = 3,
            optionExplanations = listOf(
                "Incorreta: 30% é a probabilidade de SER bege.",
                "Incorreta: 40% é a de ser verde.",
                "Incorreta: não corresponde aos casos favoráveis.",
                "Correta: Total = 10 cartões. Não-bege = 4 verdes + 3 azuis = 7. Probabilidade = 7/10 = 70%."
            ),
            generalExplanation = "Existem 10 cartões no total e 7 não são bege (ou pelo complementar: 100% - 30% = 70%).",
            examTrick = "Sempre que ouvir 'NÃO acontecer', use 100% - P(acontecer)."
        ),
        QuestionItem(
            id = "q_mat_12",
            subjectId = "matematica",
            subjectName = "Matemática",
            topicId = "mat_geometria",
            topicName = "Geometria Plana e Trigonometria",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — PAES/UEMA.",
            statement = "Um triângulo retângulo tem catetos medindo 6 cm e 8 cm. Qual é a área desse triângulo e o comprimento da sua hipotenusa?",
            options = listOf(
                "Área = 48 cm² e Hipotenusa = 10 cm",
                "Área = 24 cm² e Hipotenusa = 10 cm",
                "Área = 24 cm² e Hipotenusa = 14 cm",
                "Área = 12 cm² e Hipotenusa = 10 cm"
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: esqueceu de dividir base × altura por 2.",
                "Correta: Triângulo 3-4-5 multiplicado por 2 -> hipotenusa = 10 cm. Área = (6 × 8)/2 = 24 cm².",
                "Incorreta: somou os catetos em vez de aplicar Pitágoras.",
                "Incorreta: dividiu duas vezes por 2."
            ),
            generalExplanation = "Pelo Teorema de Pitágoras: h² = 6² + 8² = 36 + 64 = 100 -> h = 10 cm (triângulo pitagórico 3-4-5 × 2). A área do triângulo retângulo é metade do produto dos catetos: (6×8)/2 = 24 cm².",
            examTrick = "Decore os trios pitagóricos: (3,4,5), (6,8,10), (5,12,13). Economizam minutos na prova!"
        )
    )

    val flashcards = listOf(
        FlashcardTemplate(
            id = "fc_mat_1",
            subjectId = "matematica",
            topicTitle = "Vértice da Parábola",
            front = "Quais são as fórmulas do vértice (Xv, Yv) de `f(x) = ax² + bx + c`?",
            back = "Xv = -b / (2a)\nYv = -Δ / (4a) ou substitua Xv direto na função: Yv = f(Xv).",
            memoryTip = "Substituir Xv na função evita errar sinal de Delta!"
        ),
        FlashcardTemplate(
            id = "fc_mat_2",
            subjectId = "matematica",
            topicTitle = "Volumes Geométricos",
            front = "Qual a regra geral de volume para Prismas/Cilindros vs Pirâmides/Cones?",
            back = "Bases paralelas iguais (Prisma/Cilindro): V = Ab · h.\nSólido com ponta (Pirâmide/Cone): V = (Ab · h) / 3.",
            memoryTip = "Tem ponta? Divide por 3!"
        ),
        FlashcardTemplate(
            id = "fc_mat_3",
            subjectId = "matematica",
            topicTitle = "Trigonometria Notável",
            front = "Quais os valores de Seno, Cosseno e Tangente de 30°, 45° e 60°?",
            back = "Sen: 1/2, √2/2, √3/2\nCos: √3/2, √2/2, 1/2\nTg: √3/3, 1, √3",
            memoryTip = "1, 2, 3 / 3, 2, 1 / Tudo sobre 2 / Raiz no 2 e no 3!"
        ),
        FlashcardTemplate(
            id = "fc_mat_4",
            subjectId = "matematica",
            topicTitle = "Desconto e Aumento",
            front = "Se algo sobe 20% e depois cai 20%, volta ao preço inicial?",
            back = "NÃO! Fator: 1,20 × 0,80 = 0,96 -> houve redução líquida de 4%.",
            memoryTip = "Variação igual (+a% e -a%) sempre dá perda de (a²/100)%."
        )
    )
}
