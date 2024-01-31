package com.greenmars.distribuidor.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.greenmars.distribuidor.data.database.dao.CartDao
import com.greenmars.distribuidor.data.database.dao.CartItemDao
import com.greenmars.distribuidor.data.database.entity.Cart
import com.greenmars.distribuidor.data.database.entity.CartItem

@Database(entities = [Cart::class, CartItem::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCartDao(): CartDao

    abstract fun getCartItemDao(): CartItemDao
}