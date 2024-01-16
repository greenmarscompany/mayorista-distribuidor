package com.greenmars.distribuidor.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.domain.ProductStaff
import com.greenmars.distribuidor.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private var _product = MutableStateFlow<List<ProductStaff>>(emptyList())
    val products: StateFlow<List<ProductStaff>> = _product

    fun getProductsStaff(id: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getProductsStaff(id)
            }

            if (result != null) {
                _product.value = result
            }
        }
    }
}