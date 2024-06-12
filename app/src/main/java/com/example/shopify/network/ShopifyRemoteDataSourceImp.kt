package com.example.shopify.network

import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Customer

import com.example.shopify.model.Brands.BrandModel

import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.createCustomersResponse
import com.example.shopify.model.productDetails.ProductModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

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


    override fun getCustomerByEmail(email: String): Flow<createCustomersResponse?> {
        return flow {
            emit(shopifyService.getCustomerWithEmail(email).body())
        }
    }

    override fun getCustomerById(customerId: Long): Flow<createCustomerRequest?> {
        return flow {
            emit(shopifyService.getCustomerWithID(customerId).body())
        }
    }

    override fun getProductInfo(product_id: Long): Flow<ProductModel?> {
        return flow {
            emit(shopifyService.getProductInfo(product_id).body())
        }
    }

    override suspend fun getCollectionProducts(id: Long): Flow<CollectProductsModel?> {
        return flowOf(shopifyService.getCollectionProducts(id).body())

    }

    override suspend fun getProducts(
        collectionId: Long?,
        productType: String?
    ): Flow<CollectProductsModel?> {
        return flowOf(shopifyService.getProducts(collectionId , productType).body())
    }



}