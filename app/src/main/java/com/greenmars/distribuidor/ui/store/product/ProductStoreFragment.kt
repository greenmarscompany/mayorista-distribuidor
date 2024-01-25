package com.greenmars.distribuidor.ui.store.product

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.greenmars.distribuidor.databinding.FragmentProductStoreBinding
import com.greenmars.distribuidor.domain.CartStoreItem
import com.greenmars.distribuidor.ui.store.StoreActivityViewModel
import com.greenmars.distribuidor.ui.store.product.adapter.ProductStoreAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductStoreFragment : Fragment() {

    private var _binding: FragmentProductStoreBinding? = null
    private val binding get() = _binding!!
    private val productViewModel by activityViewModels<ProductStoreViewModel>()
    private val storeActivityViewModel by viewModels<StoreActivityViewModel>()

    private lateinit var productAdapter: ProductStoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductStoreBinding.inflate(layoutInflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {


        val bundle = arguments
        if (bundle != null) {
            val datosRecibidos = bundle.getString("idcompany") as String
            productViewModel.getProductsCompany(datosRecibidos)
        }

        storeActivityViewModel.getCart()
        storeActivityViewModel.getCountItem()


        productAdapter = ProductStoreAdapter(onItemSelected = {

            val storeItem = CartStoreItem(
                id = 0,
                cartId = 0L,
                product = it.id,
                quantity = 1,
                price = it.price,
                image = it.image,
                nameProduct = it.name
            )

            storeActivityViewModel.saveCartItem(storeItem)

        })

        binding.rvProductos.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = productAdapter
        }

        initState()
    }

    private fun initState() {
        val sharedViewModel =
            ViewModelProvider(requireActivity()).get(StoreActivityViewModel::class.java)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    productViewModel.products.collect {
                        productAdapter.update(it)
                    }
                }

                launch {
                    storeActivityViewModel.counter.collect {
                        sharedViewModel.getCountItem()
                    }
                }
            }
        }
    }
}