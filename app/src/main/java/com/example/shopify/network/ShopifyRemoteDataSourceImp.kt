package com.example.shopify.network

import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Customer

import com.example.shopify.model.Brands.BrandModel

import com.example.shopify.model.createCustomerRequest

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class ShopifyRemoteDataSourceImp :ShopifyRemoteDataSource {
    val shopifyService:ShopifyService by lazy {
        RetrofitHelper.retrofitInstance.create(ShopifyService::class.java)
    }

    companion object{
        private var instance:ShopifyRemoteDataSourceImp?=null
        fun getInstance():ShopifyRemoteDataSourceImp{
            return instance?: synchronized(this){
                val temp=ShopifyRemoteDataSourceImp()
                instance=temp
                temp
            }
        }
    }
    override fun createNewCustomer(customer: createCustomerRequest): Flow<createCustomerRequest?> {
        return  flow {
            emit(shopifyService.addNewCustomer(customer).body())
        }
    }

    override suspend fun getAllProducts(): Flow<CollectProductsModel?> {
        return flowOf(shopifyService.getAllProducts().body())
    }

    override suspend fun getBrands(): Flow<BrandModel?> {
        return flowOf(shopifyService.getBrands().body())
    }


}