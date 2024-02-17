package com.greenmars.distribuidor.data

import android.util.Log
import com.greenmars.distribuidor.data.database.dao.ProveedorDao
import com.greenmars.distribuidor.data.database.entity.User
import com.greenmars.distribuidor.data.network.ProveedorApi
import com.greenmars.distribuidor.data.request.LoginRequest
import com.greenmars.distribuidor.data.request.TokenUpdate
import com.greenmars.distribuidor.domain.LoginDomain
import com.greenmars.distribuidor.domain.RepositoryProveedor
import com.greenmars.distribuidor.domain.Staff
import com.greenmars.distribuidor.domain.UserDomain
import javax.inject.Inject

class RepositoryProveedorImpl @Inject constructor(
    val apiService: ProveedorApi,
    val database: ProveedorDao
) : RepositoryProveedor {
    override suspend fun getStaff(iduser: Int): Staff? {
        runCatching {
            apiService.getInformationUser(iduser)
        }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("proveedor", "Ha ocurrido un error ${it.message}") }

        return null
    }

    override suspend fun insertUser(user: UserDomain) {
        val userEntity = User(
            dni = user.dni,
            email = user.email,
            telefono = user.telefono,
            direccion = user.direccion,
            password = user.password,
            token = user.token,
            tipe = user.tipe,
            companyId = user.companyId,
            companyName = user.companyName,
            companyPhone = user.companyPhone,
            companyAddress = user.companyAddress,
            companyLongitude = user.companyLongitude,
            companyLatitude = user.companyLatitude,
            companyRuc = user.companyRuc,
            nombre = user.nombre,
            isSupplier = user.isSupplier,
            cloudId = user.cloudId
        )

        runCatching {
            database.insertUser(userEntity)
        }.onSuccess {
            Log.i("repo", "Usuario insertado correctamente: $it")
        }.onFailure {
            Log.i("repo", "Usuario no insertado: $it")
        }
    }

    override suspend fun getUser(id: Long): UserDomain? {
        runCatching {
            database.getUser(id)
        }.onSuccess {
            Log.i("repoimp", "$it")
            if (it != null) {
                return it.toDomain()
            }
        }.onFailure {
            Log.i("repo", "Usuario no encontrado: $it")
        }

        return null
    }

    override suspend fun iniciarSession(userName: String, password: String): LoginDomain? {
        runCatching {
            apiService.getIniciarSession(LoginRequest(userName, password))
        }.onSuccess {
            return it.toDomain()
        }.onFailure {
            Log.i("repo", "Error al iniciar: $it")
        }

        return null
    }

    override suspend fun updateTokenFirebase(token: String): String? {
        val tokenUpdate = TokenUpdate(token = token)

        runCatching {
            apiService.updateTokenFirebase(tokenUpdate)
        }.onSuccess {
            return it.message
        }.onFailure {
            Log.i("repo", "Error al actualizar token firebase: $it")
        }

        return null
    }

}