package com.kate.aijournalcompanion

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("analyze")
    suspend fun analyze(@Body req: JournalRequest): JournalResponse
}