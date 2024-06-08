package com.example.shopify.model

import com.example.shopify.model.Brands.BrandModel
import kotlinx.coroutines.flow.Flow

interface ShopifyRepository {
    fun createNewCustomer(customer: Customer):Flow<Customer?>

    // get brands
    suspend fun getBrands() : Flow<BrandModel?>
}