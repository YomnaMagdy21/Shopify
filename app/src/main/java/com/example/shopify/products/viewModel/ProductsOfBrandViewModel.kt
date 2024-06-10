package com.example.shopify.products.viewModel

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

class ProductsOfBrandViewModel (var repository: ShopifyRepository) : ViewModel() {

    // products of brand
    private var _productsList = MutableStateFlow<ApiState>(ApiState.Loading)
    var  accessProductsList : StateFlow<ApiState> = _productsList

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    // products of brand
    fun getProductsOfBrands(id:Long) {
        viewModelScope.launch(Dispatchers.IO+coroutineExceptionHandler) {
            repository.getCollectionProducts(id).catch { error ->
                _productsList.value = ApiState.Failure(error)
            }.collect { brands ->
                _productsList.value = ApiState.Success(brands)
            }
        }
    }

}