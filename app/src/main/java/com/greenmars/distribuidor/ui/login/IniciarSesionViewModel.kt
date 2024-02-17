package com.greenmars.distribuidor.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.domain.LoginDomain
import com.greenmars.distribuidor.domain.RepositoryProveedor
import com.greenmars.distribuidor.domain.Staff
import com.greenmars.distribuidor.domain.UserDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IniciarSesionViewModel @Inject constructor(
    private val repository: RepositoryProveedor
) : ViewModel() {

    private var _staff = MutableStateFlow<Staff?>(null)
    val staff: StateFlow<Staff?> = _staff

    private var _staffS: Staff? = null
    val staffS: Staff? = _staffS

    private var _login = MutableStateFlow<LoginDomain?>(null)
    val login: StateFlow<LoginDomain?> = _login

    private var _loginS: LoginDomain? = null
    val loginS: LoginDomain? = _loginS

    private var _user = MutableStateFlow<UserDomain?>(null)
    val user: StateFlow<UserDomain?> = _user

    private var _tokenFirebase = MutableStateFlow<String?>(null)
    val tokenFirebase: StateFlow<String?> = _tokenFirebase

    fun getStaff(iduser: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getStaff(iduser)
            }

            if (result != null) {
                // _staff.value = result
                _staffS = result
            }
        }
    }

    fun iniciarSession(username: String, password: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.iniciarSession(username, password)
            }
            if (result != null) {
                // _login.value = result
                _loginS = result
            } else {
                _loginS = LoginDomain("", false)
            }

        }
    }

    fun updateTokenFirebase(token: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.updateTokenFirebase(token)
            }
            if (result != null) {
                _tokenFirebase.value = result
            }
        }
    }

    fun getUserOrInsert(iduser: Long, staff: Staff) {
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) {
                repository.getUser(iduser)
            }

            if (user != null) {
                _user.value = user
            } else {
                val userDomain = UserDomain(
                    dni = "",
                    email = "",
                    telefono = staff.phone,
                    direccion = staff.address,
                    password = "",
                    token = _login.value?.token ?: "",
                    tipe = staff.type,
                    companyId = staff.companyId,
                    companyName = staff.nameCompany,
                    companyPhone = staff.phoneCompany,
                    companyAddress = staff.addressCompany,
                    companyLatitude = staff.latitude,
                    companyLongitude = staff.longitude,
                    companyRuc = staff.ruc,
                    nombre = staff.name,
                    isSupplier = staff.isSupplier,
                    cloudId = staff.userId.toLong()
                )
                val result = withContext(Dispatchers.IO) {
                    repository.insertUser(userDomain)
                }

                if (result != null) {
                    Log.i("viewmodel", "Usuario insertado correctamente")
                }
            }
        }
    }

}