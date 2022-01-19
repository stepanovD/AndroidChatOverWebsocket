package com.distep.chatclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.distep.chatclient.data.db.dao.MessageDao
import com.distep.chatclient.data.entity.Message

@Database(
    entities = [Message::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}