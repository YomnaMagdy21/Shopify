package com.example.shopify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.shoppingCard.view.shoppingCardFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignInFragment())
                .commit()
        }
    }
}