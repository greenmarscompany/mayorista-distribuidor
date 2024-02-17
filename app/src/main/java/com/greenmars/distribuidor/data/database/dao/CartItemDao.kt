package com.greenmars.distribuidor.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.greenmars.distribuidor.data.database.entity.CartItem

@Dao
interface CartItemDao {

    @Insert
    suspend fun insertCartItem(cartItem: CartItem): Long

    @Query("SELECT * FROM table_cart_item WHERE cartId = :cartId")
    suspend fun getCartItemsForCart(cartId: Long): List<CartItem>

    @Query("DELETE FROM table_cart_item WHERE itemId = :itemId")
    suspend fun deleteCartItem(itemId: Long)

    @Query("SELECT COUNT(cartId) FROM table_cart_item WHERE cartId = :cartId")
    suspend fun getCountItem(cartId: Long): Int

    @Query("UPDATE table_cart_item SET quantity = :quantity WHERE itemId = :id")
    suspend fun updateDataItem(quantity: Int, id: Long): Int

    @Query("DELETE FROM table_cart_item WHERE cartId = :cartId")
    suspend fun deleteAllItemCart(cartId: Long)

}