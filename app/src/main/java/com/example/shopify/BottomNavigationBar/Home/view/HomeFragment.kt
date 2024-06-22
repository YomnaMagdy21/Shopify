package com.example.shopify.BottomNavigationBar.Home.view

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shopify.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.shopify.BottomNavigationBar.Home.viewModel.HomeViewModel
import com.example.shopify.BottomNavigationBar.Home.viewModel.HomeViewModelFactory
import com.example.shopify.CheckNetwork.InternetStatus
import com.example.shopify.CheckNetwork.NetworkConectivityObserver
import com.example.shopify.CheckNetwork.NetworkObservation
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.Brands.SmartCollection
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.products.view.ProductsFragment
import com.example.shopify.utility.ApiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
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
    private lateinit var etSearch: EditText
    private lateinit var lottieAnimationView: LottieAnimationView


   private val images = listOf(
       R.drawable.ads2 to "CQS68F8QWT59",
       R.drawable.ads3 to "J6Z2HPJ6MZ5A",
       R.drawable.ads4 to "B6NQRHJJY9NA"
   )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        brandsRecyclerView = view.findViewById(R.id.rv_brands_in_home)
        progressBar = view.findViewById(R.id.progressBar)
        etSearch = view.findViewById(R.id.search_edit_text)
        lottieAnimationView = view.findViewById(R.id.lottie_no_data4)


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
        setupSearch()

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
    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBrands(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterBrands(query: String) {
        val filteredList = smartCollections.filter {
            it.title.contains(query, ignoreCase = true)
        }
        brandsAdapter.setBrandsList(filteredList)

        if(filteredList.isEmpty()){
            lottieAnimationView.visibility = View.VISIBLE
            brandsRecyclerView.visibility = View.GONE
        }else{
            lottieAnimationView.visibility = View.GONE
            brandsRecyclerView.visibility = View.VISIBLE
        }
    }
}
