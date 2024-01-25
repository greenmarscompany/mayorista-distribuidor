package com.greenmars.distribuidor.ui.store.company.adapter

import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.domain.Category

class CategoryAdapter(
    private var categoriesList: List<Category> = emptyList(),
    private val onItemSelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val selectedItems = SparseBooleanArray()

    fun update(list: List<Category>) {
        Log.i("rev", list.toString())
        categoriesList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_store_category, parent, false)
        )
    }

    override fun getItemCount(): Int = categoriesList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.render(categoriesList[position], onItemSelected)
    }

}