package com.example.shopify.BottomNavigationBar.Home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shopify.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopify.BottomNavigationBar.Home.viewModel.HomeViewModel
import com.example.shopify.BottomNavigationBar.Home.viewModel.HomeViewModelFactory
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.Brands.SmartCollection
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.products.view.ProductsFragment
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.launch


class HomeFragment : Fragment() , OnBrandClickListener {

    private lateinit var viewPager: ViewPager2
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var brandsRecyclerView: RecyclerView


    private lateinit var brandsAdapter: BrandsAdapter
    lateinit var homeViewModel: HomeViewModel
    lateinit var homeViewModelFactory: HomeViewModelFactory
    lateinit var smartCollections: List<SmartCollection>
    private lateinit var progressBar: ProgressBar


   private val images = listOf(
        R.drawable.ads2,
        R.drawable.ads3,
        R.drawable.ads4
   )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        brandsRecyclerView = view.findViewById(R.id.rv_brands_in_home)
        progressBar = view.findViewById(R.id.progressBar)

        smartCollections = listOf()
        homeViewModelFactory = HomeViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            ))
        homeViewModel = ViewModelProvider(
            this,
            homeViewModelFactory
        ).get(HomeViewModel::class.java)

        setupImageSlider()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setBrandData()
        homeViewModel.getBrands()
    }


    fun setBrandData() {
        lifecycleScope.launch {
            homeViewModel.accessBrandsList.collect() { result ->
                when (result) {
                    is ApiState.Success<*> -> {
                        // hide progressBar and show rv
                        progressBar.visibility = View.GONE
                        brandsRecyclerView.visibility = View.VISIBLE


                        var brands = result.data as BrandModel?
                        smartCollections = brands?.smart_collections ?: listOf()
                        brandsAdapter.setBrandsList(smartCollections)

                        // the retrieved data
                        // Log.d("HomeFragment============================", "Retrieved data: $smartCollections")
                    }
                    is ApiState.Failure -> {

                        //  Log.e("HomeFragment-------------------------------", "Error: ${result.msg.message}")

                        // hide progressBar and rv
                        progressBar.visibility = View.GONE
                        brandsRecyclerView.visibility = View.GONE
                    }
                    is ApiState.Loading -> {
                        // show progressBar and hide rv
                        progressBar.visibility = View.VISIBLE
                        brandsRecyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }





    private fun setupImageSlider() {
        imageSliderAdapter = ImageSliderAdapter(images)
        viewPager.adapter = imageSliderAdapter

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            var currentItem = viewPager.currentItem
            val itemCount = imageSliderAdapter.itemCount

            if (currentItem == itemCount - 1) {
                currentItem = 0
            } else {
                currentItem++
            }

            viewPager.setCurrentItem(currentItem, true)
            handler.postDelayed(runnable, 3000)
        }

        handler.postDelayed(runnable, 3000)
    }


    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 2) // 2 columns
        brandsRecyclerView.layoutManager = gridLayoutManager
        brandsAdapter = BrandsAdapter(requireContext(),  listOf(),this)
        brandsRecyclerView.adapter = brandsAdapter
    }

    override fun goToProducts(brandId: Long) {
        val bundle = Bundle()
        bundle.putLong("BRAND_ID", brandId)
        Log.d("HomeFragment============================", "send ID: $brandId")
        val fragment = ProductsFragment()
        fragment.arguments = bundle
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
