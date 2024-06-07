package com.example.shopify.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Firebase {
     var firebaseAuth = FirebaseAuth.getInstance()
    fun createCustomerAccount(email: String, password: String, callback: (FirebaseUser?, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    print("account created successfully!!!")
                    var customer = it.result.user
                    callback(customer, null)


                } else {
                    print("can't create account !!")
                    callback(null, it.exception?.message)

                }
            }
    }


}