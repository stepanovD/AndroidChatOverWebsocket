package com.distep.chatclient.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(
    tableName = "message"
)
data class Message(
    @ColumnInfo(name = "date_time")
    var datetime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),
    val text: String,
    val author: String,
    var receiver: String? = null
) : BaseEntity()