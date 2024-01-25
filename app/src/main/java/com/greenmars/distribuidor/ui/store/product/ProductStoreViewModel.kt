package com.greenmars.distribuidor.ui.store.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ProductStoreViewModel @Inject constructor(private val repository: RepositoryStore) :
    ViewModel() {


    private var _products = MutableStateFlow<List<ProductStore>>(emptyList())
    val products: StateFlow<List<ProductStore>> = _products

    fun getProductsCompany(idcompany: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getProductsCompany(idcompany)
            }

            if (result != null) {
                _products.value = result
            }
        }
    }

}