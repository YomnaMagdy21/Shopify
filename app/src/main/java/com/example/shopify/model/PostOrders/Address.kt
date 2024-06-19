package com.example.shopify.model.PostOrders

import java.io.Serializable


data class Address(
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val company: String? = null,
    val country: String? = null,
    val id: Long? = null,
    val email: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val state: String? = null,
    val note: String? = null,
    val verified_email: Boolean? = null,
    val multipass_identifier: String? = null,
    val tax_exempt: Boolean? = null,
    val phone: String? = null,
    val email_marketing_consent: MarketingConsent? = null,
    val sms_marketing_consent: MarketingConsent? = null,
    val tags: String? = null,
    val currency: String? = null,
    val tax_exemptions: List<Any>? = null,
    val admin_graphql_api_id: String? = null,
    val default_address: Address? = null

) : Serializable