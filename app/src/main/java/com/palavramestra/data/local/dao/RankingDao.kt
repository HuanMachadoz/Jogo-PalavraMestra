package com.palavramestra.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.palavramestra.data.local.entity.RankingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingDao {
    @Query("SELECT * FROM ranking ORDER BY score DESC, timeSeconds ASC")
    fun getAllRanking(): Flow<List<RankingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ranking: RankingEntity): Long

    @Update
    suspend fun update(ranking: RankingEntity)

    @Delete
    suspend fun delete(ranking: RankingEntity)

    @Query("DELETE FROM ranking")
    suspend fun clearAll()
}
