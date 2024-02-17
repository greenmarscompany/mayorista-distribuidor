package com.greenmars.distribuidor.data.network

import com.greenmars.distribuidor.data.model.OrderRegister
import com.greenmars.distribuidor.data.response.*
import com.greenmars.distribuidor.data.model.ProductRegisterStaff
import com.greenmars.distribuidor.data.request.UpdateState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface FabricanteApi {

    @GET("product/staff/{id}")
    suspend fun getProductsSupplier(@Path("id") id: String): ApiResponse

    @POST("staff/cerrar-sesion")
    suspend fun logoutStaff(): ResponseLogout

    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("markes")
    suspend fun getMarkes(): List<MarkeResponse>

    @GET("detailmeasurement")
    suspend fun getDetailMeasurements(): List<DetailMeasurementResponse>

    @GET("unitmeasurement")
    suspend fun getUnitMeasurements(): List<UnitMeasurementResponse>

    @POST("product/register-manufacturer/")
    suspend fun saveProduct(@Body product: ProductRegisterStaff): ApiProductResponse

    @Multipart
    @PUT("product/register-manufacturer/image/")
    suspend fun saveImage(
        @Part file: MultipartBody.Part,
        @Part("id_product") idproduct: RequestBody
    ): ApiImageResponse

    @GET("order-supplier")
    suspend fun getOrders(): ApiResponseCore<OrderResponse>

    @POST("order-supplier/")
    suspend fun saveOrderStaff(@Body orderData: OrderRegister): ApiResponseMessage

    @GET("order-manufacturer/details/{orderid}")
    suspend fun getOrderDetails(@Path("orderid") orderid: Int): ApiResponseTemplate<OrderDetailsResponse>

    @POST("order-manufacturer/update-state/")
    suspend fun updateStateOrder(@Body status: UpdateState): ApiResponseTemplate<String>
}