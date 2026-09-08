package com.jayshil.a24012021038_chatbot.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: String,
    val message: String,
    val access_token: String?,
    val token_type: String?,
    val user: User?
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)

interface NexusApi {

    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>
}