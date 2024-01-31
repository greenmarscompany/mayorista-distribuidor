package com.greenmars.distribuidor.ui.store.company

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.greenmars.distribuidor.MainActivity
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.databinding.FragmentStoreCompanyBinding
import com.greenmars.distribuidor.ui.store.company.adapter.CategoryAdapter
import com.greenmars.distribuidor.ui.store.company.adapter.CompanyAdapter
import com.greenmars.distribuidor.ui.store.product.ProductStoreFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StoreCompanyFragment : Fragment() {

    private var _binding: FragmentStoreCompanyBinding? = null
    private val binding get() = _binding!!
    private val storeViewModel by viewModels<StoreCompanyViewModel>()

    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var adapterCompany: CompanyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreCompanyBinding.inflate(layoutInflater, container, false)

        storeViewModel.getCategories()
        storeViewModel.getCompanies()
        initUI()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    private fun initUI() {
        adapterCategory = CategoryAdapter(onItemSelected = {
            storeViewModel.getCompanies(it.id)
        })

        binding.rvCategoriasStore.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = adapterCategory
        }

        adapterCompany = CompanyAdapter(onItemSelected = {
            enviarDatos(it.id, it.staffId)
        })

        binding.rvCompanies.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adapterCompany
        }

        initState()

    }

    private fun enviarDatos(id: String, staffId: String) {
        val fragment = ProductStoreFragment()
        val bundle = Bundle().apply {
            putString("idcompany", id)
            putString("idstaff", staffId)
        }
        fragment.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerFragments, fragment)
            .commit()
    }

    private fun initState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    storeViewModel.categories.collect {
                        adapterCategory.update(it)
                    }
                }

                launch {
                    storeViewModel.companies.collect {
                        adapterCompany.update(it)
                    }
                }
            }
        }
    }

}