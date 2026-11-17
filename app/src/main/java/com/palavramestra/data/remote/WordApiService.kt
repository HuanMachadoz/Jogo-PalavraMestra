package com.palavramestra.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WordApiService {
    @GET("word")
    suspend fun fetchRandomWords(
        @Query("length") length: Int = 5,
        @Query("number") number: Int = 10
    ): List<String>
}
