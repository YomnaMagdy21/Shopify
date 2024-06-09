package com.example.shopify.BottomNavigationBar.Category.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopify.model.ShopifyRepository

class CategoryViewModelFactory (private val repository: ShopifyRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {

            CategoryViewModel(repository) as T

        } else {

            throw IllegalArgumentException("ViewModel Class not found")

        }
    }
}