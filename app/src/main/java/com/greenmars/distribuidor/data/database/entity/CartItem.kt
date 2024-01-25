package com.greenmars.distribuidor.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.greenmars.distribuidor.domain.CartStoreItem

@Entity(
    tableName = "table_cart_item",
    foreignKeys = [ForeignKey(
        entity = Cart::class,
        parentColumns = ["cartId"],
        childColumns = ["cartId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    val cartId: Long,
    val product: Int,
    @ColumnInfo("unit_price") val unitPrice: Double,
    val quantity: Int,
    val image: String,
    val nameProduct: String
) {

    fun toDomain(): CartStoreItem {
        return CartStoreItem(
            id = itemId,
            cartId = cartId,
            product = product,
            price = unitPrice,
            quantity = quantity,
            image = image,
            nameProduct = nameProduct
        )
    }
}