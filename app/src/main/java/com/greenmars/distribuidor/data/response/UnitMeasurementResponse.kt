package com.greenmars.distribuidor.data.response

import com.greenmars.distribuidor.domain.UnitMeasurement

data class UnitMeasurementResponse(
        val id: Int,
        val name: String
) {
    fun toDomain(): UnitMeasurement {
        return UnitMeasurement(id = id, name = name)
    }
}
