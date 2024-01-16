package com.greenmars.distribuidor.ui.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.domain.ProductStaff

class ProductAdapter(private var productList: List<ProductStaff> = emptyList()) :
    RecyclerView.Adapter<ProductViewHolder>() {

    fun updateList(list: List<ProductStaff>) {
        productList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        )
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.render(productList[position])
    }
}