package com.greenmars.distribuidor.domain

import com.greenmars.distribuidor.data.model.ProductRegisterStaff
import com.greenmars.distribuidor.data.response.ApiImageResponse
import com.greenmars.distribuidor.data.response.ApiProductResponse
import com.greenmars.distribuidor.data.response.CategoryResponse
import com.greenmars.distribuidor.data.response.ResponseLogout
import java.io.File

interface Repository {
    suspend fun getProductsStaff(id: String): List<ProductStaff>
    suspend fun logoutStaff(): ResponseLogout?

    suspend fun getCategories(): List<Category>?
    suspend fun getMarkes(): List<Marke>?
    suspend fun getDetailMeasurements(): List<DetailMeasurement>?
    suspend fun getUnitMeasurements(): List<UnitMeasurement>?
    suspend fun saveProduct(product: ProductRegisterStaff): ApiProductResponse?
    suspend fun saveImage(file: File, idproduct: String): ApiImageResponse?
}