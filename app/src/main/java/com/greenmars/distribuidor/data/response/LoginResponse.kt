package com.greenmars.distribuidor.data.response

import com.greenmars.distribuidor.domain.LoginDomain

data class LoginResponse(
    val token: String
) {
    fun toDomain(): LoginDomain {
        return LoginDomain(token = token, success = true)
    }
}