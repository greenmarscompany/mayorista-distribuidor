package com.greenmars.distribuidor.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.data.response.ResponseLogout
import com.greenmars.distribuidor.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var _state = MutableStateFlow(ResponseLogout(0, ""))
    val state: StateFlow<ResponseLogout> = _state

    fun logoutStaff() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.logoutStaff()
            }

            if (result != null) {
                _state.value = result
            }
        }
    }

}