package com.jayshil.a24012021038_chatbot

data class ChatHistory(
    var id: Long,
    var title: String,
    var messages: MutableList<ChatMessage> = mutableListOf(),
    var isPinned: Boolean = false
)