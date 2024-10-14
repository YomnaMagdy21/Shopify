package com.example.shopify.model.PostOrders

import java.io.Serializable

data class PriceSet(
    val shop_money: Money? = null,
    val presentment_money: Money? = null
) : Serializable

