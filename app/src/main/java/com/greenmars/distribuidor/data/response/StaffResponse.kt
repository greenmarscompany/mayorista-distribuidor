package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName

data class StaffResponse(
    @SerializedName("staff_id") val staffId: String,
    val name: String,
    val phone: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val type: String,
    val image: String?,
    val code: Int,
    @SerializedName("token_device") val tokenDevice: String,
    @SerializedName("company_id") val companyId: String,
    @SerializedName("user_id") val userId: Int
)
