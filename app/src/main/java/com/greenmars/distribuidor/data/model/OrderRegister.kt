package com.greenmars.distribuidor.data.model

import com.google.gson.annotations.SerializedName

data class OrderRegister(
    @SerializedName("client_id") val clientId: String,
    val details: List<OrderItemRegister>
)