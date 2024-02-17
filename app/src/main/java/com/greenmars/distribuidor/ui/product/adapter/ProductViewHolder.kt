package com.greenmars.distribuidor.ui.product.adapter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.Variable
import com.greenmars.distribuidor.databinding.ItemProductoBinding
import com.greenmars.distribuidor.domain.ProductStaff

class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemProductoBinding.bind(view)
    private val context: Context = view.context
    fun render(productStaff: ProductStaff) {

        binding.apply {
            txtName.text = productStaff.name
            txtDescription.text = productStaff.description
            Glide.with(ivProduct.context)
                .load(Variable.HOST_BASE + productStaff.image)
                .fitCenter()
                .into(ivProduct)

            if (productStaff.status) {
                txtStatus.setBackgroundResource(R.drawable.rounded_status)
                txtStatus.text = "Activado"
            } else {
                txtStatus.setBackgroundResource(R.drawable.rounded_status_disable)
                txtStatus.text = "En revisión"
            }
        }
    }
}
