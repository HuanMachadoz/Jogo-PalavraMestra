package com.palavramestra.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.palavramestra.data.local.database.AppDatabase
import com.palavramestra.data.repository.RankingRepository
import com.palavramestra.data.repository.WordRepository
import com.palavramestra.ui.viewmodel.AdminViewModel
import com.palavramestra.ui.viewmodel.GameViewModel

class ViewModelFactory(
    private val wordRepository: WordRepository,
    private val rankingRepository: RankingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(wordRepository, rankingRepository)
            }
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(wordRepository)
            }
            else -> throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
        } as T
    }
}
