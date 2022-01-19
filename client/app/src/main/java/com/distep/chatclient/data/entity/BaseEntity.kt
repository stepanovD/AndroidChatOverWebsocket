package com.distep.chatclient.data.entity

import androidx.room.PrimaryKey

abstract class BaseEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}