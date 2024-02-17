package com.greenmars.distribuidor.data.request

import com.google.gson.annotations.SerializedName

data class TokenUpdate(
    @SerializedName("token_device") val token: String
)
