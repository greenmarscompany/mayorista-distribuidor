package com.greenmars.distribuidor.ui.detailorder.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.databinding.ItemOrderDetailfabBinding
import com.greenmars.distribuidor.domain.DetailRDDomain

class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemOrderDetailfabBinding.bind(view)
    fun render(detailRDDomain: DetailRDDomain) {
        binding.apply {
            tvNombre.text = detailRDDomain.description
            tvPrice.text = "S/. ${detailRDDomain.unitPrice}"
            tvQuantity.text = "${detailRDDomain.quantity} ${detailRDDomain.unidadMedida}."
            tvTotalItem.text = "S/. ${detailRDDomain.unitPrice * detailRDDomain.quantity}"
        }
    }
}