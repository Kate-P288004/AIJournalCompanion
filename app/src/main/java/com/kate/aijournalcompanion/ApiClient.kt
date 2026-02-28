package com.kate.aijournalcompanion

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * =========================================================
 * Student: Kate Odabas
 * Project: AI Journal Companion (AT2 – OOP3)
 *
 * Purpose:
 * - Connect Android app to FastAPI backend
 * - Send journal text for emotion analysis
 *
 * Assessment Concepts Demonstrated:
 * - API communication (HTTP requests)
 * - Object singleton pattern
 * - JSON conversion using Gson
 * - Logging interceptor for debugging
 * =========================================================
 */

object ApiClient {

    /**
     * ---------------------------------------------------------
     * Backend base URL
     *
     * Emulator uses:
     * 10.0.2.2 = localhost of host machine
     * ---------------------------------------------------------
     */
    private const val BASE_URL = "http://10.0.2.2:8000/"

    /**
     * ---------------------------------------------------------
     * HTTP logging interceptor
     * Displays request/response in Logcat
     * Helpful for testing and debugging API calls
     * ---------------------------------------------------------
     */
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * ---------------------------------------------------------
     * OkHttp client configuration
     * Adds logging interceptor
     * ---------------------------------------------------------
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    /**
     * ---------------------------------------------------------
     * Retrofit API instance (Singleton)
     * Converts JSON to Kotlin objects via Gson
     * ---------------------------------------------------------
     */
    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}