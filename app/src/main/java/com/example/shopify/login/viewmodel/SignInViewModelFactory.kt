package com.example.shopify.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.signup.viewmodel.SignUpViewModel

class SignInViewModelFactory (private  var _repo: ShopifyRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SignInViewModel::class.java)){
            SignInViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("ViewModel class not found")

        }
    }
}