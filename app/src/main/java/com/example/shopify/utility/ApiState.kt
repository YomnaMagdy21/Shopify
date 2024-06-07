package com.example.shopify.utility

sealed class ApiState {
    data class Success<T>(val data: T) : ApiState()
    data class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()
}
