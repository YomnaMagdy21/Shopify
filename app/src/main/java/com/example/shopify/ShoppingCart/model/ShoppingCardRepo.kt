package com.example.shopify.ShoppingCart.model

import android.util.Log
import com.example.shopify.ShoppingCart.model.PriceRule
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.network.RetrofitHelper

class ShoppingCardRepo {
    suspend fun getPriceRules(): List<PriceRule> {
        val response = RetrofitHelper.apiService.getPriceRules()
        if (response.isSuccessful) {
            return response.body()?.price_rules ?: emptyList()
        } else {
            throw Exception("Failed to fetch price rules")
        }
    }

    suspend fun createDraftOrder(draftOrder: DraftOrderResponse): DraftOrderResponse? {
        return try {
            val response = RetrofitHelper.apiService.postDraftOrders(draftOrder)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "ShoppingCardRepo",
                    "Failed to create draft order: ${response.errorBody()?.string()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("ShoppingCardRepo", "Exception creating draft order", e)
            null
        }
    }

    suspend fun getDraftOrders(): List<DraftOrder>? {
        val response = RetrofitHelper.apiService.getDraftOrders()
        if (response.isSuccessful) {
            return response.body()?.draft_orders
        } else {
            throw Exception("failed to get draft orders")
        }
    }

    // Delete draft order
    suspend fun deleteDraftOrder(id: String): Boolean {
        val response = RetrofitHelper.apiService.deleteProductFromDraftOrder(id)
        if (response.isSuccessful) {
            return response.isSuccessful
            Log.i("del", "deleteDraftOrder: "+response.isSuccessful)
        } else {
            throw Exception("failed to delete")
        }
    }

    // Update draft order
    suspend fun updateDraftOrder(id: String, draftOrder: DraftOrderResponse): DraftOrderResponse? {
        val response = RetrofitHelper.apiService.updateDraftOrder(id, draftOrder)
        if (response.isSuccessful) {
           return response.body()
            Log.i("del", "updateDraftOrder: "+response.body())
        } else {
            throw Exception("failed to update")
        }
    }
}

