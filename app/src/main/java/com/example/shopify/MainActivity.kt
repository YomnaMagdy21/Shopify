package com.example.shopify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.firebase.Firebase
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.shoppingCard.view.shoppingCardFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebase = Firebase(this)
        if (firebase.getLoginState()) {

            startActivity(Intent(this, BottomNavActivity::class.java))
            finish()
        } else {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SignInFragment())
                    .commit()
            }
        }
    }
}