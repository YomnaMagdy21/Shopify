package com.example.shopify.network

import com.example.shopify.model.Customer

import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.createCustomerRequest
import kotlinx.coroutines.flow.Flow

interface ShopifyRemoteDataSource {
 
    // get brands
    suspend fun getBrands() : Flow<BrandModel?>

    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>

}