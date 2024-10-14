package com.example.shopify.payment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopify.model.ShopifyRepository

class PaymentViewModelFactory (private val repo: ShopifyRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {

            PaymentViewModel(repo) as T

        } else {

            throw IllegalArgumentException("ViewModel Class not found")

        }
    }
}