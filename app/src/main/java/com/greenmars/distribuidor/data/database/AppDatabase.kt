package com.greenmars.distribuidor.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.greenmars.distribuidor.data.database.dao.CartDao
import com.greenmars.distribuidor.data.database.dao.CartItemDao
import com.greenmars.distribuidor.data.database.dao.ProveedorDao
import com.greenmars.distribuidor.data.database.entity.Cart
import com.greenmars.distribuidor.data.database.entity.CartItem
import com.greenmars.distribuidor.data.database.entity.User

@Database(
    entities = [
        User::class,
        Cart::class,
        CartItem::class], version = 7
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCartDao(): CartDao

    abstract fun getCartItemDao(): CartItemDao

    abstract fun getProveedorDao(): ProveedorDao
}