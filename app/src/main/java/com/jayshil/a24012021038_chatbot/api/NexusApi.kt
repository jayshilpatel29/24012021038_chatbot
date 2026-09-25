package com.jayshil.a24012021038_chatbot.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

data class ChatMessageDto(
    val message: String,
    val isUser: Boolean,
    val timestamp: String = ""
)

data class ChatDto(
    val id: Long,
    val title: String,
    val messages: List<ChatMessageDto> = emptyList(),
    val created_at: String? = null,
    val updated_at: String? = null
)

data class CreateChatRequest(
    val title: String,
    val messages: List<ChatMessageDto>
)

data class UpdateChatRequest(
    val title: String,
    val messages: List<ChatMessageDto>
)

data class ChatsResponse(
    val status: String,
    val chats: List<ChatDto>? = null
)

data class SingleChatResponse(
    val status: String,
    val chat: ChatDto? = null
)

data class GenericResponse(
    val status: String,
    val message: String? = null
)

interface NexusApi {

    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("chats/")
    fun getChats(
        @Header("Authorization") token: String
    ): Call<ChatsResponse>

    @POST("chats/")
    fun createChat(
        @Header("Authorization") token: String,
        @Body request: CreateChatRequest
    ): Call<SingleChatResponse>

    @PUT("chats/{chat_id}")
    fun updateChat(
        @Header("Authorization") token: String,
        @Path("chat_id") chatId: Long,
        @Body request: UpdateChatRequest
    ): Call<GenericResponse>

    @DELETE("chats/{chat_id}")
    fun deleteChat(
        @Header("Authorization") token: String,
        @Path("chat_id") chatId: Long
    ): Call<GenericResponse>

    @DELETE("chats/")
    fun deleteAllChats(
        @Header("Authorization") token: String
    ): Call<GenericResponse>
}