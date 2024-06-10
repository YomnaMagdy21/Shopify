package com.example.shopify.productdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.model.createCustomerRequest
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val _repo: ShopifyRepository): ViewModel() {
    private var _productInfo: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val productInfo: StateFlow<ApiState> = _productInfo

    fun getProductInfo(id : Long){
        viewModelScope.launch(Dispatchers.IO){
            _repo.getProductInfo(id)
                .catch {err->
                    _productInfo.value= ApiState.Failure(err)
                }
                .collect{
                    _productInfo.value= ApiState.Success(it)
                }
        }
    }
}