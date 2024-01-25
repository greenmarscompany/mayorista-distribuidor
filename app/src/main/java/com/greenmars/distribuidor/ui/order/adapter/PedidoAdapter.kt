package com.greenmars.distribuidor.ui.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.domain.Order

class PedidoAdapter(private var orders: List<Order> = emptyList()) :
    RecyclerView.Adapter<PedidoViewHolder>() {

    fun update(list: List<Order>) {
        orders = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        return PedidoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_supplier, parent, false)
        )
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.render(orders[position])
    }
}