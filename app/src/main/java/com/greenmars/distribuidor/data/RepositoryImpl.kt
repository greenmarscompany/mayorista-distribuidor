package com.greenmars.distribuidor.data

import android.util.Log
import com.greenmars.distribuidor.data.model.ProductRegisterStaff
import com.greenmars.distribuidor.data.network.FabricanteApi
import com.greenmars.distribuidor.data.response.*
import com.greenmars.distribuidor.domain.*
import com.squareup.okhttp.RequestBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: FabricanteApi) : Repository {
    override suspend fun getProductsStaff(id: String): List<ProductStaff> {
        val productStaffList = mutableListOf<ProductStaff>()

        Log.i("RepoImpl", "ID STAFF $id")
        runCatching {
            apiService.getProductsSupplier(id)
        }.onSuccess { it ->
            val response: List<ProductItemResponse>? = it.data


            if (response != null) {

                productStaffList.addAll(
                    response
                        .map { it.toDomain() }
                )
            }

            return productStaffList
        }.onFailure {
            Log.i("proveedor", "Ha ocurrido un error ${it.message}")
        }

        return emptyList()
    }

    override suspend fun logoutStaff(): ResponseLogout? {
        runCatching {
            apiService.logoutStaff()
        }.onSuccess {
            return it
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun getCategories(): List<Category>? {
        val categoriesData = mutableListOf<Category>()
        runCatching {
            apiService.getCategories()
        }.onSuccess { it ->
            Log.i("Repo", it.toString())
            categoriesData.addAll(it.map {
                it.toDomain()
            })

            return categoriesData
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message} ")
        }

        return null
    }

    override suspend fun getMarkes(): List<Marke>? {
        val markesData = mutableListOf<Marke>()

        runCatching {
            apiService.getMarkes()
        }.onSuccess { it ->
            markesData.addAll(it.map { it.toDomain() })

            return markesData
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun getDetailMeasurements(): List<DetailMeasurement>? {
        val detailMeasurements = mutableListOf<DetailMeasurement>()

        runCatching {
            apiService.getDetailMeasurements()
        }.onSuccess { it ->
            detailMeasurements.addAll(it.map { it.toDomain() })

            return detailMeasurements
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun getUnitMeasurements(): List<UnitMeasurement>? {
        val unitMeasurements = mutableListOf<UnitMeasurement>()

        runCatching {
            apiService.getUnitMeasurements()
        }.onSuccess { it ->
            unitMeasurements.addAll(it.map { it.toDomain() })

            return unitMeasurements
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun saveProduct(product: ProductRegisterStaff): ApiProductResponse? {
        runCatching {
            apiService.saveProduct(product)
        }.onSuccess {
            return it
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun saveImage(file: File, idproduct: String): ApiImageResponse? {
        val requestFile =
            okhttp3.RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val filePart = okhttp3.MultipartBody.Part.createFormData("file", file.name, requestFile)
        val idProductPart = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), idproduct)

        runCatching {
            apiService.saveImage(filePart, idProductPart)
        }.onSuccess {
            return it
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun getOrders(): List<Order>? {
        runCatching {
            apiService.getOrders()
        }.onSuccess { it ->
            return it.data.map {
                it.toDomain()
            }
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun saveOrder(order: OrderStore): ResponseMessage? {
        runCatching {
            apiService.saveOrderStaff(order.toEntity())
        }.onSuccess {
            return it.toDomain()
        }.onFailure {
            Log.i("RepoImpl", "Ha ocurrido un error ${it.message}")
        }

        return null
    }
}