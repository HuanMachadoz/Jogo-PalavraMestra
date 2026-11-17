package com.palavramestra.ui.viewmodel

import com.palavramestra.data.local.entity.RankingEntity

enum class GameState { PLAYING, WON, LOST }

enum class Difficulty(val label: String, val maxAttempts: Int) {
    FACIL("Fácil", 8),
    MEDIO("Médio", 6),
    DIFICIL("Difícil", 4)
}

data class GameUiState(
    val currentWord: String = "",
    val displayWord: String = "",
    val guessedLetters: Set<Char> = emptySet(),
    val wrongLetters: List<Char> = emptyList(),
    val attemptsLeft: Int = 6,
    val maxAttempts: Int = 6,
    val gameState: GameState = GameState.PLAYING,
    val score: Int = 0,
    val difficulty: Difficulty = Difficulty.MEDIO,
    val playerName: String = "",
    val gameStarted: Boolean = false,
    val ranking: List<RankingEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)
