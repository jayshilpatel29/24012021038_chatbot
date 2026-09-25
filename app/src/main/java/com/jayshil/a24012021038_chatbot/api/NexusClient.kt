package com.jayshil.a24012021038_chatbot.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NexusClient {

    // Default BASE_URL for local testing or emulator
    // Change to your PC's LAN IP address (e.g. http://192.168.1.100:8000/) for physical phone testing
    var BASE_URL = "http://192.168.1.100:8000/"

    private var retrofitInstance: Retrofit? = null

    fun getBaseUrl(): String {
        return if (BASE_URL.endsWith("/")) BASE_URL else "$BASE_URL/"
    }

    val api: NexusApi
        get() {
            val url = getBaseUrl()
            if (retrofitInstance == null || retrofitInstance?.baseUrl()?.toString() != url) {
                retrofitInstance = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofitInstance!!.create(NexusApi::class.java)
        }

    fun updateBaseUrl(newUrl: String) {
        BASE_URL = newUrl
        retrofitInstance = null
    }
}