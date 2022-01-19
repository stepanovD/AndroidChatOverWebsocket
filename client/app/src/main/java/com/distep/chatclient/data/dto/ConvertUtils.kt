package com.distep.chatclient.data.dto

import com.distep.chatclient.data.entity.Message

fun dtoToEntity(dto: ChatSocketMessage) : Message {
    return Message(
        dto.datetime,
        dto.text,
        dto.author,
        dto.receiver
    )
}

fun entityToDto(entity: Message) : ChatSocketMessage {
    return ChatSocketMessage(
        entity.text,
        entity.author,
        entity.datetime,
        entity.receiver
    )
}