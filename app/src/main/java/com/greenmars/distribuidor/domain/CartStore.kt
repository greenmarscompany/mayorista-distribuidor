package com.greenmars.distribuidor.domain

import com.greenmars.distribuidor.data.database.entity.Cart

data class CartStore(
    val id: Long,
    val clientId: String
)