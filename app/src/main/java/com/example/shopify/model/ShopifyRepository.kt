package com.example.shopify.model

import android.content.Context
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.productDetails.ProductModel
import kotlinx.coroutines.flow.Flow

interface ShopifyRepository {
    // get brands
    suspend fun getBrands() : Flow<BrandModel?>

    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>

    // get all products of category
    suspend fun getAllProducts(): Flow<CollectProductsModel?>


    fun getCustomerByEmail(email: String):Flow<createCustomersResponse?>
    fun getCustomerById(customerId: Long):Flow<createCustomerRequest?>

    fun getProductInfo(product_id: Long) : Flow<ProductModel?>

    // get all products of chosen brand
    suspend fun getCollectionProducts(id:Long) : Flow<CollectProductsModel?>

    // get category products according to the main and sub categories
    suspend fun getProducts(collectionId: Long?, productType: String?) : Flow<CollectProductsModel?>



    }