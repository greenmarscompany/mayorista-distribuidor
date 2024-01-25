package com.greenmars.distribuidor.ui.store.company.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.domain.Company

class CompanyAdapter(
    private var companiesList: List<Company> = emptyList(),
    private val onItemSelected: (Company) -> Unit
) : RecyclerView.Adapter<CompanyViewHolder>() {


    fun update(list: List<Company>) {
        companiesList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        return CompanyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_store_companies, parent, false)
        )
    }

    override fun getItemCount(): Int = companiesList.size

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.render(companiesList[position], onItemSelected)
    }
}