package com.example.shopify.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PaymentViewModel   (var repository: ShopifyRepository) : ViewModel() {
    private  var _order = MutableStateFlow<ApiState>(ApiState.Loading)
    var  accessOrder : StateFlow<ApiState> = _order
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    fun createOrder(order : PostOrderModel) {
        viewModelScope.launch(Dispatchers.IO+coroutineExceptionHandler) {
            repository.createOrder(order).catch { error ->
                _order.value = ApiState.Failure(error)
            }.collect { addresses ->
                _order.value = ApiState.Success(addresses)

            }
        }


    }

}