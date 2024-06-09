package com.example.shopify.network

import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Customer

import com.example.shopify.model.Brands.BrandModel

import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.createCustomersResponse
import com.example.shopify.model.productDetails.ProductModel

import com.example.shopify.shoppingCard.view.model.PriceRulesResponse
import com.example.shopify.utility.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ShopifyService {

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @POST("admin/api/2024-04/customers.json")

    suspend fun addNewCustomer(@Body customer: createCustomerRequest):Response<createCustomerRequest>

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2024-04/price_rules.json")
    suspend fun getPriceRules(): Response<PriceRulesResponse>



    //Get Brands
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("/admin/api/2024-04/smart_collections.json")
    suspend fun getBrands() : Response<BrandModel>

    // get all products in category
    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("admin/api/2023-04/products.json")
    suspend fun getAllProducts() : Response<CollectProductsModel>


    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("customers/search.json")
    suspend fun getCustomerWithEmail(@Query("email") email:String): Response<createCustomersResponse>

    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("customers/{customerId}.json")
    suspend fun getCustomerWithID(@Path("customerId") customerId: Long): Response<createCustomerRequest>


    @Headers("X-Shopify-Access-Token: ${Constants.adminApiAccessToken}")
    @GET("/admin/api/2024-04/products/{product_id}.json")
    suspend fun getProductInfo(@Path("product_id") product_id: Long) : Response<ProductModel>

}