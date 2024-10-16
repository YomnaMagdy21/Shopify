package com.example.shopify.model.PostOrders

import com.example.shopify.model.addressModel.Address
import java.io.Serializable

data class Order(
    val line_items: List<LineItem>? = null,
    val shipping_address: Address? = null,
    val financial_status: String? = null,
    val fulfillment_status: String? = null,
    var email: String? = null,
    val customer: Customer? = null,
    val id: Long? = null,

    val admin_graphql_api_id: String? = null,
    val app_id: Long? = null,
    val browser_ip: String? = null,
    val buyer_accepts_marketing: Boolean? = null,
    val cancel_reason: String? = null,
    val cancelled_at: String? = null,
    val cart_token: String? = null,
    val checkout_id: String? = null,
    val checkout_token: String? = null,
    val client_details: String? = null,
    val closed_at: String? = null,
    val company: String? = null,
    val confirmation_number: String? = null,
    val confirmed: Boolean? = null,
    val contact_email: String? = null,
    val created_at: String? = null,
    val currency: String? = null,
    val current_subtotal_price: String? = null,
    val current_subtotal_price_set: PriceSet? = null,
    val current_total_additional_fees_set: String? = null,
    val current_total_discounts: String? = null,
    val current_total_discounts_set: PriceSet? = null,
    val current_total_duties_set: String? = null,
    val current_total_price: String? = null,
    val current_total_price_set: PriceSet? = null,
    val current_total_tax: String? = null,
    val current_total_tax_set: PriceSet? = null,
    val customer_locale: String? = null,
    val device_id: String? = null,
    val discount_codes: List<Any>? = null,
    val estimated_taxes: Boolean? = null,
    val landing_site: String? = null,
    val landing_site_ref: String? = null,
    val location_id: String? = null,
    val merchant_of_record_app_id: String? = null,
    val name: String? = null,
    val note: String? = null,
    val note_attributes: List<NoteAttribute>? = null,
    val number: Int? = null,
    val order_number: Int? = null,
    val order_status_url: String? = null,
    val original_total_additional_fees_set: String? = null,
    val original_total_duties_set: String? = null,
    val payment_gateway_names: List<Any>? = null,
    val phone: String? = null,
    val po_number: String? = null,
    val presentment_currency: String? = null,
    val processed_at: String? = null,
    val reference: String? = null,
    val referring_site: String? = null,
    val source_identifier: String? = null,
    val source_name: String? = null,
    val source_url: String? = null,
    val subtotal_price: String? = null,
    val subtotal_price_set: PriceSet? = null,
    val tags: String? = null,
    val tax_exempt: Boolean? = null,
    val tax_lines: List<Any>? = null,
    val taxes_included: Boolean? = null,
    val test: Boolean? = null,
    val token: String? = null,
    val total_discounts: String? = null,
    val total_discounts_set: PriceSet? = null,
    val total_line_items_price: String? = null,
    val total_line_items_price_set: PriceSet? = null,
    val total_outstanding: String? = null,
    val total_price: String? = null,
    val total_price_set: PriceSet? = null,
    val total_shipping_price_set: PriceSet? = null,
    val total_tax: String? = null,
    val total_tax_set: PriceSet? = null,
    val total_tip_received: String? = null,
    val total_weight: Int? = null,
    val updated_at: String? = null,
    val user_id: String? = null,
    val billing_address: Address? = null,
    val discount_applications: List<Any>? = null,
    val fulfillments: List<Any>? = null,
    val payment_terms: String? = null,
    val refunds: List<Any>? = null,
    val shipping_lines: List<Any>? = null,
    val send_receipt: Boolean? = null
) : Serializable


data class NoteAttribute(
    var name: String?=null,
    var values: List<String>? = null
) : Serializable