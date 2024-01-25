package com.greenmars.distribuidor.ui.store.product.adapter

import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greenmars.distribuidor.Variable
import com.greenmars.distribuidor.databinding.ItemStoreCategoryBinding
import com.greenmars.distribuidor.databinding.ItemStoreProductBinding
import com.greenmars.distribuidor.domain.Category
import com.greenmars.distribuidor.domain.ProductStore

class ProductStoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemStoreProductBinding.bind(view)
    private val selectedItems = SparseBooleanArray()

    fun render(product: ProductStore, onItemSelected: (ProductStore) -> Unit) {

        binding.apply {
            tvName.text = product.name
            tvPrecio.text = "S/. ${product.price} x ${product.unitMeasurement}"
            Glide.with(ivImage.context)
                .load(Variable.HOST_BASE_MEDIA + product.image)
                .fitCenter()
                .into(ivImage)

            btnAgregarCart.setOnClickListener {
                onItemSelected(product)
            }
        }
    }
}
