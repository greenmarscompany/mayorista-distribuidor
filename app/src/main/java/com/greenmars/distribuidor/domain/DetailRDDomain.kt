package com.greenmars.distribuidor.domain
data class DetailRDDomain(
     val id: Int,
     val quantity: Int,
     val unitPrice: Double,
     val orderId: Int,
     val productId: Int,
     val description: String,
     val marca: String,
     val detalleMedida: String,
     val unidadMedida: String
)