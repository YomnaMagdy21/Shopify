package com.example.shopify.network

import com.example.shopify.model.Customer
import kotlinx.coroutines.flow.Flow

interface ShopifyRemoteDataSource {
    fun createNewCustomer(customer: Customer):Flow<Customer?>
}