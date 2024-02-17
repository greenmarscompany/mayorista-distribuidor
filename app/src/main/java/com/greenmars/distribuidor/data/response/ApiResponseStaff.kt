package com.greenmars.distribuidor.data.response

import com.google.gson.annotations.SerializedName
import com.greenmars.distribuidor.domain.Staff

data class ApiResponseStaff(
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: Data,
) {
    fun toDomain(): Staff {
        return Staff(
            staffId = data.staff.staffId,
            name = data.staff.name,
            phone = data.staff.phone,
            address = data.staff.address,
            latitude = data.staff.latitude,
            longitude = data.staff.longitude,
            type = data.staff.type,
            image = data.staff.image,
            code = data.staff.code,
            companyId = data.staff.companyId,
            userId = data.staff.userId,
            ruc = data.company.ruc,
            nameCompany = data.company.name,
            phoneCompany = data.company.phone,
            addressCompany = data.company.address,
            isSupplier = if (data.company.isSupplier) "1" else "0",
        )
    }
}

data class Data(
    val staff: StaffResponse,
    val company: CompanyResponse
)