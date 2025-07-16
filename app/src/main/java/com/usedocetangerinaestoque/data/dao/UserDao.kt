package com.usedocetangerinaestoque.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.usedocetangerinaestoque.data.entities.User

@Dao
interface UserDao {
    // provavelmente apenas para teste
    @Query("SELECT * FROM user WHERE active = 1")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE ID = :id AND active = 1")
    suspend fun getById(id: Int): User?

    @Query("SELECT * FROM user WHERE email = :email AND password = :password AND active = 1")
    suspend fun getByEmailAndPasswordAndActive(email: String, password: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE email = :email AND active = 1)")
    suspend fun existEmailAndActive(email: String): Boolean

    @Insert
    suspend fun add(user: User)
}