package com.example.shopify.BottomNavigationBar.OrderList.view

import com.example.shopify.model.PostOrders.Order

interface OnOrderClickListener {
    fun onOrderClick(order: Order)
}