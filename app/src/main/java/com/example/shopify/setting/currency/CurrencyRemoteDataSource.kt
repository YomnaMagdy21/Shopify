package com.example.shopify.setting.currency

import com.example.shopify.model.currencyModel.CurrencyModel
import retrofit2.Response

interface CurrencyRemoteDataSource {
    suspend fun convertCurrency(apiKey: String, from: String, to: String, amount: Double): Response<CurrencyModel>
}