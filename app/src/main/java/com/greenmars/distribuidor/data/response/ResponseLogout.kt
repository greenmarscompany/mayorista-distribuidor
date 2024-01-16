package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName

data class ResponseLogout(
        @SerializedName("status") val status: Int,
        @SerializedName("message") val message: String
)