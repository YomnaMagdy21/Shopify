package com.example.shopify.BottomNavigationBar.Category.view


import android.content.Context

import android.app.AlertDialog

import android.net.ConnectivityManager
import android.net.NetworkInfo

import com.example.shopify.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory

import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModelFactory

import com.example.shopify.CheckNetwork.InternetStatus
import com.example.shopify.CheckNetwork.NetworkConectivityObserver
import com.example.shopify.CheckNetwork.NetworkObservation

import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Address
import com.example.shopify.model.Brands.SmartCollection
import com.example.shopify.model.Customer
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.category.CustomCollection
import com.example.shopify.model.category.SubCustomCollections
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.view.ProductDetailsFragment
import com.example.shopify.ShoppingCart.viewModel.PriceRuleViewModelFactory
import com.example.shopify.ShoppingCart.viewModel.ShoppingCardViewModel
import com.example.shopify.model.productDetails.Product
import com.example.shopify.signup.viewmodel.SignUpViewModel
import com.example.shopify.signup.viewmodel.SignUpViewModelFactory
import com.example.shopify.utility.ApiState

import com.example.shopify.utility.SharedPreference

import com.google.android.material.snackbar.Snackbar
import com.mikhaellopez.circularimageview.CircularImageView

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() , OnCategoryClickListener {

    private lateinit var tvAll: TextView
    private lateinit var tvWomen: TextView
    private lateinit var tvMen: TextView
    private lateinit var tvKids: TextView
    private lateinit var tvSale: TextView

    private lateinit var ivTShirts: CircularImageView
    private lateinit var ivShoes: CircularImageView
    private lateinit var ivAccessories: CircularImageView
    private lateinit var ivBlock: CircularImageView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryProductsAdapter

    lateinit var categoryViewModel: CategoryViewModel
    lateinit var categoryViewModelFactory: CategoryViewModelFactory
    private lateinit var progressBar: ProgressBar
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var signUpViewModelFactory: SignUpViewModelFactory



    private lateinit var shoppingCartViewModel: ShoppingCardViewModel

    private var selectedCollectionId: Long? = null
    private var selectedProductType: SubCustomCollections? = null
    private lateinit var editTextSearch: EditText

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    private lateinit var lottieAnimationView: LottieAnimationView






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        tvAll = view.findViewById(R.id.tv_main_category_all)
        tvSale = view.findViewById(R.id.tv_main_category_sale)
        tvWomen = view.findViewById(R.id.tv_main_category_women)
        tvMen = view.findViewById(R.id.tv_main_category_men)
        tvKids = view.findViewById(R.id.tv_main_category_kids)
        ivTShirts = view.findViewById(R.id.iv_sub_cat_clothes)
        ivShoes = view.findViewById(R.id.iv_sub_cat_shoes)
        ivAccessories = view.findViewById(R.id.iv_sub_cat_bags)
        ivBlock = view.findViewById(R.id.iv_sub_cat_block)
        editTextSearch = view.findViewById(R.id.search_edit_text)
        lottieAnimationView = view.findViewById(R.id.lottie_no_data3)
        lottieAnimationView.visibility = View.GONE

        recyclerView = view.findViewById(R.id.rv_products_in_category)

        progressBar = view.findViewById(R.id.progressBar2)

      
        adapter = CategoryProductsAdapter(requireContext() , this ,  listOf())/*{ product ->
            addProductToCart(product)
        }*/
       
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        categoryViewModelFactory = CategoryViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        categoryViewModel = ViewModelProvider(
            requireActivity(),
            categoryViewModelFactory
        ).get(CategoryViewModel::class.java)

        signUpViewModelFactory = SignUpViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        signUpViewModel = ViewModelProvider(this, signUpViewModelFactory).get(SignUpViewModel::class.java)



        //shopping card view model initilization
        val shopifyRemoteDataSource = ShopifyRemoteDataSourceImp()
        val repository = ShopifyRepositoryImp(shopifyRemoteDataSource)
        val factory = PriceRuleViewModelFactory(repository)
        shoppingCartViewModel = ViewModelProvider(this, factory).get(ShoppingCardViewModel::class.java)

        favoriteViewModelFactory = FavoriteViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        favoriteViewModel = ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)


        // Click listeners for sub category
        ivTShirts.setOnClickListener {
            selectedProductType = SubCustomCollections.T_SHIRTS
            selectImageView(ivTShirts)
            fetchProducts()
        }
        ivShoes.setOnClickListener {
            selectedProductType = SubCustomCollections.SHOES
            selectImageView(ivShoes)
            fetchProducts()
        }
        ivAccessories.setOnClickListener {
            selectedProductType = SubCustomCollections.ACCESSORIES
            selectImageView(ivAccessories)
            fetchProducts()
        }

        ivBlock.setOnClickListener {
            selectedProductType = null
            selectImageView(ivBlock)
            fetchProducts()
        }

        setClickListeners()
        setProductList()

        // Default selection: get all products "without any filtration"
        fetchProducts()
        setupSearch()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var email =SharedPreference.getUserEmail(requireContext())
        val draftID = SharedPreference.getDraftOrderId(requireContext(),email)

        if (draftID != 10000000000) {
            favoriteViewModel.getFavorites(draftID.toLong())
            Log.d("DraftOrder", "Draft Order ID: $draftID")
        } else {

            Log.e("DraftOrder", "Draft Order ID not found")
        }
    }
        
   

    // Click listeners for main category
    private fun setClickListeners() {
        tvAll.setOnClickListener {
            selectedCollectionId = null
            fetchProducts(view = it)
        }
        tvSale.setOnClickListener {
            selectedCollectionId = CustomCollection.SALE.id
            fetchProducts(view = it)
        }
        tvWomen.setOnClickListener {
            selectedCollectionId = CustomCollection.WOMEN.id
            fetchProducts(view = it)
        }
        tvMen.setOnClickListener {
            selectedCollectionId = CustomCollection.MEN.id
            fetchProducts(view = it)
        }
        tvKids.setOnClickListener {
            selectedCollectionId = CustomCollection.KID.id
            fetchProducts(view = it)
        }
    }


    private fun fetchProducts(collectionId: Long? = selectedCollectionId, view: View? = null) {
        view?.let { updateTextViewStyles(it) }
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        categoryViewModel.getProducts(collectionId, selectedProductType?.type)
    }

    private fun updateTextViewStyles(selectedView: View) {
        val textViews = listOf(tvAll, tvSale, tvWomen, tvMen, tvKids)
        for (textView in textViews) {
            if (textView == selectedView) {
                textView.setBackgroundResource(R.drawable.rounded_selected_text_view)
                textView.setTextColor(resources.getColor(R.color.white, null))
            } else {
                textView.setBackgroundResource(R.drawable.rounded_unselected_text_view)
                textView.setTextColor(resources.getColor(R.color.black, null))
            }
        }
    }

    

    fun setProductList() {
        lifecycleScope.launch {
            categoryViewModel.accessAllProductList.collect { result ->
                when (result) {
                    is ApiState.Success<*> -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        lottieAnimationView.visibility = View.GONE


                        val products = (result.data as? CollectProductsModel)?.products
                        if (products.isNullOrEmpty()) {
                            lottieAnimationView.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            lottieAnimationView.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            adapter.updateData(products)
                        }
                    }

                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        lottieAnimationView.visibility = View.VISIBLE
                    }

                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        lottieAnimationView.visibility = View.GONE

                    }

                }

            }
        }
    }




    private fun selectImageView(selectedImageView: ImageView) {
        val imageViews = listOf(ivTShirts, ivShoes, ivAccessories , ivBlock)
        imageViews.forEach { imageView ->
            imageView.setPadding(0, 0, 0, 0)
            imageView.setBackgroundResource(R.drawable.rounded_unselected_image_view_filter)
        }
        selectedImageView.setPadding(4, 4, 4, 4)
        selectedImageView.setBackgroundResource(R.drawable.rounded_selected_image_view_filter)
    }

    override fun onCategoryClick(id:Long) {

        val bundle = Bundle()
        bundle.putLong("product_id",id)
        bundle.putLong("category_id",id)

        val fragmentDetails = ProductDetailsFragment()
        fragmentDetails.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragmentDetails)
            .addToBackStack(null)
            .commit()
