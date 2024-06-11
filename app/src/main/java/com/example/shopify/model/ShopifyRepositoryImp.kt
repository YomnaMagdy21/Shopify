package com.example.shopify.model

import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.addressModel.AddressesModel
import com.example.shopify.model.productDetails.ProductModel
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
    // get all products of chosen brand
    override suspend fun getCollectionProducts(id: Long): Flow<CollectProductsModel?> {
        return shopifyRemoteDataSource.getCollectionProducts(id)}


        override fun getCustomerByEmail(email: String): Flow<createCustomersResponse?> {
            return shopifyRemoteDataSource.getCustomerByEmail(email)
        }

        override fun getCustomerById(customerId: Long): Flow<createCustomerRequest?> {
            return shopifyRemoteDataSource.getCustomerById(customerId)
        }

        override fun getProductInfo(product_id: Long): Flow<ProductModel?> {
            return shopifyRemoteDataSource.getProductInfo(product_id)
        }


        override suspend fun getProducts(
            collectionId: Long?,
            productType: String?
        ): Flow<CollectProductsModel?> {
            return shopifyRemoteDataSource.getProducts(collectionId, productType)
        }

    //address
    override suspend fun getAddresses(customerId: Long): Flow<AddressesModel?> {
        return shopifyRemoteDataSource.getAddresses(customerId)
    }

    override suspend fun addAddress(customerId: Long, addresse: Address): Flow<AddressesModel?> {
        return shopifyRemoteDataSource.addAddress(customerId,addresse)
    }


}