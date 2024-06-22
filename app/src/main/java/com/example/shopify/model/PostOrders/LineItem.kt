package com.example.shopify.model.PostOrders

import java.io.Serializable

data class LineItem(
    val variant_id: Long? = null,
    val quantity: Int? = null,
    val name: String? = null,

    val id: Long? = null,
    val admin_graphql_api_id: String? = null,
    val attributed_staffs: List<Any>? = null,
    val current_quantity: Int? = null,
    val fulfillable_quantity: Int? = null,
    val fulfillment_service: String? = null,
    val fulfillment_status: String? = null,
    val gift_card: Boolean? = null,
    val grams: Int? = null,
    val price: String? = null,
    val price_set: PriceSet? = null,
    val product_exists: Boolean? = null,
    val product_id: Long? = null,
    val properties: List<Any>? = null,
    val requires_shipping: Boolean? = null,
    val sku: String? = null,
    val taxable: Boolean? = null,
    val title: String? = null,
    val total_discount: String? = null,
    val total_discount_set: PriceSet? = null,
    val variant_inventory_management: String? = null,
    val variant_title: String? = null,
    val vendor: String? = null,
    val tax_lines: List<Any>? = null,
    val duties: List<Any>? = null,
    val discount_allocations: List<Any>? = null
) : Serializable
