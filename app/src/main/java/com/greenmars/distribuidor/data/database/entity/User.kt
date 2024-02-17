package com.greenmars.distribuidor.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.greenmars.distribuidor.domain.UserDomain

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val dni: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val password: String,
    val token: String,
    val tipe: String,
    val companyId: String,
    val companyName: String,
    val companyPhone: String,
    val companyAddress: String,
    val companyLatitude: Double,
    val companyLongitude: Double,
    val companyRuc: String,
    val nombre: String,
    val isSupplier: String,
    val cloudId: Long,
) {
    fun toDomain(): UserDomain {
        return UserDomain(
            id = id,
            dni = dni,
            email = email,
            telefono = telefono,
            direccion = direccion,
            password = password,
            token = token,
            tipe = if (tipe == "t1") "1" else "2",
            companyId = companyId,
            companyName = companyName,
            companyPhone = companyPhone,
            companyAddress = companyAddress,
            companyLongitude = companyLongitude,
            companyLatitude = companyLatitude,
            companyRuc = companyRuc,
            nombre = nombre,
            isSupplier = isSupplier,
            cloudId = cloudId
        )
    }
}
