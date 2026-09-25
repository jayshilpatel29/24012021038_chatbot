package com.jayshil.a24012021038_chatbot

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val isTyping: Boolean = false,
    val timestamp: String = ""
)