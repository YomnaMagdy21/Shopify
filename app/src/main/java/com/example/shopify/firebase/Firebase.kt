package com.example.shopify.firebase


import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.model.Customer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Firebase {
     var firebaseAuth = FirebaseAuth.getInstance()
     lateinit var mDatabase: DatabaseReference
    fun createCustomerAccount(email: String, password: String, callback: (FirebaseUser?, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    print("account created successfully!!!")
                    mDatabase = FirebaseDatabase.getInstance().reference.child("Customer")

                    var customer = it.result.user
                    callback(customer, null)


                } else {
                    print("can't create account !!")
                    callback(null, it.exception?.message)

                }
            }
    }

    fun writeNewUser(customer: Customer) {
        mDatabase = FirebaseDatabase.getInstance().reference.child("Customer")

        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        mDatabase.child(user!!.uid).setValue(customer)
    }

    fun checkIfUserExists(uid: String, callback: (Boolean) -> Unit) {
        mDatabase = FirebaseDatabase.getInstance().reference.child("Customer")
        mDatabase.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {

                callback(false)
            }
        })
    }
  //  private fun loginClient(email: String, password: String) {
//        val Email: String = email.getText().toString()
//        val Password: String = password.getText().toString()
//        if (TextUtils.isEmpty(Email)) {
//            email.setError("Email cannot be empty")
//            email.requestFocus()
//        } else if (TextUtils.isEmpty(Password)) {
//            password.setError("Password cannot be empty")
//            password.requestFocus()
//        } else {
  fun loginClient(context: Context, email: String, password: String) {
//      if (TextUtils.isEmpty(email)) {
//          Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_LONG).show()
//          return
//      }
//      if (TextUtils.isEmpty(password)) {
//          Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_LONG).show()
//          return
//      }

      firebaseAuth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
              if (task.isSuccessful) {
                  Toast.makeText(context, "User logged in successfully", Toast.LENGTH_LONG).show()
                  context.startActivity(Intent(context, BottomNavActivity::class.java))
              } else {
                  Toast.makeText(context, "Login Error: " + task.exception!!.message, Toast.LENGTH_LONG).show()
              }
          })
  }
}