package com.palavramestra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palavramestra.data.local.entity.RankingEntity
import com.palavramestra.data.repository.RankingRepository
import com.palavramestra.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val wordRepository: WordRepository,
    private val rankingRepository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var gameStartedAt = 0L

    init {
        viewModelScope.launch {
            rankingRepository.getAllRanking().collect { ranking ->
                val sorted = ranking.sortedByDescending { it.score }
                _uiState.update { it.copy(ranking = sorted) }
            }
        }
    }

    fun startGame(difficulty: Difficulty, playerName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val word = wordRepository.getRandomWord(difficulty.name)
                if (word == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Nenhuma palavra encontrada para esta dificuldade.") }
                    return@launch
                }
                gameStartedAt = System.currentTimeMillis()
                _uiState.update {
                    it.copy(
                        currentWord = word.word,
                        displayWord = word.word.map { c -> "_" }.joinToString(" "),
                        guessedLetters = emptySet(),
                        wrongLetters = emptyList(),
                        attemptsLeft = difficulty.maxAttempts,
                        maxAttempts = difficulty.maxAttempts,
                        gameState = GameState.PLAYING,
                        score = 0,
                        difficulty = difficulty,
                        playerName = playerName,
                        gameStarted = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Erro ao iniciar: ${e.message}") }
            }
        }
    }

    fun guessLetter(letter: Char) {
        val current = _uiState.value
        if (current.gameState != GameState.PLAYING) return
        val upper = letter.uppercaseChar()
        if (current.guessedLetters.contains(upper)) return

        val newGuessed = current.guessedLetters + upper
        val word = current.currentWord

        val isCorrect = word.contains(upper)

        val newDisplay = word.map { c ->
            if (newGuessed.contains(c)) c.toString() else "_"
        }.joinToString(" ")

        val newAttemptsLeft = if (isCorrect) current.attemptsLeft else current.attemptsLeft - 1
        val newWrongLetters = if (isCorrect) current.wrongLetters else current.wrongLetters + upper

        val newGameState = when {
            !newDisplay.contains("_") -> GameState.WON
            newAttemptsLeft <= 0 -> GameState.LOST
            else -> GameState.PLAYING
        }

        val newScore = if (newGameState == GameState.WON) {
            word.length * 100 + newAttemptsLeft * 50
        } else current.score

        _uiState.update {
            it.copy(
                guessedLetters = newGuessed,
                displayWord = newDisplay,
                attemptsLeft = newAttemptsLeft,
                wrongLetters = newWrongLetters,
                gameState = newGameState,
                score = newScore
            )
        }

        if (newGameState == GameState.WON) {
            val elapsedSeconds = if (gameStartedAt > 0L) {
                ((System.currentTimeMillis() - gameStartedAt) / 1000L).toInt().coerceAtLeast(1)
            } else {
                0
            }
            saveResult(current.playerName, newScore, elapsedSeconds)
        }
    }

    private fun saveResult(playerName: String, score: Int, timeSeconds: Int) {
        viewModelScope.launch {
            rankingRepository.addResult(playerName, score, timeSeconds)
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, message = null) }
    }

    fun deleteRanking(ranking: RankingEntity) {
        viewModelScope.launch {
            rankingRepository.deleteResult(ranking)
        }
    }
}
