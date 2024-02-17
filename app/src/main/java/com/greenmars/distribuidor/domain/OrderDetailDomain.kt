package com.greenmars.distribuidor.domain

data class OrderDetailDomain(
    val order: OrderRDDomain,
    val details: List<DetailRDDomain>
)