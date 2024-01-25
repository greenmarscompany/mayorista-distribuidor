package com.greenmars.distribuidor.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greenmars.distribuidor.domain.CartStore

@Entity(tableName = "table_cart")
data class Cart(

    @PrimaryKey(autoGenerate = true) val cartId: Long = 0,
    @ColumnInfo("client_id") val clientId: String,
) {
    fun toDomain(): CartStore {
        return CartStore(id = cartId, clientId = clientId)
    }
}


