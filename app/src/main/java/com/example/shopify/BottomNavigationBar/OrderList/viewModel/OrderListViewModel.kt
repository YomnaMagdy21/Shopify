package com.example.shopify.BottomNavigationBar.OrderList.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OrderListViewModel(private val repository: ShopifyRepository) : ViewModel() {
    private var _orderList = MutableStateFlow<ApiState>(ApiState.Loading)
    var accessOrderList: StateFlow<ApiState> = _orderList

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun getSpecOrders(customerEmail: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            repository.getOrderList().catch { error ->
                _orderList.value = ApiState.Failure(error)
            }.collect { orders ->
                orders?.orders?.let { orderList ->
                    val filteredOrders = orderList.filter { order ->
                        order.email.equals(customerEmail, ignoreCase = true)
                    }
                    _orderList.value = ApiState.Success(filteredOrders)
                }
            }
        }
    }

}
