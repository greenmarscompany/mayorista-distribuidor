package com.greenmars.distribuidor.ui.store.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.domain.CartStore
import com.greenmars.distribuidor.domain.CartStoreItem
import com.greenmars.distribuidor.domain.Category
import com.greenmars.distribuidor.domain.Company
import com.greenmars.distribuidor.domain.ProductStore
import com.greenmars.distribuidor.domain.RepositoryStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductStoreViewModel @Inject constructor(
    private val repository: RepositoryStore,
) : ViewModel() {


    private var _carts = MutableStateFlow<CartStore?>(null)
    val carts: StateFlow<CartStore?> = _carts

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> get() = _counter


    private var _products = MutableStateFlow<List<ProductStore>>(emptyList())
    val products: StateFlow<List<ProductStore>> = _products

    fun getProductsCompany(idcompany: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getProductsCompany(idcompany)
            }

            if (result != null) {
                _products.value = result
            } else {
                _products.value = emptyList()
            }
        }
    }

    fun saveCart(cartStore: CartStore) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val resultGetCart = repository.getCart(cartStore.companyId)
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
            } else {
                _counter.value = 0
            }
        }
    }

    fun saveCartItem(companyId: String, cartStoreItem: CartStoreItem) {
        viewModelScope.launch {
            val cart = repository.getCart(companyId)
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

}