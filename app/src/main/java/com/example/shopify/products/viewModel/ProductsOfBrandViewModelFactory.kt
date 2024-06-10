package com.example.shopify.products.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopify.model.ShopifyRepository

class ProductsOfBrandViewModelFactory  (private val repo: ShopifyRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(ProductsOfBrandViewModel::class.java)) {

            ProductsOfBrandViewModel(repo) as T

        } else {

            throw IllegalArgumentException("ViewModel Class not found")

        }
    }
}