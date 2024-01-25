package com.greenmars.distribuidor.ui.store.company

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.domain.Category
import com.greenmars.distribuidor.domain.Company
import com.greenmars.distribuidor.domain.RepositoryStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StoreCompanyViewModel @Inject constructor(private val repository: RepositoryStore) :
    ViewModel() {

    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private var _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies

    fun getCategories() {
        val tempLista = mutableListOf<Category>()
        tempLista.add(Category(0, "Todo", ""))

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val res = repository.getCategories()
                if (res != null) {
                    tempLista.addAll(res)
                }
            }

            _categories.value = tempLista
        }
    }

    fun getCompanies(idcategoria: Int = 0) {

        viewModelScope.launch {
            Log.i("vmcomp", idcategoria.toString())
            val result = withContext(Dispatchers.IO) {
                repository.getCompanies(idcategoria)
            }

            if (result != null) {
                _companies.value = result
            }
        }
    }
}