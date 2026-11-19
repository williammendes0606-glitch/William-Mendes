package com.example.model

enum class KnowledgeArea(val label: String) {
    LINGUAGENS("Linguagens e Códigos"),
    MATEMATICA("Matemática"),
    HUMANAS("Ciências Humanas"),
    NATUREZA("Ciências da Natureza"),
    REDACAO("Redação Dissertativa")
}

enum class QuestionDifficulty(val label: String) {
    FACIL("Fácil"),
    MEDIO("Médio"),
    DIFICIL("Difícil")
}

enum class QuestionOrigin(val badgeLabel: String, val detailLabel: String) {
    PADRAO_PAES_UEMA(
        "Questão Inédita — Padrão PAES/UEMA",
        "Elaborada exclusivamente para treino com base nos tópicos confirmados do edital PAES/UEMA."
    ),
    IA_TREINO(
        "Gerada por IA (Treino)",
        "Criada pelo Professor de IA para reforço adaptativo. Não substitui prova oficial."
    ),
    REFERENCIA_PAES_VERIFICADA(
        "Referência Histórica UEMA/PAES",
        "Questão inspirada em cobrança histórica confirmada do PAES/UEMA."
    )
}

data class EditalTopic(
    val id: String,
    val subjectId: String,
    val title: String,
    val subtopics: List<String>,
    val confirmedInEdital: Boolean = true,
    val priorityWeight: Int = 1 // 3 = alta prioridade
)

data class SubjectDefinition(
    val id: String,
    val name: String,
    val shortName: String,
    val area: KnowledgeArea,
    val description: String,
    val editalStatusNote: String,
    val topics: List<EditalTopic>
)

data class WorkedExample(
    val title: String,
    val problem: String,
    val stepByStep: List<String>,
    val answer: String
)

data class LessonContent(
    val id: String,
    val subjectId: String,
    val topicId: String,
    val title: String,
    val estimatedMinutes: Int,
    val editalBadge: String,
    val basicExplanation: String,
    val deepExplanation: String,
    val workedExamples: List<WorkedExample>,
    val examTricks: List<String>,
    val memoryTechniques: List<String>,
    val structuredSummary: List<String>,
    val practiceQuestionIds: List<String>,
    val finalExamQuestionIds: List<String>
)

data class QuestionItem(
    val id: String,
    val subjectId: String,
    val subjectName: String,
    val topicId: String,
    val topicName: String,
    val difficulty: QuestionDifficulty,
    val origin: QuestionOrigin,
    val sourceDetail: String,
    val statement: String,
    val options: List<String>,
    val correctIndex: Int,
    val optionExplanations: List<String>,
    val generalExplanation: String,
    val examTrick: String
)

data class FlashcardTemplate(
    val id: String,
    val subjectId: String,
    val topicTitle: String,
    val front: String,
    val back: String,
    val memoryTip: String
)

data class EssayTheme(
    val id: String,
    val title: String,
    val contextText: String,
    val commandPrompt: String,
    val keyArguments: List<String>,
    val sampleIntro: String,
    val sampleOutline: List<String>
)

data class BadgeDefinition(
    val id: String,
    val title: String,
    val description: String,
    val xpBonus: Int
)
