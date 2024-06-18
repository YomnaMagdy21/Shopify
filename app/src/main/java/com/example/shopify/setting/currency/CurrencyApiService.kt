package com.example.shopify.setting.currency

import com.example.shopify.model.currencyModel.CurrencyModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET(" https://api.getgeoapi.com/v2/currency/convert")
    suspend fun convertCurrency(
        @Query("api_key") access_key: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ) : Response<CurrencyModel>

}