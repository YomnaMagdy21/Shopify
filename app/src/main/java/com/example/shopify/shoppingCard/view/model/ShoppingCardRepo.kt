package com.example.shopify.shoppingCard.view.model

import com.example.shopify.network.RetrofitHelper

class ShoppingCardRepo {
    suspend fun getPriceRules(): List<PriceRule> {
        val response = RetrofitHelper.apiService.getPriceRules()
        if (response.isSuccessful) {
            return response.body()?.price_rules ?: emptyList()
        } else {
            throw Exception("Failed to fetch price rules")
        }
    }
}