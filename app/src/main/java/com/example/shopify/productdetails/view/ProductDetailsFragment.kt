package com.example.shopify.productdetails.view

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Category.view.CategoryProductsAdapter
import com.example.shopify.BottomNavigationBar.Category.view.OnCategoryClickListener
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModelFactory
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
import com.example.shopify.ShoppingCart.viewModel.PriceRuleViewModelFactory
import com.example.shopify.ShoppingCart.viewModel.ShoppingCardViewModel
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.model.productDetails.Product
import com.example.shopify.products.view.OnProductClickListener
import com.example.shopify.products.view.ProductAdapter
import com.example.shopify.products.viewModel.ProductsOfBrandViewModel
import com.example.shopify.products.viewModel.ProductsOfBrandViewModelFactory
 
import com.example.shopify.signup.viewmodel.SignUpViewModel
import com.example.shopify.signup.viewmodel.SignUpViewModelFactory

import com.example.shopify.setting.currency.CurrencyConverter
 
import com.example.shopify.utility.ApiState
import com.example.shopify.utility.SharedPreference
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random


class ProductDetailsFragment : Fragment() ,OnCategoryClickListener,OnProductClickListener, OnProductDetailsListener {

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
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var signUpViewModelFactory: SignUpViewModelFactory
    private var brandId: Long? = null

