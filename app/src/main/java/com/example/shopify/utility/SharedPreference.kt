package com.example.shopify.utility

import android.content.Context
import android.content.SharedPreferences
import com.example.shopify.model.productDetails.Product

object SharedPreference {

    fun saveFav(context: Context,id:Long,isFav: Boolean) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(id.toString(), isFav).apply()
    }

    fun getFav(context: Context,id:Long): Boolean {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        return prefs.getBoolean(id.toString(), false) ?: false
    }

}