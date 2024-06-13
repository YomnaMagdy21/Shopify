package com.example.shopify.model.draftModel

import java.io.Serializable


data class DraftOrderResponse(
    var draft_order: DraftOrder?=null
)

data class DraftOrder(
    val admin_graphql_api_id: String?=null,
    val applied_discount: Any?=null,
    val billing_address: Any?=null,
    val completed_at: Any?=null,
    val created_at: String?=null,
    val currency: String?=null,
    val customer: Customer?=null,
    var email: String?=null,
    val id: Long?=null,
    val invoice_sent_at: Any?=null,
    val invoice_url: String?=null,
    var line_items: List<LineItem>?=null,
    val name: String?=null,
    var note: String?=null,
    var note_attributes: List<NoteAttribute>?=null,
    val order_id: Any?=null,
    val payment_terms: Any?=null,
    val shipping_address: Any?=null,
    val shipping_line: Any?=null,
    val status: String?=null,
    val subtotal_price: String?=null,
    val tags: String?=null,
    val tax_exempt: Boolean?=null,
    val tax_lines: List<TaxLineX>?=null,
    val taxes_included: Boolean?=null,
    val total_price: String?=null,
    val total_tax: String?=null,
    val updated_at: String?=null
)

data class Customer(
    val accepts_marketing: Boolean?=null,
    val accepts_marketing_updated_at: String?=null,
    val admin_graphql_api_id: String?=null,
    val created_at: String?=null,
    val currency: String?=null,
    val default_address: DefaultAddress?=null,
    val email: String?=null,
    val first_name: String?=null,
    val id: Long?=null,
    val last_name: String?=null,
    val last_order_id: Any?=null,
    val last_order_name: Any?=null,
    val marketing_opt_in_level: Any?=null,
    val multipass_identifier: Any?=null,
    val note: Any?=null,
    val orders_count: Int?=null,
    val phone: String?=null,
    val sms_marketing_consent: SmsMarketingConsent?=null,
    val state: String?=null,
    val tags: String?=null,
    val tax_exempt: Boolean?=null,
    val tax_exemptions: List<Any>?=null,
    val total_spent: String?=null,
    val updated_at: String?=null,
    val verified_email: Boolean?=null
)

data class DefaultAddress(
    val address1: String?=null,
    val address2: Any?=null,
    val city: String?=null,
    val company: Any?=null,
    val country: String?=null,
    val country_code: String?=null,
    val country_name: String?=null,
    val customer_id: Long?=null,
    val default: Boolean?=null,
    val first_name: String?=null,
    val id: Long?=null,
    val last_name: String?=null,
    val name: String?=null,
    val phone: String?=null,
    val province: String?=null,
    val province_code: Any?=null,
    val zip: String?=null
)

data class LineItem(
    val admin_graphql_api_id: String?=null,
    val applied_discount: Any?=null,
    val custom: Boolean?=null,
    val fulfillment_service: String?=null,
    val gift_card: Boolean?=null,
    val grams: Int?=null,
    val id: Long?=null,
    val name: String?=null,
    val price: String?=null,
    val product_id: Long?=null,
    val properties: List<Any>?=null,
    var quantity: Int?=null,
    val requires_shipping: Boolean?=null,
    val sku: String?=null,
    val tax_lines: List<TaxLine>?=null,
    val taxable: Boolean?=null,
    val title: String?=null,
    var variant_id: Long?=null,
    val variant_title: String?=null,
    val vendor: String?=null
): Serializable


data class NoteAttribute(
    var name: String?=null,
    var value: String?=null
)

data class SmsMarketingConsent(
    val consent_collected_from: String?=null,
    val consent_updated_at: Any?=null,
    val opt_in_level: String?=null,
    val state: String?=null
)
data class TaxLine(
    val price: String?=null,
    val rate: Double?=null,
    val title: String?=null
): Serializable

data class TaxLineX(
    val price: String?=null,
    val rate: Double?=null,
    val title: String?=null
)

data class Total_price(
    var subtotal:String?=null,
    var tax: String?=null,
    var total: String?=null
): Serializable

