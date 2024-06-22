package com.example.shopify.BottomNavigationBar.Favorite.model



import com.example.shopify.model.Customer

import java.io.Serializable
data class FavDraftOrderResponse(
    var draft_order: FavDraftOrder?=null
)
data class FavDraftOrder(
    val id: Long=1, // Optional: ID of the draft order, can be null for new draft orders
    // Optional: Customer associated with the draft order
    var line_items: List<ItemLine> =listOf(ItemLine(null, quantity = 1)),

     // List of line items in the draft order
     // Optional: Note for the draft order
     // Indicates if the order is a custom order (true for custom line items)
)

//data class LineItem(
//    val id: Long?=null, // Optional: ID of the line item, can be null for new line items
//    val variant_id: Long?=null, // Optional: ID of the product variant for the line item
//    val title: String?=null, // Optional: Title of the custom line item
//    val price: String?, // Optional: Price of the line item
//    val sku: String?=null, // Optional: SKU of the line item
//    val variant_title: String?=null, // Optional: Title of the product variant
//    val quantity: Int, // Quantity of the line item
//    val taxable: Boolean?, // Optional: Indicates if the line item is taxable
//    val name: String?=null, // Optional: Name of the line item
//    val properties: String?=null, // Optional: Properties of the line item
//    val product_id: Long?=null, // Optional: ID of the product associated with the line item
//    val gift_card: Boolean?=null // Optional: Indicates if the line item is a gift card
//
//)
data class ItemLine(
    var variant_id: Long?,
    var quantity: Int?,
    val id: Long? = null,
    val title: String? = "title",
    val price: String? = "10",
    var sku: String = "",
    var product_id: Long?=null
  //  var properties:List<String?>?=null
)

data class Items(
    var variant_id: Long?,
    var quantity: Int?,
    val id: Long? = null,
    val title: String? = "title",
    val price: String? = "10",
    var sku: String = "",
    //  var properties:List<String?>?=null
)


//data class FavDraftOrderResponse(
//    var draft_order: FavDraftOrder
//)
//
//data class FavDraftOrder(
//    val admin_graphql_api_id: String?=null,
//    val applied_discount: Any?=null,
//    val billing_address: Any?=null,
//    val completed_at: Any?=null,
//    val created_at: String?=null,
//    val currency: String?=null,
//    val customer: Customer?,
//    var email: String?,
//    var id: Long?,
//    val invoice_sent_at: Any?=null,
//    val invoice_url: String?=null,
//    var line_items: List<LineItem>?,
//    val name: String?=null,
//    var note: String?,
//    var note_attributes: List<NoteAttribute>?,
//    val order_id: Any?=null,
//    val payment_terms: Any?=null,
//    val shipping_address: Any?=null,
//    val shipping_line: Any?=null,
//    val status: String?=null,
//    val subtotal_price: String?=null,
//    val tags: String?=null,
//    val tax_exempt: Boolean?=null,
//    val tax_lines: List<TaxLineX>?=null,
//    val taxes_included: Boolean?=null,
//    val total_price: String?=null,
//    val total_tax: String?=null,
//    val updated_at: String?=null
//)
//
//
//
//data class LineItem(
//    val admin_graphql_api_id: String?=null,
//    val applied_discount: Any?=null,
//    val custom: Boolean?=null,
//    val fulfillment_service: String?=null,
//    val gift_card: Boolean?=null,
//    val grams: Int?=null,
//    val id: Long?=null,
//    val name: String?=null,
//    val price: String?=null,
//    val product_id: Long?=null,
//    val properties: List<Any>?=null,
//    var quantity: Int?=null,
//    val requires_shipping: Boolean?=null,
//    val sku: String?=null,
//    val tax_lines: List<TaxLine>?=null,
//    val taxable: Boolean?=null,
//    val title: String?=null,
//    var variant_id: Long?=null,
//    val variant_title: String?=null,
//    val vendor: String?=null
//): Serializable
//
//
//data class NoteAttribute(
//    var name: String?=null,
//    var value: String?=null
//)
//
//data class SmsMarketingConsent(
//    val consent_collected_from: String?=null,
//    val consent_updated_at: Any?=null,
//    val opt_in_level: String?=null,
//    val state: String?=null
//)
//data class TaxLine(
//    val price: String?=null,
//    val rate: Double?=null,
//    val title: String?=null
//): Serializable
//
//data class TaxLineX(
//    val price: String?=null,
//    val rate: Double?=null,
//    val title: String?=null
//)
//
//data class Total_price(
//    var subtotal:String?=null,
//    var tax: String?=null,
//    var total: String?=null
//): Serializable
//
