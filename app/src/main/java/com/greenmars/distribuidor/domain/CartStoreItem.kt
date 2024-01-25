package com.greenmars.distribuidor.domain

data class CartStoreItem(
    val id: Long,
    var cartId: Long,
    val product: Int,
    var quantity: Int,
    val price: Double,
    val image: String,
    val nameProduct: String,
)
