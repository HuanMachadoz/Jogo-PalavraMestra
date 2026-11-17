package com.palavramestra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ranking")
data class RankingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val score: Int,
    val timeSeconds: Int,
    val date: Long
)
