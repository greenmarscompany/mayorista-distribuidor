package com.greenmars.distribuidor.data.model

import com.google.gson.annotations.SerializedName

data class OrderItemRegister(
    val product: Int,
    @SerializedName("unit_price") val unitPrice: Double,
    val quantity: Int
)