package com.example.shopify.signup.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SignUpViewModel(private val _repo:ShopifyRepository):ViewModel() {

    private var _register: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val register: StateFlow<ApiState> = _register

    private var _wishList: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val wishList: StateFlow<ApiState> = _wishList

    private val _draftOrderResponse = MutableStateFlow<FavDraftOrderResponse?>(null)
    val draftOrderResponse: StateFlow<FavDraftOrderResponse?> get() = _draftOrderResponse


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

    fun createFavDraftOrders(draftOrder: FavDraftOrderResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
               // Log.d("SignUpViewModel", "Creating favorite draft order: $draftOrder")
                _repo.createFavDraftOrders(draftOrder)
                    .catch { err ->
                        _wishList.value = ApiState.Failure(err)
                      //  Log.e("SignUpViewModel", "Draft order creation error", err)
                    }
                    .collect { response ->
                        if (response != null) {
                            _wishList.value = ApiState.Success(response)
                          //  Log.d("SignUpViewModel", "Draft order created successfully: $response")
                        } else {
                            _wishList.value = ApiState.Failure(Exception("Draft order response is null"))
                           // Log.e("SignUpViewModel", "Draft order creation failed: response is null")
                        }
                    }
            } catch (e: Exception) {
                _wishList.value = ApiState.Failure(e)
              //  Log.e("SignUpViewModel", "Failed to create draft order", e)
            }
        }

    }

//    fun createFavDraftOrder(draftOrder: FavDraftOrderResponse) {
//        viewModelScope.launch {
//            try {
//                val response = _repo.createFavDraftOrder(draftOrder)
//                _draftOrderResponse.value = response
//                Log.i("draft", "createDraftOrder: $response")
//            } catch (e: Exception) {
//                Log.e("DraftOrder", "Failed to create draft order", e)
//                _draftOrderResponse.value = null
//            }
//        }
//    }

}