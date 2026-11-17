package com.palavramestra.data.repository

import com.palavramestra.data.local.dao.RankingDao
import com.palavramestra.data.local.entity.RankingEntity
import kotlinx.coroutines.flow.Flow

class RankingRepository(private val rankingDao: RankingDao) {

    fun getAllRanking(): Flow<List<RankingEntity>> = rankingDao.getAllRanking()

    suspend fun addResult(playerName: String, score: Int, timeSeconds: Int): Result<Long> {
        return try {
            val id = rankingDao.insert(
                RankingEntity(
                    playerName = playerName,
                    score = score,
                    timeSeconds = timeSeconds,
                    date = System.currentTimeMillis()
                )
            )
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteResult(ranking: RankingEntity): Result<Unit> {
        return try {
            rankingDao.delete(ranking)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearRanking(): Result<Unit> {
        return try {
            rankingDao.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
