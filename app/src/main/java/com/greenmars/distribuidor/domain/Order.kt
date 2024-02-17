package com.greenmars.distribuidor.domain

data class Order(
    val id: Int,
    val status: String,
    val empresa: String,
    val product: String,
    val phone: String,
    val total: Double
)
