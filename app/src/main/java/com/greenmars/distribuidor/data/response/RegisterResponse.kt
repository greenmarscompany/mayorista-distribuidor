package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("id") val registerId: Int,
    @SerializedName("date") val date: String,
    @SerializedName("price") val price: String,
    @SerializedName("status") val status: Boolean,
)