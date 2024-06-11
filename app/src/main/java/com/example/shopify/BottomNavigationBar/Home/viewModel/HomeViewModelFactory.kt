package com.example.shopify.BottomNavigationBar.Home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopify.model.ShopifyRepository

class HomeViewModelFactory  (private val repo: ShopifyRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {

            HomeViewModel(repo) as T

        } else {

            throw IllegalArgumentException("ViewModel Class not found")

        }
    }
}