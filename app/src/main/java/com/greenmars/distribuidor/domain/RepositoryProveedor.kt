package com.greenmars.distribuidor.domain

interface RepositoryProveedor {

    suspend fun getStaff(iduser: Int): Staff?
    suspend fun insertUser(user: UserDomain)
    suspend fun getUser(id: Long): UserDomain?
    suspend fun iniciarSession(userName: String, password: String): LoginDomain?
    suspend fun updateTokenFirebase(token: String): String?

}