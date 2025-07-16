package com.usedocetangerinaestoque.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.usedocetangerinaestoque.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category WHERE active = 1")
    fun observerAll(): Flow<List<Category>>

    @Query("SELECT * FROM category WHERE id = :id AND active = 1")
    suspend fun getById(id: Long): Category?

    @Insert
    suspend fun add(category: Category): Long

    @Query("SELECT EXISTS(SELECT name FROM category WHERE name = :name AND active = 1)")
    suspend fun existNameActive(name: String): Boolean
}