package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.Company

data class CompanyResponse(
    @SerializedName("company_id") val companyId: String,
    @SerializedName("ruc") val ruc: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("url_facturacion") val urlFacturacion: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("is_supplier") val isSupplier: Boolean
) {
    fun toDomain(): Company {
        return Company(
            id = companyId,
            name = name,
            image = image ?: ""
        )
    }
}
