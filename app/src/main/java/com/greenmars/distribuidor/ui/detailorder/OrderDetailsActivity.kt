package com.greenmars.distribuidor.ui.detailorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.greenmars.distribuidor.R
import com.greenmars.distribuidor.databinding.ActivityOrderDetailsBinding
import com.greenmars.distribuidor.ui.detailorder.adapter.DetailAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailsActivity : AppCompatActivity() {

    private val viewModel: OrderDetailsViewModel by viewModels()

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var adapterDetail: DetailAdapter
    private var orderid: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        val bundle = intent.extras
        if (bundle != null) {
            orderid = bundle.getString("orderid", "0").toInt()
            viewModel.getOrderDetails(orderid)
        }
    }

    private fun initUI() {
        adapterDetail = DetailAdapter()
        binding.rvDetails.apply {
            adapter = adapterDetail
            layoutManager = GridLayoutManager(context, 1)
        }

        binding.btnAceptar.setOnClickListener {
            viewModel.updateStatusOrder("confirm", orderid)
        }

        binding.btnRechazar.setOnClickListener {
            viewModel.updateStatusOrder("refuse", orderid)
        }

        binding.btnEntregado.setOnClickListener {
            viewModel.updateStatusOrder("delivered", orderid)
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.order.collect {
                        Log.i("order", "$it")
                        if (it != null) {
                            adapterDetail.update(it.details)

                            when (it.order.status) {
                                "confirm" -> {
                                    binding.btnRechazar.visibility = View.GONE
                                    binding.btnAceptar.visibility = View.GONE
                                    binding.btnEntregado.visibility = View.VISIBLE
                                }

                                "delivered" -> {
                                    binding.btnRechazar.visibility = View.GONE
                                    binding.btnAceptar.visibility = View.GONE
                                    binding.btnEntregado.visibility = View.GONE
                                }

                                "cancel" -> {
                                    binding.btnRechazar.visibility = View.GONE
                                    binding.btnAceptar.visibility = View.GONE
                                    binding.btnEntregado.visibility = View.GONE
                                }

                                "refuse" -> {
                                    binding.btnRechazar.visibility = View.GONE
                                    binding.btnAceptar.visibility = View.GONE
                                    binding.btnEntregado.visibility = View.GONE
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.status.collect {
                        if (it != null) {
                            if (it.status == 200) {
                                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT)
                                    .show()
                                viewModel.getOrderDetails(orderid)
                            }
                        }
                    }
                }

            }
        }
    }
}