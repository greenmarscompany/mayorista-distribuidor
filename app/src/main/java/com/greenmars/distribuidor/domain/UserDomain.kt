package com.greenmars.distribuidor.domain

data class UserDomain(
    val id: Long = 0L,
    val dni: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val password: String,
    val token: String,
    val tipe: String,
    val companyId: String,
    val companyName: String,
    val companyPhone: String,
    val companyAddress: String,
    val companyLatitude: Double,
    val companyLongitude: Double,
    val companyRuc: String,
    val nombre: String,
    val isSupplier: String,
    val cloudId: Long
)