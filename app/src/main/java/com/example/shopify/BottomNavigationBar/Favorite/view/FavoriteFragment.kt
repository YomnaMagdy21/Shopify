package com.example.shopify.BottomNavigationBar.Favorite.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModelFactory
import com.example.shopify.R
import com.example.shopify.databinding.FragmentFavoriteBinding
import com.example.shopify.login.viewmodel.SignInViewModel
import com.example.shopify.login.viewmodel.SignInViewModelFactory
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.view.ProductDetailsFragment

class FavoriteFragment : Fragment() ,onFavoriteClickListener{
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        favoriteViewModelFactory = FavoriteViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        favoriteViewModel = ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)


        setUpRecyclerView()
        val fakeData = generateFakeFavorites()
        favoriteAdapter.submitList(fakeData)
        return binding.root
    }
    fun generateFakeFavorites(): List<Favorite> {
        return listOf(
            Favorite(name = "Favorite Item 1", img = R.drawable.bag2),
            Favorite(name = "Favorite Item 2", img = R.drawable.bag),
            Favorite(name = "Favorite Item 3", img = R.drawable.clothes2),
            Favorite(name = "Favorite Item 4", img = R.drawable.bag2),
            Favorite(name = "Favorite Item 5", img = R.drawable.clothes),
            Favorite(name = "Favorite Item 6", img = R.drawable.bag),
            Favorite(name = "Favorite Item 7", img = R.drawable.bag2),
            Favorite(name = "Favorite Item 8", img = R.drawable.clothes1),
            Favorite(name = "Favorite Item 9", img = R.drawable.clothes2)

        )
    }
    fun setUpRecyclerView(){
        favoriteAdapter=FavoriteAdapter(requireContext(),this)
        val gridLayoutManager = GridLayoutManager(context, 2) // 2 columns

        binding.recView.apply {
            adapter = favoriteAdapter
            layoutManager = gridLayoutManager

        }

    }

    override fun onFavClick() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, ProductDetailsFragment() )
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
