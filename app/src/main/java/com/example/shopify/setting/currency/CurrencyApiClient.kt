package com.example.shopify.setting.currency

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CurrencyApiClient {

    fun getApiService(): CurrencyApiService {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.getgeoapi.com/api/v2/currency/")
            //.addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(CurrencyApiService::class.java)

    }
}