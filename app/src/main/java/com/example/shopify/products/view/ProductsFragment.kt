package com.example.shopify.products.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModelFactory
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.R
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.LineItem

import com.example.shopify.model.draftModel.NoteAttribute
import com.example.shopify.model.productDetails.Product
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.view.ProductDetailsFragment
import com.example.shopify.products.viewModel.ProductsOfBrandViewModel
import com.example.shopify.products.viewModel.ProductsOfBrandViewModelFactory
import com.example.shopify.utility.ApiState
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ProductsFragment : Fragment() ,OnProductClickListener {

    private var brandId: Long? = null
    private lateinit var productsOfBrandAdapter: ProductAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var filterImg : ImageView
    private lateinit var filterSlider : Slider

    lateinit var viewModel: ProductsOfBrandViewModel
    lateinit var factory: ProductsOfBrandViewModelFactory
    lateinit var collectProducts: List<Product>
    private lateinit var editTextSearch: EditText
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var categoryViewModelFactory: CategoryViewModelFactory
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory






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
        filterImg = view.findViewById(R.id.filter)
        filterSlider = view.findViewById(R.id.filterSlider)
        editTextSearch = view.findViewById(R.id.search_edit_text)

        categoryViewModelFactory = CategoryViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        categoryViewModel = ViewModelProvider(
            requireActivity(),
            categoryViewModelFactory
        ).get(CategoryViewModel::class.java)

        favoriteViewModelFactory = FavoriteViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        favoriteViewModel = ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)



        setupSearch()

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

        // filter
        filterImg.setOnClickListener {
            if(filterSlider.visibility == View.GONE){
                filterSlider.visibility = View.VISIBLE
                filterImg.setImageResource(R.drawable.unfilter)
            }
            else{

                // to reset the value of slider after unfilter the filterImg
                filterSlider.value = filterSlider.valueFrom

                filterSlider.visibility = View.GONE
                filterImg.setImageResource(R.drawable.filter)

                // to show all products after unfilter the filterImg
                productsOfBrandAdapter.setProductsBrandsList(collectProducts)
            }
        }

        // change in slider listener
        filterSlider.addOnChangeListener { slider, value, fromUser ->
            filterProductsByPrice(value)
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


    // filter
    fun filterProductsByPrice(price: Float) {
        val filteredProducts = collectProducts.filter { product ->
            val productPrice = product.variants?.firstOrNull()?.price?.toFloatOrNull()
            productPrice != null && productPrice <= price
        }
        productsOfBrandAdapter.setProductsBrandsList(filteredProducts)
    }

    override fun goToDetails(id:Long) {
        // Implementation for goToDetails
        val bundle = Bundle()
        bundle.putLong("product_id",id)
        val fragmentDetails = ProductDetailsFragment()
        fragmentDetails.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragmentDetails)
            .addToBackStack(null)
            .commit()
    }

    override fun onFavBtnClick(favorite: Product) {
        addProductToFav(favorite)
    }

    private fun setupSearch() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBrands(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterBrands(query: String) {
        val filteredList = collectProducts.filter {
            it.title?.contains(query, ignoreCase = true) ?: true
        }
        productsOfBrandAdapter.setProductsBrandsList(filteredList)
    }

    private fun addProductToFav(product: Product) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        Log.d("AddToFav", "Attempting to add product to fav: $product")

        if (currentUser != null) {
            val variantId = product?.variants?.get(0)?.id
            if (variantId != null) {
                if (categoryViewModel.addedProductIds.contains(variantId)) {
                    Snackbar.make(requireView(), "Product already in cart", Snackbar.LENGTH_SHORT).show()
                    return
                }

                Log.d("AddToFav", "Product not already in cart. Proceeding to add.")

                var order = FavDraftOrder()
               // order.email = userEmail

               // order.note = "fav"
                var lineItems = ItemLine(variantId,quantity = 1)
                lineItems.quantity = 1
                lineItems.variant_id = product.variants!![0].id
                order.line_items = listOf(lineItems)
                var note_attribute = NoteAttribute()
                note_attribute.name = "image"
                note_attribute.value = product.images!![0].src
             //   order.note_attributes = listOf(note_attribute)
              var  draft_orders = FavDraftOrderResponse(order)

                Log.d("DraftOrder", "Creating Draft Order: $draft_orders")

              //  favoriteViewModel.createFavDraftOrders(draft_orders)

                val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
                val draftOrderId = sharedPreferences.getString("draft_order_id", null)

                if (draftOrderId != null) {
                    favoriteViewModel.updateFavorite(draftOrderId.toLong(),draft_orders)
                    Log.d("DraftOrder", "Draft Order ID: $draftOrderId")
                } else {

                    Log.e("DraftOrder", "Draft Order ID not found")
                }


                lifecycleScope.launch {
                    favoriteViewModel.wishList.collectLatest {result ->
                        when(result){
                            is ApiState.Loading ->{

                                Log.i("TAG", "addProductToFav:loadingggg ")
                            }
                            is ApiState.Success<*> ->{
                                val wishList = result.data as? FavDraftOrderResponse
                                val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("draft_order_id", wishList?.draft_order?.id.toString())
                                editor.apply()
                                Log.i("TAG", "onViewCreated: draft order in fav = ${wishList?.draft_order?.id}")
                            }
                            else->{

                            }
                        }
                    }
                }

                lifecycleScope.launch {
                    favoriteViewModel.fav.collect { draftOrderResponse ->
                        if (draftOrderResponse != null) {
                            // Add the id
                            variantId.let { categoryViewModel.addedProductIds.add(it) }
                            Snackbar.make(requireView(), "Added to Fav", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Log.e("AddToFav", "Failed to create draft order")
                        }
                    }
                }
            }
        } else {
            Snackbar.make(requireView(), "User Not Logged In", Snackbar.LENGTH_SHORT).show()
        }
    }
}
