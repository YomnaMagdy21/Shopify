package com.example.shopify.CheckNetwork

import kotlinx.coroutines.flow.Flow

interface NetworkObservation {
    fun observeOnNetwork () : Flow<InternetStatus>

}