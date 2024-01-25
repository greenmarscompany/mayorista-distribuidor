package com.greenmars.distribuidor.ui.store.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.domain.OrderStore
import com.greenmars.distribuidor.domain.ProductStaff
import com.greenmars.distribuidor.domain.Repository
import com.greenmars.distribuidor.domain.ResponseMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private var _messageOrder = MutableStateFlow(ResponseMessage(0, "error"))
    val messageOrder: StateFlow<ResponseMessage> = _messageOrder

    fun saveOrder(order: OrderStore) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.saveOrder(order)
            }

            if (result != null) {
                _messageOrder.value = result
            }
        }
    }

}