package com.hereliesaz.julesapisdk.testapp

data class Message(
    val text: String,
    val type: MessageType
)

enum class MessageType {
    USER,
    BOT,
    ERROR
}
