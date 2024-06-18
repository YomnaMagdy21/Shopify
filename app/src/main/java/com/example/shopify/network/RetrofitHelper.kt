package com.example.shopify.network

import com.example.shopify.utility.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
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
         //  .addCallAdapterFactory(CoroutineCallAdapterFactory())
    }

    val apiService: ShopifyService by lazy {
        retrofitInstance.create(ShopifyService::class.java)
    }
}