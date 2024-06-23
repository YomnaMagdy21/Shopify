package com.example.shopify.products.view

import android.content.Context
import android.content.SharedPreferences
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
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.BottomNavigationBar.Favorite.model.Items
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
import com.example.shopify.signup.viewmodel.SignUpViewModel
import com.example.shopify.signup.viewmodel.SignUpViewModelFactory
import com.example.shopify.utility.ApiState
import com.example.shopify.utility.SharedPreference
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class ProductsFragment : Fragment() ,OnProductClickListener {

    private var brandId: Long? = null
    private lateinit var productsOfBrandAdapter: ProductAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var filterImg : ImageView
    private lateinit var filterSlider : Slider

    lateinit var sharedPreferencesFav: SharedPreferences

    lateinit var signUpViewModel: SignUpViewModel
    lateinit var signUpViewModelFactory: SignUpViewModelFactory

    lateinit var viewModel: ProductsOfBrandViewModel
    lateinit var factory: ProductsOfBrandViewModelFactory
    lateinit var collectProducts: List<Product>
    private lateinit var editTextSearch: EditText
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var categoryViewModelFactory: CategoryViewModelFactory
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory

    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var filteredPrice : TextView





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
        lottieAnimationView = view.findViewById(R.id.animationView)
        filteredPrice = view.findViewById(R.id.tv_filteredPrice)
        filteredPrice.text = "Price: 0.00"
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
        signUpViewModelFactory = SignUpViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        signUpViewModel = ViewModelProvider(this, signUpViewModelFactory).get(SignUpViewModel::class.java)


        val sharedPreferences =
            requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
        var email =SharedPreference.getUserEmail(requireContext())
        val draftID = SharedPreference.getDraftOrderId(requireContext(),email)


        if (draftID != 10000000000) {
            favoriteViewModel.getFavorites(draftID.toLong())// Use the draftOrderId as needed
            Log.d("DraftOrder", "Draft Order ID: $draftID")
        } else {

            Log.e("DraftOrder", "Draft Order ID not found")
        }

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

         sharedPreferencesFav = requireContext().getSharedPreferences("favPref", Context.MODE_PRIVATE)


        brandId?.let {
            viewModel.getProductsOfBrands(it)
            setBrandData()
        }

      //  SharedPreference.clearPreferences(requireContext())
        // filter
        filterImg.setOnClickListener {
            if(filterSlider.visibility == View.GONE){
                filterSlider.visibility = View.VISIBLE
                filteredPrice.visibility = View.VISIBLE
                filterImg.setImageResource(R.drawable.unfilter)
                productsOfBrandAdapter.setProductsBrandsList(emptyList())

            }
            else{

                // to reset the value of slider after unfilter the filterImg
                filterSlider.value = filterSlider.valueFrom
                filteredPrice.visibility = View.GONE
                filterSlider.visibility = View.GONE
                filterImg.setImageResource(R.drawable.filter)
                lottieAnimationView.visibility = View.GONE

                // to show all products after unfilter the filterImg
                productsOfBrandAdapter.setProductsBrandsList(collectProducts)
            }
        }

        // change in slider listener
        filterSlider.addOnChangeListener { slider, value, fromUser ->
            filterProductsByPrice(value)
            val formattedPrice = DecimalFormat("#.00").format(value)
            filteredPrice.text = "Price: $formattedPrice"
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

        if (filteredProducts.isEmpty()) {

            lottieAnimationView.visibility = View.VISIBLE
            productsOfBrandAdapter.setProductsBrandsList(emptyList())
        } else {

            lottieAnimationView.visibility = View.GONE
            productsOfBrandAdapter.setProductsBrandsList(filteredProducts)
        }
    }


    override fun goToDetails(id:Long) {
        // Implementation for goToDetails
        val bundle = Bundle()
        bundle.putLong("product_id",id)
        brandId?.let { bundle.putLong("brand_id", it) }
    //    brandId?.let { SharedPreference.saveBrandID(requireContext(),id, it) }
        val fragmentDetails = ProductDetailsFragment()
        fragmentDetails.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragmentDetails)
            .addToBackStack(null)
            .commit()
    }

    override fun onFavBtnClick(product: Product) {
        product.isFav = true
        var email = SharedPreference.getUserEmail(requireContext())
        var draftID = SharedPreference.getDraftOrderId(requireContext(), email)
        if (draftID == 10000000000) {
            val lineItems = listOf(
                ItemLine(quantity = 1, variant_id = product.variants?.get(0)?.id, sku = "")
            )
            val draftOrder = FavDraftOrder(

                line_items = lineItems,
            )

            var order = FavDraftOrderResponse(draftOrder)

            signUpViewModel.createFavDraftOrders(order)
            lifecycleScope.launch {
                signUpViewModel.wishList.collectLatest { result ->
                    when (result) {
                        is ApiState.Loading -> {

                        }

                        is ApiState.Success<*> -> {
                            val wishList = result.data as? FavDraftOrderResponse
                            wishList?.draft_order?.id?.let {
                                SharedPreference.saveDraftOrderId(
                                    requireContext(),
                                    it, email
                                )
                            }
                            Log.i(
                                "TAG",
                                "onViewCreated: draft order in  = ${wishList?.draft_order?.id}"
                            )
                            if (wishList != null) {

                                Log.i(
                                    "TAG",
                                    "onViewCreated: draft order in  product fragment = ${wishList?.draft_order?.id}"
                                )
                            }
                        }

                        else -> {

                        }
                    }
                }
            }
        } else {

            lifecycleScope.launch {
                viewModel.accessProductsList.collectLatest {
                        fetchDraftOrder(draftID.toLong()) { draftOrder ->
                            val productTitle = product.title
                            val productVariantId = product.variants?.get(0)?.id
                            val productImageSrc = product.image?.src


                            Log.i(
                                "TAG",
                                "Product details: title=$productTitle, variantId=$productVariantId, imageSrc=$productImageSrc"
                            )

                            if (productTitle != null && productVariantId != null && productImageSrc != null) {
                                val newLineItem = ItemLine(
                                    title = productTitle,
                                    variant_id = productVariantId,
                                    quantity = 1,
                                    sku = productImageSrc
                                )


                                Log.i("TAG", "New Line Item: $newLineItem")

                                val updatedLineItems =
                                    draftOrder?.line_items?.toMutableList() ?: mutableListOf()

                                val itemExists =
                                    updatedLineItems.any { it.variant_id == newLineItem.variant_id }

                                if (!itemExists) {
                                    updatedLineItems.add(newLineItem)
                                    Log.i(
                                        "TAG",
                                        "onViewCreated: updatedLineItems22222 ${updatedLineItems}"
                                    )
                                    val favDraftOrder = FavDraftOrder(
                                        id = draftID.toLong(),
                                        line_items = updatedLineItems
                                    )
                                    val favDraftOrderResponse = FavDraftOrderResponse(favDraftOrder)

                                    Log.i(
                                        "TAG",
                                        "onFavBtnClick: favDraftOrderResponse${favDraftOrderResponse}"
                                    )

                                    favoriteViewModel.updateFavorite(
                                        draftID.toLong(),
                                        favDraftOrderResponse
                                    )
                                }
                                // Log updated line items before and after update
                                Log.i("TAG", "Before update: $updatedLineItems")


                            } else {
                                Log.e(
                                    "TAG",
                                    "Product details are incomplete. Title: $productTitle, VariantId: $productVariantId, ImageSrc: $productImageSrc"
                                )
                            }
                        }
                    }
//                else {
//                        Log.e("DraftOrder", "Draft Order ID not found")
//                    }


                }
            }

        }


    override fun onClickToRemove(id: Long) {

        var email = SharedPreference.getUserEmail(requireContext())
        var draftID = SharedPreference.getDraftOrderId(requireContext(),email)

        if (draftID != 10000000000) {
            fetchDraftOrder(draftID) { draftOrder ->
                val updatedLineItems = draftOrder?.line_items?.toMutableList() ?: mutableListOf()
                Log.i("TAG", "Initial updatedLineItems: $updatedLineItems")

                val itemToRemove = updatedLineItems.find { it.variant_id == id }

                if (itemToRemove != null) {
                    updatedLineItems.remove(itemToRemove)
                    Log.i("TAG", "Updated updatedLineItems after removal: $updatedLineItems")

                    val favDraftOrder = FavDraftOrder(
                        id = draftID,
                        line_items = updatedLineItems
                    )
                    val favDraftOrderResponse = FavDraftOrderResponse(favDraftOrder)

                    favoriteViewModel.updateFavorite(draftID, favDraftOrderResponse)
                    favoriteViewModel.deleteFavorite(id)
                } else {
                    Log.i("TAG", "Item not found in updatedLineItems")
                }

                if (draftOrder?.id == null) {
                    Log.i("TAG", "deleteFav: draftOrder.id is null, saving default id")
                    SharedPreference.saveDraftOrderId(requireContext(), 10000000000, email)
                } else {
                    Log.i("TAG", "deleteFav: draft order id is ${draftOrder.id}")

                    SharedPreference.saveDraftOrderId(requireContext(), draftOrder.id, email)
                }
            }
        } else {
            Log.e("DraftOrder", "Draft Order ID not found")
        }


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

        if (filteredList.isEmpty()) {
            lottieAnimationView.visibility = View.VISIBLE
            productsOfBrandAdapter.setProductsBrandsList(emptyList())
        } else {
            lottieAnimationView.visibility = View.GONE
            productsOfBrandAdapter.setProductsBrandsList(filteredList)
        }
    }


    private fun fetchDraftOrder(draftOrderId: Long, callback: (FavDraftOrder?) -> Unit) {
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
