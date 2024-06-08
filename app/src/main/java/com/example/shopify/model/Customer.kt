package com.example.shopify.model

data class Customer(
    val id: Long?,
    val email: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val first_name: String,
    val last_name: String,
    val password: String,
    val password_confirmation : String?,
    val ordersCount: Int?,
    val state: String?,
    val totalSpent: String?,
    val verified_email: Boolean,
    val currency: String?,
    val phone: String?,
    val addresses: List<Address>?,
    val defaultAddress: Address?
)

data class Address(
    val id: Long,
    val customerId: Long,
    val first_name: String,
    val last_name: String,
    val address1: String,
    val address2: String?,
    val city: String,
    val province: String,
    val country: String,
    val zip: String,
    val phone: String?,
    val name: String,
    val provinceCode: String,
    val countryCode: String,
    val countryName: String,
    val default: Boolean
)

data class createCustomerRequest(
    val customer: Customer
)
