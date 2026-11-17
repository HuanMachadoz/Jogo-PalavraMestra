package com.palavramestra.di

import android.content.Context
import com.palavramestra.data.local.database.AppDatabase
import com.palavramestra.data.repository.RankingRepository
import com.palavramestra.data.repository.WordRepository

class AppContainer(context: Context) {
    private val database: AppDatabase = AppDatabase.getDatabase(context)

    val wordRepository = WordRepository(database.wordDao())
    val rankingRepository = RankingRepository(database.rankingDao())

    val viewModelFactory = ViewModelFactory(wordRepository, rankingRepository)
}
