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

object PortugueseCurriculum {
    val subject = SubjectDefinition(
        id = "portugues",
        name = "Língua Portuguesa",
        shortName = "Português",
        area = KnowledgeArea.LINGUAGENS,
        description = "Base essencial do PAES/UEMA e da Redação: interpretação textual, sintaxe, concordância, regência, crase e coesão.",
        editalStatusNote = "Matriz curricular baseada no programa de Língua Portuguesa do PAES/UEMA (Ensino Médio completo).",
        topics = listOf(
            EditalTopic(
                id = "port_interpretacao",
                subjectId = "portugues",
                title = "Leitura, Interpretação e Tipologia Textual",
                subtopics = listOf("Ideia central e implícitos", "Gêneros textuais", "Coesão e coerência", "Intertextualidade"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "port_morfologia",
                subjectId = "portugues",
                title = "Morfologia, Classes Gramaticais e Semântica",
                subtopics = listOf("Verbos e vozes verbais", "Pronomes e colocação pronominal", "Conjunções coordenativas e subordinativas", "Polissemia e figuras de linguagem"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "port_sintaxe",
                subjectId = "portugues",
                title = "Sintaxe do Período Simples e Composto",
                subtopics = listOf("Sujeito, predicado e transitividade", "Orações subordinadas substantivas, adjetivas e adverbiais", "Orações coordenadas"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "port_concordancia",
                subjectId = "portugues",
                title = "Concordância Verbal e Nominal",
                subtopics = listOf("Sujeito composto", "Verbos impessoais (haver, fazer)", "Partícula SE (apassivadora vs indeterminação)", "Casos especiais de adjetivos"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "port_regencia_crase",
                subjectId = "portugues",
                title = "Regência Verbal/Nominal e Uso da Crase",
                subtopics = listOf("Verbos com mudança de sentido", "Preposições exigidas", "Crase obrigatória, facultativa e proibida"),
                priorityWeight = 3
            ),
            EditalTopic(
                id = "port_pontuacao",
                subjectId = "portugues",
                title = "Pontuação e Acentuação Gráfica",
                subtopics = listOf("Uso da vírgula na ordem direta e inversa", "Vírgula em orações explicativas vs restritivas", "Regras do Novo Acordo Ortográfico"),
                priorityWeight = 2
            )
        )
    )

    val lessons = listOf(
        LessonContent(
            id = "lesson_port_crase",
            subjectId = "portugues",
            topicId = "port_regencia_crase",
            title = "Regência e Crase Sem Mistério",
            estimatedMinutes = 25,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "Crase não é um acento qualquer: ela é a FUSÃO de dois sons iguais — a preposição 'a' (exigida por um verbo ou nome) mais o artigo feminino 'a' (que acompanha um substantivo feminino). Quando essa fusão acontece, indicamos na escrita com o acento grave (`à`). Se faltar a preposição OU se a palavra seguinte não aceitar artigo feminino, não há crase!",
            deepExplanation = "Para dominar regência e crase no PAES/UEMA, você precisa analisar dois lados da frase:\n\n1) TERMO REGENTE (à esquerda): exige a preposição 'a'? Exemplos: quem vai, vai A algum lugar; quem obedece, obedece A alguém; quem tem acesso, tem acesso A algo; quem se refere, refere-se A algo.\n2) TERMO REGIDO (à direita): é palavra feminina que admite artigo 'a'? Substantivos femininos determinados admitem ('a escola', 'a prova', 'a PM'). Verbos no infinitivo ('estudar'), pronomes pessoais ('ela', 'você'), palavras masculinas ('o edital') e palavras no plural após 'a' no singular ('a pessoas') NÃO admitem artigo 'a', portanto NÃO têm crase.\n\nCasos especiais que caem muito em provas:\n• Locuções adverbiais, prepositivas e conjuntivas femininas: 'à medida que', 'à proporção que', 'às vezes', 'à noite', 'à procura de', 'à vista'.\n• Pronomes demonstrativos 'aquele(s)', 'aquela(s)', 'aquilo': se o termo anterior pedir preposição 'a', ocorre crase (`àquele`, `àquela`, `àquilo`).\n• Crase facultativa (memorize o trio 'P-N-A'): antes de Pronome possessivo feminino singular ('à/a minha mãe'), antes de Nome próprio feminino ('à/a Maria') e depois da preposição 'Até' ('até à/a escola').\n• Palavras 'casa' e 'terra': só levam crase quando especificadas ('Voltei à casa dos meus pais' / 'Os marinheiros desceram à terra firme'). Sem especificação, ficam sem crase ('Voltei a casa').",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — O Truque da Troca pelo Masculino",
                    problem = "Devo escrever: 'O candidato dedicou-se (a / à) preparação para o CFO'?",
                    stepByStep = listOf(
                        "1. Identifique o termo regente: quem se dedica, dedica-se A algo (tem preposição 'a').",
                        "2. Troque o substantivo feminino 'preparação' por um masculino equivalente, como 'treinamento'.",
                        "3. Leia a frase: 'O candidato dedicou-se AO treinamento'.",
                        "4. Apareceu 'AO'? Isso prova que há preposição 'a' + artigo! Logo, no feminino há crase."
                    ),
                    answer = "Correto: 'dedicou-se à preparação'."
                ),
                WorkedExample(
                    title = "Exemplo 2 — Crase Proibida por Singular antes de Plural",
                    problem = "Analise: 'O edital refere-se (a / às / à) vagas remanescentes.'",
                    stepByStep = listOf(
                        "1. Veja o número da palavra seguinte: 'vagas' está no PLURAL.",
                        "2. Se o 'a' estiver no SINGULAR ('a vagas'), ele é apenas preposição pura — não há artigo 'as'.",
                        "3. Sem artigo, nunca existe crase ('à vagas' é erro clássico!).",
                        "4. Só haveria crase se ambos estivessem no plural: 'refere-se às vagas'."
                    ),
                    answer = "Correto: 'refere-se a vagas' (sem crase) OU 'às vagas' (no plural)."
                ),
                WorkedExample(
                    title = "Exemplo 3 — Regência com 'Assistir' e 'Visar'",
                    problem = "Diferencie: 'Assistimos ao desfile' vs 'Assistimos o ferido'.",
                    stepByStep = listOf(
                        "1. 'Assistir' no sentido de VER/PRESENCIAR é transitivo indireto: exige preposição 'a' ('Assistimos ao desfile').",
                        "2. 'Assistir' no sentido de AJUDAR/SOCORRER é transitivo direto: não exige preposição ('O médico assistiu o ferido').",
                        "3. O mesmo cuidado vale para 'Visar': mirar/dar visto = direto ('visar o cheque'); ter como objetivo = indireto com 'a' ('visar à aprovação')."
                    ),
                    answer = "Sempre confira o sentido do verbo antes de julgar a preposição."
                )
            ),
            examTricks = listOf(
                "Macete do 'AO': troque a palavra feminina por uma masculina. Se virar 'AO', tem crase (`à`). Se virar só 'A' ou 'O', não tem crase!",
                "Macete das Cidades ('Volto DA / Volto DE'): Se você 'volta DA' Bahia, vai 'À' Bahia (tem crase). Se você 'volta DE' São Luís, vai 'A' São Luís (sem crase). Se especificar ('a bela São Luís'), ganha crase!",
                "Regra de Ouro do 'A' singular + palavra no plural: CRASE NEM A PAU! ('a pessoas', 'a redações' jamais levam crase)."
            ),
            memoryTechniques = listOf(
                "Mnemônico P-N-A para Crase Facultativa: Possessivo feminino, Nome de mulher, preposição Até.",
                "Frase-âncora de proibições: 'Diante de Verbo, Masculino e Pronome de tratamento, crase é lamento' (não se usa crase antes de verbo, palavra masculina, 'você/Vossa Senhoria' — exceto 'à senhora/senhorita')."
            ),
            structuredSummary = listOf(
                "Crase = preposição 'a' + artigo 'a' (ou 'a' de aquele/aquela/aquilo).",
                "Troque o termo feminino por masculino -> 'AO' confirma crase.",
                "Proibida antes de verbos, masculinos, 'a' singular + plural, pronomes indefinidos ('toda', 'cada', 'alguma').",
                "Obrigatória em locuções femininas ('às vezes', 'à vista', 'à medida que') e horas exatas ('às 14h')."
            ),
            practiceQuestionIds = listOf("q_port_01", "q_port_02", "q_port_03"),
            finalExamQuestionIds = listOf("q_port_04", "q_port_05")
        ),
        LessonContent(
            id = "lesson_port_concordancia",
            subjectId = "portugues",
            topicId = "port_concordancia",
            title = "Concordância Verbal e Partícula SE",
            estimatedMinutes = 25,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "Concordância verbal é a regra de harmonia da frase: o verbo deve concordar em número (singular ou plural) e pessoa com o seu SUJEITO. O erro mais comum em provas acontece quando o sujeito aparece longe do verbo ou depois dele.",
            deepExplanation = "Os três pilares mais cobrados em concursos e vestibulares como o PAES/UEMA:\n\n1) VERBOS IMPESSOAIS (Ficam SEMPRE no singular — 3ª pessoa do singular!):\n• HAVER no sentido de existir, acontecer ou tempo decorrido ('Havia muitos candidatos'; 'Há dois anos').\n• FAZER indicando tempo decorrido ou clima ('Faz três meses que estudo'; 'Faz noites frias').\n• Atenção: o verbo EXISTIR tem sujeito normal e vai para o plural ('Existem muitos candidatos'). Se houver locução verbal com HAVER ('Deve haver soluções'), o auxiliar também fica no singular!\n\n2) PARTÍCULA 'SE':\n• Pronome Apassivador (PA): ocorre com Verbo Transitivo Direto (VTD). A frase pode ser passada para a voz passiva analítica, e o termo paciente é o SUJEITO! Logo, o verbo concorda com ele ('Alugam-se casas' = Casas são alugadas; 'Realizou-se a prova' = A prova foi realizada).\n• Índice de Indeterminação do Sujeito (IIS): ocorre com Verbo Transitivo Indireto (VTI), Intransitivo (VI) ou de Ligação (VL) + preposição. Não há sujeito determinado -> verbo SEMPRE no singular ('Precisa-se de oficiais dedicados'; 'Trata-se de temas atuais').\n\n3) SUJEITO COMPOSTO E PARTITIVOS:\n• Sujeito composto ANTES do verbo -> plural ('A disciplina e o foco garantem a vaga').\n• Sujeito composto DEPOIS do verbo -> concorda com o mais próximo ou vai para o plural ('Faltou foco e disciplina' / 'Faltaram foco e disciplina').\n• Expressões partitivas ('A maioria dos candidatos...', 'Grande parte dos alunos...'): verbo pode ficar no singular (concordando com 'maioria') ou ir para o plural (concordando com 'candidatos').",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — Haver vs Existir",
                    problem = "Complete corretamente: 'Na última edição, ______ poucas desistências, pois ______ critérios claros.'",
                    stepByStep = listOf(
                        "1. Primeira lacuna: se usar 'haver' (sentido de existir), ele é impessoal -> singular ('houve').",
                        "2. Segunda lacuna: se usar 'existir', ele é pessoal e concorda com 'critérios claros' -> plural ('existiam')."
                    ),
                    answer = "'houve poucas desistências, pois existiam critérios claros'."
                ),
                WorkedExample(
                    title = "Exemplo 2 — O Segredo do 'SE'",
                    problem = "Qual está correta: 'Necessita-se de novos livros' ou 'Necessitam-se de novos livros'?",
                    stepByStep = listOf(
                        "1. Pergunte ao verbo: quem necessita, necessita DE algo (Verbo Transitivo Indireto).",
                        "2. Existe a preposição 'DE' ligando o verbo ao complemento. Preposição impede esse termo de ser sujeito!",
                        "3. Logo, o 'se' é Índice de Indeterminação do Sujeito -> verbo obrigatório no singular."
                    ),
                    answer = "Correto: 'Necessita-se de novos livros' (sempre no singular)."
                )
            ),
            examTricks = listOf(
                "Viu 'DE', 'EM', 'COM', 'A' logo depois de 'verbo + se'? Verbo travado no SINGULAR! ('Trata-se de...', 'Precisa-se de...').",
                "Conseguiu transformar 'Verbo-se X' em 'X é/são + particípio'? Então X é sujeito e o verbo concorda com X! ('Vendem-se apostilas' = Apostilas são vendidas).",
                "Locução com HAVER ('Vai haver', 'Deve haver', 'Pode haver'): o verbo HAVER contamina o auxiliar e deixa TUDO no singular!"
            ),
            memoryTechniques = listOf(
                "Haver = Existir -> Fica quieto no Singular. Existir -> Vai pro Plural se o sujeito mandar.",
                "Regra da Preposição no 'SE': 'Com preposição no meio, o plural é feio!' (fica no singular)."
            ),
            structuredSummary = listOf(
                "Haver (existir/tempo) e Fazer (tempo/clima) são impessoais: 3ª pessoa do singular.",
                "VTD + SE = Pronome Apassivador -> verbo concorda com o sujeito paciente.",
                "VTI/VI/VL + SE = Índice de Indeterminação -> verbo sempre na 3ª pessoa do singular.",
                "Porcentagem + substantivo: o verbo concorda com o número ou com o substantivo ('20% da turma passou/passaram')."
            ),
            practiceQuestionIds = listOf("q_port_06", "q_port_07", "q_port_08"),
            finalExamQuestionIds = listOf("q_port_09", "q_port_10")
        ),
        LessonContent(
            id = "lesson_port_sintaxe_interpretacao",
            subjectId = "portugues",
            topicId = "port_sintaxe",
            title = "Coesão, Conjunções e Período Composto",
            estimatedMinutes = 20,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "As orações dentro de um texto não vivem isoladas: elas são conectadas por palavras chamadas CONJUNÇÕES. Entender o valor lógico de cada conjunção resolve tanto questões de interpretação de texto e gramática quanto a competência de coesão na Redação.",
            deepExplanation = "As 5 relações lógicas mais cobradas no PAES/UEMA:\n\n1) OPOSIÇÃO / ADVERSIDADE vs CONCESSÃO:\n• Adversativas (coordenadas): 'mas, porém, contudo, todavia, entretanto, no entanto' — dão destaque ao argumento que vem DEPOIS.\n• Concessivas (subordinadas): 'embora, ainda que, mesmo que, conquanto, apesar de que, posto que' — reconhecem um obstáculo, mas afirmam que a ação principal prevalece ('Embora chovesse, treinou').\n\n2) CAUSA vs CONSEQUÊNCIA:\n• Causais: 'porque, já que, visto que, uma vez que, como (no início da frase)'.\n• Consecutivas: 'tão/tanto/tal... QUE' ('Estudou tanto que foi aprovado').\n\n3) CONDIÇÃO vs PROPORÇÃO:\n• Condicionais: 'se, caso, desde que, contanto que, a menos que'.\n• Proporcionais: 'à medida que, à proporção que, quanto mais... mais'.\n\n4) CONCLUSÃO vs EXPLICAÇÃO:\n• Conclusivas: 'logo, portanto, por conseguinte, assim, destarte'.\n• Explicativas: 'pois (antes do verbo), porque, que'. Dica: 'pois' DEPOIS do verbo e entre vírgulas tem valor CONCLUSIVO ('Estudou; será, pois, aprovado').",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — O 'Pois' Camaleão",
                    problem = "Identifique o sentido de 'pois' em: (A) 'Foco, pois a prova exige preparo' e (B) 'A prova exige preparo; mantenha, pois, o foco'.",
                    stepByStep = listOf(
                        "1. Em (A), 'pois' aparece antes do verbo da 2ª oração -> é EXPLICATIVO (pode trocar por 'porque').",
                        "2. Em (B), 'pois' está deslocado após o verbo 'mantenha' e isolado por vírgulas -> é CONCLUSIVO (pode trocar por 'portanto')."
                    ),
                    answer = "(A) Explicativo; (B) Conclusivo."
                ),
                WorkedExample(
                    title = "Exemplo 2 — 'Como' Causal vs Comparativo",
                    problem = "Qual o valor de 'Como' em: 'Como havia estudado o edital, resolveu a prova com segurança'?",
                    stepByStep = listOf(
                        "1. Teste substituir 'Como' no início da frase por 'Já que' / 'Visto que'.",
                        "2. 'Já que havia estudado o edital, resolveu a prova com segurança' -> manteve o sentido exato!",
                        "3. Portanto, não é comparação: é oração subordinada adverbial CAUSAL."
                    ),
                    answer = "Valor de CAUSA (equivalente a 'já que')."
                )
            ),
            examTricks = listOf(
                "'Posto que' em provas tradicionais da UEMA significa EMBORA (concessão), nunca 'já que'!",
                "'Conquanto' = EMBORA (concessão). 'Porquanto' = PORQUE (explicação/causa). Não confunda os dois!",
                "'Enquanto' indica tempo simultâneo ou contraste; 'Ao passo que' indica proporção ou contraste."
            ),
            memoryTechniques = listOf(
                "Regra dos 3 C's do 'Pois': Posposto ao verbo + entre vírgulas = Conclusivo (= portanto).",
                "ConQUANTO tem 'Q' de 'Que pena, mas aconteceu' (Concessão = embora). PorQUANTO tem 'Por' de Porquê (Causa/Explicação)."
            ),
            structuredSummary = listOf(
                "Adversativas ('contudo, todavia, porém') e Concessivas ('embora, conquanto, ainda que') indicam contraste.",
                "Concessivas pedem verbo no subjuntivo ('embora seja'); Adversativas usam indicativo ('mas é').",
                "'Pois' antes do verbo = explicação; depois do verbo e entre vírgulas = conclusão.",
                "Use conectivos variados ('ademais', 'outrossim', 'portanto', 'nesse viés') na Redação PAES/UEMA."
            ),
            practiceQuestionIds = listOf("q_port_11", "q_port_12"),
            finalExamQuestionIds = listOf("q_port_03", "q_port_09")
        )
    )

    val questions = listOf(
        QuestionItem(
            id = "q_port_01",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_regencia_crase",
            topicName = "Crase",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral elaborada conforme o tópico de Crase do PAES/UEMA.",
            statement = "Assinale a alternativa em que o emprego do acento indicativo de crase está CORRETO.",
            options = listOf(
                "O candidato começou à estudar cedo para o CFO.",
                "A entrega dos documentos ocorrerá à partir de segunda-feira.",
                "Os alunos dirigiram-se à biblioteca da universidade.",
                "Ele entregou a redação à uma professora da banca."
            ),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: não se usa crase antes de verbo no infinitivo ('estudar').",
                "Incorreta: 'partir' é verbo; além disso, 'a partir de' jamais leva crase.",
                "Correta: quem se dirige, dirige-se A (preposição) + A biblioteca (artigo feminino).",
                "Incorreta: antes de artigo indefinido 'uma' não ocorre crase."
            ),
            generalExplanation = "O verbo 'dirigir-se' exige a preposição 'a', e o substantivo feminino 'biblioteca' aceita o artigo 'a'. Trocando por masculino: 'dirigiram-se AO auditório', confirmando a crase.",
            examTrick = "Nunca há crase antes de verbo ou artigo indefinido ('uma')."
        ),
        QuestionItem(
            id = "q_port_02",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_regencia_crase",
            topicName = "Crase",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "Leia a frase: 'O projeto refere-se ___ metas que visam ___ melhoria da segurança e ___ atendimento rápido ___ população.' Assinale a sequência que preenche corretamente as lacunas.",
            options = listOf(
                "a – à – ao – à",
                "à – à – ao – a",
                "às – a – ao – à",
                "a – a – à – à"
            ),
            correctIndex = 0,
            optionExplanations = listOf(
                "Correta: 'a metas' (singular antes de plural = sem crase); 'visam à melhoria' (visar no sentido de objetivar pede 'a' + 'a melhoria'); 'ao atendimento' (masculino); 'à população' (atendimento a + a população).",
                "Incorreta: na última lacuna ocorre crase ('à população').",
                "Incorreta: 'visam' no sentido de objetivar exige preposição ('à melhoria').",
                "Incorreta: 'atendimento' é masculino ('ao'), não feminino."
            ),
            generalExplanation = "Analisando cada termo: (1) 'a metas' está no singular antes de palavra no plural, logo sem crase; (2) 'visar' = objetivar -> VTI com preposição 'a' + artigo 'a' = 'à melhoria'; (3) 'atendimento' é masculino -> 'ao'; (4) 'atendimento' exige 'a' + 'a população' = 'à população'.",
            examTrick = "A primeira lacuna já elimina alternativas erradas: 'a' singular + plural nunca leva crase!"
        ),
        QuestionItem(
            id = "q_port_03",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_regencia_crase",
            topicName = "Regência Verbal",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "Em qual alternativa a regência verbal obedece integralmente à norma-padrão?",
            options = listOf(
                "Prefiro mais estudar História do que revisar Física.",
                "O candidato chegou no local de prova com antecedência.",
                "Todos aspiravam ao cargo de Oficial da PMMA.",
                "O comandante procedeu a leitura do boletim diário."
            ),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: 'preferir' rejeita 'mais' e 'do que'; o padrão é 'Prefiro estudar História a revisar Física'.",
                "Incorreta: quem chega, chega A algum lugar ('chegou ao local'), não 'em'.",
                "Correta: 'aspirar' no sentido de almejar/desejar é transitivo indireto e exige preposição 'a' ('ao cargo').",
                "Incorreta: 'proceder' no sentido de dar início/executar exige preposição 'a' ('procedeu à leitura')."
            ),
            generalExplanation = "Na norma culta: 'aspirar' (respirar) é direto; 'aspirar' (desejar/almejar) é indireto com preposição 'a'. Já 'chegar' exige 'a', 'preferir' usa 'a' sem 'mais', e 'proceder' (realizar) exige 'a'.",
            examTrick = "Chegar, ir e voltar exigem preposição 'a', nunca 'em' na norma culta."
        ),
        QuestionItem(
            id = "q_port_04",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_regencia_crase",
            topicName = "Crase Facultativa e Obrigatória",
            difficulty = QuestionDifficulty.DIFICIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral de nível avançado — PAES/UEMA.",
            statement = "Assinale a alternativa em que o acento grave é OBRIGATÓRIO (e não facultativo nem proibido).",
            options = listOf(
                "Entreguei o formulário à minha coordenadora.",
                "Caminhou até à entrada do quartel.",
                "A medida entrou em vigor às pressas após a reunião.",
                "Fez uma dedicatória à Beatriz antes da formatura."
            ),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: antes de pronome possessivo feminino singular ('minha'), a crase é facultativa.",
                "Incorreta: depois da preposição 'até', a crase é facultativa.",
                "Correta: 'às pressas' é locução adverbial feminina de modo — a crase é obrigatória.",
                "Incorreta: antes de nome próprio feminino ('Beatriz'), a crase é facultativa."
            ),
            generalExplanation = "As três alternativas distratoras trazem exatamente os três casos clássicos de crase facultativa (P-N-A: Possessivo, Nome próprio feminino, preposição Até). Já 'às pressas' é locução adverbial feminina, exigindo crase obrigatória.",
            examTrick = "Lembre do trio P-N-A (Possessivo, Nome de mulher, Até) para identificar crase facultativa instantaneamente."
        ),
        QuestionItem(
            id = "q_port_05",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_regencia_crase",
            topicName = "Crase com Demonstrativos",
            difficulty = QuestionDifficulty.DIFICIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral de nível avançado — PAES/UEMA.",
            statement = "Assinale a opção em que a crase com pronome demonstrativo está empregada corretamente.",
            options = listOf(
                "Não me refiro àquela regra, mas à esta exceção.",
                "Obedecemos àquilo que foi determinado no edital.",
                "Entreguei os livros àquele mesmo aluno que me pediu.",
                "As alternativas B e C estão corretas."
            ),
            correctIndex = 3,
            optionExplanations = listOf(
                "Incorreta: não ocorre crase antes de 'esta' (pronome demonstrativo que não começa com 'a').",
                "Correta individualmente: quem obedece, obedece A + aquilo = àquilo.",
                "Correta individualmente: quem entrega, entrega algo A + aquele = àquele.",
                "Correta: tanto B quanto C empregam a fusão da preposição 'a' com 'aquilo'/'aquele'."
            ),
            generalExplanation = "Os pronomes 'aquele(s)', 'aquela(s)' e 'aquilo' recebem crase sempre que o termo anterior exigir a preposição 'a'. Já 'este/esta/isto' nunca recebem crase porque não iniciam com 'a'.",
            examTrick = "Troque 'àquele/àquilo' por 'a esse / a isso'. Se fizer sentido, a crase em 'àquele/àquilo' está certa!"
        ),
        QuestionItem(
            id = "q_port_06",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_concordancia",
            topicName = "Concordância Verbal",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "Indique a frase em que a concordância verbal com verbo impessoal está CORRETA.",
            options = listOf(
                "Houveram muitas dúvidas durante o simulado.",
                "Fazem cinco anos que o curso foi ampliado.",
                "Devem haver novas oportunidades neste semestre.",
                "Há vários motivos para manter a constância nos estudos."
            ),
            correctIndex = 3,
            optionExplanations = listOf(
                "Incorreta: 'haver' no sentido de existir é impessoal e deve ficar no singular ('Houve').",
                "Incorreta: 'fazer' indicando tempo decorrido é impessoal ('Faz cinco anos').",
                "Incorreta: na locução com 'haver' impessoal, o auxiliar também fica no singular ('Deve haver').",
                "Correta: 'Há' permanece na 3ª pessoa do singular por ser impessoal."
            ),
            generalExplanation = "Verbos impessoais não possuem sujeito e permanecem na 3ª pessoa do singular. 'Haver' (existir/tempo) e 'Fazer' (tempo/clima) são os campeões de prova.",
            examTrick = "Se você puder trocar 'haver' por 'existir', lembre-se: 'haver' trava no singular!"
        ),
        QuestionItem(
            id = "q_port_07",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_concordancia",
            topicName = "Partícula SE",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "Assinale a alternativa em que a concordância com a partícula 'se' está INCORRETA.",
            options = listOf(
                "Analisaram-se todas as propostas de redação.",
                "Precisa-se de candidatos disciplinados.",
                "Tratam-se de assuntos indispensáveis para a aprovação.",
                "Elaboram-se simulados semanais na plataforma."
            ),
            correctIndex = 2,
            optionExplanations = listOf(
                "Correta: 'Todas as propostas foram analisadas' (VTD + PA -> plural).",
                "Correta: 'Precisar de' é VTI -> 'se' é IIS -> singular obrigatório.",
                "Incorreta (gabarito): 'Tratar de' é VTI com preposição 'de'. O 'se' é IIS e o verbo deve ficar no singular ('Trata-se de assuntos').",
                "Correta: 'Simulados semanais são elaborados' (VTD + PA -> plural)."
            ),
            generalExplanation = "Na letra C, o verbo 'tratar' está acompanhado da preposição 'de', o que indica que o 'se' é Índice de Indeterminação do Sujeito. Assim, o verbo não pode ir para o plural.",
            examTrick = "'Trata-se de...' NUNCA vai para o plural!"
        ),
        QuestionItem(
            id = "q_port_08",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_concordancia",
            topicName = "Concordância Nominal e Verbal",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "Assinale a alternativa que respeita integralmente as normas de concordância.",
            options = listOf(
                "Anexo aos requerimentos seguem as certidões solicitadas.",
                "É proibido a entrada de aparelhos eletrônicos na sala.",
                "Mais de um candidato resolveu a questão com rapidez.",
                "Aluga-se salas de estudo no centro de São Luís."
            ),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: 'anexo' é adjetivo e concorda com 'certidões' ('Anexas aos requerimentos...').",
                "Incorreta: com determinante ('a entrada'), 'proibido' concorda no feminino ('É proibida a entrada').",
                "Correta: a expressão 'Mais de um' deixa o verbo no singular, salvo se indicar reciprocidade.",
                "Incorreta: 'salas' é sujeito paciente -> 'Alugam-se salas'."
            ),
            generalExplanation = "'Mais de um + substantivo singular' leva o verbo ao singular. Apenas em casos de reciprocidade ('Mais de um colega se abraçaram') vai ao plural.",
            examTrick = "'É proibido entrada' (sem artigo) = invariável. 'É proibida A entrada' (com artigo) = concorda!"
        ),
        QuestionItem(
            id = "q_port_09",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_sintaxe",
            topicName = "Período Composto e Pontuação",
            difficulty = QuestionDifficulty.DIFICIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "Compare as duas frases:\nI. Os candidatos que estudaram o edital avançaram de fase.\nII. Os candidatos, que estudaram o edital, avançaram de fase.\nSobre o sentido e a pontuação, é correto afirmar que:",
            options = listOf(
                "Em I e II o sentido é idêntico; as vírgulas são meramente estilísticas.",
                "Em I a oração adjetiva é restritiva (apenas parte dos candidatos estudou); em II é explicativa (todos estudaram).",
                "Em II a oração adjetiva restringe o conjunto de candidatos.",
                "A frase II apresenta erro gramatical por separar o sujeito do verbo."
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: a vírgula muda o sentido semântico da oração adjetiva.",
                "Correta: sem vírgulas = oração subordinada adjetiva restritiva (limita o grupo); com vírgulas = explicativa (generaliza uma característica de todo o grupo).",
                "Incorreta: é exatamente o contrário.",
                "Incorreta: não se trata de vírgula única entre sujeito e verbo, mas de um par de vírgulas isolando oração explicativa."
            ),
            generalExplanation = "As orações subordinadas adjetivas mudam de classificação e de sentido conforme o uso das vírgulas: sem vírgula = restritiva; entre vírgulas = explicativa.",
            examTrick = "Sem vírgula = Restringe parte do grupo. Entre vírgulas = Explica o grupo inteiro."
        ),
        QuestionItem(
            id = "q_port_10",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_morfologia",
            topicName = "Colocação Pronominal",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "Assinale a alternativa em que a colocação pronominal está de acordo com a norma culta.",
            options = listOf(
                "Me disseram que o resultado sai amanhã.",
                "Não entreguei-lhe o resumo ontem.",
                "Quem dedicou-se ao edital teve bom desempenho.",
                "Jamais se esqueça de revisar o caderno de erros."
            ),
            correctIndex = 3,
            optionExplanations = listOf(
                "Incorreta: não se inicia frase com pronome oblíquo átono na norma culta.",
                "Incorreta: palavra negativa ('Não') atrai o pronome para antes do verbo ('Não lhe entreguei').",
                "Incorreta: pronome relativo/interrogativo ('Quem') é fator de próclise ('Quem se dedicou').",
                "Correta: o advérbio negativo 'Jamais' atrai o pronome 'se' para antes do verbo (próclise)."
            ),
            generalExplanation = "Palavras de sentido negativo ('não', 'nunca', 'jamais'), pronomes relativos, indefinidos e conjunções subordinativas atraem o pronome oblíquo átono para antes do verbo (próclise).",
            examTrick = "Mnemônico de atração (Próclise): Nega, Relata, Subordina, Advérbio sem vírgula."
        ),
        QuestionItem(
            id = "q_port_11",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_sintaxe",
            topicName = "Conjunções e Coesão",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "No período 'Conquanto o cansaço fosse grande, concluiu sua meta diária de revisão', a conjunção destacada estabelece relação de:",
            options = listOf(
                "Causa",
                "Conclusão",
                "Concessão",
                "Condição"
            ),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: causa seria 'visto que' ou 'porque'.",
                "Incorreta: conclusão seria 'portanto' ou 'logo'.",
                "Correta: 'conquanto' equivale a 'embora', expressando concessão (fato contrário que não impede o principal).",
                "Incorreta: condição seria 'caso' ou 'desde que'."
            ),
            generalExplanation = "A conjunção 'conquanto' é sinônimo culto de 'embora', 'ainda que', 'apesar de que', introduzindo oração subordinada adverbial concessiva.",
            examTrick = "Conquanto = Embora (Concessão). Porquanto = Porque (Explicação/Causa)."
        ),
        QuestionItem(
            id = "q_port_12",
            subjectId = "portugues",
            subjectName = "Língua Portuguesa",
            topicId = "port_interpretacao",
            topicName = "Coesão Referencial e Semântica",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral no padrão PAES/UEMA.",
            statement = "Em um texto argumentativo, a expressão 'nesse viés' ou 'diante desse cenário' desempenha papel de:",
            options = listOf(
                "Coesão referencial e sequencial, retomando ideia anterior para dar continuidade.",
                "Contradição lógica entre introdução e conclusão.",
                "Falta de objetividade gramatical.",
                "Substituição obrigatória da tese inicial."
            ),
            correctIndex = 0,
            optionExplanations = listOf(
                "Correta: pronomes demonstrativos ('nesse', 'desse') retomam o que já foi mencionado (anáfora) e conectam os parágrafos.",
                "Incorreta: não há contradição, mas continuidade lógica.",
                "Incorreta: são recursos recomendados pela norma culta.",
                "Incorreta: eles sustentam a tese, não a substituem."
            ),
            generalExplanation = "Na coesão textual, os pronomes demonstrativos com '-ss-' ('esse', 'nesse', 'disso') retomam ideias já apresentadas no texto (função anafórica).",
            examTrick = "Retomou o que já escreveu antes? Use 'Esse/Nessa/Disso' (com SS)."
        )
    )

    val flashcards = listOf(
        FlashcardTemplate(
            id = "fc_port_1",
            subjectId = "portugues",
            topicTitle = "Crase Proibida",
            front = "Cite 4 situações em que a crase é terminantemente PROIBIDA.",
            back = "1) Antes de verbos;\n2) Antes de palavras masculinas;\n3) 'A' no singular antes de palavra no plural;\n4) Antes de pronomes pessoais e indefinidos ('ela', 'você', 'toda', 'cada').",
            memoryTip = "Troque pelo masculino: se não virar 'AO', não tem crase!"
        ),
        FlashcardTemplate(
            id = "fc_port_2",
            subjectId = "portugues",
            topicTitle = "Crase Facultativa",
            front = "Quais são os 3 casos clássicos de crase FACULTATIVA?",
            back = "1) Antes de pronome possessivo feminino singular ('à/a minha');\n2) Antes de nome próprio feminino ('à/a Ana');\n3) Após a preposição 'até' ('até à/a escola').",
            memoryTip = "Mnemônico P-N-A: Possessivo, Nome feminino, Até."
        ),
        FlashcardTemplate(
            id = "fc_port_3",
            subjectId = "portugues",
            topicTitle = "Partícula SE",
            front = "Como diferenciar 'Vende-se / Vendem-se' de 'Precisa-se'?",
            back = "Sem preposição (VTD): 'se' é Pronome Apassivador e o verbo concorda com o sujeito ('Vendem-se casas' = Casas são vendidas).\nCom preposição (VTI/VI): 'se' é Índice de Indeterminação e o verbo fica SEMPRE no singular ('Precisa-se de casas').",
            memoryTip = "Preposição depois do verbo + se? Trava o verbo no singular!"
        ),
        FlashcardTemplate(
            id = "fc_port_4",
            subjectId = "portugues",
            topicTitle = "Conjunções",
            front = "Qual a diferença entre 'Conquanto' e 'Porquanto'?",
            back = "'Conquanto' = Embora (Concessiva).\n'Porquanto' = Porque / Visto que (Explicativa ou Causal).",
            memoryTip = "PORquanto lembra PORquê; CONquanto = CONcessão."
        )
    )
}