    private var isDeleteInProgress = false
    private var isFavProgress =false
    private var updatedLineItems: MutableList<ItemLine> = mutableListOf()
    lateinit var draftOrder: FavDraftOrderResponse
    lateinit var sizesAdapter: SizesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            brandId = it.getLong("brand_id")
            Log.d("ProductsOfBrandFragment", "Received ID: $brandId")
        }

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

        productDetailsViewModel = ViewModelProvider(
            this,
            productDetailsViewModelFactory
        ).get(ProductDetailsViewModel::class.java)

        val shopifyRemoteDataSource = ShopifyRemoteDataSourceImp()
        val repository = ShopifyRepositoryImp(shopifyRemoteDataSource)
        val factory = PriceRuleViewModelFactory(repository)
        shoppingCartViewModel =
            ViewModelProvider(this, factory).get(ShoppingCardViewModel::class.java)

        categoryViewModelFactory = CategoryViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        favoriteViewModelFactory = FavoriteViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        favoriteViewModel = ViewModelProvider(
            requireActivity(),
            favoriteViewModelFactory
        ).get(FavoriteViewModel::class.java)

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

        categoryAdapter = CategoryProductsAdapter(requireContext(), this, listOf())

        productsOfBrandAdapter= ProductAdapter(requireContext() ,   listOf(),this)
        sizesAdapter = SizesAdapter(requireContext(),this)

        signUpViewModelFactory = SignUpViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        signUpViewModel = ViewModelProvider(this, signUpViewModelFactory).get(SignUpViewModel::class.java)


        draftOrder = FavDraftOrderResponse()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updatedLineItems.clear()

        isFavProgress = false
        isDeleteInProgress = false


        binding.backImage.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        setUpRecyclerView()
        setUpSizesRecView()

        val bundle = arguments

        val productID = bundle?.getLong("product_id")
        productID?.let {
            productDetailsViewModel.getProductInfo(it)
        }
        var brand_ID = bundle?.getLong("brand_id")
        Log.d("ProductsOfBrandFragment", "Received Brand ID: $brand_ID")

        brand_ID?.let {
            Log.d("ProductsOfBrandFragment", "Fetching products for brand ID: $it")
            viewModel.getProductsOfBrands(it)
        }
        setUpSuggestionsRecViewFromProduct()
        Log.d("ProductsOfBrandFragment", "Called setUpSuggestionsRecViewFromProduct()")

        val categoryID = bundle?.getLong("category_id")

        if (categoryID?.toInt() != 0) {
            Log.d("ProductsOfBrandFragment", "Received Category ID: $categoryID")
            setUpSuggestionsRecView()
            Log.d("ProductsOfBrandFragment", "Called setUpSuggestionsRecView()")
        }
        var fav_id = bundle?.getLong("favorite_id")
        if (fav_id?.toInt() != 0) {
            binding.sug.visibility = View.GONE
        } else {
            binding.sug.visibility = View.VISIBLE
        }


        var email = SharedPreference.getUserEmail(requireContext())
        val draftID = SharedPreference.getDraftOrderId(requireContext(), email)


        lifecycleScope.launch {
            productDetailsViewModel.productInfo.collectLatest { result ->
                when (result) {
                    is ApiState.Loading -> {

                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ApiState.Success<*> -> {
//                        draftOrder.draft_order= (result.data as? FavDraftOrder)!!
                        binding.progressBar.visibility = View.GONE
                        var data = result.data as? ProductModel

                        //add to card
                        //var data2 = result.data as? Product


                        binding.addToCart.setOnClickListener {
                            var guest = SharedPreference.getGuest(requireContext())
                            //  var email = SharedPreference.getUserEmail(context)
                            if (guest == "yes") {
                              showAlertDialog()
                            } else {
                                Log.i("hi", "onViewCreated: hiiiiiiiiiiiiiiiiii")
                                if (data != null) {
                                    addProductToCart(data)

                                }


                            }
                        }
                        var email = SharedPreference.getUserEmail(requireContext())
                        val fav = data?.product?.variants?.get(0)?.id?.let {
                            SharedPreference.getFav(
                                requireContext(),
                                it, email
                            )
                        }
                        if (fav == true) {
                            binding.fav.setImageResource(R.drawable.favorite)
                        } else {
                            binding.fav.setImageResource(R.drawable.heart_unfilled)
                        }
                        binding.fav.setOnClickListener {
                            var guest = SharedPreference.getGuest(requireContext())
                            //  var email = SharedPreference.getUserEmail(context)
                            if (guest == "yes") {

                                showAlertDialog()
                            } else {
                                if (data != null) {
                                    addFav(data)

                                }
                            }
                        }

                        data?.product?.let { product ->
                            val sizes = product.options
                                ?.firstOrNull { it.name == "Size" }
                                ?.values
                                ?: emptyList()

                            sizesAdapter.submitList(sizes)
                        }
                        if (data?.product?.options?.get(1)?.name == "Color"){
                        //var color = data?.product?.options?.get(0)?.values?.get(0)
                        // Check if the color resource ID is valid before using it
                        var color = data?.product?.options?.get(1)?.values?.get(0)
                            Log.i("TAG", "onViewCreated: color$color")
                            Log.i("TAG", "onViewCreated: color${data?.product?.options?.get(1)?.name}")

                            if (color != null) {
                            val colorResId = requireContext().resources.getIdentifier(
                                color,
                                "color",
                                context?.packageName
                            )

                            if (colorResId != 0) {
                                binding.color.setCardBackgroundColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        colorResId
                                    )
                                )
                            } else {
                                // Handle the case where the color name is not a valid resource
                                Log.e("ProductDetailsFragment", "Invalid color resource name: $color")
                                // Optionally, set a default color
                                binding.color.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
                            }
                        } else {
                            // Optionally, set a default color if no color value is found
                            binding.color.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
                        }

                           }
                        binding.title.text = data?.product?.title
                        //convert currency
                        val convertedPrice = data?.product?.variants?.get(0)?.price?.let {
                            CurrencyConverter.convertToUSD(
                                it.toDouble()
                            )
                        }
                        binding.price.text = convertedPrice?.let {
                            CurrencyConverter.formatCurrency(
                                it
                            )
                        }
                        binding.descriptionText.text = data?.product?.body_html


                        val random = Random.nextInt(1, 5).toFloat()
                        binding.ratingBar.rating = random
                        val imageUrls = data?.product?.images?.map { it.src } ?: emptyList()

                        // Check if images are available
                        if (imageUrls.isNotEmpty()) {
                            // Create and set the adapter
                            val adapter = ImageAdapter(requireContext(), imageUrls)
                            binding.photosViewpager.adapter = adapter
                            binding.tabLayout.setupWithViewPager(
                                binding.photosViewpager,
                                true
                            )
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
                                    var myProducts = it.products
                                    categoryAdapter.updateData(it.products)
                                    //productsOfBrandAdapter.setProductsBrandsList(it.products)

                                }
                            }

                            is ApiState.Failure -> {

                            }

                            is ApiState.Loading -> {

                            }

                            else -> {}
                        }

                    }
                }
                collectProducts = listOf()

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
                                Log.i(
                                    "TAG",
                                    "onViewCreated: unexpected state: ${result::class.java.simpleName}"
                                )
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


                    val reviewsRecyclerView =
                        popupView.findViewById<RecyclerView>(R.id.reviewsRecyclerView)
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

            fun setUpSuggestionsRecView() {
                categoryAdapter = CategoryProductsAdapter(requireContext(), this, listOf())
                binding.recV.apply {
                    adapter = categoryAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

                }
            }

            fun setUpSuggestionsRecViewFromProduct() {
                productsOfBrandAdapter = ProductAdapter(requireContext(), listOf(), this)
                binding.recV.apply {
                    adapter = productsOfBrandAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

                }
            }
    fun setUpSizesRecView() {
        sizesAdapter = SizesAdapter(requireContext(),this)
        binding.sizeRecView.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        }
    }



            override fun onCategoryClick(id: Long) {
                val bundle = Bundle()
                bundle.putLong("product_id", id)
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
                            Snackbar.make(
                                requireView(),
                                "Product already in cart",
                                Snackbar.LENGTH_SHORT
                            ).show()
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
                                    Snackbar.make(
                                        requireView(),
                                        "Added to Cart",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
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
                bundle.putLong("product_id", id)
                brandId?.let { bundle.putLong("brand_id", it) }
                val fragmentDetails = ProductDetailsFragment()
                fragmentDetails.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragmentDetails)
                    .addToBackStack(null)
                    .commit()
            }

            override fun onFavBtnClick(favorite: Product) {
            }

            override fun onClickToRemove(id: Long) {

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

            fun deleteFav(id: Long) {
                var email = SharedPreference.getUserEmail(requireContext())
                var draftID = SharedPreference.getDraftOrderId(requireContext(), email)

               // updatedLineItems.clear()
                if (draftID != 10000000000) {
                    fetchDraftOrder(draftID) { draftOrder ->
                       var  updatedLineItems =
                            draftOrder?.line_items?.toMutableList() ?: mutableListOf()
                        Log.i("TAG", "Initial updatedLineItems: $updatedLineItems")

                        // Find the item to remove by matching the id (or other unique identifier)
                        val itemToRemove = updatedLineItems.find { it.variant_id == id }

                        if (itemToRemove != null) {
                            updatedLineItems.remove(itemToRemove)
                            Log.i(
                                "TAG",
                                "Updated updatedLineItems after removal: $updatedLineItems"
                            )

                            val favDraftOrder = FavDraftOrder(
                                id = draftID,
                                line_items = updatedLineItems
                            )
                            val favDraftOrderResponse = FavDraftOrderResponse(favDraftOrder)

                            favoriteViewModel.updateFavorite(draftID, favDraftOrderResponse)
                            favoriteViewModel.deleteFavorite(id)
                            if (draftOrder?.id == null) {
                                Log.i("TAG", "deleteFav: draftOrder.id is null, saving default id")
                                SharedPreference.saveDraftOrderId(
                                    requireContext(),
                                    10000000000,
                                    email
                                )
                            } else {
                                Log.i("TAG", "deleteFav: draft order id is ${draftOrder.id}")
                                // You can save the actual draftOrder id if needed
                                SharedPreference.saveDraftOrderId(
                                    requireContext(),
                                    draftOrder.id,
                                    email
                                )
                            }
                        } else {
                            Log.i("TAG", "Item not found in updatedLineItems")
                        }

                    }
                } else {
                    Log.e("DraftOrder", "Draft Order ID not found")
                }


            }

            fun addFav(product: ProductModel) {
                var email = SharedPreference.getUserEmail(requireContext())
                val isFav =
                    product.product?.variants?.get(0)?.id?.let { it1 ->
                        SharedPreference.getFav(
                            requireContext(),
                            it1,
                            email
                        )
                    }
                if (isFav == true) {
                    product.product?.variants?.get(0)?.id?.let { it1 ->
                        SharedPreference.saveFav(
                            requireContext(),
                            it1, email, false
                        )
                    }
                    product.product?.variants?.get(0)?.id?.let { it1 -> deleteFav(it1) }
                      binding.fav.setImageResource(R.drawable.heart_unfilled)
                } else {
                    product.product?.variants?.get(0)?.id?.let { it1 ->
                        SharedPreference.saveFav(
                            requireContext(),
                            it1, email, true
                        )
                    }

                    binding.fav.setImageResource(R.drawable.favorite)
                    var draftID =
                        SharedPreference.getDraftOrderId(requireContext(), email)
                    if (draftID == 10000000000) {
                        val lineItems = listOf(
                            ItemLine(
                                quantity = 1,
                                variant_id = product.product?.variants?.get(0)?.id,
                                sku = ""
                            )
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
                                        val wishList =
                                            result.data as? FavDraftOrderResponse
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
                                            Log.i("TAG", "onViewCreated: draft order in  product fragment = ${wishList?.draft_order?.id}")
                                        }
                                    }

                                    else -> {

                                    }
                                }
                            }
                        }
                    } else {
                        fetchDraftOrder(draftID) { draftOrder ->
                            updatedLineItems.clear()
                            if (draftOrder != null) {
                                val productTitle = product.product?.title
                                val productVariantId = product.product?.variants?.get(0)?.id
                                val productImageSrc = product.product?.image?.src

                                if (productTitle != null && productVariantId != null && productImageSrc != null) {
                                    // Ensure properties are correctly assigned
                                    val properties = listOf(productImageSrc)

                                    val newLineItem = ItemLine(
                                        title = productTitle,
                                        variant_id = productVariantId,
                                        quantity = 1,
                                        sku = productImageSrc
                                        // properties = properties
                                    )


                                    updatedLineItems =
                                        draftOrder?.line_items?.toMutableList()
                                            ?: mutableListOf()
                                    Log.i(
                                        "TAG",
                                        "onViewCreated: updatedLineItems111 ${updatedLineItems}"
                                    )
                                    val itemExists =
                                        updatedLineItems.any { it.variant_id == newLineItem.variant_id }

                                    if (!itemExists) {
                                        updatedLineItems.add(newLineItem)
                                        Log.i(
                                            "TAG",
                                            "onViewCreated: updatedLineItems222 ${updatedLineItems}"
                                        )
                                        Log.i(
                                            "TAG",
                                            "onViewCreated: updatedLineItems3333 ${updatedLineItems}"
                                        )


                                        val favDraftOrder = FavDraftOrder(
                                            id = draftID,
                                            line_items = updatedLineItems
                                        )
                                        val favDraftOrderResponse =
                                            FavDraftOrderResponse(favDraftOrder)


                                        favoriteViewModel.updateFavorite(
                                            draftID,
                                            favDraftOrderResponse
                                        )

                                    }

                                }
                            }
                        }
                    }


                }
            }

    override fun onSizeClick(size: String) {

    }

    fun showAlertDialog() {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.guest_alert, null)
        dialogView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val loginButton: Button = dialogView.findViewById(R.id.login)
        val cancelButton: Button = dialogView.findViewById(R.id.cancel)

        loginButton.setOnClickListener {
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.home_fragment, SignInFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


}