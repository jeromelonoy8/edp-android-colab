package com.liceo.liceochat.data.remote

import com.liceo.liceochat.domain.Message
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull

fun MessageDto.toDomain(): Message {
    val time = when (val p = createdAt) {
        is JsonPrimitive -> {
            if (p.isString) {
                p.content.toLongOrNull() ?: 0L
            } else {
                p.longOrNull ?: 0L
            }
        }
        else -> 0L
    }
    
    return Message(
        id = id ?: "",
        sender = sender ?: "Anonymous",
        text = text ?: "",
        createdAt = time
    )
}

fun List<MessageDto>.toDomain(): List<Message> {
    return map { it.toDomain() }
}
