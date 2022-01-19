package com.distep.chatclient.data.db.dao

import androidx.room.*
import com.distep.chatclient.data.entity.BaseEntity

@Dao
interface AbstractDao<T : BaseEntity> {

    @Insert
    suspend fun insertAll(entities: List<T>): LongArray

    @Insert
    suspend fun insert(entity: T): Long

    @Delete
    suspend fun delete(entity: T)

    @Update
    suspend fun update(entity: T)

    @Update
    suspend fun updateMany(entity: List<T>)

}
