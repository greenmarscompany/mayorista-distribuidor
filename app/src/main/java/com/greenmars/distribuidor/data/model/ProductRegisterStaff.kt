package com.greenmars.distribuidor.data.model

import com.google.gson.annotations.SerializedName

data class ProductRegisterStaff(
        @SerializedName("category_id") val idCategory: Int,
        @SerializedName("marke_id") val idMarke: Int,
        @SerializedName("detail_measurement_id") val idDetailMeasurement: Int,
        @SerializedName("description") val description: String,
        @SerializedName("measurement") val measurement: Int,
        @SerializedName("unit_measurement_id") val idUnitMeasurement: Int,
        @SerializedName("unit_price") val uniPrice: Double,
)