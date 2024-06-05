package com.example.shopify.productdetails.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shopify.R
import com.example.shopify.databinding.CustomTabBinding
import com.example.shopify.databinding.FragmentFavoriteBinding
import com.example.shopify.databinding.FragmentProductDetailsBinding
import com.google.android.material.tabs.TabLayout


class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrls = listOf(
            R.drawable.bag2,
            R.drawable.clothes,
            R.drawable.bag,
            R.drawable.clothes1,
            R.drawable.bag2,
            R.drawable.clothes,
            R.drawable.bag,
            R.drawable.clothes1
        )

        val adapter = ImageAdapter(requireContext(), imageUrls)
        binding.photosViewpager.adapter = adapter


        binding.tabLayout.setupWithViewPager(binding.photosViewpager, true)

       // setupTabDots()
    }

    private fun setupTabDots() {
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            if (tab != null) {
                val tabBinding = CustomTabBinding.inflate(LayoutInflater.from(requireContext()))
                tab.customView = tabBinding.root
            }
        }
    }

}