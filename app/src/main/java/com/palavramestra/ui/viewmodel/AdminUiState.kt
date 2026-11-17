package com.palavramestra.ui.viewmodel

import com.palavramestra.data.local.entity.WordEntity

data class AdminUiState(
    val words: List<WordEntity> = emptyList(),
    val filteredWords: List<WordEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val editingWord: WordEntity? = null,
    val searchQuery: String = "",
    val selectedDifficulty: String? = null,
    val pendingCloudWords: List<String> = emptyList()
)
