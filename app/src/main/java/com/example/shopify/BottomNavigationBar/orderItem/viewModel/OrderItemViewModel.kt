package com.example.shopify.BottomNavigationBar.orderItem.viewModel

import androidx.lifecycle.ViewModel
import com.example.shopify.model.PostOrders.Order

class OrderItemViewModel (val order: Order) : ViewModel() {
    val customerName = order.customer?.first_name + " " + order.customer?.last_name
    val address = order.customer?.default_address?.address1 ?: "N/A"
    val phoneNumber = order.customer?.default_address?.company ?: "N/A"
    val paymentMethod = order.tags ?: "N/A"
    val totalPrice = order.total_price + " " + order.currency
    val lineItems = order.line_items ?: emptyList()
}