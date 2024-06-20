package com.example.shopify.model

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.shopify.BottomNavigationBar.BottomNavActivity
 
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse

import com.example.shopify.Models.orderList.RetriveOrderModel
 
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.ShoppingCart.model.PriceRule
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.RetriveOrder.RetriveOrder
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.addressModel.AddressesModel
 
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.Draft_orders_list

import com.example.shopify.model.currencyModel.CurrencyModel
import com.example.shopify.model.draftModel.DraftOrder

import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.network.RetrofitHelper
import com.example.shopify.network.ShopifyRemoteDataSource
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.Flow
 
import kotlinx.coroutines.flow.flow

import retrofit2.http.Body
 

class ShopifyRepositoryImp(private var shopifyRemoteDataSource: ShopifyRemoteDataSource) : ShopifyRepository{
    var firebaseAuth = FirebaseAuth.getInstance()
    lateinit var mDatabase: DatabaseReference
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

    override suspend fun addAddress(customerId: Long, addresse: AddNewAddress): Flow<AddressesModel?> {
        return shopifyRemoteDataSource.addAddress(customerId,addresse)
    }

    override suspend fun removeAddresses(customerId: Long, addressId: Long) {
        shopifyRemoteDataSource.removeAddresses(customerId,addressId)
    }

 
    override fun getFavDraftOrders(id:Long): Flow<FavDraftOrderResponse?> {
       return shopifyRemoteDataSource.getFavDraftOrders(id)
    }

    override  fun createFavDraftOrders(draftOrderResponse: FavDraftOrderResponse): Flow<FavDraftOrderResponse?> {
        return shopifyRemoteDataSource.createFavDraftOrder(draftOrderResponse)
    }

    override fun updateFavDraftOrder(
        id: Long,
        draftOrderResponse: FavDraftOrderResponse
    ): Flow<FavDraftOrderResponse?> {
        return shopifyRemoteDataSource.updateFavDraftOrder(id,draftOrderResponse)
    }

    override fun deleteFavDraftOrder(
        id: Long
    ): Flow<FavDraftOrderResponse?> {
        return shopifyRemoteDataSource.deleteFavDraftOrder(id)
    }
//    override fun createFavDraftOrder(draftOrder: FavDraftOrderResponse): FavDraftOrderResponse? {
//        return try {
//            val response = RetrofitHelper.apiService.createFavDraftOrders(draftOrder)
//            if (response.isSuccessful) {
//                response.body()
//            } else {
//                Log.e(
//                    "ShoppingCardRepo",
//                    "Failed to create draft order: ${response.errorBody()?.string()}"
//                )
//                null
//            }
//        } catch (e: Exception) {
//            Log.e("ShoppingCardRepo", "Exception creating draft order", e)
//            null
//        }
//    }


    override suspend fun makeAddressDefault(customerId: Long, addressId: Long): Flow<AddressesModel?> {
        return shopifyRemoteDataSource.makeAddressDefault(customerId,addressId)
    }

    override suspend fun editAddress(customerId: Long, addressId: Long,addresse: AddNewAddress): Flow<AddressesModel?> {
        return shopifyRemoteDataSource.editAddress(customerId,addressId,addresse)
    }

    override suspend fun createOrder(order: PostOrderModel): Flow<RetriveOrder?> {
        return  shopifyRemoteDataSource.createOrder(order)
    }

    override suspend fun getOrderList(): Flow<RetriveOrderModel?> {
        return  shopifyRemoteDataSource.getOrderList()
    }


}