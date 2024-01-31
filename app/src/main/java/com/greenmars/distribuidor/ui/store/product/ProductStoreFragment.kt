package com.greenmars.distribuidor.ui.store.product

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.databinding.FragmentProductStoreBinding
import com.greenmars.distribuidor.domain.CartStore
import com.greenmars.distribuidor.domain.CartStoreItem
import com.greenmars.distribuidor.ui.store.StoreActivityViewModel
import com.greenmars.distribuidor.ui.store.cart.CartFragment
import com.greenmars.distribuidor.ui.store.company.StoreCompanyFragment
import com.greenmars.distribuidor.ui.store.product.adapter.ProductStoreAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProductStoreFragment : Fragment() {

    private var _binding: FragmentProductStoreBinding? = null
    private val binding get() = _binding!!
    private val productViewModel by viewModels<ProductStoreViewModel>()
    private val storeActivityViewModel by viewModels<StoreActivityViewModel>()

    private lateinit var badge: BadgeDrawable
    private lateinit var productAdapter: ProductStoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductStoreBinding.inflate(layoutInflater, container, false)

        initUI()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentInits = StoreCompanyFragment()
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

        if (productViewModel.counter.value < 0) {
            binding.tvCounter.visibility = View.GONE
        }


        var staffId = ""
        var datosRecibidos = ""
        val bundle = arguments
        if (bundle != null) {
            datosRecibidos = bundle.getString("idcompany") as String
            staffId = bundle.getString("idstaff") as String
            productViewModel.getProductsCompany(datosRecibidos)
            // Log.i("fragm", "idcompañia: ${datosRecibidos}")

            val store = CartStore(
                id = 0,
                clientId = staffId,
                companyId = datosRecibidos
            )

            productViewModel.saveCart(store)
            productViewModel.getCart(datosRecibidos)
            productViewModel.getCountItem(datosRecibidos)
            Log.i("fragm", "idcompañia: ${datosRecibidos}")
        }




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

            productViewModel.saveCartItem(datosRecibidos, storeItem)
            animarCart()

        })

        binding.rvProductos.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = productAdapter
        }

        binding.btnOrder.setOnClickListener {
            val fragmentInit = CartFragment()
            val bundles = Bundle().apply {
                putString("idcompany", datosRecibidos)
                putString("idstaff", staffId)
            }
            fragmentInit.arguments = bundles
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.containerFragments, fragmentInit)
                .commit()
        }

        initState()
    }

    private fun animarCart() {
        val animator = ObjectAnimator.ofFloat(binding.btnOrder, "translationX", -10f, 10f)
        animator.duration = 50 // Duración de cada "vibración" en milisegundos
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.repeatCount = 5 // Número de "vibraciones"
        animator.repeatMode = ObjectAnimator.REVERSE // Invertir la animación en cada repetición
        animator.start()
    }

    private fun initState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    productViewModel.products.collect {
                        productAdapter.update(it)
                    }
                }

                launch {
                    productViewModel.counter.collect {
                        Log.i("counter", "Cantidad Activity: $it")
                        binding.tvCounter.visibility = if (it > 0) View.VISIBLE else View.GONE
                        binding.tvCounter.text = it.toString()

                    }
                }
            }
        }
    }

}