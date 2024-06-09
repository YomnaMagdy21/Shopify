package com.example.shopify.BottomNavigationBar.Category.view


import com.example.shopify.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Address
import com.example.shopify.model.Customer
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.LineItem
import com.example.shopify.model.draftModel.NoteAttribute
import com.example.shopify.model.productDetails.Product
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.productdetails.view.ProductDetailsFragment
import com.example.shopify.shoppingCard.view.model.ShoppingCardRepo
import com.example.shopify.shoppingCard.view.viewModel.PriceRuleViewModelFactory
import com.example.shopify.shoppingCard.view.viewModel.ShoppingCardViewModel
import com.example.shopify.utility.ApiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() , OnCategoryClickListener {

    private lateinit var tvAll: TextView
    private lateinit var tvWomen: TextView
    private lateinit var tvMen: TextView
    private lateinit var tvKids: TextView
    private lateinit var ivClothes: ImageView
    private lateinit var ivShoes: ImageView
    private lateinit var ivBags: ImageView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryProductsAdapter

    lateinit var myProducts: List<Product>
    lateinit var categoryViewModel: CategoryViewModel
    lateinit var categoryViewModelFactory: CategoryViewModelFactory
    private lateinit var progressBar: ProgressBar

    private lateinit var shoppingCartViewModel: ShoppingCardViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        tvAll = view.findViewById(R.id.tv_main_category_all)
        tvWomen = view.findViewById(R.id.tv_main_category_women)
        tvMen = view.findViewById(R.id.tv_main_category_men)
        tvKids = view.findViewById(R.id.tv_main_category_kids)
        ivClothes = view.findViewById(R.id.iv_sub_cat_clothes)
        ivShoes = view.findViewById(R.id.iv_sub_cat_shoes)
        ivBags = view.findViewById(R.id.iv_sub_cat_bags)

        recyclerView = view.findViewById(R.id.rv_products_in_category)

        progressBar = view.findViewById(R.id.progressBar2)


        // Click listeners for TextViews
        tvAll.setOnClickListener { selectCategory(tvAll) }
        tvWomen.setOnClickListener { selectCategory(tvWomen) }
        tvMen.setOnClickListener { selectCategory(tvMen) }
        tvKids.setOnClickListener { selectCategory(tvKids) }

        // Click listeners for ImageViews
        ivClothes.setOnClickListener { selectImageView(ivClothes) }
        ivShoes.setOnClickListener { selectImageView(ivShoes) }
        ivBags.setOnClickListener { selectImageView(ivBags) }

        // Default selections
        selectCategory(tvAll)
        selectImageView(ivClothes)


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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        myProducts = listOf()
        adapter = CategoryProductsAdapter(requireContext() , this ,  listOf()){ product ->
            addProductToCart(product)
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter
        setProductList()
        categoryViewModel.getAllProducts()


    }

    //still we need to check not to add twic and to choose the size and color
    private fun addProductToCart(product: Product) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        if (currentUser != null) {

            var order = DraftOrder()
            order.email = userEmail
            var draft_orders = DraftOrderResponse()
            order.note = "cart"
            var lineItems = LineItem()
            lineItems.quantity = 1
            lineItems.variant_id = product.variants!![0].id
            order.line_items = listOf(lineItems)
            var note_attribute = NoteAttribute()
            note_attribute.name = "image"
            note_attribute.value = product.images!![0].src
            order.note_attributes = listOf(note_attribute)
            draft_orders = DraftOrderResponse(order)


            Log.d("DraftOrder", "Creating Draft Order: $draft_orders")

            shoppingCartViewModel.createDraftOrder(draft_orders)

            lifecycleScope.launch {
                shoppingCartViewModel.draftOrderResponse.collect { draftOrderResponse ->
                    if (draftOrderResponse != null) {
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    fun setProductList() {
        lifecycleScope.launch {
            categoryViewModel.accessAllProductList.collect { result ->
                when (result) {
                    is ApiState.Success<*> -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE


                        var products = result.data as CollectProductsModel?
                        products?.let {
                            myProducts = it.products
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


    private fun selectCategory(selectedTextView: TextView) {
        val categories = listOf(tvAll, tvWomen, tvMen, tvKids)
        categories.forEach { textView ->
            textView.setBackgroundResource(R.drawable.rounded_unselected_text_view)
            textView.setTextColor(resources.getColor(R.color.black))
        }

        selectedTextView.setBackgroundResource(R.drawable.rounded_selected_text_view)
        selectedTextView.setTextColor(resources.getColor(R.color.white))
    }

    private fun selectImageView(selectedImageView: ImageView) {
        val imageViews = listOf(ivClothes, ivShoes, ivBags)
        imageViews.forEach { imageView ->
            imageView.setBackgroundResource(R.drawable.rounded_unselected_image_view_filter)
        }

        selectedImageView.setBackgroundResource(R.drawable.rounded_selected_image_view_filter)
    }

    override fun onCategoryClick() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, ProductDetailsFragment() )
        transaction.addToBackStack(null)
        transaction.commit()
    }

}