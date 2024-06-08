package com.example.shopify.shoppingCard.view.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shopify.shoppingCard.view.model.PriceRule
import com.example.shopify.shoppingCard.view.model.ShoppingCardRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingCardViewModel(private val repo: ShoppingCardRepo) : ViewModel() {

    private val _priceRules = MutableStateFlow<List<PriceRule>>(emptyList())
    val priceRules: StateFlow<List<PriceRule>> get() = _priceRules

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