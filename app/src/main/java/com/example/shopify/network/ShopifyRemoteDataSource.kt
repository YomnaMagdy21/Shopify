package com.example.shopify.network

import com.example.shopify.Models.orderList.RetriveOrderModel
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Customer

import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.RetriveOrder.RetriveOrder
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.addressModel.AddressesModel
import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.createCustomersResponse
import com.example.shopify.model.productDetails.ProductModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body

interface ShopifyRemoteDataSource {
 
    // get brands
    suspend fun getBrands() : Flow<BrandModel?>

    fun createNewCustomer(customer: createCustomerRequest):Flow<createCustomerRequest?>

    // get all products of category
    suspend fun getAllProducts(): Flow<CollectProductsModel?>


    fun getCustomerByEmail(email: String):Flow<createCustomersResponse?>

    fun getCustomerById(customerId: Long):Flow<createCustomerRequest?>

    fun getProductInfo( product_id: Long) : Flow<ProductModel?>

    // get all products of brand
    suspend fun getCollectionProducts(id: Long): Flow<CollectProductsModel?>

    // get category products according to the main and sub categories
    suspend fun getProducts(collectionId: Long?, productType: String?) : Flow<CollectProductsModel?>

    suspend fun getAddresses(customerId :Long):Flow<AddressesModel?>
    suspend fun addAddress(customerId:Long, addresse: AddNewAddress): Flow<AddressesModel?>

    suspend fun removeAddresses(customerId: Long, addressId: Long)
    suspend fun makeAddressDefault(customerId: Long, addressId: Long): Flow<AddressesModel?>

    suspend fun editAddress(customerId: Long, addressId: Long,addresse: AddNewAddress): Flow<AddressesModel?>

    // post order
    suspend fun createOrder(order: PostOrderModel): Flow<RetriveOrder?>

    // get all orders
    suspend fun getOrderList(): Flow<RetriveOrderModel?>

    // clear draft order
    suspend fun clearAllDraftOrders()

}