package com.example.shopify.BottomNavigationBar.Category.view


import android.content.Context
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
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModelFactory
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Address
import com.example.shopify.model.Brands.SmartCollection
import com.example.shopify.model.Customer
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.category.CustomCollection
import com.example.shopify.model.category.SubCustomCollections
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.view.ProductDetailsFragment
import com.example.shopify.ShoppingCart.model.ShoppingCardRepo
import com.example.shopify.ShoppingCart.viewModel.PriceRuleViewModelFactory
import com.example.shopify.ShoppingCart.viewModel.ShoppingCardViewModel
import com.example.shopify.model.productDetails.Product
import com.example.shopify.signup.viewmodel.SignUpViewModel
import com.example.shopify.signup.viewmodel.SignUpViewModelFactory
import com.example.shopify.utility.ApiState
import com.example.shopify.utility.SharedPreference
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() , OnCategoryClickListener {

    private lateinit var tvAll: TextView
    private lateinit var tvWomen: TextView
    private lateinit var tvMen: TextView
    private lateinit var tvKids: TextView
    private lateinit var tvSale: TextView

    private lateinit var ivTShirts: ImageView
    private lateinit var ivShoes: ImageView
    private lateinit var ivAccessories: ImageView
    private lateinit var ivBlock: ImageView

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
        val factory = PriceRuleViewModelFactory(ShoppingCardRepo())
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
            favoriteViewModel.getFavorites(draftID.toLong())// Use the draftOrderId as needed
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

    
//    private fun addProductToCart(product: Product) {
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        val userEmail = currentUser?.email
//
//        Log.d("AddToCart", "Attempting to add product to cart: $product")
//
//        if (currentUser != null) {
//            val variantId = product.variants?.get(0)?.id
//            if (variantId != null && !categoryViewModel.addedProductIds.contains(variantId)) {
//                Log.d("AddToCart", "Product not already in cart. Proceeding to add.")
//                var order = DraftOrder()
//                order.email = userEmail
//                var draft_orders = DraftOrderResponse()
//                order.note = "cart"
//                var lineItems = LineItem()
//                lineItems.quantity = 1
//                lineItems.variant_id = product.variants!![0].id
//                order.line_items = listOf(lineItems)
//                var note_attribute = NoteAttribute()
//                note_attribute.name = "image"
//                note_attribute.value = product.images!![0].src
//                order.note_attributes = listOf(note_attribute)
//                draft_orders = DraftOrderResponse(order)
//
//
//                Log.d("DraftOrder", "Creating Draft Order: $draft_orders")
//
//                shoppingCartViewModel.createDraftOrder(draft_orders)
//
//                lifecycleScope.launch {
//                    shoppingCartViewModel.draftOrderResponse.collect { draftOrderResponse ->
//                        if (draftOrderResponse != null) {
//                            //add the id
//                            variantId.let { categoryViewModel.addedProductIds.add(it) }
//                            Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_LONG)
//                                .show()
//                        } else {
//                            Toast.makeText(
//                                requireContext(),
//                                "Failed to add to cart",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    }
//                }
//            }
//        } else {
//            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
//        }
//    }

    fun setProductList() {
        lifecycleScope.launch {
            categoryViewModel.accessAllProductList.collect { result ->
                when (result) {
                    is ApiState.Success<*> -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE


                        var products = result.data as CollectProductsModel?
                        products?.let {
                            adapter.updateData(it.products)
                        }
                    }

                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.GONE

                    }

                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE

                    }

                }

            }
        }
    }




    private fun selectImageView(selectedImageView: ImageView) {
        val imageViews = listOf(ivTShirts, ivShoes, ivAccessories , ivBlock)
        imageViews.forEach { imageView ->
            imageView.setBackgroundResource(R.drawable.rounded_unselected_image_view_filter)
        }

        selectedImageView.setBackgroundResource(R.drawable.rounded_selected_image_view_filter)
    }

    override fun onCategoryClick(id:Long) {

        val bundle = Bundle()
        bundle.putLong("product_id",id)
        bundle.putLong("category_id",id)
       /// bundle.putString("type",selectedProductType?.type)
//        selectedProductType?.type?.let {
//            SharedPreference.saveCollectionType(requireContext(),id,
//                it
//            )
//        }

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
//        selectedProductType?.type?.let {
//            product.id?.let { it1 ->
//                SharedPreference.saveCollectionType(requireContext(), it1,
//                    it
//                )
//            }
//        }
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
        }

//                val sharedPreferences = requireContext().getSharedPreferences("draftPref", Context.MODE_PRIVATE)
//                val draftOrderId = sharedPreferences.getString("draft_order_id", null)?.toLong()
//
//                if (draftOrderId != null) {
        else {
            fetchDraftOrder(draftID) { draftOrder ->
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
//        else {
//            Log.e("DraftOrder", "Draft Order ID not found")
//        }



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
                    favoriteViewModel.deleteFavorite(id)
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
}