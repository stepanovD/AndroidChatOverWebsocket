package com.distep.chatclient.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.distep.chatclient.data.entity.Message

@Dao
interface MessageDao: AbstractDao<Message> {
    @Query("SELECT * FROM message WHERE id=:id")
    suspend fun getOneById(id: Long): Message?

    @Query("SELECT * FROM message")
    suspend fun getAll(): List<Message>

    @Query("SELECT * FROM message ORDER BY date_time DESC")
    fun getItems(): DataSource.Factory<Int, Message>
}