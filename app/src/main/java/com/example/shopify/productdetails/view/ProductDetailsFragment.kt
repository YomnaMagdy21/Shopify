package com.example.shopify.productdetails.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.example.shopify.databinding.FragmentProductDetailsBinding
import com.example.shopify.databinding.ReviewItemBinding
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.LineItem
import com.example.shopify.model.draftModel.NoteAttribute
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.model.getRandomlyShuffledReviews
import com.example.shopify.productdetails.viewmodel.ProductDetailsViewModel
import com.example.shopify.productdetails.viewmodel.ProductDetailsViewModelFactory
import com.example.shopify.ShoppingCart.model.ShoppingCardRepo
import com.example.shopify.ShoppingCart.viewModel.PriceRuleViewModelFactory
import com.example.shopify.ShoppingCart.viewModel.ShoppingCardViewModel
import com.example.shopify.model.productDetails.Product
import com.example.shopify.products.view.OnProductClickListener
import com.example.shopify.products.view.ProductAdapter
import com.example.shopify.products.viewModel.ProductsOfBrandViewModel
import com.example.shopify.products.viewModel.ProductsOfBrandViewModelFactory
import com.example.shopify.utility.ApiState
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random


class ProductDetailsFragment : Fragment() ,OnCategoryClickListener,OnProductClickListener{

    private lateinit var binding: FragmentProductDetailsBinding
    lateinit var productDetailsViewModel: ProductDetailsViewModel
    lateinit var productDetailsViewModelFactory: ProductDetailsViewModelFactory
    lateinit var reviewItemBinding: ReviewItemBinding
    lateinit var reviewsAdapter: ReviewsAdapter
    private var isExpanded = false
    lateinit var categoryAdapter: CategoryProductsAdapter
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var categoryViewModelFactory: CategoryViewModelFactory
    lateinit var viewModel: ProductsOfBrandViewModel
    lateinit var factoryProducts: ProductsOfBrandViewModelFactory
    private lateinit var shoppingCartViewModel: ShoppingCardViewModel
    lateinit var collectProducts: List<Product>
    private lateinit var productsOfBrandAdapter: ProductAdapter

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

        val factory = PriceRuleViewModelFactory(ShoppingCardRepo())
        shoppingCartViewModel = ViewModelProvider(this, factory).get(ShoppingCardViewModel::class.java)

        categoryViewModelFactory = CategoryViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        categoryViewModel = ViewModelProvider(
            requireActivity(),
            categoryViewModelFactory
        ).get(CategoryViewModel::class.java)

        factoryProducts = ProductsOfBrandViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        viewModel = ViewModelProvider(
            this,
            factoryProducts
        ).get(ProductsOfBrandViewModel::class.java)

        categoryAdapter= CategoryProductsAdapter(requireContext() , this ,  listOf())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.backImage.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setUpRecyclerView()
        setUpSuggestionsRecView()

        setUpSuggestionsRecViewFromProduct()

        val bundle = arguments

//        val productID = bundle?.getLong("product_id")
//        if (productID != null) {
//
//            productDetailsViewModel.getProductInfo(productID)
//        }
//
//        val productId = bundle?.getLong("product_ID")
//        Log.i("TAG", "onViewCreated: productID : $productID")
//        if (productId != null) {
//           // setUpSuggestionsRecViewFromProduct()
//            productDetailsViewModel.getProductInfo(productId)
//        }
        val productID = bundle?.getLong("product_id")
        productID?.let {
            productDetailsViewModel.getProductInfo(it)
            viewModel.getProductsOfBrands(productID)
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

                        //add to card
                        //var data2 = result.data as? Product

                        binding.addToCart.setOnClickListener{
                            Log.i("hi", "onViewCreated: hiiiiiiiiiiiiiiiiii")
                            if (data != null) {
                                addProductToCart(data)
                            }
                        }

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
                            productsOfBrandAdapter.setProductsBrandsList(it.products)

                        }
                    }

                    is ApiState.Failure -> {

                    }

                    is ApiState.Loading -> {

                    }

                }

            }
        }
        collectProducts =  listOf()

        lifecycleScope.launch {
            viewModel.accessProductsList.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.i("TAG", "onViewCreated: loading")
                    }
                    is ApiState.Success<*> -> {
                        binding.progressBar.visibility = View.GONE
                        val products = result.data as? CollectProductsModel
                        products?.let {
                            Log.d(
                                "ProductsOfBrandFragment",
                                "Retrieved data: ${it.products.size}"
                            )
                            collectProducts = it.products
                            productsOfBrandAdapter.setProductsBrandsList(it.products)
                        }
                    }
                    is ApiState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Log.i("TAG", "onViewCreated: failureeeeee")
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        Log.i("TAG", "onViewCreated: unexpected state: ${result::class.java.simpleName}")
                    }
                }
            }



