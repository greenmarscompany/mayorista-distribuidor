package com.greenmars.distribuidor.ui.store.company.adapter

import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.databinding.ItemStoreCategoryBinding
import com.greenmars.distribuidor.domain.Category

class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemStoreCategoryBinding.bind(view)
    private val selectedItems = SparseBooleanArray()

    fun render(category: Category, onItemSelected: (Category) -> Unit) {

        binding.apply {
            tvName.text = category.name
            parent.setOnClickListener {
                onItemSelected(category)
                Log.i("vhcat", category.toString())
            }
        }
    }
}
