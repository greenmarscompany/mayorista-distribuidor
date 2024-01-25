package com.greenmars.distribuidor.domain

data class Order(
    val id: Int,
    val status: String,
    val empresa: String,
    val product: String,
    val total: Double
)
