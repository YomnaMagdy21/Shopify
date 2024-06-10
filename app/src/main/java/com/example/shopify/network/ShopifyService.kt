package com.example.shopify.network

import com.example.shopify.Models.products.CollectProductsModel

import com.example.shopify.model.Brands.BrandModel

import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.Draft_orders_list

import com.example.shopify.shoppingCard.view.model.PriceRulesResponse
import com.example.shopify.utility.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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


}