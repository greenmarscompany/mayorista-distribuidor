package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.ProductStaff

data class ProductItemResponse(
    @SerializedName("product") val product: ProductoResponse,
    @SerializedName("register") val register: RegisterResponse
) {
    fun toDomain(): ProductStaff {
        return ProductStaff(
            id = product.id,
            image = product.image,
            description = product.description,
            name = product.category.name ?: "",
            status = register.status
        )
    }
}