package com.example.shopify.network

import com.example.shopify.model.Customer
import com.example.shopify.utility.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ShopifyService {

//    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
//    @POST("admin/api/2024-04/customers.json")
//    suspend fun addNewCustomer(@Body customer: Customer):Response<Customer>
}