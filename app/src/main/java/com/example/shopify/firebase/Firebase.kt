package com.example.shopify.firebase


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.model.Customer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Firebase(private val context: Context) {
     var firebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser? = firebaseAuth.getCurrentUser()


    lateinit var mDatabase: DatabaseReference
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ShopifyPrefs", Context.MODE_PRIVATE)

    // Retrieve archived data from the Firebase Realtime Database
    // Example code to retrieve data

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
    fun checkIfEmailExists(email: String, callback: (Boolean) -> Unit) {
        mDatabase = FirebaseDatabase.getInstance().reference.child("Customer")
        mDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false)
            }
        })
    }


    fun loginClient(context: Context, email: String, password: String, onComplete: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   // Toast.makeText(context, "User logged in successfully", Toast.LENGTH_LONG).show()
                    context.startActivity(Intent(context, BottomNavActivity::class.java))
                    saveLoginState(true)
                    onComplete(true)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException) {
                        // Handle non-existent email
                        Toast.makeText(context, "Login Error: Email does not exist", Toast.LENGTH_LONG).show()
                    } else {
                        // Handle other errors
                        Toast.makeText(context, "Login Error: " + exception!!.message, Toast.LENGTH_LONG).show()
                    }
                    onComplete(false)
                }
            }
    }
    fun saveLoginState(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    fun getLoginState(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
    fun logout() {
        firebaseAuth.signOut()
        saveLoginState(false)
    }














    fun fetchUserData(firebaseUser: FirebaseUser, context: Context?, onComplete: (String?, String?,String?) -> Unit) {
        val userId = firebaseUser.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Customer").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firstName = snapshot.child("first_name").getValue(String::class.java)
                val password = snapshot.child("password").getValue(String::class.java)

                val email = firebaseUser.email

                // Return the retrieved data
                onComplete(firstName, email,password)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if any
                Log.e("fetchUserData", "Error fetching user data: ${error.message}")
                onComplete(null, null,null) // Return null values if there's an error
            }
        })
    }


    fun showFirstName(firebaseUser: FirebaseUser, context: Context?) :String{
        var firstname:String=""
        val userId = firebaseUser.uid

        val favoritesRef: DatabaseReference  = FirebaseDatabase.getInstance().reference.child("Customer")
            .child(userId)
        favoritesRef.child("first_name").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                 firstname = snapshot.getValue().toString()
                Log.i("TAG", "onDataChange: firstname $firstname")
//                for (dataSnapshot in snapshot.children) {
//                    val meal: Meal? = dataSnapshot.getValue(Meal::class.java)
//                    mealsRepositoryImp = MealsRepositoryImp.getInstance(
//                        MealsRemoteDataSourceImp.getInstance(), MealLocalDataSourceImp.getInstance(
//                            context
//                        )
//                    )
//                    mealsRepositoryImp.insertMeal(meal)
//                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return firstname
    }

}
























//    fun loginClient(context: Context, email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
//        firebaseAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // User logged in successfully
//                    saveLoginState(true)
//                    onComplete(true, null)
//                } else {
//                    // Handle different types of errors
//                    val exception = task.exception
//                    val errorMessage = when (exception) {
//                        is FirebaseAuthInvalidUserException -> "Email does not exist"
//                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password"
//                        else -> exception?.message ?: "Unknown error"
//                    }
//                    onComplete(false, errorMessage)
//                }
//            }
//    }

//    fun loginClient(context: Context, email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
//        firebaseAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(context, "User logged in successfully", Toast.LENGTH_LONG).show()
//                    context.startActivity(Intent(context, BottomNavActivity::class.java))
//                    saveLoginState(true)
//                    onComplete(true, null)
//                } else {
//                    val exception = task.exception
//                    val errorMessage = when (exception) {
//                        is FirebaseAuthInvalidUserException -> "Email does not exist"
//                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password"
//                        else -> exception?.message ?: "Unknown error"
//                    }
//                    onComplete(false, errorMessage)
//                }
//            }
//    }
