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

object HumanitiesCurriculum {
    val subjects = listOf(
        SubjectDefinition(
            id = "literatura",
            name = "Literatura Brasileira e Maranhense",
            shortName = "Literatura",
            area = KnowledgeArea.LINGUAGENS,
            description = "Escolas literárias, literatura maranhense (Gonçalves Dias, Aluísio Azevedo, Josué Montello, Ferreira Gullar) e análise literária.",
            editalStatusNote = "Baseado no programa de Literatura do PAES/UEMA; as obras literárias específicas mudam a cada edital anual publicado pela UEMA.",
            topics = listOf(
                EditalTopic("lit_escolas", "literatura", "Escolas Literárias: Barroco ao Pré-Modernismo", listOf("Barroco e Arcadismo", "Romantismo (1ª, 2ª e 3ª gerações)", "Realismo, Naturalismo e Parnasianismo"), priorityWeight = 2),
                EditalTopic("lit_modernismo", "literatura", "Modernismo e Contemporaneidade", listOf("Semana de 22", "Geração de 30 e 45", "Poesia Concreta e Neoconcretismo"), priorityWeight = 2),
                EditalTopic("lit_maranhao", "literatura", "Tradição Literária Maranhense na UEMA", listOf("Gonçalves Dias (Indianismo)", "Aluísio Azevedo (Naturalismo)", "Sousândrade, Graça Aranha, Josué Montello e Ferreira Gullar"), priorityWeight = 3)
            )
        ),
        SubjectDefinition(
            id = "estrangeira",
            name = "Língua Estrangeira (Inglês / Espanhol)",
            shortName = "Inglês/Esp.",
            area = KnowledgeArea.LINGUAGENS,
            description = "Leitura instrumental, inferência lexical, falsos cognatos, conectivos e interpretação de textos em língua estrangeira.",
            editalStatusNote = "Matriz curricular do PAES/UEMA (o candidato opta por Inglês ou Espanhol na inscrição).",
            topics = listOf(
                EditalTopic("est_leitura", "estrangeira", "Estratégias de Leitura (Skimming e Scanning)", listOf("Cognatos e falsos cognatos", "Referência pronominal", "Conectores lógicos"), priorityWeight = 2),
                EditalTopic("est_gramatica", "estrangeira", "Estruturas Verbais e Modais em Contexto", listOf("Tempos verbais em Inglês", "Heterosemânticos em Espanhol", "Voz passiva e condicionais"), priorityWeight = 2)
            )
        ),
        SubjectDefinition(
            id = "historia",
            name = "História (Geral, Brasil e Maranhão)",
            shortName = "História",
            area = KnowledgeArea.HUMANAS,
            description = "Da Antiguidade à Nova República, com foco especial na História do Maranhão exigida pelo PAES/UEMA.",
            editalStatusNote = "Matriz curricular confirmada no PAES/UEMA incluindo História Regional do Maranhão.",
            topics = listOf(
                EditalTopic("hist_maranhao", "historia", "História do Maranhão", listOf("França Equinocial (1612)", "Revolta de Bequimão (1684)", "Adesão do Maranhão à Independência (1823)", "Balaiada (1838–1841)"), priorityWeight = 3),
                EditalTopic("hist_brasil", "historia", "História do Brasil (Colônia, Império e República)", listOf("Ciclo açucareiro e minerador", "Primeiro e Segundo Reinado", "Era Vargas, Regime Militar e Redemocratização"), priorityWeight = 3),
                EditalTopic("hist_geral", "historia", "História Geral e Contemporânea", listOf("Antiguidade Clássica e Feudalismo", "Revolução Industrial e Francesa", "Guerras Mundiais e Guerra Fria"), priorityWeight = 2)
            )
        ),
        SubjectDefinition(
            id = "geografia",
            name = "Geografia (Geral, Brasil e Maranhão)",
            shortName = "Geografia",
            area = KnowledgeArea.HUMANAS,
            description = "Cartografia, climatologia, urbanização, geopolítica e aspectos físicos/econômicos do território maranhense.",
            editalStatusNote = "Matriz curricular confirmada no PAES/UEMA incluindo Geografia do Maranhão.",
            topics = listOf(
                EditalTopic("geo_maranhao", "geografia", "Geografia do Maranhão", listOf("Domínios: Amazônia, Cerrado e Mata dos Cocais", "Bacias do Itapecuru, Mearim e Tocantins", "Porto do Itaqui, Carajás e MATOPIBA"), priorityWeight = 3),
                EditalTopic("geo_fisica", "geografia", "Cartografia, Relevo, Clima e Meio Ambiente", listOf("Escalas e fusos horários", "Massas de ar e biomas brasileiros", "Impactos ambientais"), priorityWeight = 2),
                EditalTopic("geo_humana", "geografia", "População, Urbanização e Geopolítica", listOf("Transição demográfica", "Agronegócio e questão agrária", "Globalização e blocos econômicos"), priorityWeight = 2)
            )
        ),
        SubjectDefinition(
            id = "filo_socio",
            name = "Filosofia e Sociologia",
            shortName = "Filo/Socio",
            area = KnowledgeArea.HUMANAS,
            description = "Ética, teoria política, cidadania, direitos humanos, cultura, trabalho e pensamento sociológico clássico e contemporâneo.",
            editalStatusNote = "Matriz curricular confirmada no PAES/UEMA para Ciências Humanas e suas Tecnologias.",
            topics = listOf(
                EditalTopic("fs_filosofia", "filo_socio", "Filosofia: Ética, Política e Conhecimento", listOf("Sócrates, Platão e Aristóteles", "Contratualistas: Hobbes, Locke e Rousseau", "Kant e ética do dever"), priorityWeight = 2),
                EditalTopic("fs_sociologia", "filo_socio", "Sociologia: Estado, Cultura e Cidadania", listOf("Durkheim, Weber e Marx", "Cidadania e Direitos Humanos", "Movimentos sociais e violência urbana"), priorityWeight = 2)
            )
        )
    )

