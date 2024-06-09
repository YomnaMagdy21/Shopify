package com.example.shopify.products.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.R
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.productDetails.Product
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.products.viewModel.ProductsOfBrandViewModel
import com.example.shopify.products.viewModel.ProductsOfBrandViewModelFactory
import com.example.shopify.utility.ApiState
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch


class ProductsFragment : Fragment() ,OnProductClickListener {

    private var brandId: Long? = null
    private lateinit var productsOfBrandAdapter: ProductAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar


    lateinit var viewModel: ProductsOfBrandViewModel
    lateinit var factory: ProductsOfBrandViewModelFactory
    lateinit var collectProducts: List<Product>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)
        collectProducts = listOf()
        factory = ProductsOfBrandViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        viewModel = ViewModelProvider(
            this,
            factory
        ).get(ProductsOfBrandViewModel::class.java)

        productsRecyclerView = view.findViewById(R.id.recView)
        progressBar = view.findViewById(R.id.progressBar)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            brandId = it.getLong("BRAND_ID")
            Log.d("ProductsOfBrandFragment", "Received ID: $brandId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsOfBrandAdapter = ProductAdapter(requireContext(), listOf(), this)
        val gridLayoutManager = GridLayoutManager(context, 2) // 2 columns
        productsRecyclerView.layoutManager = gridLayoutManager
        productsRecyclerView.adapter = productsOfBrandAdapter

        brandId?.let {
            viewModel.getProductsOfBrands(it)
            setBrandData()
        }


    }

    fun setBrandData() {
        lifecycleScope.launch {
            viewModel.accessProductsList.collect { result ->
                when (result) {
                    is ApiState.Success<*> -> {
                        progressBar.visibility = View.GONE
                        productsRecyclerView.visibility = View.VISIBLE

                        val products = result.data as CollectProductsModel
                        collectProducts = products?.products ?: listOf()

                        Log.d("ProductsOfBrandFragment", "Retrieved data: ${collectProducts.size}")

                        productsOfBrandAdapter.setProductsBrandsList(collectProducts)
                    }
                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        productsRecyclerView.visibility = View.GONE
                    }
                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        productsRecyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun goToDetails() {
        // Implementation for goToDetails
    }
}
