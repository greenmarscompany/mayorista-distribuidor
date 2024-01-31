package com.greenmars.distribuidor.ui.store

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.domain.CartStore
import com.greenmars.distribuidor.domain.CartStoreItem
import com.greenmars.distribuidor.domain.RepositoryStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StoreActivityViewModel @Inject constructor(
    private val repository: RepositoryStore
) : ViewModel() {

    private var _carts = MutableStateFlow<CartStore?>(null)
    val carts: StateFlow<CartStore?> = _carts

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> get() = _counter

    private var _itemsCart = MutableStateFlow<List<CartStoreItem>>(emptyList())
    val itemsCart: StateFlow<List<CartStoreItem>> = _itemsCart

    private var _totalPrices = MutableStateFlow<Double>(0.0)
    val totalPrices: StateFlow<Double> = _totalPrices

    /*fun saveCart(cartStore: CartStore) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val resultGetCart = repository.getCart()
                if (resultGetCart == null) {
                    Log.i("vmstore", "entro para guardar")
                    repository.saveCart(cartStore)
                }

            }
        }
    }

    fun getCart(companyId: String) {
        viewModelScope.launch {
            Log.i("vmstore", "Se llamo a getCart()")
            val result = withContext(Dispatchers.IO) {
                repository.getCart(companyId)
            }

            if (result != null) {
                _carts.value = result
                Log.i("vmstore", "Data desde viewmodel: $result")
            }
        }
    }

    fun getCountItem(companyId: String) {
        viewModelScope.launch {
            val cart = repository.getCart(companyId)
            val result = withContext(Dispatchers.IO) {
                repository.getItemCount(cart?.id ?: 0L)
            }
            Log.i("vmstore", "Cantidad de items: $result")
            if (result > 0) {
                _counter.value = result
            }
        }
    }*/

    fun saveCartItem(companyId: String, cartStoreItem: CartStoreItem) {
        viewModelScope.launch {
            val cart = repository.getCart(companyId)
            Log.i("vmstore", "Antes de guardar items: " + cart.toString())
            val itemsCart = repository.getCartItems(cart?.id ?: 0L)
            var isExists = false
            if (itemsCart != null) {
                for (item in itemsCart) {
                    if (item.product == cartStoreItem.product) {
                        isExists = true
                        repository.updateQuantityItem(item.quantity + 1, item.id)
                        break
                    }
                }
            }

            if (!isExists) {
                cartStoreItem.cartId = cart?.id ?: 0L
                withContext(Dispatchers.IO) {
                    repository.saveCartItem(cartStoreItem)
                    val actualCounter = counter.value
                    _counter.value = actualCounter + 1
                }
            }

        }
    }

    fun getItemsCart(companyId: String) {
        viewModelScope.launch {
            val cart = repository.getCart(companyId)
            val result = withContext(Dispatchers.IO) {
                repository.getCartItems(cart?.id ?: 0L)
            }

            if (result != null) {
                _itemsCart.value = result
                getTotals()
            }
        }
    }

    fun deleteItem(itemId: CartStoreItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _itemsCart.update { currentItem ->
                    currentItem.filterNot { it == itemId }
                }

                repository.deleteCartItem(itemId.id)
                _counter.value = _counter.value - 1
                getTotals()
            }
        }
    }

    fun updateQuantityPlus(item: CartStoreItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.i("vmstore", "${item.quantity}")
                repository.updateQuantityItem(item.quantity, item.id)
                getTotals()
            }
        }
    }

    private fun getTotals() {
        val dataItems = _itemsCart.value
        val sumTotal = dataItems.sumOf {
            it.price * it.quantity
        }

        _totalPrices.value = sumTotal
    }
}