    val lessons = listOf(
        LessonContent(
            id = "lesson_hist_maranhao",
            subjectId = "historia",
            topicId = "hist_maranhao",
            title = "História do Maranhão: França Equinocial, Bequimão e Balaiada",
            estimatedMinutes = 25,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "Diferente de vestibulares nacionais genéricos, o PAES/UEMA cobra com frequência processos históricos próprios do Maranhão! Entender por que o Maranhão teve ligação direta com Lisboa (Estado do Maranhão e Grão-Pará) e os conflitos locais garante pontos decisivos.",
            deepExplanation = "1) FRANÇA EQUINOCIAL (1612–1615):\n• Os franceses (Daniel de La Touche) fundaram o Forte de São Luís em 1612 na Ilha de Upaon-Açu, buscando criar uma colônia francesa no norte da América Portuguesa.\n• Foram expulsos pelas forças luso-espanholas (União Ibérica) lideradas por Jerônimo de Albuquerque e Alexandre de Moura após a Batalha de Guaxenduba (1614/1615).\n\n2) REVOLTA DE BEQUIMÃO (1684):\n• Movimento nativista liderado pelos irmãos Manuel e Tomás Beckman contra o monopólio da Companhia de Comércio do Estado do Maranhão (que não fornecia escravizados nem comprava produtos locais por preço justo) e contra a oposição dos jesuítas à escravização indígena.\n• Manuel Beckman foi preso e executado; contudo, a Coroa acabou extinguindo a Companhia de Comércio.\n\n3) ADESÃO DO MARANHÃO À INDEPENDÊNCIA (28 DE JULHO DE 1823):\n• O Maranhão era fiel às Cortes de Lisboa devido aos fortes laços comerciais marítimos.\n• Só aderiu à Independência do Brasil em 28/07/1823 (feriado estadual no Maranhão!) após atuação militar e naval de Lorde Cochrane e das tropas brasileiras no interior (Caxias).\n\n4) BALAIADA (1838–1841):\n• Grande revolta popular do Período Regencial no Maranhão envolvendo sertanejos pobres, vaqueiros, artesãos e escravizados quilombolas.\n• Principais líderes: Raimundo Gomes (vaqueiro, conhecido como Cara Preta), Manuel Francisco dos Anjos Ferreira (o 'Balaio', fabricante de cestos) e Cosme Bento das Chagas (o 'Negro Cosme', líder quilombola de cerca de 3 mil ex-escravizados).\n• Foi reprimida duramente pelo então coronel Luís Alves de Lima e Silva (futuro Duque de Caxias).",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — Conectando Líderes e Motivações da Balaiada",
                    problem = "Por que a Balaiada é considerada uma revolta de caráter popular e heterogêneo?",
                    stepByStep = listOf(
                        "1. Identifique a base social: não era apenas uma briga de elites locais (Bem-te-vis vs Cabanos), mas envolveu camadas populares marginalizadas.",
                        "2. Veja os 3 líderes principais: Raimundo Gomes (vaqueiro), Manoel 'Balaio' (artesão) e Negro Cosme (líder quilombola).",
                        "3. Reivindicações: fim do recrutamento forçado militar, abuso de autoridades provinciais e liberdade contra a escravidão."
                    ),
                    answer = "Uniu trabalhadores livres pobres, artesãos e quilombolas contra a opressão da elite provincial."
                )
            ),
            examTricks = listOf(
                "28 de Julho de 1823: Data da Adesão do Maranhão à Independência do Brasil (não foi em 7 de setembro de 1822!).",
                "Não confunda o líder Luís Alves de Lima e Silva (Duque de Caxias): o município maranhense de Caxias recebeu o título dele após a repressão à Balaiada!",
                "Revolta de Bequimão (1684): foco na Companhia de Comércio do Maranhão e conflito com jesuítas."
            ),
            memoryTechniques = listOf(
                "Linha do tempo 16-16-18-18: 1612 (França Equinocial) -> 1684 (Bequimão) -> 1823 (Adesão à Independência) -> 1838 (Balaiada).",
                "3 nomes da Balaiada: 'Ra-Ba-Co' -> Raimundo Gomes, Balaio e Cosme."
            ),
            structuredSummary = listOf(
                "1612–1615: França Equinocial expulsa após Guaxenduba.",
                "1684: Revolta de Bequimão contra monopólio da Companhia de Comércio.",
                "28/07/1823: Adesão do Maranhão à Independência.",
                "1838–1841: Balaiada (revolta popular reprimida pelo futuro Duque de Caxias)."
            ),
            practiceQuestionIds = listOf("q_hist_01", "q_hist_02"),
            finalExamQuestionIds = listOf("q_hist_03")
        ),
        LessonContent(
            id = "lesson_geo_maranhao",
            subjectId = "geografia",
            topicId = "geo_maranhao",
            title = "Geografia do Maranhão: Domínios, Bacias e Economia",
            estimatedMinutes = 20,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "O Maranhão ocupa uma posição geográfica estratégica de transição entre o Norte (Amazônia), o Nordeste (semiárido/litoral) e o Centro-Oeste (Cerrado). Por isso, possui diversidade de biomas e forte logística portuária e ferroviária.",
            deepExplanation = "1) BIOMAS E MATA DOS COCAIS:\n• Oeste do Maranhão: Floresta Amazônica (clima equatorial úmido).\n• Sul e Leste: Cerrado (chapadões e expansão agrícola do MATOPIBA — Maranhão, Tocantins, Piauí, Bahia).\n• Faixa central de transição: Mata dos Cocais (palmeiras de babaçu e carnaúba — grande importância socioeconômica para quebradeiras de coco babaçu).\n• Litoral: Manguezais, Golfão Maranhense e os Lençóis Maranhenses (campos de dunas e lagoas interdunares alimentadas pelas chuvas).\n\n2) HIDROGRAFIA:\n• Bacias genuinamente maranhenses de destaque: Rios Itapecuru (abastece grande parte do estado, inclusive São Luís) e Mearim (com seu afluente Pindaré).\n• Rios de divisa: Tocantins (a oeste, divisa com TO) e Parnaíba (a leste, divisa com PI).\n\n3) INFRAESTRUTURA E ECONOMIA:\n• Complexo Portuário do Itaqui (São Luís): águas profundas, escoa minério de ferro da Estrada de Ferro Carajás (Vale) e grãos da Ferrovia Norte-Sul.",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — O Papel da Mata dos Cocais",
                    problem = "Por que o Maranhão é chamado de estado de transição ecológica?",
                    stepByStep = listOf(
                        "1. Localize o estado no mapa: a oeste recebe influência úmida da Amazônia; ao sul/sudeste predomina o Cerrado; a leste aproxima-se da Caatinga.",
                        "2. Entre esses grandes domínios surge a Mata dos Cocais, rica em babaçu e carnaúba."
                    ),
                    answer = "Porque concentra uma faixa ecótona (Mata dos Cocais) entre Amazônia, Cerrado e Caatinga."
                )
            ),
            examTricks = listOf(
                "Rio Itapecuru e Rio Mearim são os grandes rios genuinamente maranhenses; Parnaíba faz fronteira com o Piauí!",
                "MATOPIBA = Maranhão, Tocantins, Piauí e Bahia (fronteira agrícola do Cerrado).",
                "Porto do Itaqui é conectado à Estrada de Ferro Carajás (minério) e Ferrovia Norte-Sul (grãos)."
            ),
            memoryTechniques = listOf(
                "3 Áreas no Maranhão: Oeste = Amazônia (A); Centro = Cocais (C); Sul = Cerrado (C).",
                "Divisas aquáticas: Tocantins (TO) à esquerda, Parnaíba (PI) à direita, Itapecuru/Mearim no coração do MA."
            ),
            structuredSummary = listOf(
                "Transição ecológica: Amazônia (oeste), Mata dos Cocais (centro), Cerrado (sul/leste).",
                "Bacias centrais: Itapecuru e Mearim; fronteiras: Tocantins e Parnaíba.",
                "Economia: Porto do Itaqui, Carajás, agronegócio no MATOPIBA e extrativismo do babaçu."
            ),
            practiceQuestionIds = listOf("q_geo_01", "q_geo_02"),
            finalExamQuestionIds = listOf("q_geo_03")
        ),
        LessonContent(
            id = "lesson_lit_maranhao",
            subjectId = "literatura",
            topicId = "lit_maranhao",
            title = "Autores Maranhenses e Escolas Literárias no PAES/UEMA",
            estimatedMinutes = 20,
            editalBadge = "Confirmado no Edital PAES/UEMA",
            basicExplanation = "São Luís ficou conhecida historicamente como a 'Atenas Brasileira' pela riqueza de seus escritores. O PAES/UEMA valoriza tanto as características das escolas literárias quanto o contexto social das obras maranhenses.",
            deepExplanation = "• GONÇALVES DIAS (Romantismo — 1ª Geração): nascido em Caxias (MA). Símbolo do Indianismo e nacionalismo ('Canção do Exílio', 'I-Juca-Pirama'). Exalta a natureza brasileira e o indígena como herói ético.\n• SOUSÂNDRADE (Romantismo avançado): autor de 'O Guesa', inovador na linguagem e crítica ao capitalismo em Nova York ('O Inferno de Wall Street').\n• ALUÍSIO AZEVEDO (Naturalismo): nascido em São Luís. Romances de tese com determinismo, zoomorfização e crítica social: 'O Mulato' (ambientado em São Luís, critica o preconceito racial e o clero), 'Casa de Pensão' e 'O Cortiço'.\n• JOSUÉ MONTELLO (Modernismo/Contemporâneo): cronista da memória ludovicense ('Os Tambores de São Luís', abordando escravidão, resistência e cultura maranhense).\n• FERREIRA GULLAR (Neoconcretismo e poesia social): nascido em São Luís, autor de 'Poema Sujo' (escrito no exílio em 1975, revisitando a infância e as ruas de São Luís).",
            workedExamples = listOf(
                WorkedExample(
                    title = "Exemplo 1 — 'O Mulato' e o Naturalismo",
                    problem = "Como o romance 'O Mulato' (1881), de Aluísio Azevedo, dialoga com o contexto maranhense?",
                    stepByStep = listOf(
                        "1. A narrativa se passa em São Luís do século XIX.",
                        "2. O protagonista Raimundo, educado na Europa, sofre discriminação racial e religiosa da sociedade local ao tentar casar-se com Ana Rosa.",
                        "3. Marca o início do Naturalismo no Brasil, com análise social crítica."
                    ),
                    answer = "Inaugura o Naturalismo brasileiro denunciando o racismo e a hipocrisia social na São Luís oitocentista."
                )
            ),
            examTricks = listOf(
                "Aluísio Azevedo ('O Mulato', 1881) = marco inicial do Naturalismo no Brasil!",
                "Gonçalves Dias = Caxias (MA), Indianismo romântico.",
                "Ferreira Gullar = 'Poema Sujo' (memória de São Luís e engajamento político)."
            ),
            memoryTechniques = listOf(
                "Quarteto de ouro do MA: Dias (Índio), Azevedo (Naturalismo), Montello (Tambores), Gullar (Poema Sujo)."
            ),
            structuredSummary = listOf(
                "Romantismo: Gonçalves Dias (Indianismo, Caxias-MA).",
                "Naturalismo: Aluísio Azevedo ('O Mulato', 'O Cortiço').",
                "Modernismo/Contemporâneo: Josué Montello e Ferreira Gullar."
            ),
            practiceQuestionIds = listOf("q_lit_01", "q_lit_02"),
            finalExamQuestionIds = listOf("q_lit_01")
        )
    )

    val questions = listOf(
        QuestionItem(
            id = "q_hist_01",
            subjectId = "historia",
            subjectName = "História",
            topicId = "hist_maranhao",
            topicName = "Balaiada",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — História do Maranhão (PAES/UEMA).",
            statement = "A Balaiada (1838–1841), ocorrida no Maranhão durante o Período Regencial, destacou-se por reunir diferentes grupos populares, incluindo:",
            options = listOf(
                "Vaqueiros, artesãos e quilombolas liderados por nomes como Raimundo Gomes, Manoel 'Balaio' e Negro Cosme.",
                "Apenas latifundiários algodoeeiros contrários à abolição imediata.",
                "Invasores franceses interessados em retomar o Forte de São Luís.",
                "Imigrantes europeus da região Sul do Brasil."
            ),
            correctIndex = 0,
            optionExplanations = listOf(
                "Correta: a Balaiada teve forte participação popular e quilombola.",
                "Incorreta: a elite local acabou reprimindo o levante popular.",
                "Incorreta: a França Equinocial ocorreu no século XVII (1612–1615).",
                "Incorreta: sem relação histórica com o movimento."
            ),
            generalExplanation = "A Balaiada combinou disputas políticas regionais com uma rebelião popular de vaqueiros, sertanejos e escravizados quilombolas (Cosme Bento das Chagas).",
            examTrick = "Balaiada = Período Regencial (1838-1841), Ra-Ba-Co + repressão de Duque de Caxias."
        ),
        QuestionItem(
            id = "q_hist_02",
            subjectId = "historia",
            subjectName = "História",
            topicId = "hist_maranhao",
            topicName = "Revolta de Bequimão e Independência",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — História do Maranhão (PAES/UEMA).",
            statement = "Sobre a Revolta de Bequimão (1684) no Maranhão, é correto afirmar que foi motivada principalmente:",
            options = listOf(
                "Pela insatisfação dos colonos com o monopólio da Companhia de Comércio do Estado do Maranhão e com os conflitos envolvendo os jesuítas sobre a mão de obra indígena.",
                "Pela Proclamação da República em 1889.",
                "Pela descoberta de grandes jazidas de ouro na Baixada Maranhense.",
                "Pelo apoio irrestrito de Manuel Beckman ao domínio holandês."
            ),
            correctIndex = 0,
            optionExplanations = listOf(
                "Correta: a Companhia de Comércio não cumpria os prazos de fornecimento de mão de obra africana e praticava preços abusivos.",
                "Incorreta: erro cronológico de mais de 200 anos.",
                "Incorreta: a economia local baseava-se em drogas do sertão e agricultura.",
                "Incorreta: não teve relação com domínio holandês."
            ),
            generalExplanation = "Em 1684, colonos liderados por Manuel Beckman rebelaram-se contra os abusos da Companhia de Comércio e expulsaram temporariamente os jesuítas.",
            examTrick = "Bequimão (1684) = Monopólio comercial + questão indígena com jesuítas."
        ),
        QuestionItem(
            id = "q_hist_03",
            subjectId = "historia",
            subjectName = "História",
            topicId = "hist_brasil",
            topicName = "Era Vargas e República",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — História do Brasil (PAES/UEMA).",
            statement = "A Constituição de 1937, outorgada por Getúlio Vargas na instauração do Estado Novo, ficou conhecida como 'Polaca' e caracterizou-se por:",
            options = listOf(
                "Ampliar o voto direto e a autonomia plena dos governadores estaduais.",
                "Centralizar o poder no Executivo, extinguir partidos políticos e instituir censura prévia.",
                "Implantar o parlamentarismo e garantir liberdade sindical irrestrita.",
                "Transferir imediatamente a capital federal para Brasília."
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: interventores nomeados substituíram governadores eleitos.",
                "Correta: o Estado Novo (1937–1945) foi um regime autoritário e centralizador (DIP, censura, fechamento do Congresso).",
                "Incorreta: os sindicatos passaram a ser tutelados pelo Estado.",
                "Incorreta: Brasília foi inaugurada em 1960 (governo JK)."
            ),
            generalExplanation = "O Estado Novo concentrou poderes no Executivo federal, fechou o Legislativo, suspendeu eleições e criou o DIP (Departamento de Imprensa e Propaganda).",
            examTrick = "Constituição de 1937 = Outorgada (imposta), autoritária, inspirada em modelos europeus autoritários."
        ),
        QuestionItem(
            id = "q_geo_01",
            subjectId = "geografia",
            subjectName = "Geografia",
            topicId = "geo_maranhao",
            topicName = "Geografia do Maranhão",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Geografia do Maranhão (PAES/UEMA).",
            statement = "A formação vegetal típica da zona de transição ecológica localizada no centro do Maranhão, marcada pela presença expressiva de palmeiras de babaçu e carnaúba, denomina-se:",
            options = listOf(
                "Mata de Araucárias",
                "Mata dos Cocais",
                "Campos Sulinos",
                "Pântano Costeiro"
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: típica da Região Sul do Brasil.",
                "Correta: a Mata dos Cocais ocupa a faixa de transição entre Amazônia, Cerrado e Caatinga no Maranhão e Piauí.",
                "Incorreta: Pampa gaúcho.",
                "Incorreta: não corresponde ao bioma de palmeiras."
            ),
            generalExplanation = "A Mata dos Cocais é um ecótono estratégico no Maranhão, base do extrativismo sustentável do babaçu.",
            examTrick = "Babaçu + Carnaúba + Transição no MA/PI = Mata dos Cocais."
        ),
        QuestionItem(
            id = "q_geo_02",
            subjectId = "geografia",
            subjectName = "Geografia",
            topicId = "geo_maranhao",
            topicName = "Hidrografia e Logística do MA",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Geografia do Maranhão (PAES/UEMA).",
            statement = "Qual bacia hidrográfica nasce no centro-sul do Maranhão e desempenha papel vital no abastecimento hídrico de grande parte da população, incluindo a Região Metropolitana de São Luís?",
            options = listOf(
                "Rio Itapecuru",
                "Rio São Francisco",
                "Rio Paraná",
                "Rio Negro"
            ),
            correctIndex = 0,
            optionExplanations = listOf(
                "Correta: o Rio Itapecuru é o principal rio genuinamente maranhense no abastecimento da capital e de dezenas de municípios.",
                "Incorreta: o São Francisco não banha o Maranhão.",
                "Incorreta: bacia do Centro-Sul do país.",
                "Incorreta: afluente do Amazonas no estado do Amazonas."
            ),
            generalExplanation = "A bacia do Rio Itapecuru percorre o Maranhão de sul a norte até desaguar na Baía de São José.",
            examTrick = "Abastecimento de São Luís (Sistema Italuís) -> Rio Itapecuru!"
        ),
        QuestionItem(
            id = "q_geo_03",
            subjectId = "geografia",
            subjectName = "Geografia",
            topicId = "geo_fisica",
            topicName = "Escala Cartográfica",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Cartografia (PAES/UEMA).",
            statement = "Em um mapa do Maranhão na escala 1:500.000, a distância em linha reta entre dois municípios mede 4 cm. Qual é a distância real aproximada entre eles?",
            options = listOf("2 km", "20 km", "200 km", "50 km"),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: erro de uma casa decimal.",
                "Correta: 1 cm no mapa = 500.000 cm = 5.000 m = 5 km. Logo, 4 cm × 5 km = 20 km.",
                "Incorreta: 200 km seria se fosse 40 cm.",
                "Incorreta: cálculo incorreto."
            ),
            generalExplanation = "Para transformar centímetros em quilômetros rapidamente, corte 5 zeros da escala: 500.000 cm = 5 km. Depois multiplique pelos centímetros medidos: 4 × 5 = 20 km.",
            examTrick = "Cortar 5 zeros transforma centímetros direto em quilômetros! (100.000 cm = 1 km)."
        ),
        QuestionItem(
            id = "q_lit_01",
            subjectId = "literatura",
            subjectName = "Literatura",
            topicId = "lit_maranhao",
            topicName = "Naturalismo e Romantismo Maranhense",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Literatura Maranhense (PAES/UEMA).",
            statement = "Qual escritor maranhense inaugurou o Naturalismo no Brasil com a obra 'O Mulato' (1881), criticando o preconceito racial e a hipocrisia social em São Luís?",
            options = listOf(
                "Gonçalves Dias",
                "Aluísio Azevedo",
                "José de Alencar",
                "Machado de Assis"
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: Gonçalves Dias foi poeta romântico da 1ª geração.",
                "Correta: Aluísio Azevedo publicou 'O Mulato' em 1881, marco inicial do Naturalismo brasileiro.",
                "Incorreta: José de Alencar era cearense e romântico.",
                "Incorreta: Machado de Assis é o grande nome do Realismo, natural do Rio de Janeiro."
            ),
            generalExplanation = "1881 marca tanto o Realismo ('Memórias Póstumas de Brás Cubas', de Machado) quanto o Naturalismo ('O Mulato', do maranhense Aluísio Azevedo).",
            examTrick = "1881 duplo: Machado (Realismo) e Aluísio Azevedo (Naturalismo no Maranhão)."
        ),
        QuestionItem(
            id = "q_lit_02",
            subjectId = "literatura",
            subjectName = "Literatura",
            topicId = "lit_modernismo",
            topicName = "Poesia Contemporânea",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Literatura (PAES/UEMA).",
            statement = "Escrito durante o exílio em 1975, o livro 'Poema Sujo', que revisita memórias da infância em São Luís do Maranhão com forte carga política e inventividade formal, foi escrito por:",
            options = listOf(
                "Ferreira Gullar",
                "Olavo Bilac",
                "Sousândrade",
                "Gregório de Matos"
            ),
            correctIndex = 0,
            optionExplanations = listOf(
                "Correta: Ferreira Gullar (José Ribamar Ferreira), nascido em São Luís, escreveu 'Poema Sujo' em Buenos Aires.",
                "Incorreta: poeta parnasiano.",
                "Incorreta: poeta romântico do século XIX.",
                "Incorreta: poeta barroco do século XVII."
            ),
            generalExplanation = "Ferreira Gullar é um dos maiores poetas brasileiros do século XX e referência recorrente na UEMA.",
            examTrick = "Ferreira Gullar = São Luís + Neoconcretismo + 'Poema Sujo'."
        ),
        QuestionItem(
            id = "q_est_01",
            subjectId = "estrangeira",
            subjectName = "Língua Estrangeira",
            topicId = "est_leitura",
            topicName = "Leitura Instrumental (Inglês)",
            difficulty = QuestionDifficulty.FACIL,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Língua Estrangeira (PAES/UEMA).",
            statement = "In the sentence: 'Although the candidates were tired, they maintained their daily study routine', the word 'Although' expresses an idea of:",
            options = listOf(
                "Addition (Adição)",
                "Concession / Contrast (Concessão / Contraste)",
                "Cause (Causa)",
                "Conclusion (Conclusão)"
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: adição seria 'moreover' ou 'in addition'.",
                "Correta: 'Although' significa 'embora', indicando concessão/contraste.",
                "Incorreta: causa seria 'because' ou 'since'.",
                "Incorreta: conclusão seria 'therefore' ou 'thus'."
            ),
            generalExplanation = "'Although', 'though' e 'even though' equivalem à conjunção concessiva 'embora' em português.",
            examTrick = "Conector-chave em Inglês: Although/However = contraste; Therefore = conclusão; Since/Because = causa."
        ),
        QuestionItem(
            id = "q_est_02",
            subjectId = "estrangeira",
            subjectName = "Língua Estrangeira",
            topicId = "est_gramatica",
            topicName = "Falsos Cognatos (Inglês/Espanhol)",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Língua Estrangeira (PAES/UEMA).",
            statement = "Em textos em inglês e espanhol, falsos cognatos geram pegadinhas clássicas. Qual alternativa apresenta a tradução CORRETA?",
            options = listOf(
                "Inglês: 'Actually' = Atualmente.",
                "Inglês: 'Pretend' = Pretender.",
                "Espanhol: 'Taller' = Oficina / Laboratório prático.",
                "Espanhol: 'Exquisito' = Estranho ou feio."
            ),
            correctIndex = 2,
            optionExplanations = listOf(
                "Incorreta: 'Actually' significa 'na verdade'; atualmente é 'currently'.",
                "Incorreta: 'Pretend' significa 'fingir'; pretender é 'intend'.",
                "Correta: em espanhol, 'taller' significa oficina/workshop.",
                "Incorreta: 'exquisito' em espanhol significa delicioso/refinado."
            ),
            generalExplanation = "Identificar falsos cognatos (false friends) evita erros de interpretação de texto no PAES.",
            examTrick = "Actually = na verdade! Currently = atualmente!"
        ),
        QuestionItem(
            id = "q_fs_01",
            subjectId = "filo_socio",
            subjectName = "Filosofia e Sociologia",
            topicId = "fs_filosofia",
            topicName = "Contratualismo e Ética",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Filosofia Política (PAES/UEMA).",
            statement = "Para John Locke, o pacto social que origina o Estado político tem como finalidade principal:",
            options = listOf(
                "Eliminar completamente a propriedade privada e submeter os cidadãos a um soberano absoluto.",
                "Proteger e garantir os direitos naturais preexistentes, como a vida, a liberdade e a propriedade.",
                "Substituir a razão pela tradição mítica.",
                "Impedir qualquer possibilidade de resistência civil diante de governos tirânicos."
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: Hobbes defendia o soberano absoluto, não Locke.",
                "Correta: Locke é o pai do liberalismo contratualista; o Estado existe para preservar direitos naturais (vida, liberdade, bens).",
                "Incorreta: Locke era empirista iluminista.",
                "Incorreta: Locke defendia o direito de resistência caso o governo violasse o pacto."
            ),
            generalExplanation = "Enquanto Hobbes via o estado de natureza como guerra constante exigindo um Leviatã absoluto, Locke defendia que o Estado limitado garante direitos naturais.",
            examTrick = "Hobbes = Absoluto; Locke = Liberdade e Propriedade; Rousseau = Vontade Geral."
        ),
        QuestionItem(
            id = "q_fs_02",
            subjectId = "filo_socio",
            subjectName = "Filosofia e Sociologia",
            topicId = "fs_sociologia",
            topicName = "Clássicos da Sociologia",
            difficulty = QuestionDifficulty.MEDIO,
            origin = QuestionOrigin.PADRAO_PAES_UEMA,
            sourceDetail = "Questão autoral — Sociologia (PAES/UEMA).",
            statement = "Segundo Émile Durkheim, o objeto de estudo da Sociologia é o 'fato social', cujas três características essenciais são:",
            options = listOf(
                "Subjetividade, isolamento e espontaneidade individual.",
                "Coercitividade, exterioridade e generalidade.",
                "Luta de classes, mais-valia e alienação.",
                "Ação carismática, ação afetiva e desencantamento."
            ),
            correctIndex = 1,
            optionExplanations = listOf(
                "Incorreta: o fato social não nasce da vontade isolada de um indivíduo.",
                "Correta: para Durkheim, o fato social impõe-se ao indivíduo (coercitividade), vem de fora da consciência individual (exterioridade) e repete-se na coletividade (generalidade).",
                "Incorreta: conceitos de Karl Marx.",
                "Incorreta: conceitos de Max Weber."
            ),
            generalExplanation = "Os três pilares clássicos: Durkheim (Fato Social), Weber (Ação Social) e Marx (Classes Sociais / Modo de Produção).",
            examTrick = "Fato Social de Durkheim = CEG (Coercitividade, Exterioridade, Generalidade)."
        )
    )

    val flashcards = listOf(
        FlashcardTemplate(
            id = "fc_hist_1",
            subjectId = "historia",
            topicTitle = "Adesão do Maranhão",
            front = "Em que data o Maranhão aderiu oficialmente à Independência do Brasil?",
            back = "28 de julho de 1823 (com atuação naval de Lorde Cochrane em São Luís).",
            memoryTip = "28 de julho é feriado estadual da Adesão do Maranhão!"
        ),
        FlashcardTemplate(
            id = "fc_geo_1",
            subjectId = "geografia",
            topicTitle = "Escala Cartográfica",
            front = "Como converter rapidamente uma escala numérica (ex: 1:250.000) para km por cm?",
            back = "Corte 5 zeros do denominador! 250.000 cm = 2,5 km para cada 1 cm no mapa.",
            memoryTip = "1 km = 100.000 cm (5 zeros)."
        ),
        FlashcardTemplate(
            id = "fc_fs_1",
            subjectId = "filo_socio",
            topicTitle = "Clássicos Sociológicos",
            front = "Qual o objeto de estudo de Durkheim, Weber e Marx?",
            back = "Durkheim: Fato Social (CEG).\nWeber: Ação Social.\nMarx: Relações de produção e Luta de Classes.",
            memoryTip = "D = Fato; W = Ação; M = Classe."
        )
    )
}
