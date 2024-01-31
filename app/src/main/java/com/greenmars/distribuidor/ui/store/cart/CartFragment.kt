package com.greenmars.distribuidor.ui.store.cart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.databinding.FragmentCartBinding
import com.greenmars.distribuidor.domain.CartStoreItem
import com.greenmars.distribuidor.domain.OrderItemStore
import com.greenmars.distribuidor.domain.OrderStore
import com.greenmars.distribuidor.ui.store.StoreActivityViewModel
import com.greenmars.distribuidor.ui.store.cart.adapter.CartAdapter
import com.greenmars.distribuidor.ui.store.cart.adapter.OnItemEventListener
import com.greenmars.distribuidor.ui.store.company.StoreCompanyFragment
import com.greenmars.distribuidor.ui.store.product.ProductStoreFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    // private val viewModel: CartViewModel by viewModels()
    private val viewModelStores: StoreActivityViewModel by activityViewModels()
    private val viewModelCart: CartViewModel by viewModels()

    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding get() = _binding!!

    private lateinit var adapterCart: CartAdapter
    private lateinit var idstaff: String
    private lateinit var companyId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(layoutInflater, container, false)

        initUI()
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentInits = ProductStoreFragment()
                val bundle = Bundle().apply {
                    putString("idcompany", companyId)
                    putString("idstaff", idstaff)
                }
                fragmentInits.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.containerFragments, fragmentInits)
                    .addToBackStack(null)
                    .commit()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    private fun initUI() {

        ocultarOpciones()

        val bundle = arguments
        if (bundle != null) {
            companyId = bundle.getString("idcompany") as String
            idstaff = bundle.getString("idstaff") as String
            viewModelStores.getItemsCart(companyId)
            viewModelCart.getCart(companyId)
        }

        adapterCart = CartAdapter(onItemListener = object : OnItemEventListener {
            override fun onClickAumentarQuantity(item: CartStoreItem) {
                viewModelStores.updateQuantityPlus(item)
            }

            override fun onClickDisminuirQuantity(item: CartStoreItem) {
                viewModelStores.updateQuantityPlus(item)
            }

            override fun onDeleteItem(item: CartStoreItem) {
                viewModelStores.deleteItem(item)
            }
        })
        binding.rvCart.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = adapterCart
        }

        binding.btnGuardar.setOnClickListener {
            val cart = viewModelCart.carts.value
            val itemsCart = viewModelStores.itemsCart.value
            val listItem = itemsCart.map {
                OrderItemStore(
                    product = it.product,
                    unitPrice = it.price,
                    quantity = it.quantity
                )
            }.toMutableList()

            val order = OrderStore(
                clientId = cart?.clientId ?: "",
                details = listItem
            )

            viewModelCart.saveOrder(order)
        }

        initState()
    }

    private fun initState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModelStores.itemsCart.collect {
                        if (it.isNotEmpty()) {
                            mostrarOpciones()
                        } else {
                            ocultarOpciones()
                        }
                        adapterCart.update(it)
                    }
                }

                launch {
                    viewModelStores.totalPrices.collect {
                        binding.tvTotal.text = "Total S/. ${it}"
                    }
                }

                launch {
                    viewModelCart.messageOrder.collect {
                        if (it.status != 0) {
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun mostrarOpciones() {
        binding.tvTotal.visibility = View.VISIBLE
        binding.btnGuardar.visibility = View.VISIBLE
        binding.btnLimpiar.visibility = View.VISIBLE
        binding.tvNoItems.visibility = View.GONE
    }

    private fun ocultarOpciones() {
        binding.tvTotal.visibility = View.GONE
        binding.btnGuardar.visibility = View.GONE
        binding.btnLimpiar.visibility = View.GONE
        binding.tvNoItems.visibility = View.VISIBLE
    }
}