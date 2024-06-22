package com.example.shopify.ShoppingCart.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.ShoppingCart.model.PriceRule
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.model.draftModel.LineItem
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch

class ShoppingCardViewModel(private val repo: ShopifyRepository) : ViewModel() {

    //coupines
    private val _priceRules = MutableStateFlow<List<PriceRule>>(emptyList())
    val priceRules: StateFlow<List<PriceRule>> get() = _priceRules

    //draft orders
    private val _draftOrderResponse = MutableStateFlow<DraftOrderResponse?>(null)
    val draftOrderResponse: StateFlow<DraftOrderResponse?> get() = _draftOrderResponse

    //get draft orders
    private val _getDraftOrderList = MutableStateFlow<List<DraftOrder>?>(emptyList())
    val draftOrderList : StateFlow<List<DraftOrder>?> = _getDraftOrderList

    //update draft order
    private val _updateDraftOrderList = MutableStateFlow<DraftOrderResponse?>(null)
    val updateDraftOrderList : StateFlow<DraftOrderResponse?> = _updateDraftOrderList

    //delete product
    private val _deleteDraftOrderList = MutableStateFlow<DraftOrderResponse?>(null)
    val deleteDraftOrderList : StateFlow<DraftOrderResponse?> = _deleteDraftOrderList


    fun fetchPriceRules() {
        viewModelScope.launch {
            repo.getPriceRules()
                .catch { e -> Log.e("ShoppingCardViewModel", "Failed to fetch price rules", e) }
                .collect { rules -> _priceRules.value = rules }
        }
    }

    fun validateCoupon(coupon: String): PriceRule? {
        return _priceRules.value.find { it.title == coupon }
    }

    fun createDraftOrder(draftOrder: DraftOrderResponse) {
        viewModelScope.launch {
            repo.createDraftOrder(draftOrder)
                .catch { e -> Log.e("ShoppingCardViewModel", "Failed to create draft order", e) }
                .collect { response -> _draftOrderResponse.value = response }
        }
    }

    fun getDraftOrders(email: String) {
        viewModelScope.launch {
            repo.getDraftOrders()
                .catch { e -> Log.e("ShoppingCardViewModel", "Failed to fetch draft orders", e) }
                .collect { draftOrders ->
                    _getDraftOrderList.value = draftOrders.filter { it.email == email }
                }
        }
    }

    fun deleteDraftOrder(id: String) {
        viewModelScope.launch {
            repo.deleteDraftOrder(id)
                .catch { e -> Log.e("ShoppingCardViewModel", "Failed to delete draft order", e) }
                .collect { success ->
                    if (success) {
                        _getDraftOrderList.value = _getDraftOrderList.value?.filter { it.id.toString() != id }
                    } else {
                        Log.e("ShoppingCardViewModel", "Failed to delete draft order")
                    }
                }
        }
    }

    fun updateDraftOrder(id: String, draftOrder: DraftOrderResponse) {
        viewModelScope.launch {
            repo.updateDraftOrder(id, draftOrder)
                .catch { e -> Log.e("ShoppingCardViewModel", "Failed to update draft order", e) }
                .collect { updatedOrder ->
                    if (updatedOrder != null) {
                        _getDraftOrderList.value = _getDraftOrderList.value?.map {
                            if (it.id.toString() == id) updatedOrder.draft_order!! else it
                        }
                    } else {
                        Log.e("ShoppingCardViewModel", "Failed to update draft order")
                    }
                }
        }
    }

    // clear the draft order
    fun clearAllDraftOrder() {
        viewModelScope.launch {
            try {
                _getDraftOrderList.value?.forEach { draftOrder ->
                    repo.deleteDraftOrder(draftOrder.id.toString())
                        .collect { success ->
                            if (success) {
                                _getDraftOrderList.value = emptyList()
                                Log.i("ShoppingCardViewModel", "All draft orders cleared successfully")
                            } else {
                                Log.e("ShoppingCardViewModel", "Failed to delete draft order")
                            }
                        }
                }
            } catch (e: Exception) {
                Log.e("ShoppingCardViewModel", "Failed to clear all draft orders", e)
            }
        }
    }
}

class PriceRuleViewModelFactory(private val repository: ShopifyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingCardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}