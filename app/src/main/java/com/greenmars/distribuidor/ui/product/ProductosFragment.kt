package com.greenmars.distribuidor.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.database.DatabaseHelper
import com.greenmars.distribuidor.databinding.FragmentProductosBinding
import com.greenmars.distribuidor.model.Account
import com.greenmars.distribuidor.ui.product.adapter.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductosFragment : Fragment() {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!
    private lateinit var account: Account

    private val productViewModel by viewModels<ProductViewModel>()
    private lateinit var productAdapter: ProductAdapter


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductosBinding.inflate(layoutInflater, container, false)
        val databaseHelper = DatabaseHelper(context)
        account = databaseHelper.acountToken
        initUI()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            productViewModel.products.collect {
                productAdapter.updateList(it)
            }
        }
        productViewModel.getProductsStaff(account.company_id)
    }

    private fun initUI() {
        initList()
        initUIState()

        binding.btnOpenAdd.setOnClickListener {
            findNavController().navigate(
                    ProductosFragmentDirections.actionProductosFragmentToAgregarProductoActivity2()
            )
        }
    }

    private fun initUIState() {

        Log.i("ProductoFragment", "token db: " + account.token)
        productViewModel.getProductsStaff(account.company_id)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.products.collect() {
                    Log.i("Productos", it.toString())
                    productAdapter.updateList(it)
                }
            }
        }
    }

    private fun initList() {
        productAdapter = ProductAdapter()
        binding.rvProductos.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = productAdapter
        }
    }


}