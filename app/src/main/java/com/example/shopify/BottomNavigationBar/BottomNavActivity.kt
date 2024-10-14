package com.example.shopify.BottomNavigationBar

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.shopify.BottomNavigationBar.Category.view.CategoryFragment
import com.example.shopify.BottomNavigationBar.Favorite.view.FavoriteFragment
import com.example.shopify.BottomNavigationBar.Home.view.HomeFragment
import com.example.shopify.BottomNavigationBar.Me.MeFragment
import com.example.shopify.CheckNetwork.InternetStatus
import com.example.shopify.CheckNetwork.NetworkConectivityObserver
import com.example.shopify.CheckNetwork.NetworkObservation
import com.example.shopify.R

import com.example.shopify.databinding.ActivityBottomNavBinding
import com.example.shopify.ShoppingCart.view.shoppingCardFragment
import com.example.shopify.utility.SharedPreference
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale


class BottomNavActivity : AppCompatActivity() {
    lateinit var binding: ActivityBottomNavBinding
    lateinit var networkObservation: NetworkObservation
    private lateinit var lottieAnimationView: LottieAnimationView
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        lottieAnimationView = binding.lottieNoNetwork6

        replaceFragments(HomeFragment())
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_id -> replaceFragments(HomeFragment())
                R.id.category_id -> replaceFragments(CategoryFragment())
                R.id.cart_id -> replaceFragments(shoppingCardFragment(), "SHOPPING_CARD_FRAGMENT_TAG")
                R.id.fav_id -> replaceFragments(FavoriteFragment())
                R.id.me_id -> replaceFragments(MeFragment())
            }
            true
        }
        checkNetworkAndAppearData()
       var language= SharedPreference.getLanguage(this)
        updateAppContext(language)

    }

    private fun replaceFragments(fragment: Fragment, tag: String? = null) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        currentFragment?.let { fragmentTransaction.remove(it) }

        if (tag != null) {
            fragmentTransaction.replace(R.id.frame_layout, fragment, tag)
        } else {
            fragmentTransaction.replace(R.id.frame_layout, fragment)
        }
        currentFragment = fragment
        fragmentTransaction.commit()
    }

    private fun checkNetworkAndAppearData() {
        networkObservation = NetworkConectivityObserver(this)
        lifecycleScope.launch {
            networkObservation.observeOnNetwork().collectLatest { status ->
                when (status) {
                    InternetStatus.Available -> {
                        lottieAnimationView.visibility = View.GONE
                        binding.bottomAppBar.visibility = View.VISIBLE
                        binding.bottomNavigationView.visibility = View.VISIBLE
                        replaceFragments(HomeFragment())

                    }
                    InternetStatus.Lost, InternetStatus.UnAvailable -> {
                        lottieAnimationView.visibility = View.VISIBLE
                        binding.bottomAppBar.visibility = View.GONE
                        binding.bottomNavigationView.visibility = View.GONE
                        clearHomeFragment()
                    }
                }
            }
        }
        if (!isNetworkAvailable()) {
            lottieAnimationView.visibility = View.VISIBLE
            binding.bottomAppBar.visibility = View.GONE
            binding.bottomNavigationView.visibility = View.GONE
            clearHomeFragment()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }


    private fun clearHomeFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        currentFragment?.let { fragmentTransaction.remove(it) }
        currentFragment = null
        fragmentTransaction.commit()
    }
    private fun updateAppContext(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }
}
