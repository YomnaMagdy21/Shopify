package com.example.shopify.BottomNavigationBar.orderItem.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopify.model.PostOrders.Order
class OrderItemViewModelFactory  (private val order: Order) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(OrderItemViewModel::class.java)) {

            OrderItemViewModel(order) as T

        } else {

            throw IllegalArgumentException("ViewModel Class not found")

        }
    }
}