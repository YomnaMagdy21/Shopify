package com.example.shopify.network

import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Customer

import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.createCustomerRequest
import kotlinx.coroutines.flow.Flow

interface ShopifyRemoteDataSource {
 
    // get brands
    suspend fun getBrands() : Flow<BrandModel?>

    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>

    // get all products of category
    suspend fun getAllProducts(): Flow<CollectProductsModel?>

    // get all products of brand
    suspend fun getCollectionProducts(id: Long): Flow<CollectProductsModel?>

    // get category products according to the main and sub categories
    suspend fun getProducts(collectionId: Long?, productType: String?) : Flow<CollectProductsModel?>


}