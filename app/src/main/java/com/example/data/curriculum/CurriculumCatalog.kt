package com.example.data.curriculum

import com.example.model.BadgeDefinition
import com.example.model.EssayTheme
import com.example.model.FlashcardTemplate
import com.example.model.LessonContent
import com.example.model.QuestionDifficulty
import com.example.model.QuestionItem
import com.example.model.SubjectDefinition

object CurriculumCatalog {
    const val EDITAL_TRANSPARENCY_NOTE =
        "Documento de referência: Estrutura programática oficial do PAES/UEMA (Ensino Médio completo + História/Geografia/Literatura do Maranhão + Redação dissertativo-argumentativa) aplicada ao ingresso no CFO PMMA. Questões inéditas são identificadas como 'Padrão PAES/UEMA' ou 'Gerada por IA (Treino)' — nunca simulamos questões inventadas como se fossem provas oficiais. Consulte sempre o edital anual publicado pela UEMA/PMMA para datas e obras literárias vigentes."

    val allSubjects: List<SubjectDefinition> = listOf(
        PortugueseCurriculum.subject,
        MathCurriculum.subject
    ) + HumanitiesCurriculum.subjects + NaturalSciencesCurriculum.subjects

    val allLessons: List<LessonContent> =
        PortugueseCurriculum.lessons +
            MathCurriculum.lessons +
            HumanitiesCurriculum.lessons +
            NaturalSciencesCurriculum.lessons

    val allQuestions: List<QuestionItem> =
        PortugueseCurriculum.questions +
            MathCurriculum.questions +
            HumanitiesCurriculum.questions +
            NaturalSciencesCurriculum.questions

    val allFlashcards: List<FlashcardTemplate> =
        PortugueseCurriculum.flashcards +
            MathCurriculum.flashcards +
            HumanitiesCurriculum.flashcards +
            NaturalSciencesCurriculum.flashcards

    val allEssayThemes: List<EssayTheme> = listOf(
        EssayTheme(
            id = "red_seguranca_cidadania",
            title = "Segurança Pública, Direitos Humanos e Prevenção Social no Brasil Contemporâneo",
            contextText = "O debate sobre segurança pública envolve tanto a atuação ostensiva e técnica das forças policiais quanto políticas públicas de educação, prevenção e fortalecimento comunitário.",
            commandPrompt = "Com base nos seus conhecimentos e na realidade brasileira e maranhense, redija um texto dissertativo-argumentativo defendendo caminhos para integrar segurança pública eficiente e respeito aos direitos fundamentais.",
            keyArguments = listOf(
                "Art. 144 da CF/88: segurança pública como dever do Estado e responsabilidade de todos.",
                "Policiamento comunitário, inteligência policial e integração com escolas e assistência social.",
                "Importância da formação ética e técnica dos oficiais para reduzir letalidade e aumentar a confiança social."
            ),
            sampleIntro = "Na Constituição Federal de 1988, a segurança pública é consagrada como dever do Estado e direito fundamental do cidadão. Contudo, no cenário brasileiro contemporâneo, o enfrentamento à criminalidade ainda oscila entre respostas meramente reativas e a insuficiência de ações preventivas. Diante disso, torna-se indispensável unir preparo técnico-operacional das instituições policiais e políticas sociais integradas para promover paz duradoura.",
            sampleOutline = listOf(
                "Introdução: Contextualizar com o Art. 144 da CF/88 + apresentar tese de integração entre preparo técnico e prevenção.",
                "Desenvolvimento 1: Analisar o papel da inteligência, formação continuada e policiamento comunitário.",
                "Desenvolvimento 2: Discutir as causas sociais da vulnerabilidade juvenil e a cooperação entre Estado e sociedade.",
                "Conclusão: Retomar a tese com síntese propositiva articulando Estado, forças de segurança e educação."
            )
        ),
        EssayTheme(
            id = "red_etica_servico_publico",
            title = "Ética, Disciplina e Transparência no Serviço Público Contemporâneo",
            contextText = "A credibilidade das instituições públicas depende da integridade dos servidores, do uso responsável de recursos e do combate contínuo às práticas antiéticas.",
            commandPrompt = "Redija um texto dissertativo-argumentativo analisando o papel da ética institucional e da transparência na consolidação da confiança entre Estado e sociedade.",
            keyArguments = listOf(
                "Princípios da Administração Pública (LIMPE: Legalidade, Impessoalidade, Moralidade, Publicidade e Eficiência).",
                "Pensamento ético kantiano: o dever moral como norma universal acima de interesses privados.",
                "Mecanismos de controle interno, ouvidorias e formação de lideranças comprometidas."
            ),
            sampleIntro = "Desde a Antiguidade Clássica, pensadores como Aristóteles associam a virtude ética ao bem comum da pólis. No contexto contemporâneo, entretanto, desvios de conduta e falhas de transparência fragilizam a legitimidade das instituições públicas. Logo, o fortalecimento da integridade no serviço público exige formação ética rigorosa e controle social efetivo.",
            sampleOutline = listOf(
                "Introdução: Repertório filosófico (Aristóteles ou Kant) + recorte da confiança institucional.",
                "Desenvolvimento 1: Relação entre impessoalidade/moralidade pública e qualidade dos serviços prestados.",
                "Desenvolvimento 2: Papel da transparência digital e da liderança exemplar nas corporações.",
                "Conclusão: Síntese propositiva reforçando a cultura de integridade."
            )
        ),
        EssayTheme(
            id = "red_educacao_juventude_ma",
            title = "Educação, Tecnologia e Redução das Desigualdades Regionais no Maranhão e no Brasil",
            contextText = "O acesso desigual à educação de qualidade e à conectividade ainda limita oportunidades para jovens em diversas regiões do país.",
            commandPrompt = "Elabore uma redação dissertativo-argumentativa discutindo os desafios e caminhos para que a educação e a tecnologia atuem como motores de mobilidade social.",
            keyArguments = listOf(
                "Paulo Freire / Constituição (Art. 205): educação como direito universal e instrumento de emancipação.",
                "Desafios da infraestrutura digital no interior e capacitação docente.",
                "Impacto da qualificação juvenil na redução da vulnerabilidade social."
            ),
            sampleIntro = "Conforme estabelece o artigo 205 da Constituição Federal, a educação é direito de todos e base para o pleno desenvolvimento da pessoa. No entanto, a assimetria no acesso à conectividade e ao ensino estruturado perpetua barreiras históricas de mobilidade social. Nesse sentido, democratizar ferramentas tecnológicas e investir na escola pública são medidas urgentes para reduzir desigualdades regionais.",
            sampleOutline = listOf(
                "Introdução: Art. 205 da CF + problematização da desigualdade educacional e digital.",
                "Desenvolvimento 1: Exclusão digital e impacto no aprendizado de jovens de regiões periféricas e rurais.",
                "Desenvolvimento 2: Educação integral como fator de desenvolvimento econômico e cidadania.",
                "Conclusão: Proposta integrada de infraestrutura, formação docente e inclusão."
            )
        )
    )

