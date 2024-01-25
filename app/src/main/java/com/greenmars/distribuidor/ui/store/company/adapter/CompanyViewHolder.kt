package com.greenmars.distribuidor.ui.store.company.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.databinding.ItemStoreCompaniesBinding
import com.greenmars.distribuidor.domain.Company

class CompanyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemStoreCompaniesBinding.bind(view)

    fun render(company: Company, onItemSelected: (Company) -> Unit) {
        binding.apply {
            tvName.text = company.name
            parent.setOnClickListener {
                onItemSelected(company)
            }
        }
    }

}
