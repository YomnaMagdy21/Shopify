package com.example.shopify.network

 
import android.util.Log
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse

import com.example.shopify.Models.orderList.RetriveOrderModel
 
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.ShoppingCart.model.PriceRule
import com.example.shopify.ShoppingCart.model.PriceRulesResponse
import com.example.shopify.model.Customer

import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.RetriveOrder.RetriveOrder
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.addressModel.AddressesModel

import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.createCustomersResponse
 
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.Draft_orders_list

import com.example.shopify.model.currencyModel.CurrencyModel
import com.example.shopify.model.draftModel.DraftOrder

import com.example.shopify.model.productDetails.ProductModel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

import retrofit2.Response
import retrofit2.http.Body

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

    override suspend fun getAddresses(customerId: Long): Flow<AddressesModel?> {
        return flowOf(shopifyService.getCustomerAddresses(customerId).body())
    }

    override suspend fun addAddress(customerId: Long, addresse: AddNewAddress): Flow<AddressesModel?> {
        return flowOf(shopifyService.addCustomerAddresse(customerId, addresse).body())
    }

    override suspend fun removeAddresses(customerId: Long, addressId: Long) {
        shopifyService.removeCustomerAddresse(customerId,addressId)
    }

 
    override fun getFavDraftOrders(id:Long): Flow<FavDraftOrderResponse?> {
        return flow {
            emit(shopifyService.getFavDraftOrders(id).body())
        }
    }

    override fun createFavDraftOrder(draftOrderResponse: FavDraftOrderResponse): Flow<FavDraftOrderResponse?> {
        return flow {
            emit(shopifyService.createFavDraftOrders(draftOrderResponse).body())
        }
    }

    override fun updateFavDraftOrder(
        id: Long,
        draftOrderResponse: FavDraftOrderResponse
    ): Flow<FavDraftOrderResponse?> {
        return flow {
            emit(shopifyService.updateFavDraftOrder(id,draftOrderResponse).body())
        }
    }

    override fun deleteFavDraftOrder(
        id: Long
    ): Flow<FavDraftOrderResponse?> {
        return flow {
            emit(shopifyService.deleteFavDraftOrder(id).body())
        }
    }

    override fun getSpecificOrder(id: Long): Flow<RetriveOrder?> {
        return flow {
            emit(shopifyService.getOrder(id).body())
        }
    }


//    override fun createFavDraftOrder(draftOrderResponse: FavDraftOrderResponse): Flow<FavDraftOrderResponse?> {
//        return flow {
//            val response = shopifyService.createFavDraftOrders(draftOrderResponse)
//            if (response.isSuccessful) {
//                emit(response.body())
//            } else {
//                throw Exception("Draft order creation failed: ${response.errorBody()?.string()}")
//            }
//        }.catch { e ->
//            // Emit an error or handle it as needed
//            emit(null)
//            throw e
//        }
//    }


    override suspend fun makeAddressDefault(customerId: Long, addressId: Long): Flow<AddressesModel?> {
        return flowOf(shopifyService.makeAddressDefault(customerId,addressId).body())
    }

    override suspend fun editAddress(customerId: Long, addressId: Long,addresse: AddNewAddress): Flow<AddressesModel?> {
        return flowOf(shopifyService.editAddress(customerId,addressId,addresse).body())
    }
    override suspend fun createOrder(order: PostOrderModel): Flow<RetriveOrder?> {
        return  flowOf(shopifyService.createOrder(order).body())
    }

    override suspend fun getOrderList(): Flow<RetriveOrderModel?> {
        return  flowOf(shopifyService.getAllOrders().body())
    }


    override suspend fun clearAllDraftOrders() {
        val response = shopifyService.getDraftOrders()
        if (response.isSuccessful) {
            val draftOrders = response.body()?.draft_orders ?: emptyList()
            for (draftOrder in draftOrders) {
                shopifyService.deleteProductFromDraftOrder(draftOrder.id.toString())
            }
        } else {
            // Handle the error
            throw Exception("Failed to fetch draft orders")
        }
    }



}