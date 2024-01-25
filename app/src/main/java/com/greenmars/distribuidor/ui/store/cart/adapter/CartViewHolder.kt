package com.greenmars.distribuidor.ui.store.cart.adapter


import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greenmars.distribuidor.Variable
import com.greenmars.distribuidor.databinding.ItemStoreCartBinding
import com.greenmars.distribuidor.domain.CartStoreItem
import com.greenmars.distribuidor.util.StringUtils

class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemStoreCartBinding.bind(itemView)


    fun render(item: CartStoreItem, onItemEventListener: OnItemEventListener) {
        Log.i("vhcart", "Inicio el view holder")
        binding.apply {
            tvName.text = StringUtils.truncarTexto(item.nameProduct, 15)
            Glide.with(ivImage.context).load(Variable.HOST_BASE_MEDIA + item.image).fitCenter()
                .into(ivImage)
            tvQuantity.text = item.quantity.toString()
            tvPrice.text = "Total: S/. ${item.quantity * item.price}"
            tvUnitPrice.text = "Precio: S/. ${item.price}"
        }

        binding.btnDisminuir.isEnabled = item.quantity > 1



        binding.tvEliminarItem.setOnClickListener {
            onItemEventListener.onDeleteItem(item)
        }

        binding.btnDisminuir.setOnClickListener {
            val tempItem = item


            if (item.quantity > 1) {
                tempItem.quantity--
                onItemEventListener.onClickDisminuirQuantity(item)
                binding.tvQuantity.text = "${item.quantity}"
                binding.tvPrice.text = "Total: S/. ${item.quantity * item.price}"
            } else {
                binding.btnDisminuir.isEnabled = false
            }
            binding.btnDisminuir.isEnabled = item.quantity > 1
        }

        binding.btnAumentar.setOnClickListener {
            item.quantity++
            onItemEventListener.onClickAumentarQuantity(item)
            binding.tvQuantity.text = "${item.quantity}"
            binding.tvPrice.text = "Total: S/. ${item.quantity * item.price}"

            binding.btnDisminuir.isEnabled = item.quantity > 1
        }
    }

}