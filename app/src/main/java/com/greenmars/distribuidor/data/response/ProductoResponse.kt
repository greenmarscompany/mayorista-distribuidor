package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.ProductStaff


data class ProductoResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("description") val description: String,
    @SerializedName("category_id") val category: CategoryResponse,
    @SerializedName("image") val image: String,

    ) {

}