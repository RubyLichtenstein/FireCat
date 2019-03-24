package com.example.firecat.chat

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)