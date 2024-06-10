package com.example.shopify.productdetails.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Category.view.CategoryFragment
import com.example.shopify.BottomNavigationBar.Category.view.CategoryProductsAdapter
import com.example.shopify.BottomNavigationBar.Category.view.OnCategoryClickListener
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.R
import com.example.shopify.databinding.CustomTabBinding
import com.example.shopify.databinding.FragmentFavoriteBinding
import com.example.shopify.databinding.FragmentProductDetailsBinding
import com.example.shopify.databinding.ReviewItemBinding
import com.example.shopify.login.viewmodel.SignInViewModel
import com.example.shopify.login.viewmodel.SignInViewModelFactory
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.model.getRandomlyShuffledReviews
import com.example.shopify.productdetails.model.staticReviews
import com.example.shopify.productdetails.viewmodel.ProductDetailsViewModel
import com.example.shopify.productdetails.viewmodel.ProductDetailsViewModelFactory
import com.example.shopify.products.view.ProductAdapter
import com.example.shopify.utility.ApiState
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random


class ProductDetailsFragment : Fragment() ,OnCategoryClickListener{

    private lateinit var binding: FragmentProductDetailsBinding
    lateinit var productDetailsViewModel: ProductDetailsViewModel
    lateinit var productDetailsViewModelFactory: ProductDetailsViewModelFactory
    lateinit var reviewItemBinding: ReviewItemBinding
    lateinit var reviewsAdapter: ReviewsAdapter
    private var isExpanded = false
    lateinit var categoryAdapter: CategoryProductsAdapter
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var categoryViewModelFactory: CategoryViewModelFactory






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        productDetailsViewModelFactory = ProductDetailsViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        productDetailsViewModel = ViewModelProvider(this, productDetailsViewModelFactory).get(ProductDetailsViewModel::class.java)


        categoryViewModelFactory = CategoryViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        categoryViewModel = ViewModelProvider(
            requireActivity(),
            categoryViewModelFactory
        ).get(CategoryViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.backImage.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, CategoryFragment())
                .addToBackStack(null)
                .commit()
        }

        setUpRecyclerView()
        setUpSuggestionsRecView()

        val bundle = arguments

        val productID = bundle?.getLong("product_id")
        if (productID != null) {
            productDetailsViewModel.getProductInfo(productID)
        }
        lifecycleScope.launch {
            productDetailsViewModel.productInfo.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {

                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success<*> -> {
                        binding.progressBar.visibility = View.GONE
                        var data = result.data as? ProductModel
                        binding.title.text = data?.product?.title
                        binding.price.text = data?.product?.variants?.get(0)?.price + " EGP"
                        binding.descriptionText.text = data?.product?.body_html


                        val random = Random.nextInt(1,5).toFloat()
                        binding.ratingBar.rating = random
                        val imageUrls = data?.product?.images?.map { it.src } ?: emptyList()

                        // Check if images are available
                        if (imageUrls.isNotEmpty()) {
                            // Create and set the adapter
                            val adapter = ImageAdapter(requireContext(), imageUrls)
                            binding.photosViewpager.adapter = adapter
                            binding.tabLayout.setupWithViewPager(binding.photosViewpager, true)
                        } else {
                            Log.i("TAG", "No images found for this product.")
                        }
                        Log.i("TAG", "onViewCreated: ${data?.product?.title}")
                    }

                    else -> {


                    }

                }
            }

            // setupTabDots()
        }
        lifecycleScope.launch {
            categoryViewModel.accessAllProductList.collect { result ->
                when (result) {
                    is ApiState.Success<*> -> {


                        var products = result.data as CollectProductsModel?
                        products?.let {
                          var  myProducts = it.products
                            categoryAdapter.updateData(it.products)
                        }
                    }

                    is ApiState.Failure -> {

                    }

                    is ApiState.Loading -> {

                    }

                }

            }
        }
    }

    fun setUpRecyclerView(){
        val myReviews=getRandomlyShuffledReviews()
        reviewsAdapter= ReviewsAdapter(myReviews)

        binding.reviewsRecyclerView.apply {
            adapter = reviewsAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        }
        binding.viewMoreButton.setOnClickListener {
            isExpanded = !isExpanded

            if (isExpanded) {
                binding.viewMoreButton.text = "View Less"
                reviewsAdapter.visibleReviews =
                    myReviews
            } else {
                binding.viewMoreButton.text = "View More"
                reviewsAdapter.visibleReviews =
                    myReviews.take(3)
            }
            reviewsAdapter.notifyDataSetChanged()



        }
    }
    fun setUpSuggestionsRecView(){
        categoryAdapter= CategoryProductsAdapter(requireContext() , this ,  listOf())

        binding.recV.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        }
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

    override fun onCategoryClick(id: Long) {
        val bundle = Bundle()
        bundle.putLong("product_id",id)
        val fragmentDetails = ProductDetailsFragment()
        fragmentDetails.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragmentDetails)
            .addToBackStack(null)
            .commit()
    }


}