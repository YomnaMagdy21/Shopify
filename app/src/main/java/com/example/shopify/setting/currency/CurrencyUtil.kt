package com.example.shopify.setting.currency

import android.util.Log
import com.example.shopify.model.currencyModel.Rates
import com.example.shopify.setting.viewModel.CurrencyViewModel
import kotlin.math.log

object CurrencyConverter {
    private var exchangeRate: Double = 1.0
    private var currencyCode: String = "EGP"


    fun setCurrencyCode(code: String) {
        currencyCode = code
    }
    fun setExchangeRate(rate: Double) {
        exchangeRate = rate
        Log.i("rate", "setExchangeRate: "+ exchangeRate)
    }

    fun convertToUSD(amount: Double): Double {
        return amount * exchangeRate
        Log.i("rate", "convertToUSD: "+amount/ exchangeRate)
    }
    fun formatCurrency(amount: Double): String {
        return when (currencyCode) {
            "USD" -> "$%.2f".format(amount)
            "EGP" -> "EGP %.2f".format(amount)
            else -> "%.2f".format(amount)  // Default format
        }
    }

}
