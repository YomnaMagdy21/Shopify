package com.example.shopify.model

import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Brands.BrandModel
import kotlinx.coroutines.flow.Flow

interface ShopifyRepository {
    // get brands
    suspend fun getBrands() : Flow<BrandModel?>

    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>

    // get all products of category
    suspend fun getAllProducts(): Flow<CollectProductsModel?>

    // get all products of chosen brand
    suspend fun getCollectionProducts(id:Long) : Flow<CollectProductsModel?>
}