package com.example.shopify.setting.currency

import com.example.shopify.model.currencyModel.CurrencyModel
import retrofit2.Response

class CurrencyRepository(private val remoteDataSource: CurrencyRemoteDataSource) {
    suspend fun convertCurrency(apiKey: String, from: String, to: String, amount: Double): Response<CurrencyModel> {
        return remoteDataSource.convertCurrency(apiKey, from, to, amount)
    }
}
