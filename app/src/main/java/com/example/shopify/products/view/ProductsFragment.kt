package com.example.shopify.products.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.view.FavoriteAdapter
import com.example.shopify.R
import com.example.shopify.databinding.FragmentFavoriteBinding
import com.example.shopify.databinding.FragmentProductsBinding
import com.example.shopify.productdetails.view.ProductDetailsFragment


class ProductsFragment : Fragment() ,OnProductClickListener{


    private lateinit var binding: FragmentProductsBinding
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductsBinding.inflate(inflater, container, false)


        setUpRecyclerView()
        val fakeData = generateFakeProduct()
        productAdapter.submitList(fakeData)
        return binding.root
    }


    fun generateFakeProduct(): List<Favorite> {
        return listOf(
            Favorite(name = "Product Item 1", img = R.drawable.bag2),
            Favorite(name = "Product Item 2", img = R.drawable.bag),
            Favorite(name = "Product Item 3", img = R.drawable.clothes2),
            Favorite(name = "Product Item 4", img = R.drawable.bag2),
            Favorite(name = "Product Item 5", img = R.drawable.clothes),
            Favorite(name = "Product Item 6", img = R.drawable.bag),
            Favorite(name = "Product Item 7", img = R.drawable.bag2),
            Favorite(name = "Product Item 8", img = R.drawable.clothes1)
            // Favorite(name = "Product Item 9", img = R.drawable.clothes2)

        )
    }
    fun setUpRecyclerView(){
        productAdapter=ProductAdapter(requireContext(),this)
        val gridLayoutManager = GridLayoutManager(context, 2) // 2 columns

        binding.recView.apply {
            adapter = productAdapter
            layoutManager = gridLayoutManager

        }

    }

    override fun goToDetails() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, ProductDetailsFragment() )
        transaction.addToBackStack(null)
        transaction.commit()
    }
}