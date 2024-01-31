package com.greenmars.distribuidor.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.greenmars.distribuidor.data.database.entity.Cart

@Dao
interface CartDao {

    @Insert
    suspend fun insertCart(cart: Cart): Long

    @Query("SELECT * FROM table_cart WHERE company_id = :companyId LIMIT 1")
    suspend fun getCart(companyId: String): Cart?

    @Query("SELECT * FROM table_cart WHERE cartId = :cartId")
    suspend fun getCartById(cartId: Long): Cart?


}