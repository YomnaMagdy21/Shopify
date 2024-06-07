package com.example.shopify.network

import com.example.shopify.model.Customer
import com.example.shopify.model.createCustomerRequest
import kotlinx.coroutines.flow.Flow

interface ShopifyRemoteDataSource {
    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>
}