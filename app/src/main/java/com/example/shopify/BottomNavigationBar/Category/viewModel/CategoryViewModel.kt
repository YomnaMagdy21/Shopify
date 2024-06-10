package com.example.shopify.BottomNavigationBar.Category.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoryViewModel (var repository: ShopifyRepository) : ViewModel() {
    private var _allProductList = MutableStateFlow<ApiState>(ApiState.Loading)
    var  accessAllProductList : StateFlow<ApiState> = _allProductList

    // Store the IDs of products added to the cart
    val addedProductIds = mutableListOf<Long>()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    fun getAllProducts() {
        viewModelScope.launch(Dispatchers.IO+coroutineExceptionHandler) {
            repository.getAllProducts().catch { error ->
                _allProductList.value = ApiState.Failure(error)
            }.collect { brands ->
                _allProductList.value = ApiState.Success(brands)
            }
        }


    }

}