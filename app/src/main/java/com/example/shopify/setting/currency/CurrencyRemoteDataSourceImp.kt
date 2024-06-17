package com.example.shopify.setting.currency

import com.example.shopify.model.currencyModel.CurrencyModel
import retrofit2.Response

class CurrencyRemoteDataSourceImpl(private val apiService: CurrencyApiService) : CurrencyRemoteDataSource {
    override suspend fun convertCurrency(apiKey: String, from: String, to: String, amount: Double): Response<CurrencyModel> {
        return apiService.convertCurrency(apiKey, from, to, amount)
    }
}