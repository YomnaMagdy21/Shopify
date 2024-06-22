package com.example.shopify.BottomNavigationBar.OrderList.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.model.ShopifyRepository

class OrderListViewModelFactory  (private val repository: ShopifyRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(OrderListViewModel::class.java)) {

            OrderListViewModel(repository) as T

        } else {

            throw IllegalArgumentException("ViewModel Class not found")

        }
    }
}