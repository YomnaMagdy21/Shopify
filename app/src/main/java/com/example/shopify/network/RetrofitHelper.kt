package com.example.shopify.network

import com.example.shopify.utility.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    //${Constants.apiKey}:${Constants.adminApiAccessToken}@
    const val BASE_URL="https://${Constants.storeURL}"

     val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ShopifyService by lazy {
        retrofitInstance.create(ShopifyService::class.java)
    }
}