package com.example.shopify.signup.viewmodel

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

class SignUpViewModel(private val _repo:ShopifyRepository):ViewModel() {

    private var _register: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val register: StateFlow<ApiState> = _register

    fun registerCustomerInAPI(customer: createCustomerRequest){
        viewModelScope.launch(Dispatchers.IO){
            _repo.createNewCustomer(customer)
                .catch {err->
                    _register.value=ApiState.Failure(err)
                }
                .collect{
                    _register.value=ApiState.Success(it)
                }
        }
    }

}