package com.example.shopify.setting.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shopify.model.currencyModel.CurrencyModel
import com.example.shopify.setting.currency.CurrencyConverter
import com.example.shopify.setting.currency.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response


class CurrencyViewModel(private val repository: CurrencyRepository) : ViewModel() {

    private val _currencyState = MutableStateFlow<Response<CurrencyModel>?>(null)
    val currencyState: StateFlow<Response<CurrencyModel>?> get() = _currencyState

     fun convertCurrency(apiKey: String, from: String, to: String, amount: Double) {
        viewModelScope.launch {
            val response = repository.convertCurrency(apiKey, from, to, amount)
            _currencyState.value = response
            updateExchangeRate(response)
        }
    }

    private fun updateExchangeRate(response: Response<CurrencyModel>) {
        if (response.isSuccessful) {
            val rates = response.body()?.rates
            val exchangeRate = rates?.get("USD")?.rate_for_amount ?: 1.0
            CurrencyConverter.setExchangeRate(exchangeRate)
        }
    }

}

class CurrencyViewModelFactory(private val repository: CurrencyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            return CurrencyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}