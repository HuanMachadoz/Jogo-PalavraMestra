package com.palavramestra.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.palavramestra.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(difficulty: String): WordEntity?

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: WordEntity): Long

    @Update
    suspend fun update(word: WordEntity)

    @Delete
    suspend fun delete(word: WordEntity)

    @Query("DELETE FROM words WHERE category = :category")
    suspend fun deleteByCategory(category: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)
}
