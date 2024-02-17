package com.greenmars.distribuidor.data.network

import com.greenmars.distribuidor.data.request.LoginRequest
import com.greenmars.distribuidor.data.request.TokenUpdate
import com.greenmars.distribuidor.data.response.ApiResponseCore
import com.greenmars.distribuidor.data.response.ApiResponseStaff
import com.greenmars.distribuidor.data.response.ApiResponseTemplate
import com.greenmars.distribuidor.data.response.LoginResponse
import com.greenmars.distribuidor.data.response.OrderDetailsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProveedorApi {

    @GET("staff/{iduser}")
    suspend fun getInformationUser(@Path("iduser") iduser: Int): ApiResponseStaff

    @POST("auth/obtain_token/")
    suspend fun getIniciarSession(@Body loginRequest: LoginRequest): LoginResponse

    @POST("staffs/device/update-token/")
    suspend fun updateTokenFirebase(@Body tokenUpdate: TokenUpdate): ApiResponseCore<String>

}