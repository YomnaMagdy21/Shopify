package com.example.shopify.shoppingCard.view.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.Draft_orders_list
import com.example.shopify.shoppingCard.view.model.PriceRule
import com.example.shopify.shoppingCard.view.model.ShoppingCardRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingCardViewModel(private val repo: ShoppingCardRepo) : ViewModel() {

    //coupines
    private val _priceRules = MutableStateFlow<List<PriceRule>>(emptyList())
    val priceRules: StateFlow<List<PriceRule>> get() = _priceRules

    //draft orders
    private val _draftOrderResponse = MutableStateFlow<DraftOrderResponse?>(null)
    val draftOrderResponse: StateFlow<DraftOrderResponse?> get() = _draftOrderResponse

    //get draft orders
    private val _getDraftOrderList = MutableStateFlow<List<DraftOrder>?>(emptyList())
    val draftOrderList : StateFlow<List<DraftOrder>?> = _getDraftOrderList

    fun fetchPriceRules() {
        viewModelScope.launch {
            try {
                val rules = repo.getPriceRules()
                _priceRules.value = rules
            } catch (e: Exception) {

            }
        }
    }

    fun validateCoupon(coupon: String): PriceRule? {
        return _priceRules.value.find { it.title == coupon }
    }

    fun createDraftOrder(draftOrder: DraftOrderResponse) {
        viewModelScope.launch {
            try {
                val response = repo.createDraftOrder(draftOrder)
                _draftOrderResponse.value = response
                Log.i("draft", "createDraftOrder: $response")
            } catch (e: Exception) {
                Log.e("DraftOrder", "Failed to create draft order", e)
                _draftOrderResponse.value = null
            }
        }
    }

    fun getDraftOrders(email: String){
        viewModelScope.launch {
            try {
                val draftOrders = repo.getDraftOrders()
                //_getDraftOrderList.value = draftOrders
                _getDraftOrderList.value = draftOrders?.filter { it.email == email } ?: emptyList()
            } catch (e: Exception) {
                Log.e("ShoppingCardViewModel", "Failed to fetch draft orders", e)
            }
        }
    }
}

class PriceRuleViewModelFactory(private val repository: ShoppingCardRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingCardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
