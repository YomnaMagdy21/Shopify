package com.example.shopify.model

data class Customer(
    val id: Long,
    val email: String,
    val createdAt: String,
    val updatedAt: String,
    val firstName: String,
    val lastName: String,
    val ordersCount: Int,
    val state: String,
    val totalSpent: String,
    val verifiedEmail: Boolean,
    val currency: String,
    val phone: String?,
    val addresses: List<Address>,
    val defaultAddress: Address
)

data class Address(
    val id: Long,
    val customerId: Long,
    val firstName: String,
    val lastName: String,
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

data class CustomerResponse(
    val customers: List<Customer>
)
