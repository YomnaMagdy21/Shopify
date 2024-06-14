package com.example.shopify.network

import com.example.shopify.Models.products.CollectProductsModel

import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.addressModel.AddressesModel

import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.Draft_orders_list
import com.example.shopify.model.createCustomersResponse
import com.example.shopify.model.productDetails.ProductModel


import com.example.shopify.ShoppingCart.model.PriceRulesResponse
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.RetriveOrder.RetriveOrder
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.utility.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

import retrofit2.http.Query

interface ShopifyService {

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @POST("admin/api/2024-04/customers.json")

    suspend fun addNewCustomer(@Body customer: createCustomerRequest):Response<createCustomerRequest>

    //coupones
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/price_rules.json")
    suspend fun getPriceRules(): Response<PriceRulesResponse>


    //draft orders..create draft order
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @POST("admin/api/2024-04/draft_orders.json")
    suspend fun postDraftOrders(@Body order: DraftOrderResponse): Response<DraftOrderResponse>

    //get all draft orders
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/draft_orders.json")
    suspend fun getDraftOrders(): Response<Draft_orders_list>

    //delete draft order
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @DELETE("admin/api/2024-04/draft_orders/{id}.json")
    suspend fun deleteProductFromDraftOrder(@Path("id") id: String?): Response<DraftOrderResponse>

    //update draft orders
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @PUT("admin/api/2024-04/draft_orders/{id}.json")
    suspend fun updateDraftOrder(@Path("id") id: String, @Body order: DraftOrderResponse): Response<DraftOrderResponse>



    //Get Brands
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("/admin/api/2024-04/smart_collections.json")
    suspend fun getBrands() : Response<BrandModel>

    // get all products in category
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2023-04/products.json")
    suspend fun getAllProducts() : Response<CollectProductsModel>

    // get all products of chosen brand

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/products.json")
    suspend fun getCollectionProducts(@Query("collection_id") collectionId: Long): Response<CollectProductsModel>


    // get category products according to the main and sub categories
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/products.json")
    suspend fun getProducts(
        @Query("collection_id") collectionId: Long?,
        @Query("product_type") productType: String?
    ): Response<CollectProductsModel>

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/customers/search.json")
    suspend fun getCustomerWithEmail(@Query("email") email: String): Response<createCustomersResponse>

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/customers/{customerId}.json")
    suspend fun getCustomerWithID(@Path("customerId") customerId: Long): Response<createCustomerRequest>


    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("/admin/api/2024-04/products/{product_id}.json")
    suspend fun getProductInfo(@Path("product_id") product_id: Long) : Response<ProductModel>

    //get address
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/customers/{customerId}/addresses.json")
    suspend fun getCustomerAddresses(@Path(value = "customerId") id:Long) : Response<AddressesModel>

    //add address
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @POST("admin/api/2024-04/customers/{customerId}/addresses.json")
    suspend fun addCustomerAddresse(@Path(value = "customerId") id:Long,@Body addresse: AddNewAddress) : Response<AddressesModel>

    //delete address
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @DELETE("admin/api/2024-04/customers/{customerId}/addresses/{addressId}.json")
    suspend fun removeCustomerAddresse(@Path(value = "customerId") customerId:Long ,@Path(value = "addressId") addressId:Long )


    //change defult address for the customer
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @PUT("admin/api/2024-04/customers/{customerId}/addresses/{addressId}/default.json")
    suspend fun makeAddressDefault(@Path(value="customerId") customerId:Long, @Path(value="addressId") addressId:Long) :Response<AddressesModel>

    //edit address
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @PUT("admin/api/2024-04/customers/{customerId}/addresses/{addressId}.json")
    suspend fun editAddress(@Path(value="customerId") customerId:Long, @Path(value="addressId") addressId:Long,@Body addresse: AddNewAddress) :Response<AddressesModel>




    // post order
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @POST("admin/api/2024-04/orders.json")
    suspend fun createOrder(@Body order: PostOrderModel) : Response<RetriveOrder>

}