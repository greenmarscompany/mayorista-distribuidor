package com.greenmars.distribuidor.domain

import com.greenmars.distribuidor.data.model.OrderRegister

data class OrderStore(
    val clientId: String,
    val details: List<OrderItemStore>
) {
    fun toEntity(): OrderRegister {
        return OrderRegister(
            clientId = clientId,
            details = details.map { it.toEntity() }
        )
    }
}