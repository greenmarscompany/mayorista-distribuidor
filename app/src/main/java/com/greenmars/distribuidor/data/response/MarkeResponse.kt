package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.Category
import com.greenmars.distribuidor.domain.Marke

data class MarkeResponse(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
) {
    fun toDomain(): Marke {
        return Marke(
                id = id,
                name = name
        )
    }
}
