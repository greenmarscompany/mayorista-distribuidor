package com.greenmars.distribuidor.ui.order

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.databinding.FragmentPedidosBinding
import com.greenmars.distribuidor.ui.order.adapter.PedidoAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PedidosFragment : Fragment() {

    private var _binding: FragmentPedidosBinding? = null
    private val binding get() = _binding!!
    private val pedidosViewModel by viewModels<PedidosViewModel>()

    private lateinit var pedidoAdapter: PedidoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPedidosBinding.inflate(layoutInflater, container, false)

        initUI()

        return binding.root
    }

    private fun initUI() {
        initState()
        pedidoAdapter = PedidoAdapter()

        binding.rvPedidos.apply {
            layoutManager = GridLayoutManager(context, 1)
            adapter = pedidoAdapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            pedidosViewModel.getOrders()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initState() {

        pedidosViewModel.getOrders()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pedidosViewModel.orders.collect {
                    pedidoAdapter.update(it)
                }
            }
        }
    }
}