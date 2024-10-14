package com.example.shopify.model.PostOrders

import java.io.Serializable

data class Money(
    val amount: String? = null,
    val currency_code: String?= null
) : Serializable