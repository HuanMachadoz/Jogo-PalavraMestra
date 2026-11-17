package com.palavramestra.data.repository

import com.palavramestra.data.local.dao.WordDao
import com.palavramestra.data.local.entity.WordEntity
import com.palavramestra.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()

    suspend fun addWord(word: String, category: String, difficulty: String): Result<Long> {
        return try {
            val id = wordDao.insert(
                WordEntity(
                    word = word.trim().uppercase(),
                    category = category.trim().uppercase(),
                    difficulty = difficulty
                )
            )
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWord(word: WordEntity): Result<Unit> {
        return try {
            wordDao.update(word)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteWord(word: WordEntity): Result<Unit> {
        return try {
            wordDao.delete(word)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRandomWord(difficulty: String): WordEntity? {
        return try {
            wordDao.getRandomWord(difficulty)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchWordsFromCloud(): Result<List<String>> {
        return try {
            val words = RetrofitClient.apiService.fetchRandomWords(length = 5, number = 10)
            Result.success(words.map { it.uppercase() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveCloudWords(words: List<String>): Result<Int> {
        return try {
            val entities = words.map { word ->
                WordEntity(word = word.uppercase(), category = "API", difficulty = "MEDIO")
            }
            wordDao.insertAll(entities)
            Result.success(entities.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCloudWords(): Result<Unit> {
        return try {
            wordDao.deleteByCategory("API")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun seedIfEmpty() {
        if (wordDao.count() == 0) {
            val seed = listOf(
                WordEntity(word = "BANANA", category = "FRUTA", difficulty = "FACIL"),
                WordEntity(word = "MELANCIA", category = "FRUTA", difficulty = "MEDIO"),
                WordEntity(word = "ELEFANTE", category = "ANIMAL", difficulty = "MEDIO"),
                WordEntity(word = "GATO", category = "ANIMAL", difficulty = "FACIL"),
                WordEntity(word = "COMPUTADOR", category = "TECNOLOGIA", difficulty = "DIFICIL"),
                WordEntity(word = "JANELA", category = "CASA", difficulty = "FACIL"),
                WordEntity(word = "GUITARRA", category = "MUSICA", difficulty = "MEDIO"),
                WordEntity(word = "PLANETA", category = "ESPACO", difficulty = "MEDIO"),
                WordEntity(word = "CHURROS", category = "COMIDA", difficulty = "MEDIO"),
                WordEntity(word = "SOL", category = "NATUREZA", difficulty = "FACIL")
            )
            wordDao.insertAll(seed)
        }
    }
}
