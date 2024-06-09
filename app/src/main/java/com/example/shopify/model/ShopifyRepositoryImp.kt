package com.example.shopify.model

import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.network.ShopifyRemoteDataSource
import kotlinx.coroutines.flow.Flow

class ShopifyRepositoryImp(private var shopifyRemoteDataSource: ShopifyRemoteDataSource) : ShopifyRepository{

    companion object{
        private var instance:ShopifyRepositoryImp?=null
        fun getInstance(
            shopifyRemoteDataSource: ShopifyRemoteDataSource
        ):ShopifyRepositoryImp{
            return instance?: synchronized(this){
                val temp=ShopifyRepositoryImp(
                    shopifyRemoteDataSource)
                instance=temp
                temp
            }
        }
    }
    override fun createNewCustomer(customer: createCustomerRequest): Flow<createCustomerRequest?> {
       return shopifyRemoteDataSource.createNewCustomer(customer)
    }


    override suspend fun getBrands(): Flow<BrandModel?> {
        return shopifyRemoteDataSource.getBrands()
    }

    override suspend fun getAllProducts(): Flow<CollectProductsModel?> {
        return shopifyRemoteDataSource.getAllProducts()
    }

    override fun getCustomerByEmail(email: String): Flow<createCustomersResponse?> {
        return shopifyRemoteDataSource.getCustomerByEmail(email)
    }

    override fun getCustomerById(customerId: Long): Flow<createCustomerRequest?> {
        return shopifyRemoteDataSource.getCustomerById(customerId)
    }


}