package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName

class ApiImageResponse(
        @SerializedName("status") val status: Int,
        @SerializedName("message") val message: String,
)
