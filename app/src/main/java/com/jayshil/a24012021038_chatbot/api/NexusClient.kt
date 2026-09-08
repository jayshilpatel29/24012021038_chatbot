package com.jayshil.a24012021038_chatbot.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NexusClient {

    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: NexusApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NexusApi::class.java)
    }
}