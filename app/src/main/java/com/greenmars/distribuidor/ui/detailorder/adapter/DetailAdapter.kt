package com.greenmars.distribuidor.ui.detailorder.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.domain.DetailRDDomain
import com.greenmars.distribuidor.domain.ProductStore
import com.greenmars.distribuidor.ui.store.product.adapter.ProductStoreViewHolder

class DetailAdapter(private var details: List<DetailRDDomain> = emptyList()) :
    RecyclerView.Adapter<DetailViewHolder>() {

    fun update(list: List<DetailRDDomain>) {
        details = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_detailfab, parent, false)
        )
    }

    override fun getItemCount(): Int = details.size

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.render(details[position])
    }
}