//            viewModel.accessProductsList.collect { result ->
//                when (result) {
//                    is ApiState.Success<*> -> {
//                        binding.progressBar.visibility = View.GONE
//
//
//                        val products = result.data as CollectProductsModel?
//                        collectProducts = products?.products ?: listOf()
//                        products?.let {
//                            Log.d(
//                                "ProductsOfBrandFragment",
//                                "Retrieved data: ${collectProducts.size}"
//                            )
//
//                            productsOfBrandAdapter.setProductsBrandsList(it.products)
//                        }
//                    }
//                    is ApiState.Failure -> {
//                        binding.progressBar.visibility = View.GONE
//                        Log.i("TAG", "onViewCreated: failureeeeee")
//
//                    }
//                    is ApiState.Loading -> {
//                        binding.progressBar.visibility = View.VISIBLE
//
//                    }
//                }
//            }
        }

    }

    fun setUpRecyclerView() {
        val myReviews = getRandomlyShuffledReviews()
        reviewsAdapter = ReviewsAdapter(myReviews)

        binding.reviewsRecyclerView.apply {
            adapter = reviewsAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        }
        binding.viewMoreButton.setOnClickListener {

            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.popup_reviews, null)


            val reviewsRecyclerView = popupView.findViewById<RecyclerView>(R.id.reviewsRecyclerView)
            reviewsRecyclerView.layoutManager = LinearLayoutManager(context)
            val popupAdapter = ReviewsAdapter(myReviews)
            reviewsRecyclerView.adapter = popupAdapter
            popupAdapter.loadAllReviews()


            val dialog = AlertDialog.Builder(context)
                .setView(popupView)
                .create()


            val closeButton = popupView.findViewById<ImageView>(R.id.closeButton)
            closeButton.setOnClickListener {
                dialog.dismiss()
            }


            dialog.show()


    }
    }
    fun setUpSuggestionsRecView(){
        categoryAdapter= CategoryProductsAdapter(requireContext() , this ,  listOf())
        binding.recV.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        }
    }
    fun setUpSuggestionsRecViewFromProduct(){
        productsOfBrandAdapter= ProductAdapter(requireContext() ,   listOf(),this)
        binding.recV.apply {
            adapter = productsOfBrandAdapter
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

    private fun addProductToCart(product: ProductModel) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        Log.d("AddToCart", "Attempting to add product to cart: $product")

        if (currentUser != null) {
            val variantId = product.product?.variants?.get(0)?.id
            if (variantId != null) {
                if (categoryViewModel.addedProductIds.contains(variantId)) {
                    Snackbar.make(requireView(), "Product already in cart", Snackbar.LENGTH_SHORT).show()
                    return
                }

                Log.d("AddToCart", "Product not already in cart. Proceeding to add.")

                var order = DraftOrder()
                order.email = userEmail
                var draft_orders = DraftOrderResponse()
                order.note = "cart"
                var lineItems = LineItem()
                lineItems.quantity = 1
                lineItems.variant_id = product.product.variants!![0].id
                order.line_items = listOf(lineItems)
                var note_attribute = NoteAttribute()
                note_attribute.name = "image"
                note_attribute.value = product.product.images!![0].src
                order.note_attributes = listOf(note_attribute)
                draft_orders = DraftOrderResponse(order)

                Log.d("DraftOrder", "Creating Draft Order: $draft_orders")

                shoppingCartViewModel.createDraftOrder(draft_orders)

                lifecycleScope.launch {
                    shoppingCartViewModel.draftOrderResponse.collect { draftOrderResponse ->
                        if (draftOrderResponse != null) {
                            // Add the id
                            variantId.let { categoryViewModel.addedProductIds.add(it) }
                            Snackbar.make(requireView(), "Added to Cart", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Log.e("AddToCart", "Failed to create draft order")
                        }
                    }
                }
            }
        } else {
            Snackbar.make(requireView(), "User Not Logged In", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun goToDetails(id: Long) {
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