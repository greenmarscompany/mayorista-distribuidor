package com.greenmars.distribuidor.domain

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.data.model.OrderItemRegister

data class OrderItemStore(
    val product: Int,
    val unitPrice: Double,
    val quantity: Int
) {
    fun toEntity(): OrderItemRegister {
        return OrderItemRegister(
            product = product,
            unitPrice = unitPrice,
            quantity = quantity
        )
    }
}