package com.greenmars.distribuidor.domain

data class ProductStore(
    val id: Int,
    val name: String,
    val unitMeasurement: String,
    val image: String,
    val price: Double,
    val companyId: String
)
