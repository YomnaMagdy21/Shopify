package com.example.shopify.BottomNavigationBar.Category.view


import com.example.shopify.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.category.CustomCollection
import com.example.shopify.model.category.SubCustomCollections
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.view.ProductDetailsFragment
import com.example.shopify.ShoppingCart.model.ShoppingCardRepo
import com.example.shopify.ShoppingCart.viewModel.PriceRuleViewModelFactory
import com.example.shopify.ShoppingCart.viewModel.ShoppingCardViewModel
import com.example.shopify.utility.ApiState
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


    private lateinit var shoppingCartViewModel: ShoppingCardViewModel

    private var selectedCollectionId: Long? = null
    private var selectedProductType: SubCustomCollections? = null

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


        //shopping card view model initilization
        val factory = PriceRuleViewModelFactory(ShoppingCardRepo())
        shoppingCartViewModel = ViewModelProvider(this, factory).get(ShoppingCardViewModel::class.java)

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



        return view
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

}