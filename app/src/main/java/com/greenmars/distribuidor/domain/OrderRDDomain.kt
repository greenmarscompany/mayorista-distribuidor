package com.greenmars.distribuidor.domain

data class OrderRDDomain(
    val id: Int,
    val date: String,
    val time: String,
    val status: String,
    val client: String,
    val staff: String
)