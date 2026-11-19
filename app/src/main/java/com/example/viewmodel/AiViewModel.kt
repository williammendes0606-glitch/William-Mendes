package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AiTutorRepository
import com.example.data.repository.EssayEvaluationResult
import com.example.model.QuestionDifficulty
import com.example.model.QuestionItem
import com.example.model.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TutorChatMessage(
    val id: String,
    val fromUser: Boolean,
    val text: String,
    val streaming: Boolean = false
)

class AiViewModel(
    private val aiRepository: AiTutorRepository = AiTutorRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(
            TutorChatMessage(
                id = "welcome",
                fromUser = false,
                text = "Olá! Sou seu Professor Virtual especialista no CFO PMMA (PAES/UEMA). " +
                    "Posso explicar conteúdos do zero (principalmente Português e Matemática), resolver questões passo a passo, ensinar macetes de prova ou gerar exercícios inéditos de treino."
            )
        )
    )
    val messages: StateFlow<List<TutorChatMessage>> = _messages.asStateFlow()

    private val _chatBusy = MutableStateFlow(false)
    val chatBusy: StateFlow<Boolean> = _chatBusy.asStateFlow()

    private val _generatedQuestionState = MutableStateFlow<UiState<QuestionItem>?>(null)
    val generatedQuestionState: StateFlow<UiState<QuestionItem>?> = _generatedQuestionState.asStateFlow()

    private val _essayEvalState = MutableStateFlow<UiState<EssayEvaluationResult>?>(null)
    val essayEvalState: StateFlow<UiState<EssayEvaluationResult>?> = _essayEvalState.asStateFlow()

    fun sendMessage(prompt: String) {
        val clean = prompt.trim()
        if (clean.isEmpty() || _chatBusy.value) return

        val historySnapshot = _messages.value.map { it.fromUser to it.text }
        val userMsg = TutorChatMessage(
            id = "u_${System.currentTimeMillis()}",
            fromUser = true,
            text = clean
        )
        val botId = "m_${System.currentTimeMillis() + 1}"
        _messages.value = _messages.value + userMsg + TutorChatMessage(
            id = botId,
            fromUser = false,
            text = "",
            streaming = true
        )
        _chatBusy.value = true

        viewModelScope.launch {
            try {
                var lastText = ""
                aiRepository.streamTutorReply(historySnapshot, clean).collect { partial ->
                    lastText = partial
                    _messages.value = _messages.value.map {
                        if (it.id == botId) it.copy(text = partial, streaming = true) else it
                    }
                }
                val finalText = lastText.ifBlank { "Não consegui gerar uma resposta agora. Tente reformular sua dúvida." }
                _messages.value = _messages.value.map {
                    if (it.id == botId) it.copy(text = finalText, streaming = false) else it
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Tutor chat timed out", e)
                updateBotError(botId, "Tempo limite excedido (60s). Verifique sua conexão e tente novamente.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Tutor chat failed", e)
                updateBotError(botId, "Erro no Professor IA: ${e.localizedMessage ?: "tente novamente."}")
            } finally {
                _chatBusy.value = false
            }
        }
    }

    private fun updateBotError(botId: String, errorMsg: String) {
        _messages.value = _messages.value.map {
            if (it.id == botId) it.copy(text = errorMsg, streaming = false) else it
        }
    }

    fun generatePracticeQuestion(
        subjectId: String,
        subjectName: String,
        topicName: String,
        difficulty: QuestionDifficulty
    ) {
        _generatedQuestionState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val question = aiRepository.generateAdaptiveQuestion(subjectId, subjectName, topicName, difficulty)
                _generatedQuestionState.value = UiState.Success(question)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Question generation timed out", e)
                _generatedQuestionState.value = UiState.Error("Tempo limite excedido ao gerar questão.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Question generation failed", e)
                _generatedQuestionState.value = UiState.Error(
                    e.localizedMessage ?: "Falha ao gerar questão com IA."
                )
            }
        }
    }

    fun clearGeneratedQuestion() {
        _generatedQuestionState.value = null
    }

    fun evaluateEssay(themeTitle: String, draftText: String) {
        if (draftText.trim().length < 40) {
            _essayEvalState.value = UiState.Error("Escreva pelo menos algumas linhas completas para receber a avaliação.")
            return
        }
        _essayEvalState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = aiRepository.evaluateEssay(themeTitle, draftText)
                _essayEvalState.value = UiState.Success(result)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Essay evaluation timed out", e)
                _essayEvalState.value = UiState.Error("Tempo limite excedido (60s) ao corrigir redação.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Essay evaluation failed", e)
                _essayEvalState.value = UiState.Error(
                    e.localizedMessage ?: "Falha na correção da redação."
                )
            }
        }
    }

    fun clearEssayEval() {
        _essayEvalState.value = null
    }

    private companion object {
        const val TAG = "AiViewModel"
    }
}
