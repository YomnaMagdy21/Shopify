package com.example.shopify.utility

import android.content.Context
import android.content.SharedPreferences
import com.example.shopify.model.productDetails.Product

object SharedPreference {

    fun saveFav(context: Context,id:Long,email:String,isFav: Boolean) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(email+id.toString(), isFav).apply()
    }

    fun getFav(context: Context,id:Long,email :String): Boolean {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        return prefs.getBoolean(email+id.toString(), false) ?: false
    }

    fun saveUserEmail(context: Context,email:String) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putString("Email", email).apply()
    }

    fun getUserEmail(context: Context): String {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        return prefs.getString("Email", "") ?: ""
    }

    private val lock = Any()

    fun saveDraftOrderId(context: Context, id: Long, email: String) {
        synchronized(lock) {
            val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
            //Log.i("SharedPreference", "saveDraftOrderID called with id: $id for email: $email")
            prefs.edit().putLong(email, id).apply()
        }
    }

    fun getDraftOrderId(context: Context, email: String): Long {
        synchronized(lock) {
            val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
            val draftOrderID = prefs.getLong(email, 10000000000)
           // Log.i("SharedPreference", "getDraftOrderID retrieved id: $draftOrderID for email: $email")
            return draftOrderID
        }
    }
    fun clearPreferences(context: Context) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }



}