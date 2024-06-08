package com.example.shopify.network

import com.example.shopify.model.Customer
import com.example.shopify.model.createCustomerRequest
import com.example.shopify.shoppingCard.view.model.PriceRulesResponse
import com.example.shopify.utility.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopifyService {

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @POST("admin/api/2024-04/customers.json")

    suspend fun addNewCustomer(@Body customer: createCustomerRequest):Response<createCustomerRequest>

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/price_rules.json")
    suspend fun getPriceRules(): Response<PriceRulesResponse>
}