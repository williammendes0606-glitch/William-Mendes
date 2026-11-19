package com.example.data.repository

import android.util.Log
import com.example.model.QuestionDifficulty
import com.example.model.QuestionItem
import com.example.model.QuestionOrigin
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

data class EssayEvaluationResult(
    val grammarErrors: List<String>,
    val cohesionFeedback: String,
    val improvementSuggestions: List<String>,
    val rewrittenSuggestion: String,
    val formativeScore: Long,
    val aiSummary: String
)

class AiTutorRepository {

    private val tutorSystemPrompt = """
        Você é o Professor Virtual especialista no CFO PMMA (Curso de Formação de Oficiais da Polícia Militar do Maranhão) via processo seletivo PAES/UEMA.
        Regras obrigatórias de conduta pedagógica:
        1. Explique em português claro, acolhedor e didático, começando do básico e aprofundando passo a passo.
        2. Sempre foque exclusivamente no conteúdo do PAES/UEMA (Português, Matemática, História/Geografia/Literatura do Maranhão, Ciências Humanas, Ciências da Natureza e Redação dissertativo-argumentativa). Não misture matérias de PRF ou PM de outros estados.
        3. NUNCA afirme que uma questão criada por você é oficial de prova anterior. Sempre deixe transparente quando algo é exercício inédito de treino.
        4. Não invente critérios secretos ou editais fictícios.
        5. Ao explicar questões, analise por que a alternativa correta está certa e por que cada distrator está errado, além de ensinar um macete de resolução rápida.
    """.trimIndent()

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content { text(tutorSystemPrompt) }
        )
    }

    private val questionSchema = Schema.obj(
        properties = mapOf(
            "subjectName" to Schema.string("Nome da disciplina do PAES/UEMA"),
            "topicName" to Schema.string("Assunto específico"),
            "statement" to Schema.string("Enunciado claro e completo da questão inédita"),
            "options" to Schema.array(Schema.string(), "Lista exata de 4 alternativas"),
            "correctIndex" to Schema.integer("Índice de 0 a 3 da alternativa correta"),
            "optionExplanations" to Schema.array(Schema.string(), "Explicação de cada uma das 4 alternativas"),
            "generalExplanation" to Schema.string("Resolução passo a passo didática"),
            "examTrick" to Schema.string("Macete rápido de prova")
        )
    )

    private val questionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content { text(tutorSystemPrompt) },
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = questionSchema
            }
        )
    }

    private val essaySchema = Schema.obj(
        properties = mapOf(
            "grammarErrors" to Schema.array(Schema.string(), "Lista objetiva de desvios gramaticais encontrados no texto"),
            "cohesionFeedback" to Schema.string("Análise da coerência, progressão argumentativa e uso de conectivos"),
            "improvementSuggestions" to Schema.array(Schema.string(), "Sugestões práticas para elevar a qualidade do texto"),
            "rewrittenSuggestion" to Schema.string("Versão aprimorada de um ou dois parágrafos-chave para comparação"),
            "formativeScore" to Schema.integer("Nota formativa estimada de 0 a 1000 apenas para orientação de treino"),
            "aiSummary" to Schema.string("Parecer pedagógico geral, deixando claro que a correção é orientativa e não substitui a banca oficial da UEMA")
        )
    )

    private val essayModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "Você é um professor corretor de redações dissertativo-argumentativas focado no estilo PAES/UEMA. " +
                        "Avalie gramática, coesão, coerência, argumentação e repertório com rigor e clareza. " +
                        "Nunca invente critérios oficiais secretos da UEMA; informe que a nota é formativa para treino."
                )
            },
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = essaySchema
            }
        )
    }

    fun streamTutorReply(history: List<Pair<Boolean, String>>, userMessage: String): Flow<String> = flow {
        val sdkHistory = history.takeLast(12).map { (isUser, text) ->
            content(if (isUser) "user" else "model") { text(text) }
        }
        val chat = chatModel.startChat(history = sdkHistory)
        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            chat.sendMessageStream(userMessage).collect { chunk ->
                val part = chunk.text.orEmpty()
                if (part.isNotEmpty()) {
                    accumulated.append(part)
                    emit(accumulated.toString())
                }
            }
        }
    }

    suspend fun generateAdaptiveQuestion(
        subjectId: String,
        subjectName: String,
        topicName: String,
        difficulty: QuestionDifficulty
    ): QuestionItem {
        val prompt = """
            Crie 1 questão inédita de treino no padrão PAES/UEMA para o candidato ao CFO PMMA.
            Disciplina: $subjectName
            Assunto: $topicName
            Dificuldade: ${difficulty.label}
            Forneça exatamente 4 alternativas e explique cada uma delas detalhadamente.
        """.trimIndent()

        val response = withTimeout(60_000L) {
            questionModel.generateContent(prompt)
        }
        val raw = response.text ?: throw IllegalStateException("Resposta vazia da IA.")
        val json = JSONObject(raw)
        val optsArr = json.getJSONArray("options")
        val opts = (0 until optsArr.length()).map { optsArr.getString(it) }.take(4)
        val expArr = json.getJSONArray("optionExplanations")
        val exps = (0 until expArr.length()).map { expArr.getString(it) }.take(4)
        val correctIdx = json.optInt("correctIndex", 0).coerceIn(0, (opts.size - 1).coerceAtLeast(0))

        return QuestionItem(
            id = "ai_${System.currentTimeMillis()}",
            subjectId = subjectId,
            subjectName = subjectName,
            topicId = "${subjectId}_ia",
            topicName = json.optString("topicName", topicName),
            difficulty = difficulty,
            origin = QuestionOrigin.IA_TREINO,
            sourceDetail = "Questão inédita gerada por IA para treino adaptativo (não oficial).",
            statement = json.getString("statement"),
            options = opts,
            correctIndex = correctIdx,
            optionExplanations = exps,
            generalExplanation = json.optString("generalExplanation", ""),
            examTrick = json.optString("examTrick", "")
        )
    }

    suspend fun evaluateEssay(themeTitle: String, essayText: String): EssayEvaluationResult {
        val prompt = """
            Tema da proposta: $themeTitle

            Texto do candidato:
            $essayText

            Realize uma análise pedagógica completa: aponte desvios gramaticais reais, avalie coesão e coerência, sugira melhorias práticas, reescreva um trecho-chave como exemplo comparativo e atribua uma nota formativa de 0 a 1000 para treino.
        """.trimIndent()

        try {
            val response = withTimeout(60_000L) {
                essayModel.generateContent(prompt)
            }
            val raw = response.text ?: throw IllegalStateException("A IA retornou resposta vazia.")
            val json = JSONObject(raw)
            val errorsArr = json.optJSONArray("grammarErrors")
            val errors = if (errorsArr != null) (0 until errorsArr.length()).map { errorsArr.getString(it) } else emptyList()
            val sugArr = json.optJSONArray("improvementSuggestions")
            val suggestions = if (sugArr != null) (0 until sugArr.length()).map { sugArr.getString(it) } else emptyList()

            return EssayEvaluationResult(
                grammarErrors = errors,
                cohesionFeedback = json.optString("cohesionFeedback", ""),
                improvementSuggestions = suggestions,
                rewrittenSuggestion = json.optString("rewrittenSuggestion", ""),
                formativeScore = json.optLong("formativeScore", 700L).coerceIn(0L, 1000L),
                aiSummary = json.optString("aiSummary", "")
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("AiTutorRepository", "Essay evaluation failed", e)
            throw e
        }
    }
}
