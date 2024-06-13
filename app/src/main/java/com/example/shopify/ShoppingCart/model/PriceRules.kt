package com.example.shopify.ShoppingCart.model

data class PriceRulesResponse(val price_rules: List<PriceRule>)

data class PriceRule(
    val id: Long,
    val title: String,
    val value_type: String,
    val value: String
)