package com.greenmars.distribuidor.ui.order.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.FabricanteActivity
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.databinding.ItemOrderSupplierBinding
import com.greenmars.distribuidor.domain.Order
import com.greenmars.distribuidor.ui.detailorder.OrderDetailsActivity

class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemOrderSupplierBinding.bind(view)
    private val context: Context = view.context

    fun render(order: Order) {
        binding.apply {
            tvStatus.text = getStringStatus(order.status)
            tvEmpresa.text = "${order.empresa} - ${order.phone}"
            tvProductos.text = order.product
            tvTotal.text = "S/. ${order.total}"
        }

        binding.clickOrder.setOnClickListener {
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("orderid", order.id.toString())
            context.startActivity(intent)
            Log.i("holder", "Order click: $order")
        }

        setColorTitle(order.status)
    }

    private fun getStringStatus(status: String): String {
        return when (status) {
            "wait" -> "PEDIDO NUEVO"
            "refuse" -> "PEDIDO RECHAZADO"
            "confirm" -> "PEDIDO ACEPTADO"
            "delivered" -> "PEDIDO ENTREGADO"
            "cancel" -> "PEDIDO CANCELADO"
            else -> ""
        }
    }

    private fun setColorTitle(status: String) {
        when (status) {
            "wait" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#E6AD02"))
                binding.ivStatus.setImageResource(R.drawable.icon_waiting_box)
            }

            "refuse" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#9B004F"))
                binding.ivStatus.setImageResource(R.drawable.icon_refuse_box)
            }

            "confirm" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                binding.ivStatus.setImageResource(R.drawable.ico_acept_box)
            }

            "delivered" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#0E7ED6"))
                binding.ivStatus.setImageResource(R.drawable.icon_send_box)
            }

            "cancel" -> {
                binding.tvStatus.setTextColor(Color.parseColor("#F44336"))
                binding.ivStatus.setImageResource(R.drawable.icon_cancel_box)
            }

            else -> binding.tvStatus.setTextColor(Color.parseColor("#FFC107"))
        }
    }

}