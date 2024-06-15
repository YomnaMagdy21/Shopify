package com.example.shopify.MyAddress.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.math.log

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

    fun deleteAddress(customerId: Long, addressId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.removeAddresses(customerId, addressId)
                getAllAddresses(customerId)
            } catch (e: Exception) {
                Log.i("delete", "deleteAddress: cant delete"+e)
            }
        }
    }

    fun makeDefaultAddress(customerId: Long, addressId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.makeAddressDefault(customerId, addressId)
                getAllAddresses(customerId)
            } catch (e: Exception) {
                Log.e("default", "makeDefaultAddress: can't make default" + e)
            }
        }
    }

    fun editAddress(customerId: Long, addressId: Long, address: AddNewAddress) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.editAddress(customerId, addressId, address).collect { editedAddress ->
                    if (editedAddress != null) {
                        _allAddressesList.value = ApiState.Success(editedAddress)
                        Log.d("editAddress", "Successfully edited address with ID: $addressId")
                    } else {
                        _allAddressesList.value = ApiState.Failure(Exception("Failed to edit address with ID: $addressId"))
                        Log.e("editAddress", "Failed to edit address with ID: $addressId")
                    }
                }
                getAllAddresses(customerId)
            } catch (e: Exception) {
                _allAddressesList.value = ApiState.Failure(e)
                Log.e("editAddress", "Failed to edit address with ID: $addressId", e)
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


