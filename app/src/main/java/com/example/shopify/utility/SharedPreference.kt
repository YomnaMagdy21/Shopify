package com.example.shopify.utility

import android.content.Context

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




    fun saveDraftOrderId(context: Context, id: Long, email: String) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putLong(email, id).apply()
    }

    fun getDraftOrderId(context: Context, email: String): Long {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        // Use contains to check if the value exists and is of type Long
        return if (prefs.contains(email)) {
            try {
                prefs.getLong(email, 10000000000)
            } catch (e: ClassCastException) {
                // Handle the case where the value is not a Long (fallback to default value)
                10000000000
            }
        } else {
            10000000000
        }
    }


    fun saveBrandID(context: Context,product_id:Long,brand_id:Long) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putLong(product_id.toString(), brand_id).apply()
    }

    fun getBrandID(context: Context,product_id:Long): Long {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        return prefs.getLong(product_id.toString(), 0) ?: 0
    }
    fun saveCollectionType(context: Context,product_id:Long,type:String) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putString(product_id.toString(), type).apply()
    }

    fun getCollectionType(context: Context, product_id: Long): String {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        return prefs.getString(product_id.toString(), "") ?: ""
    }

    fun saveGuest(context: Context,flag:String) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putString("Guest", flag).apply()
    }

    fun getGuest(context: Context): String {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        return prefs.getString("Guest", "") ?: ""
    }

    fun saveFirstName(context: Context, email: String, name: String) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putString(email + "_name", name).apply()
    }

    fun getFirstName(context: Context, email: String): String {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        return prefs.getString(email + "_name", "") ?: ""
    }


    fun saveLanguage(context: Context, lang: String) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().putString("KEY_LANGUAGE", lang).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        return prefs.getString("KEY_LANGUAGE", "") ?: ""
    }


    fun clearPreferences(context: Context) {
        val prefs = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }



}