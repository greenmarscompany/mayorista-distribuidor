package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.ResponseMessage

data class ApiResponseMessage(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
) {
    fun toDomain(): ResponseMessage {
        return ResponseMessage(
            status = status,
            message = message
        )
    }
}