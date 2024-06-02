package com.example.dateapp

data class Message(
    val text: String = "",
    val senderEmail: String = "",
    val receiverEmail: String = "",
    val timestamp: Long = 0
)
