package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.Category

data class CategoryResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?
) {
    fun toDomain(): Category {
        return Category(
            id = id,
            name = name,
            image = image ?: ""
        )
    }
}
