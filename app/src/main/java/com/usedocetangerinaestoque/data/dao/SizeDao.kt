package com.usedocetangerinaestoque.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.usedocetangerinaestoque.data.entities.Size
import kotlinx.coroutines.flow.Flow

@Dao
interface SizeDao {
    @Query("SELECT * FROM size WHERE active = 1")
    fun observeAll(): Flow<List<Size>>

    @Query("SELECT * FROM size where id = :id AND active = 1")
    suspend fun getById(id: Long): Size?

    @Insert
    suspend fun add(size: Size)

    @Query("SELECT EXISTS(SELECT name FROM size WHERE name = :name AND active = 1)")
    suspend fun existNameActive(name: String): Boolean
}