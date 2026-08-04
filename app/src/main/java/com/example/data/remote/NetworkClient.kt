package com.example.data.remote

import com.example.data.remote.api.DineReserveApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private const val DEFAULT_BASE_URL = "https://api.dinereserve.com/"

    /**
     * Moshi JSON parser instance configured with Kotlin Reflection Adapter
     * for seamless Kotlin data class serialization and deserialization.
     */
    val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * OkHttpClient instance configured with timeouts and HTTP logging interceptor.
     */
    val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Retrofit instance configured with Moshi Converter Factory and OkHttpClient.
     */
    fun createRetrofit(baseUrl: String = DEFAULT_BASE_URL): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    /**
     * Lazily initialized API Service instance ready for network calls.
     */
    val apiService: DineReserveApiService by lazy {
        createRetrofit().create(DineReserveApiService::class.java)
    }
}
