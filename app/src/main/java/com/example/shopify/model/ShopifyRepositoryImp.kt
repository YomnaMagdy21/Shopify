package com.example.shopify.model

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
    override fun createNewCustomer(customer: Customer): Flow<Customer?> {
       return shopifyRemoteDataSource.createNewCustomer(customer)
    }

}