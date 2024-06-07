package com.example.shopify.model

import kotlinx.coroutines.flow.Flow

interface ShopifyRepository {
    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>
}