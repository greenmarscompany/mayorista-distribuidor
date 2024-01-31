package com.greenmars.distribuidor.ui.store.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.domain.CartStore
import com.greenmars.distribuidor.domain.OrderStore
import com.greenmars.distribuidor.domain.ProductStaff
import com.greenmars.distribuidor.domain.Repository
import com.greenmars.distribuidor.domain.RepositoryStore
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
    private val repository: Repository,
    private val repositoryStore: RepositoryStore,
) : ViewModel() {
    private var _messageOrder = MutableStateFlow(ResponseMessage(0, "error"))
    val messageOrder: StateFlow<ResponseMessage> = _messageOrder

    private var _carts = MutableStateFlow<CartStore?>(null)
    val carts: StateFlow<CartStore?> = _carts

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

    fun getCart(companyId: String) {
        viewModelScope.launch {
            Log.i("vmstore", "Se llamo a getCart()")
            val result = withContext(Dispatchers.IO) {
                repositoryStore.getCart(companyId)
            }

            if (result != null) {
                _carts.value = result
                Log.i("vmstore", "Data desde viewmodel: $result")
            }
        }
    }

}