package com.example.shopify.BottomNavigationBar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.shopify.BottomNavigationBar.Category.view.CategoryFragment
import com.example.shopify.BottomNavigationBar.Favorite.view.FavoriteFragment
import com.example.shopify.BottomNavigationBar.Home.view.HomeFragment
import com.example.shopify.BottomNavigationBar.Me.MeFragment
import com.example.shopify.R

import com.example.shopify.databinding.ActivityBottomNavBinding
import com.example.shopify.ShoppingCart.view.shoppingCardFragment


class BottomNavActivity : AppCompatActivity() {
    lateinit var binding: ActivityBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        replaceFragments(HomeFragment())
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_id -> replaceFragments(HomeFragment())
                R.id.category_id -> replaceFragments(CategoryFragment())
                R.id.cart_id -> replaceFragments(shoppingCardFragment())
                R.id.fav_id -> replaceFragments(FavoriteFragment())
                R.id.me_id -> replaceFragments(MeFragment())
            }
            true
        }
    }

    private fun replaceFragments(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
