package com.example.shopify.newAddress.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NewAddressViewModel(val repo:ShopifyRepository):ViewModel() {

    private var _addAddresses = MutableStateFlow<ApiState>(ApiState.Loading)
    var addAddresses: StateFlow<ApiState> = _addAddresses

    fun addAddresses(customerId: Long, address: AddNewAddress) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addAddress(customerId, address).catch { error ->
                _addAddresses.value = ApiState.Failure(error)
                Log.i("neww", "addAddresses: Failure(msg=${error.message})")
            }.collect { addressesModel ->
                addressesModel?.addresses?.let { addresses ->
                    if (addresses.isNotEmpty()) {
                        _addAddresses.value = ApiState.Success(addresses.first())
                        Log.i("neww", "addAddresses: Success(data=$addresses)")
                    } else {
                        _addAddresses.value = ApiState.Failure(Exception("No address returned"))
                        Log.i("neww", "addAddresses: Failure(msg=No address returned)")
                    }
                }
            }
        }
    }

}
class NewAddressFactory(private val repository: ShopifyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewAddressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewAddressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}