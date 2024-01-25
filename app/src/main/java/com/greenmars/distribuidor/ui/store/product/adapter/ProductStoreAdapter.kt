package com.greenmars.distribuidor.ui.store.product.adapter

import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.domain.Category
import com.greenmars.distribuidor.domain.ProductStore

class ProductStoreAdapter(
    private var productsList: List<ProductStore> = emptyList(),
    private val onItemSelected: (ProductStore) -> Unit
) : RecyclerView.Adapter<ProductStoreViewHolder>() {

    private val selectedItems = SparseBooleanArray()

    fun update(list: List<ProductStore>) {
        Log.i("rev", list.toString())
        productsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductStoreViewHolder {
        return ProductStoreViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_store_product, parent, false)
        )
    }

    override fun getItemCount(): Int = productsList.size

    override fun onBindViewHolder(holder: ProductStoreViewHolder, position: Int) {
        holder.render(productsList[position], onItemSelected)
    }

}