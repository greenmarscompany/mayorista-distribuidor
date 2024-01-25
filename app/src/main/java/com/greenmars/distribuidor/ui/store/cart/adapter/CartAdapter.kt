package com.greenmars.distribuidor.ui.store.cart.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.domain.CartStoreItem

class CartAdapter(
    private var items: List<CartStoreItem> = emptyList(),
    private val onItemListener: OnItemEventListener,
) : RecyclerView.Adapter<CartViewHolder>() {

    private var onDataDeleted: (() -> Unit)? = null

    fun setOnDataDeteled(listener: () -> Unit) {
        this.onDataDeleted = listener
    }

    fun update(newList: List<CartStoreItem>) {
        Log.i("adpcart", newList.toString())
        items = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_store_cart, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.render(items[position], onItemListener)
    }
}