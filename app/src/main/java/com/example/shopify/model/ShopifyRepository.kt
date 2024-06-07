package com.example.shopify.model

import kotlinx.coroutines.flow.Flow

interface ShopifyRepository {
    fun createNewCustomer(customer: Customer):Flow<Customer?>
}