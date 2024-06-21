package com.example.shopify.ShoppingCart.model

import android.util.Log
import com.example.shopify.ShoppingCart.model.PriceRule
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.network.RetrofitHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/*class ShoppingCardRepo {

    private val apiService = RetrofitHelper.apiService
    suspend fun getPriceRules(): Flow<List<PriceRule>> = flow {
        val response = apiService.getPriceRules()
        if (response.isSuccessful) {
            emit(response.body()?.price_rules ?: emptyList())
        } else {
            throw Exception("Failed to fetch price rules")
        }
    }

    suspend fun createDraftOrder(draftOrder: DraftOrderResponse): Flow<DraftOrderResponse?> = flow {
        try {
            val response = apiService.postDraftOrders(draftOrder)
            if (response.isSuccessful) {
                emit(response.body())
            } else {
                Log.e("ShoppingCardRepo", "Failed to create draft order: ${response.errorBody()?.string()}")
                emit(null)
            }
        } catch (e: Exception) {
            Log.e("ShoppingCardRepo", "Exception creating draft order", e)
            emit(null)
        }
    }

    suspend fun getDraftOrders(): Flow<List<DraftOrder>> = flow {
        val response = apiService.getDraftOrders()
        if (response.isSuccessful) {
            emit(response.body()?.draft_orders ?: emptyList())
        } else {
            throw Exception("Failed to get draft orders")
        }
    }

    suspend fun deleteDraftOrder(id: String): Flow<Boolean> = flow {
        val response = apiService.deleteProductFromDraftOrder(id)
        if (response.isSuccessful) {
            emit(true)
        } else {
            throw Exception("Failed to delete draft order")
        }
    }

    suspend fun updateDraftOrder(id: String, draftOrder: DraftOrderResponse): Flow<DraftOrderResponse?> = flow {
        val response = apiService.updateDraftOrder(id, draftOrder)
        if (response.isSuccessful) {
            emit(response.body())
        } else {
            throw Exception("Failed to update draft order")
        }
    }
}*/

