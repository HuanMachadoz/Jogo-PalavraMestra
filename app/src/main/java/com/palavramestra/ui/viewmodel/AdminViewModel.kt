package com.palavramestra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.palavramestra.data.local.entity.WordEntity
import com.palavramestra.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(private val wordRepository: WordRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            wordRepository.getAllWords().collect { words ->
                _uiState.update { it.copy(words = words, isLoading = false) }
                applyFilterAndSort()
            }
        }
    }

    fun addWord(word: String, category: String, difficulty: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = wordRepository.addWord(word, category, difficulty)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, successMessage = "Palavra adicionada com sucesso!") }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = "Erro ao adicionar: ${e.message}") }
            }
        }
    }

    fun updateWord(word: WordEntity, newWord: String, newCategory: String, newDifficulty: String) {
        viewModelScope.launch {
            val updated = word.copy(
                word = newWord.trim().uppercase(),
                category = newCategory.trim().uppercase(),
                difficulty = newDifficulty
            )
            val result = wordRepository.updateWord(updated)
            result.onSuccess {
                _uiState.update { it.copy(successMessage = "Palavra atualizada!", editingWord = null) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Erro ao atualizar: ${e.message}") }
            }
        }
    }

    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            val result = wordRepository.deleteWord(word)
            result.onSuccess {
                _uiState.update { it.copy(successMessage = "Palavra removida!") }
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Erro ao deletar: ${e.message}") }
            }
        }
    }

    fun syncFromCloud() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            val result = wordRepository.fetchWordsFromCloud()
            result.onSuccess { words ->
                _uiState.update { it.copy(isLoading = false, pendingCloudWords = words) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = "Falha na sincronização: ${e.message}") }
            }
        }
    }

    fun confirmCloudWords() {
        val words = _uiState.value.pendingCloudWords
        viewModelScope.launch {
            val result = wordRepository.saveCloudWords(words)
            result.onSuccess { count ->
                _uiState.update {
                    it.copy(pendingCloudWords = emptyList(), successMessage = "$count palavras da nuvem adicionadas.")
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Erro ao salvar: ${e.message}") }
            }
        }
    }

    fun discardCloudWords() {
        _uiState.update { it.copy(pendingCloudWords = emptyList(), successMessage = "Palavras da nuvem descartadas.") }
    }

    fun deleteCloudWords() {
        viewModelScope.launch {
            val result = wordRepository.deleteCloudWords()
            result.onSuccess {
                _uiState.update { it.copy(successMessage = "Palavras da internet removidas.") }
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Erro ao remover: ${e.message}") }
            }
        }
    }

    fun applyFilterAndSort() {
        val current = _uiState.value
        val filtered = current.words
            .filter { word ->
                val matchesSearch = current.searchQuery.isBlank() ||
                    word.word.contains(current.searchQuery.uppercase(), ignoreCase = true) ||
                    word.category.contains(current.searchQuery, ignoreCase = true)
                val matchesDifficulty = current.selectedDifficulty == null ||
                    word.difficulty == current.selectedDifficulty
                matchesSearch && matchesDifficulty
            }
            .sortedBy { it.word }
        _uiState.update { it.copy(filteredWords = filtered) }
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSort()
    }

    fun updateDifficultyFilter(difficulty: String?) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
        applyFilterAndSort()
    }

    fun setEditingWord(word: WordEntity?) {
        _uiState.update { it.copy(editingWord = word) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
