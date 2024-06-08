package com.example.shopify.BottomNavigationBar.Home.viewModel

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

class HomeViewModel (var repository: ShopifyRepository) : ViewModel() {
    private var _brandsList = MutableStateFlow<ApiState>(ApiState.Loading)
    var  accessBrandsList : StateFlow<ApiState> = _brandsList

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    fun getBrands() {
        viewModelScope.launch(Dispatchers.IO+coroutineExceptionHandler) {
            repository.getBrands().catch { error ->
                _brandsList.value = ApiState.Failure(error)
            }.collect { brands ->
                _brandsList.value = ApiState.Success(brands)
            }
        }


    }
}