package com.greenmars.distribuidor.ui.detailorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenmars.distribuidor.data.request.UpdateState
import com.greenmars.distribuidor.data.response.ApiResponseTemplate
import com.greenmars.distribuidor.domain.OrderDetailDomain
import com.greenmars.distribuidor.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    val repo: Repository
) : ViewModel() {

    private var _order = MutableStateFlow<OrderDetailDomain?>(null)
    val order: StateFlow<OrderDetailDomain?> = _order

    private var _status = MutableStateFlow<ApiResponseTemplate<String>?>(null)
    val status: StateFlow<ApiResponseTemplate<String>?> = _status
    fun getOrderDetails(orderid: Int) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getOrderDetails(orderid)
            }

            if (result != null) {
                _order.value = result
            }
        }
    }

    fun updateStatusOrder(status: String, orderid: Int) {
        val enti = UpdateState(orderid = orderid, state = status)

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.updateStateOrder(enti)
            }

            if (result != null) {
                _status.value = result
            }
        }
    }

}