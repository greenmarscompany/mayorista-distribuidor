package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.DetailRDDomain
import com.greenmars.distribuidor.domain.OrderDetailDomain
import com.greenmars.distribuidor.domain.OrderRDDomain
import java.util.stream.Collectors

data class OrderDetailsResponse(
    @SerializedName("order") val order: OrderRD,
    @SerializedName("details") val details: List<DetailsRD>
) {
    fun toDomain(): OrderDetailDomain {
        val detailsD: List<DetailRDDomain> = details.map {
            DetailRDDomain(
                id = it.id,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                orderId = it.orderId,
                productId = it.productId,
                description = it.description,
                marca = it.marca,
                detalleMedida = it.detalleMedida,
                unidadMedida = it.unidadMedida
            )
        }
        return OrderDetailDomain(
            order = OrderRDDomain(
                id = order.id,
                date = order.date,
                time = order.time,
                status = order.status,
                client = order.client,
                staff = order.staff
            ),
            details = detailsD
        )
    }
}

data class OrderRD(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("status") val status: String,
    @SerializedName("client") val client: String,
    @SerializedName("staff") val staff: String
)

data class DetailsRD(
    @SerializedName("id") val id: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unit_price") val unitPrice: Double,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("description") val description: String,
    @SerializedName("marca") val marca: String,
    @SerializedName("detalle_medida") val detalleMedida: String,
    @SerializedName("unidad_medida") val unidadMedida: String
)