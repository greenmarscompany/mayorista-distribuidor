package com.greenmars.distribuidor.data.database.dao

import androidx.room.*
import com.greenmars.distribuidor.data.database.entity.User
import io.reactivex.rxjava3.core.Maybe

@Dao
interface ProveedorDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user WHERE cloudId=:id")
    suspend fun getUser(id: Long): User?

    @Query("UPDATE user SET token = :token WHERE id = :id")
    suspend fun updateToken(token: String, id: Long)

    @Query("DELETE FROM user WHERE id=:id ")
    suspend fun deleteUser(id: Long)

    @Query("SELECT * FROM user WHERE cloudId=:id")
    fun getUserSin(id: Long): User?

    @Query("SELECT * FROM user WHERE cloudId=:id")
    fun getUserRx(id: Long): Maybe<User>
}