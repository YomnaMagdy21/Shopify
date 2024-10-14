package com.example.shopify.model

import android.content.Context
 
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
 
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.Draft_orders_list

import com.example.shopify.model.currencyModel.CurrencyModel
 
import com.example.shopify.model.productDetails.ProductModel
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body

interface ShopifyRepository {
    // get brands
    suspend fun getBrands() : Flow<BrandModel?>

    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>

    fun getCustomerByEmail(email: String):Flow<createCustomersResponse?>
    fun getCustomerById(customerId: Long):Flow<createCustomerRequest?>

    fun getProductInfo(product_id: Long) : Flow<ProductModel?>

    // get all products of chosen brand
    suspend fun getCollectionProducts(id:Long) : Flow<CollectProductsModel?>

    // get category products according to the main and sub categories
    suspend fun getProducts(collectionId: Long?, productType: String?) : Flow<CollectProductsModel?>


    //addresses
    suspend fun getAddresses(customerId: Long): Flow<AddressesModel?>

    suspend fun addAddress(customerId:Long, addresse: AddNewAddress): Flow<AddressesModel?>

    suspend fun removeAddresses(customerId :Long , addressId :Long)

 
    fun getFavDraftOrders(id:Long):Flow<FavDraftOrderResponse?>
    fun createFavDraftOrders(draftOrderResponse :FavDraftOrderResponse):Flow<FavDraftOrderResponse?>

    fun updateFavDraftOrder(id:Long,draftOrderResponse :FavDraftOrderResponse):Flow<FavDraftOrderResponse?>

    fun deleteFavDraftOrder(id:Long):Flow<FavDraftOrderResponse?>


    //  fun createFavDraftOrder(draftOrder: FavDraftOrderResponse): FavDraftOrderResponse?
    suspend fun makeAddressDefault(customerId: Long, addressId: Long): Flow<AddressesModel?>

    suspend fun editAddress(customerId: Long, addressId: Long,addresse: AddNewAddress): Flow<AddressesModel?>

    suspend fun createOrder(order : PostOrderModel): Flow<RetriveOrder?>

    suspend fun getOrderList(): Flow<RetriveOrderModel?>

    //cart
    suspend fun updateDraftOrder(id: String, draftOrder: DraftOrderResponse): Flow<DraftOrderResponse?>
    suspend fun deleteDraftOrder(id: String): Flow<Boolean>
    suspend fun getDraftOrders(): Flow<List<DraftOrder>>
    suspend fun createDraftOrder(draftOrder: DraftOrderResponse): Flow<DraftOrderResponse?>
    suspend fun getPriceRules(): Flow<List<PriceRule>>


}