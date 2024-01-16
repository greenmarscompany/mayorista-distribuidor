package com.greenmars.distribuidor.ui.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.data.model.ProductRegisterStaff
import com.greenmars.distribuidor.data.response.ApiImageResponse
import com.greenmars.distribuidor.data.response.ApiProductResponse
import com.greenmars.distribuidor.data.response.ProductData
import com.greenmars.distribuidor.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AgregarProductoViewModel @Inject constructor(
        private val repository: Repository
) : ViewModel() {
    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private var _markes = MutableStateFlow<List<Marke>>(emptyList())
    val markes: StateFlow<List<Marke>> = _markes

    private var _details = MutableStateFlow<List<DetailMeasurement>>(emptyList())
    val details: StateFlow<List<DetailMeasurement>> = _details

    private var _units = MutableStateFlow<List<UnitMeasurement>>(emptyList())
    val units: StateFlow<List<UnitMeasurement>> = _units

    private var _product = MutableStateFlow(ApiProductResponse(0, "Error", ProductData(0)))
    val product: StateFlow<ApiProductResponse> = _product

    private var _image = MutableStateFlow(ApiImageResponse(0, "Error"))
    val image: StateFlow<ApiImageResponse> = _image

    fun getCategories() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                Log.i("viewmoldel", "entro en el dispachets")
                repository.getCategories()

            }
            Log.i("viewmoldel", "data: " + result)
            if (result != null) {
                _categories.value = result
            }
        }
    }

    fun getMarkes() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getMarkes()
            }

            if (result != null) {
                _markes.value = result
            }
        }
    }

    fun getDetailMeasurements() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getDetailMeasurements()
            }

            if (result != null) {
                _details.value = result
            }
        }
    }

    fun getUnitMeasurements() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getUnitMeasurements()
            }

            if (result != null) {
                _units.value = result
            }
        }
    }

    fun saveProduct(category: Int, marca: Int, detail: Int, unit: Int, description: String, medida: Int, precio: Double) {

        val categoryM = _categories.value[category]
        val marcaM = _markes.value[marca]
        val detailM = _details.value[detail]
        val unitM = _units.value[unit]

        val product = ProductRegisterStaff(
                idCategory = categoryM.id,
                idMarke = marcaM.id,
                idDetailMeasurement = detailM.id,
                idUnitMeasurement = unitM.id,
                description = description,
                measurement = medida,
                uniPrice = precio
        )

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.saveProduct(product)
            }

            if (result != null) {
                _product.value = result
            }
        }
    }

    fun saveImage(file: File, idproduct: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.saveImage(file, idproduct)
            }

            if (result != null) {
                _image.value = result
            }
        }
    }
}