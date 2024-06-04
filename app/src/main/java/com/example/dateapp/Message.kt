package com.example.dateapp

data class Message(
    val text: String = "",
    val senderUID: String = "",
    val receiverUID: String = "",
    val timestamp: Long = 0
)