package com.greenmars.distribuidor.data.network

import com.greenmars.distribuidor.data.model.OrderRegister
import com.greenmars.distribuidor.data.response.ApiResponseCore
import com.greenmars.distribuidor.data.response.ApiResponseMessage
import com.greenmars.distribuidor.data.response.CategoryResponse
import com.greenmars.distribuidor.data.response.CompanyResponse
import com.greenmars.distribuidor.data.response.ProductStoreReponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StoreApi {
    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("category/company/{idcategoria}")
    suspend fun getCompanies(@Path("idcategoria") idcategoria: Int): ApiResponseCore<CompanyResponse>

    @GET("product/company/{idcompany}")
    suspend fun getProductsCompany(@Path("idcompany") idcompany: String): ApiResponseCore<ProductStoreReponse>


}