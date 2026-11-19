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

object NaturalSciencesCurriculum {
    val subjects = listOf(
        SubjectDefinition(
            id = "fisica",
            name = "Física",
            shortName = "Física",
            area = KnowledgeArea.NATUREZA,
            description = "Mecânica, leis de Newton, energia, termologia, óptica, ondulatória e eletrodinâmica aplicada ao PAES/UEMA.",
            editalStatusNote = "Matriz curricular confirmada no programa de Física do PAES/UEMA.",
            topics = listOf(
                EditalTopic("fis_mecanica", "fisica", "Cinemática, Leis de Newton e Energia", listOf("MRU e MRUV", "1ª, 2ª e 3ª Leis de Newton", "Trabalho, potência e conservação de energia"), priorityWeight = 3),
                EditalTopic("fis_termo_optica", "fisica", "Termologia, Óptica e Ondas", listOf("Escalas termométricas e calorimetria", "Reflexão, refração e lentes", "Frequência, comprimento de onda e velocidade"), priorityWeight = 2),
                EditalTopic("fis_eletro", "fisica", "Eletricidade e Eletromagnetismo", listOf("1ª e 2ª Leis de Ohm", "Associação de resistores", "Potência e consumo elétrico (kWh)"), priorityWeight = 3)
            )
        ),
        SubjectDefinition(
            id = "quimica",
            name = "Química",
            shortName = "Química",
            area = KnowledgeArea.NATUREZA,
            description = "Estrutura atômica, ligações, funções inorgânicas, estequiometria, soluções, pH e química orgânica.",
            editalStatusNote = "Matriz curricular confirmada no programa de Química do PAES/UEMA.",
            topics = listOf(
                EditalTopic("quim_geral", "quimica", "Atomística, Ligações e Funções Inorgânicas", listOf("Tabela periódica e ligações iônica/covalente", "Ácidos, bases, sais e óxidos", "Balanceamento e estequiometria"), priorityWeight = 3),
                EditalTopic("quim_fisico", "quimica", "Soluções, Termoquímica e Equilíbrio", listOf("Concentração comum e molaridade", "Entalpia endotérmica e exotérmica", "Escala de pH e deslocamento de equilíbrio"), priorityWeight = 2),
                EditalTopic("quim_organica", "quimica", "Química Orgânica", listOf("Classificação de cadeias carbônicas", "Funções orgânicas (álcool, ácido, éster, amina)", "Isomeria e reações orgânicas"), priorityWeight = 2)
            )
        ),
        SubjectDefinition(
            id = "biologia",
            name = "Biologia",
            shortName = "Biologia",
            area = KnowledgeArea.NATUREZA,
            description = "Citologia, metabolismo celular, genética, evolução, ecologia e fisiologia humana voltadas ao PAES/UEMA.",
            editalStatusNote = "Matriz curricular confirmada no programa de Biologia do PAES/UEMA.",
            topics = listOf(
                EditalTopic("bio_cito_gen", "biologia", "Citologia, Metabolismo e Genética", listOf("Organelas e transporte na membrana", "Fotossíntese e respiração celular", "1ª e 2ª Leis de Mendel e grupos sanguíneos"), priorityWeight = 3),
                EditalTopic("bio_eco_fisio", "biologia", "Ecologia, Saúde Pública e Fisiologia", listOf("Cadeias tróficas e ciclos biogeoquímicos", "Sistemas cardiovascular, respiratório e imunológico", "Endemias e prevenção"), priorityWeight = 3)
            )
        )
    )

