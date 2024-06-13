package com.example.shopify.BottomNavigationBar.Favorite.viewmodel

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

class FavoriteViewModel (private val _repo: ShopifyRepository): ViewModel() {
    private var _fav: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val fav: StateFlow<ApiState> = _fav

//    fun getFavorites(customer: createCustomerRequest){
//        viewModelScope.launch(Dispatchers.IO){
//            _repo.createNewCustomer(customer)
//                .catch {err->
//                    _fav.value= ApiState.Failure(err)
//                }
//                .collect{
//                    _fav.value= ApiState.Success(it)
//                }
//        }
//    }
}