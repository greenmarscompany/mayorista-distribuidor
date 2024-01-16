package com.greenmars.distribuidor.data.response

import com.greenmars.distribuidor.domain.DetailMeasurement

data class DetailMeasurementResponse(
        val id: Int,
        val name: String
) {
    fun toDomain(): DetailMeasurement {
        return DetailMeasurement(id = id,
                name = name)
    }
}
