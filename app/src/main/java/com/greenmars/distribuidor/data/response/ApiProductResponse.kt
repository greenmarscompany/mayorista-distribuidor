package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName

data class ApiProductResponse(
        @SerializedName("status") val status: Int,
        @SerializedName("message") val message: String,
        @SerializedName("data") val data: ProductData
)

data class ProductData(
        @SerializedName("idproduct") val idproduct: Int
)
