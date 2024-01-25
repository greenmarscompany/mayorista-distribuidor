package com.greenmars.distribuidor.ui.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.domain.Order
import com.greenmars.distribuidor.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PedidosViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    fun getOrders() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getOrders()
            }
            if (result != null) {
                _orders.value = result
            }
        }
    }

}