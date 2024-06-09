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
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

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


    //Get Brands
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("/admin/api/2024-04/smart_collections.json")
    suspend fun getBrands() : Response<BrandModel>

    // get all products in category
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2023-04/products.json")
    suspend fun getAllProducts() : Response<CollectProductsModel>


}