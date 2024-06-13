package com.example.shopify.login.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SignInViewModel (private val _repo: ShopifyRepository): ViewModel() {

    private var _login: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val login: StateFlow<ApiState> = _login



    fun getCustomerByEmail(email:String){
        viewModelScope.launch(Dispatchers.IO){
            _repo.getCustomerByEmail(email)
                .catch {err->
                    _login.value=ApiState.Failure(err)
                }
                .collect{
                    _login.value=ApiState.Success(it)
                }
        }
    }

}