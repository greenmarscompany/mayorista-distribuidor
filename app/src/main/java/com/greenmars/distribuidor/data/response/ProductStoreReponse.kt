package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.ProductStore

data class ProductStoreReponse(
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("measurement")
    val measurement: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("image")
    val image: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("detail_measurement_id")
    val detailMeasurementId: Int,
    @SerializedName("marke_id")
    val markeId: Int,
    @SerializedName("unit_measurement_id")
    val unitMeasurementId: Int,
    @SerializedName("detail_measurement_name")
    val detailMeasurementName: String,
    @SerializedName("unit_measurement_name")
    val unitMeasurementName: String
) {
    fun toDomain(): ProductStore {
        return ProductStore(
            id = id,
            companyId = companyId,
            name = description,
            unitMeasurement = unitMeasurementName,
            image = image,
            price = unitPrice
        )
    }
}
