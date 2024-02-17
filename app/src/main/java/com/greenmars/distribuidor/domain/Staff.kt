package com.greenmars.distribuidor.domain

data class Staff(
    val staffId: String,
    val name: String,
    val phone: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val type: String,
    val image: String?,
    val code: Int,
    val companyId: String,
    val userId: Int,
    val ruc: String,
    val nameCompany: String,
    val phoneCompany: String,
    val addressCompany: String,
    val isSupplier: String
)