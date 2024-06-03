package com.example.shopify.BottomNavigationBar.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shopify.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView



class HomeFragment : Fragment() {

    private lateinit var brandsRecyclerView: RecyclerView


    private lateinit var brandsAdapter: BrandsAdapter


    private val brandImages = listOf(
        R.drawable.nike,
        R.drawable.puma,
        R.drawable.adiddas,
        R.drawable.ck2,
        R.drawable.nike,
        R.drawable.puma,
        R.drawable.adiddas,
        R.drawable.ck2
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        brandsRecyclerView = view.findViewById(R.id.rv_brands_in_home)

        setupRecyclerView()
        return view
    }



    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 2) // 2 columns
        brandsRecyclerView.layoutManager = gridLayoutManager
        brandsAdapter = BrandsAdapter(requireContext(), brandImages)
        brandsRecyclerView.adapter = brandsAdapter
    }
}
