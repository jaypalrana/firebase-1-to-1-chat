package com.firebasechatkotlin.models

import java.io.Serializable

data class Message(
    var message: String,
    val senderId: String,
    val receiverId: String,
    val timestamp: Long,
    val image: String = "",
    val user: User,
) : Serializable {

}