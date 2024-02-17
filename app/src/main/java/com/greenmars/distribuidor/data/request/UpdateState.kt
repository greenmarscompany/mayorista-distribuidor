package com.greenmars.distribuidor.data.request

import com.google.gson.annotations.SerializedName

data class UpdateState(
    @SerializedName("order_id") val orderid: Int,
    @SerializedName("state") val state: String,
)
