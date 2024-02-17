package com.greenmars.distribuidor.data.network

import android.content.Context
import okhttp3.Route

class TokenAuthenticator(private val context: Context) : okhttp3.Authenticator {
    override fun authenticate(route: Route?, response: okhttp3.Response): okhttp3.Request {
        val sharedPreferences = context.getSharedPreferences("mi_pref", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        return response.request.newBuilder()
            .header("Authorization", "jwt $token")
            .build()
    }

}