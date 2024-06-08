package com.example.shopify.model

import com.example.shopify.model.Brands.BrandModel
import kotlinx.coroutines.flow.Flow

interface ShopifyRepository {
    // get brands
    suspend fun getBrands() : Flow<BrandModel?>

    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>

}