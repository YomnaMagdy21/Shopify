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

    override fun onFavBtnClick(product: Product) {
        //   addProductToFav(product)
        //  binding.fav.setImageResource(R.drawable.favorite)
        product.isFav = true
//        val editor = sharedPreferencesFav.edit()
//        editor.putBoolean(product.id.toString(), true)
//        editor.apply()
        // product.id?.let { productsOfBrandAdapter.updateFavoriteState(it, true) }
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
//                            val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
//                            val editor = sharedPreferences.edit()
//                            editor.putString("draft_order_id", wishList?.draft_order?.id.toString())
//                            editor.apply()
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
//                    val sharedPreferences =
//                        requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
//                    val draftOrderId = sharedPreferences.getString("draft_order_id", null)?.toLong()
//
//                    if (draftOrderId != null) {
                        fetchDraftOrder(draftID.toLong()) { draftOrder ->
                            val productTitle = product.title
                            val productVariantId = product.variants?.get(0)?.id
                            val productImageSrc = product.image?.src

                            // Log product details for verification
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


                                // Log the new line item creation
                                Log.i("TAG", "New Line Item: $newLineItem")

                                // Check if the item with this variant_id already exists in the draft order
                                val updatedLineItems =
                                    draftOrder?.line_items?.toMutableList() ?: mutableListOf()
//                            val existingItemIndex = updatedLineItems.indexOfFirst { it.variant_id == productVariantId }
//
//                            if (existingItemIndex != -1) {
//                                // Update the existing item's SKU
//                                updatedLineItems[existingItemIndex] = updatedLineItems[existingItemIndex].copy(sku = productImageSrc)
//                            } else {
//                                // Add the new item if it doesn't already exist
//                                updatedLineItems.add(newLineItem)
//                            }

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


//                            // Now create a new draft order response with updated line items
//                            val updatedDraftOrder = FavDraftOrderResponse(
//                                draft_order = FavDraftOrder(
//                                    id = draftOrderId,
//                                    line_items = updatedLineItems
//                                )
//                            )
////                            var items = Items(
////                                title = productTitle,
////                                variant_id = updatedLineItems.get(0).variant_id,
////                                quantity = 1,
////                                sku = productImageSrc
////                            )
//                            Log.i("TAG", "After update: $updatedLineItems")
//                            Log.i("TAG", "onFavBtnClick: sku ${updatedLineItems.get(0).sku}")
//                            // Update the draft order using your ViewModel or Shopify API
//                            favoriteViewModel.updateFavorite(draftOrderId, updatedDraftOrder)

                                // Log the updated draft order response for verification
                                //    Log.i("TAG", "Fav Draft Order Response: $updatedDraftOrder")
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
//        val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
//        val draftOrderId = sharedPreferences.getString("draft_order_id", null)?.toLong()
//
//        if (draftOrderId != null) {
        var email = SharedPreference.getUserEmail(requireContext())
        var draftID = SharedPreference.getDraftOrderId(requireContext(),email)

        if (draftID != 10000000000) {
            fetchDraftOrder(draftID) { draftOrder ->
                val updatedLineItems = draftOrder?.line_items?.toMutableList() ?: mutableListOf()
                Log.i("TAG", "Initial updatedLineItems: $updatedLineItems")

                // Find the item to remove by matching the id (or other unique identifier)
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
                } else {
                    Log.i("TAG", "Item not found in updatedLineItems")
                }

                if (draftOrder?.id == null) {
                    Log.i("TAG", "deleteFav: draftOrder.id is null, saving default id")
                    SharedPreference.saveDraftOrderId(requireContext(), 10000000000, email)
                } else {
                    Log.i("TAG", "deleteFav: draft order id is ${draftOrder.id}")
                    // You can save the actual draftOrder id if needed
                    SharedPreference.saveDraftOrderId(requireContext(), draftOrder.id, email)
                }
            }
        } else {
            Log.e("DraftOrder", "Draft Order ID not found")
        }

        favoriteViewModel.deleteFavorite(id)
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

              //  var order = FavDraftOrder()
               // order.email = userEmail

               // order.note = "fav"
                var order = DraftOrder()
                order.email = userEmail
                var draft_orders = DraftOrderResponse()
                order.note = "fav"
                var lineItems = LineItem()
                lineItems.quantity = 1
                lineItems.variant_id = product.variants!![0].id
                order.line_items = listOf(lineItems)
                var note_attribute = NoteAttribute()
                note_attribute.name = "image"
                note_attribute.value = product.images!![0].src
                order.note_attributes = listOf(note_attribute)



                Log.d("DraftOrder", "Creating Draft Order: $draft_orders")

               // favoriteViewModel.createFavDraftOrders(draft_orders)

                val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
                val draftOrderId = sharedPreferences.getString("draft_order_id", null)

                if (draftOrderId != null) {
                 //   favoriteViewModel.updateFavorite(draftOrderId.toLong(),draft_orders)
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
    fun deleteFav(id: Long){
        val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
        val draftOrderId = sharedPreferences.getString("draft_order_id", null)?.toLong()

        if (draftOrderId != null) {
            fetchDraftOrder(draftOrderId) { draftOrder ->
                val updatedLineItems = draftOrder?.line_items?.toMutableList() ?: mutableListOf()
                Log.i("TAG", "Initial updatedLineItems: $updatedLineItems")

                // Find the item to remove by matching the id (or other unique identifier)
                val itemToRemove = updatedLineItems.find { it.variant_id == id }

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

}
