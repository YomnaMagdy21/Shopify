package com.example.shopify.network

import com.example.shopify.model.Customer
import com.example.shopify.model.Brands.BrandModel
import kotlinx.coroutines.flow.Flow

interface ShopifyRemoteDataSource {
    fun createNewCustomer(customer: Customer):Flow<Customer?>

    // get brands
    suspend fun getBrands() : Flow<BrandModel?>
}