package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.Order
import java.lang.StringBuilder

data class OrderResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: String,
    @SerializedName("client") val client: ClientResponse,
    @SerializedName("staff") val staff: String,
    @SerializedName("details") val details: List<OrderDetailResponse>,
) {

    fun toDomain(): Order {

        val sb = StringBuilder()
        val total = details.sumOf { it.unitPrice * it.quantity }

        for (detail in details.take(2)) {
            sb.append("->")
                .append(detail.productId)
                .append("\n")
        }

        return Order(
            id = id,
            status = status,
            empresa = client.name,
            product = sb.toString(),
            phone = client.phone,
            total = total
        )
    }

}

data class ClientResponse(
    @SerializedName("company_id")
    val companyId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String
)

data class OrderDetailResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("product_id")
    val productId: String
)