//            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.frame_layout, ProductDetailsFragment() )
//        transaction.addToBackStack(null)
//        transaction.commit()
    }

    override fun onFavBtnClick(product: Product) {

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
        }

        else {
            fetchDraftOrder(draftID) { draftOrder ->
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
                            id = draftID,
                            line_items = updatedLineItems
                        )
                        val favDraftOrderResponse = FavDraftOrderResponse(favDraftOrder)

                        Log.i(
                            "TAG",
                            "onFavBtnClick: favDraftOrderResponse${favDraftOrderResponse}"
                        )

                        favoriteViewModel.updateFavorite(
                            draftID,
                            favDraftOrderResponse
                        )
                    }

                    Log.i("TAG", "Before update: $updatedLineItems")



                } else {
                    Log.e(
                        "TAG",
                        "Product details are incomplete. Title: $productTitle, VariantId: $productVariantId, ImageSrc: $productImageSrc"
                    )
                }
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

    fun setupSearch(){
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun filterProducts(query: String) {
        lifecycleScope.launch {
            categoryViewModel.accessAllProductList.collect { result ->
                if (result is ApiState.Success<*>) {
                    val products = result.data as CollectProductsModel?
                    products?.let {
                        val filteredProducts = it.products.filter { product ->
                            product.title?.contains(query, ignoreCase = true) ?: true
                        }
                        adapter.updateData(filteredProducts)
                    }
                }
            }
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
















