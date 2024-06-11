package com.example.shopify.setting.MyAddress.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.shoppingCard.view.model.ShoppingCardRepo
import com.example.shopify.shoppingCard.view.viewModel.ShoppingCardViewModel
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MyAddressViewModel(var repo:ShopifyRepository) :ViewModel(){

    private var _allAddressesList = MutableStateFlow<ApiState>(ApiState.Loading)
    var  accessAllAddressesList : StateFlow<ApiState> = _allAddressesList

    fun getAllAddresses(customerId : Long ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAddresses(customerId).catch { error ->
                _allAddressesList.value = ApiState.Failure(error)
            }.collect { addresses ->
                _allAddressesList.value = ApiState.Success(addresses)
            }
        }
    }

}

class MyAddressFactory(private val repository: ShopifyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyAddressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyAddressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