    val lessons = listOf(
        LessonContent(
            id = "lesson_fis_mecanica_eletro",
            subjectId = "fisica",
            topicId = "fis_mecanica",
            title = "Leis de Newton, Cinemática e Eletricidade Básica",
            estimatedMinutes = 25,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "Física de prova fica simples quando você conecta a fórmula à realidade: velocidade mede o quão rápido a posição muda; força mede o que altera o movimento; potência mede quanta energia você gasta por segundo!",
            deepExplanation = "1) CINEMÁTICA E UNIDADES:\n• Velocidade média: `v = ΔS / Δt`.\n• Conversão obrigatória: `m/s -> km/h` (multiplica por `3,6`); `km/h -> m/s` (divide por `3,6`). Ex: `72 km/h = 20 m/s`.\n\n2) LEIS DE NEWTON E ENERGIA:\n• 1ª Lei (Inércia): corpo tende a manter repouso ou MRU se força resultante = 0.\n• 2ª Lei: `FR = m · a` (em Newtons: kg·m/s²).\n• 3ª Lei: Ação e Reação têm mesma intensidade, mesma direção, sentidos opostos e atuam em CORPOS DIFERENTES (por isso nunca se anulam!).\n• Energia Cinética: `Ec = m·v² / 2` | Energia Potencial Gravitacional: `Ep = m·g·h`.\n\n3) ELETRICIDADE NO DIA A DIA:\n• Lei de Ohm: `U = R · i` (Tensão = Resistência × Corrente).\n• Potência Elétrica: `P = U · i` (em Watts).\n• Energia gasta na conta de luz: `E (kWh) = Potência (kW) × Tempo (horas)`.",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — Consumo Elétrico em kWh",
                    problem = "Um aparelho de ar-condicionado de 2.000 W funciona durante 5 horas por dia durante 30 dias. Quantos kWh consome no mês?",
                    stepByStep = listOf(
                        "1. Passe a potência de Watts (W) para quilowatts (kW) dividindo por 1.000: `2000 W = 2 kW`.",
                        "2. Tempo total no mês: `5 h/dia × 30 dias = 150 horas`.",
                        "3. Multiplique: `E = 2 kW × 150 h = 300 kWh`."
                    ),
                    answer = "300 kWh."
                )
            ),
            examTricks = listOf(
                "`36 km/h = 10 m/s`, `72 km/h = 20 m/s`, `108 km/h = 30 m/s`. Memorize esses 3 múltiplos de 36!",
                "Ação e reação nunca se equilibram porque atuam em corpos distintos!",
                "Em resistores em SÉRIE, a corrente `i` é igual em todos. Em PARALELO, a tensão `U` é igual em todos."
            ),
            memoryTechniques = listOf(
                "U = R · i -> 'Quem Vê Ri' (V = R·i).",
                "P = U · i -> 'PUI' (Potência = U × i)."
            ),
            structuredSummary = listOf(
                "Conversão: km/h ÷ 3,6 = m/s.",
                "2ª Lei de Newton: `FR = m · a`; Energia mecânica conservada sem atrito.",
                "Eletricidade: `U = R · i`, `P = U · i`, `E(kWh) = P(kW) · t(h)`."
            ),
            practiceQuestionIds = listOf("q_fis_01", "q_fis_02"),
            finalExamQuestionIds = listOf("q_fis_03")
        ),
        LessonContent(
            id = "lesson_quim_estequio_ph",
            subjectId = "quimica",
            topicId = "quim_geral",
            title = "Estequiometria, Soluções e Escala de pH",
            estimatedMinutes = 20,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "Estequiometria é a 'receita de bolo' da Química: a proporção em MOL entre reagentes e produtos obedece exatamente aos coeficientes da equação balanceada.",
            deepExplanation = "1) ESTEQUIOMETRIA EM 3 PASSOS:\n• Passo 1: Balanceie a equação química.\n• Passo 2: Monte a linha dos coeficientes (em mol, ou multiplicando pela Massa Molar em gramas).\n• Passo 3: Faça a regra de três com o dado fornecido e a incógnita.\n\n2) ESCALA DE pH (a 25 °C):\n• `pH < 7`: meio ÁCIDO (maior concentração de `H⁺`).\n• `pH = 7`: meio NEUTRO.\n• `pH > 7`: meio BÁSICO / ALCALINO.\n• Atenção: a escala é logarítmica! Cada 1 unidade de pH reduzida significa 10 vezes mais acidez.\n\n3) LIGAÇÕES QUÍMICAS:\n• Iônica (Metal + Ametal): transferência de elétrons, alto ponto de fusão, conduz corrente quando fundida ou dissolvida em água.\n• Covalente (Ametal + Ametal / H): compartilhamento de elétrons.",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — Proporção em Mol",
                    problem = "Na reação `2 H₂ + O₂ -> 2 H₂O`, quantos mols de água são formados a partir de 3 mols de O₂ em excesso de H₂?",
                    stepByStep = listOf(
                        "1. A equação já está balanceada: 1 mol de O₂ gera 2 mols de H₂O.",
                        "2. Proporção 1 : 2. Com 3 mols de O₂ -> `3 × 2 = 6` mols de H₂O."
                    ),
                    answer = "6 mols de água."
                )
            ),
            examTricks = listOf(
                "Regra do MACHO para balanceamento: comece por Metais, depois Ametais, depois Carbono, Hidrogênio e por último Oxigênio!",
                "pH 4 é 100 vezes mais ácido que pH 6 (diferença de 2 unidades = 10² = 100×)."
            ),
            memoryTechniques = listOf(
                "Ordem de balanceamento: M-A-C-H-O.",
                "Covalente = Compartilha; Iônica = Doa/Recebe Íons."
            ),
            structuredSummary = listOf(
                "Sempre balanceie a equação antes de calcular massas ou mols.",
                "pH < 7 ácido; pH = 7 neutro; pH > 7 básico.",
                "Iônica (metal + ametal); Covalente (ametais)."
            ),
            practiceQuestionIds = listOf("q_quim_01", "q_quim_02"),
            finalExamQuestionIds = listOf("q_quim_02")
        ),
        LessonContent(
            id = "lesson_bio_cito_ecologia",
            subjectId = "biologia",
            topicId = "bio_cito_gen",
            title = "Citologia, Genética e Ecologia para o PAES/UEMA",
            estimatedMinutes = 20,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "A Biologia no PAES/UEMA conecta o funcionamento microscópico da célula (organelas e DNA) com grandes temas de saúde pública e desequilíbrios ambientais.",
            deepExplanation = "1) ORGANELAS ESSENCIAIS:\n• Mitocôndria: respiração celular e produção de energia (ATP).\n• Ribossomo: síntese de proteínas.\n• Complexo Golgiense: empacotamento e secreção celular.\n• Lisossomo: digestão intracelular.\n• Cloroplasto (célula vegetal): fotossíntese.\n\n2) GENÉTICA E TIPAGEM SANGUÍNEA (Sistema ABO):\n• Alelos `Iᴬ` e `Iᴮ` são codominantes entre si e dominantes sobre `i`.\n• Tipo O (`ii`) é doador universal de hemácias; tipo AB (`IᴬIᴮ`) é receptor universal.\n\n3) ECOLOGIA:\n• Fluxo de energia nas cadeias alimentares é UNIDIRECIONAL e diminui a cada nível trófico (pirâmide de energia nunca é invertida).\n• Eutrofização: excesso de esgoto/fertilizantes gera proliferação de algas, bloqueia a luz, morrem organismos e bactérias aeróbias consomem o oxigênio da água.",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — Cruzamento ABO",
                    problem = "Um casal formado por pai tipo A heterozigoto (`Iᴬi`) e mãe tipo B heterozigota (`Iᴮi`) pode ter filho com qual tipo sanguíneo?",
                    stepByStep = listOf(
                        "1. Faça o cruzamento: `Iᴬi × Iᴮi`.",
                        "2. Combinações possíveis: `IᴬIᴮ` (AB), `Iᴬi` (A), `Iᴮi` (B) e `ii` (O)."
                    ),
                    answer = "Qualquer um dos 4 tipos (A, B, AB ou O), cada um com 25% de chance!"
                )
            ),
            examTricks = listOf(
                "Pirâmide de ENERGIA na ecologia NUNCA pode ser invertida!",
                "Mitocôndria e Cloroplasto possuem DNA próprio (Teoria Endossimbiótica).",
                "Eutrofização = excesso de nutrientes -> explosão de algas -> queda de oxigênio dissolvido."
            ),
            memoryTechniques = listOf(
                "Mito = Motor (ATP); Ribo = Rede de proteínas; Liso = Lixeiro (digestão)."
            ),
            structuredSummary = listOf(
                "Mitocôndria (ATP), Ribossomo (proteínas), Lisossomo (digestão), Golgi (secreção).",
                "Sistema ABO: `Iᴬ` e `Iᴮ` codominantes sobre `i`.",
                "Fluxo de energia é decrescente e unidirecional; matéria é cíclica."
            ),
            practiceQuestionIds = listOf("q_bio_01", "q_bio_02"),
            finalExamQuestionIds = listOf("q_bio_01")
        )
    )

    val questions = listOf(
        QuestionItem(
            id = "q_fis_01",
            subjectId = "fisica",
            subjectName = "Física",
            topicId = "fis_mecanica",
            topicName = "Cinemática",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Física (PAES/UEMA).",
            statement = "Uma viatura realiza um deslocamento de 144 km em linha reta durante 2 horas. Qual é a velocidade escalar média da viatura em metros por segundo (m/s)?",
            options = listOf("10 m/s", "20 m/s", "36 m/s", "72 m/s"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: 10 m/s equivale a 36 km/h.",
                "Correta: vm = 144 km / 2 h = 72 km/h. Dividindo por 3,6 -> 72 / 3,6 = 20 m/s.",
                "Incorreta: erro de conversão.",
                "Incorreta: 72 é o valor em km/h, não em m/s!"
            ),
            generalExplanation = "Primeiro calculamos 144 / 2 = 72 km/h. Em seguida convertemos para o SI dividindo por 3,6: 72 / 3,6 = 20 m/s.",
            examTrick = "Sempre cheque a unidade pedida no final: km/h ou m/s!"
        ),
        QuestionItem(
            id = "q_fis_02",
            subjectId = "fisica",
            subjectName = "Física",
            topicId = "fis_eletro",
            topicName = "Eletricidade",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Física (PAES/UEMA).",
            statement = "Um resistor de 20 Ω é percorrido por uma corrente elétrica constante de 3 A. Qual a tensão elétrica aplicada e a potência dissipada por esse resistor?",
            options = listOf(
                "U = 60 V e P = 180 W",
                "U = 23 V e P = 60 W",
                "U = 60 V e P = 60 W",
                "U = 6,6 V e P = 180 W"
            ),
            correctIndex = 0,
            optionExplanations = listOf(
                "Correta: U = R · i = 20 × 3 = 60 V. Potência P = U · i = 60 × 3 = 180 W.",
                "Incorreta: somou R + i.",
                "Incorreta: confundiu U com P.",
                "Incorreta: dividiu em vez de multiplicar."
            ),
            generalExplanation = "Pela 1ª Lei de Ohm, U = 20 × 3 = 60 V. Pela fórmula de potência, P = U · i = 60 × 3 = 180 W.",
            examTrick = "Lembre de 'U = R·i' e 'P = U·i'."
        ),
        QuestionItem(
            id = "q_fis_03",
            subjectId = "fisica",
            subjectName = "Física",
            topicId = "fis_mecanica",
            topicName = "Leis de Newton",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Física (PAES/UEMA).",
            statement = "Por que o par de forças de Ação e Reação (3ª Lei de Newton) NÃO se anula mutuamente?",
            options = listOf(
                "Porque possuem intensidades diferentes.",
                "Porque atuam sempre em corpos diferentes.",
                "Porque formam ângulo de 90° entre si.",
                "Porque só existem quando há atrito."
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: têm exatamente a mesma intensidade (módulo).",
                "Correta: como atuam em corpos distintos, cada corpo sente apenas uma das forças.",
                "Incorreta: têm a mesma direção (180° opostas).",
                "Incorreta: existem em qualquer interação entre dois corpos, inclusive à distância."
            ),
            generalExplanation = "Só forças aplicadas no MESMO corpo podem somar-se e anular-se. Ação e reação agem em corpos distintos.",
            examTrick = "Ação em A, Reação em B -> corpos diferentes, nunca se anulam!"
        ),
        QuestionItem(
            id = "q_quim_01",
            subjectId = "quimica",
            subjectName = "Química",
            topicId = "quim_fisico",
            topicName = "Escala de pH",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Química (PAES/UEMA).",
            statement = "Uma solução aquosa a 25 °C apresenta pH = 3, enquanto outra apresenta pH = 5. Comparando as duas, afirma-se corretamente que:",
            options = listOf(
                "Ambas são soluções básicas.",
                "A primeira é ácida e possui concentração de íons H⁺ 100 vezes maior que a segunda.",
                "A segunda é 2 vezes mais ácida que a primeira.",
                "A primeira é neutra e a segunda é alcalina."
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: pH < 7 indica meio ácido.",
                "Correta: quanto menor o pH, maior a acidez; diferença de 2 unidades na escala logarítmica = 10² = 100× mais H⁺.",
                "Incorreta: erro clássico de interpretar a escala como linear.",
                "Incorreta: neutro é pH = 7."
            ),
            generalExplanation = "Como pH = -log[H⁺], cada unidade a menos de pH multiplica por 10 a concentração hidrogeniônica.",
            examTrick = "Diferença de ΔpH = n -> acidez muda por um fator de 10ⁿ!"
        ),
        QuestionItem(
            id = "q_quim_02",
            subjectId = "quimica",
            subjectName = "Química",
            topicId = "quim_organica",
            topicName = "Funções Orgânicas",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Química (PAES/UEMA).",
            statement = "Um composto orgânico apresenta um grupo hidroxila (-OH) ligado diretamente a um carbono saturado (que só faz ligações simples). Essa função orgânica é:",
            options = listOf("Fenol", "Álcool", "Ácido carboxílico", "Éter"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: no fenol, a hidroxila liga-se diretamente a um anel aromático.",
                "Correta: -OH ligado a carbono saturado caracteriza a função Álcool.",
                "Incorreta: ácido carboxílico possui carboxila (-COOH).",
                "Incorreta: éter possui oxigênio entre dois carbonos (R-O-R')."
            ),
            generalExplanation = "A posição da hidroxila define a função: em carbono saturado = álcool; em anel aromático = fenol; em carbono com dupla ligação = enol.",
            examTrick = "-OH em carbono saturado = Álcool; -OH no anel benzênico = Fenol."
        ),
        QuestionItem(
            id = "q_bio_01",
            subjectId = "biologia",
            subjectName = "Biologia",
            topicId = "bio_cito_gen",
            topicName = "Organelas Celulares",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Biologia (PAES/UEMA).",
            statement = "Em células musculares com alta demanda energética durante provas físicas (TAF), qual organela celular é encontrada em grande quantidade por ser responsável pela respiração celular e produção de ATP?",
            options = listOf(
                "Lisossomo",
                "Mitocôndria",
                "Centríolo",
                "Peroxissomo"
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: realiza digestão intracelular.",
                "Correta: a mitocôndria realiza o ciclo de Krebs e a cadeia respiratória, gerando ATP.",
                "Incorreta: atua na divisão celular e formação de cílios/flagelos.",
                "Incorreta: degrada peróxido de hidrogênio."
            ),
            generalExplanation = "Células com alto gasto energético, como fibras musculares cardíacas e esqueléticas, são ricas em mitocôndrias.",
            examTrick = "ATP / Respiração celular = Mitocôndria."
        ),
        QuestionItem(
            id = "q_bio_02",
            subjectId = "biologia",
            subjectName = "Biologia",
            topicId = "bio_eco_fisio",
            topicName = "Ecologia",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Biologia (PAES/UEMA).",
            statement = "Em relação ao fluxo de energia e ao ciclo da matéria nos ecossistemas, é correto afirmar que:",
            options = listOf(
                "A energia é totalmente reciclada pelos decompositores e retorna aos produtores.",
                "O fluxo de energia é unidirecional e diminui a cada nível trófico, enquanto a matéria é reciclada.",
                "Os consumidores terciários recebem mais energia que os produtores.",
                "As pirâmides de energia podem aparecer invertidas em ecossistemas aquáticos."
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: a matéria é reciclada; a energia se dissipa na forma de calor.",
                "Correta: apenas cerca de 10% da energia passa ao nível seguinte; a matéria circula continuamente.",
                "Incorreta: recebem muito menos energia.",
                "Incorreta: pirâmide de energia NUNCA é invertida."
            ),
            generalExplanation = "A energia entra principalmente via fotossíntese e perde-se em parte como calor a cada nível trófico; já átomos como carbono e nitrogênio retornam ao meio pelos decompositores.",
            examTrick = "Energia = Unidirecional (decresce). Matéria = Cíclica (recicla)."
        )
    )

    val flashcards = listOf(
        FlashcardTemplate(
            id = "fc_fis_1",
            subjectId = "fisica",
            topicTitle = "Conversão de Velocidade",
            front = "Como converter km/h para m/s e vice-versa?",
            back = "km/h -> m/s: divide por 3,6.\nm/s -> km/h: multiplica por 3,6.",
            memoryTip = "36 km/h = 10 m/s; 72 km/h = 20 m/s; 108 km/h = 30 m/s."
        ),
        FlashcardTemplate(
            id = "fc_quim_1",
            subjectId = "quimica",
            topicTitle = "Escala de pH",
            front = "O que muda na acidez quando o pH cai de 6 para 4?",
            back = "A concentração de H⁺ aumenta 100 vezes (10²), pois cada unidade na escala de pH vale fator 10.",
            memoryTip = "pH menor = ácido mais forte!"
        ),
        FlashcardTemplate(
            id = "fc_bio_1",
            subjectId = "biologia",
            topicTitle = "Genética ABO",
            front = "Qual o genótipo de uma pessoa com sangue tipo O e com sangue tipo AB?",
            back = "Tipo O: ii (recessivo, doa hemácias para todos).\nTipo AB: IᴬIᴮ (codominante, recebe de todos).",
            memoryTip = "O = ii; AB = IᴬIᴮ."
        )
    )
}
