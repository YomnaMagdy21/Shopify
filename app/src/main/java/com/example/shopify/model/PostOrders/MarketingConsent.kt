package com.example.shopify.model.PostOrders

import java.io.Serializable

data class MarketingConsent(
    val state: String? = null,
    val opt_in_level: String? = null,
    val consent_updated_at: String? = null,
    val consent_collected_from: String? = null

) : Serializable