    val allBadges: List<BadgeDefinition> = listOf(
        BadgeDefinition(
            id = "badge_primeira_missao",
            title = "Primeiro Passo de Cadete",
            description = "Concluiu sua primeira atividade prática real no Missão CFO MA.",
            xpBonus = 40
        ),
        BadgeDefinition(
            id = "badge_diagnostico",
            title = "Mapeamento Tático",
            description = "Concluiu uma avaliação diagnóstica por disciplina.",
            xpBonus = 60
        ),
        BadgeDefinition(
            id = "badge_primeira_aula",
            title = "Base Sólida",
            description = "Concluiu uma aula completa do Modo Aula com avaliação final.",
            xpBonus = 80
        ),
        BadgeDefinition(
            id = "badge_simulado",
            title = "Prova de Fogo",
            description = "Finalizou seu primeiro simulado cronometrado com correção completa.",
            xpBonus = 100
        ),
        BadgeDefinition(
            id = "badge_redacao",
            title = "Voz Argumentativa",
            description = "Escreveu e avaliou sua primeira redação dissertativo-argumentativa.",
            xpBonus = 90
        ),
        BadgeDefinition(
            id = "badge_constancia_3",
            title = "Disciplina Inabalável",
            description = "Manteve uma sequência de 3 dias de estudo.",
            xpBonus = 120
        ),
        BadgeDefinition(
            id = "badge_erros_superados",
            title = "Erros Convertidos em Pontos",
            description = "Superou 3 questões do Caderno de Erros refazendo-as corretamente.",
            xpBonus = 90
        )
    )

    fun subjectById(id: String): SubjectDefinition? = allSubjects.firstOrNull { it.id == id }

    fun lessonsForSubject(subjectId: String): List<LessonContent> =
        allLessons.filter { it.subjectId == subjectId }

    fun questionsForSubject(subjectId: String): List<QuestionItem> =
        allQuestions.filter { it.subjectId == subjectId }

    fun questionsForTopic(topicId: String): List<QuestionItem> =
        allQuestions.filter { it.topicId == topicId }

    fun questionById(id: String): QuestionItem? = allQuestions.firstOrNull { it.id == id }

    fun questionsByDifficulty(difficulty: QuestionDifficulty): List<QuestionItem> =
        allQuestions.filter { it.difficulty == difficulty }
}
