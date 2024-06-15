package com.example.shopify.BottomNavigationBar.Favorite.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.BottomNavigationBar.Favorite.model.Items
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModelFactory
import com.example.shopify.R
import com.example.shopify.ShoppingCart.view.Item
import com.example.shopify.databinding.FragmentFavoriteBinding
import com.example.shopify.login.viewmodel.SignInViewModel
import com.example.shopify.login.viewmodel.SignInViewModelFactory
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.Draft_orders_list
import com.example.shopify.model.draftModel.LineItem
import com.example.shopify.model.draftModel.NoteAttribute
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.view.ProductDetailsFragment
import com.example.shopify.utility.ApiState
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

class FavoriteFragment : Fragment() ,onFavoriteClickListener{
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var categoryViewModelFactory: CategoryViewModelFactory
    private lateinit var favProducts: MutableList<ItemLine>


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

        categoryViewModelFactory = CategoryViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        categoryViewModel = ViewModelProvider(
            requireActivity(),
            categoryViewModelFactory
        ).get(CategoryViewModel::class.java)
        favoriteAdapter=FavoriteAdapter(requireContext(),this)


        favProducts = mutableListOf()
        setUpRecyclerView()

        //favoriteAdapter.submitList(favProducts)



        //val fakeData = generateFakeFavorites()
       // favoriteAdapter.submitList(fakeData)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
        val draftOrderId = sharedPreferences.getString("draft_order_id", null)

        if (draftOrderId != null) {
            favoriteViewModel.getFavorites(draftOrderId.toLong())// Use the draftOrderId as needed
            Log.d("DraftOrder", "Draft Order ID: $draftOrderId")
        } else {

            Log.e("DraftOrder", "Draft Order ID not found")
        }


        lifecycleScope.launch {
            favoriteViewModel.fav.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        Log.i("FavoriteFragment", "onViewCreated: loading...")
                    }
                    is ApiState.Success<*> -> {

                        favProducts.clear()
                        Log.i("FavoriteFragment", "onViewCreated: success")
                        val data = result.data as? FavDraftOrderResponse
                        Log.i("FavoriteFragment", "DraftOrderResponse: ${data?.draft_order?.id}")
                        if (data != null) {

                            var items = data.draft_order?.line_items

                            items?.let { favProducts.addAll(it) }
                            Log.i("FavoriteFragment", "Number of items: ${favProducts.size}") // Log number of items
                           // favoriteAdapter.submitList(favProducts)
                        }
                        favoriteAdapter.submitList(favProducts)
                        favoriteAdapter.notifyDataSetChanged()
                      //  data?.draft_order?.let { favProducts.add(it) }

                    }
                    is ApiState.Failure -> {
                        Log.e("FavoriteFragment", "onViewCreated: failure: ${result.msg}")
                    }
                    else -> {
                        Log.i("FavoriteFragment", "onViewCreated: unknown state")
                    }
                }
            }
        }
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

    override fun removeFavorite(id: Long) {


        val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
        val draftOrderId = sharedPreferences.getString("draft_order_id", null)?.toLong()

        if (draftOrderId != null) {
            fetchDraftOrder(draftOrderId) { draftOrder ->
                val updatedLineItems = draftOrder?.line_items?.toMutableList() ?: mutableListOf()
                Log.i("TAG", "Initial updatedLineItems: $updatedLineItems")

                // Find the item to remove by matching the id (or other unique identifier)
                val itemToRemove = updatedLineItems.find { it.id == id }

                if (itemToRemove != null) {
                    updatedLineItems.remove(itemToRemove)
                    Log.i("TAG", "Updated updatedLineItems after removal: $updatedLineItems")

                    val favDraftOrder = FavDraftOrder(
                        id = draftOrderId,
                        line_items = updatedLineItems
                    )
                    val favDraftOrderResponse = FavDraftOrderResponse(favDraftOrder)

                    favoriteViewModel.updateFavorite(draftOrderId, favDraftOrderResponse)
                } else {
                    Log.i("TAG", "Item not found in updatedLineItems")
                }
            }
        } else {
            Log.e("DraftOrder", "Draft Order ID not found")
        }
        favoriteViewModel.deleteFavorite(id)
    }

    private fun fetchDraftOrder(draftOrderId: Long, callback: (FavDraftOrder?) -> Unit) {
        // Assuming you have a method in your ViewModel to get the current DraftOrder
        favoriteViewModel.getFavorites(draftOrderId)
        lifecycleScope.launch {
            favoriteViewModel.fav.collectLatest { result ->
                when (result) {
                    is ApiState.Success<*> -> {
                        val data = result.data as? FavDraftOrderResponse
                        callback(data?.draft_order)
                    }
                    else -> {
                        callback(null)
                    }
                }
            }
        }
    }

}
