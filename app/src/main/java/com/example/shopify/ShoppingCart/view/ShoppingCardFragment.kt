package com.example.shopify.ShoppingCart.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModel
import com.example.shopify.BottomNavigationBar.Category.viewModel.CategoryViewModelFactory
import com.example.shopify.R
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.payment.paymentFragment
import com.example.shopify.ShoppingCart.model.PriceRule
import com.example.shopify.ShoppingCart.model.ShoppingCardRepo
import com.example.shopify.ShoppingCart.viewModel.PriceRuleViewModelFactory
import com.example.shopify.ShoppingCart.viewModel.ShoppingCardViewModel
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.productDetails.Product
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.setting.currency.CurrencyConverter
import com.example.shopify.utility.ApiState
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class shoppingCardFragment : Fragment() {

    private lateinit var viewModel: ShoppingCardViewModel
    private lateinit var adapter: ShoppingCardAdapter
    private lateinit var products: MutableList<DraftOrder>
    private lateinit var totalPriceTextView: TextView
    private lateinit var discountText: TextView

    private var discountAmount: Double = 0.0
    private var couponApplied = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = PriceRuleViewModelFactory(ShoppingCardRepo())
        viewModel = ViewModelProvider(this, factory).get(ShoppingCardViewModel::class.java)
        viewModel.fetchPriceRules()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCardList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        products = mutableListOf()
        adapter = ShoppingCardAdapter(emptyList(),::onAddProduct, ::onRemoveProduct)
        recyclerView.adapter = adapter
        totalPriceTextView = view.findViewById(R.id.textView3)
        discountText = view.findViewById(R.id.textView7)

        //cards
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email.toString()
        viewModel.getDraftOrders(userEmail)

        //update card list
        lifecycleScope.launch {
            viewModel.draftOrderList.collectLatest { draftOrders ->
                products.clear()
                if (draftOrders != null) {
                    products.addAll(draftOrders)
                    val items = draftOrders.map { draftOrder ->
                        val imageUrl = draftOrder.note_attributes?.find { it.name == "image" }?.value ?: ""
                        Item(
                            title = draftOrder.line_items?.get(0)?.title ?: "No Name",
                            price = draftOrder.total_price ?: "0.0",
                            numberOfItems = draftOrder.line_items?.sumOf {
                                it.quantity ?: 0
                            } ?: 0,
                            imageUrl = imageUrl
                        )
                    }
                    adapter.updateItems(items)
                    calculateTotalPrice(draftOrders)
                }
            }
        }

       //navigationg to checkout fragment
        val checkOut = view.findViewById<Button>(R.id.checkOutButton)
        checkOut.setOnClickListener {
            val newFragment = paymentFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }

        //coupone validation
        val editText = view.findViewById<EditText>(R.id.editTextText)
        val applyButton = view.findViewById<Button>(R.id.applyButton)
        val textView = view.findViewById<TextView>(R.id.textView8)

        applyButton.setOnClickListener {
            val inputText = editText.text.toString()
            if (inputText.isNotEmpty()) {
                validateCoupon(inputText, textView)
            }
        }

    }

    private fun validateCoupon(coupon: String, textView: TextView) {
        val matchingRule = viewModel.validateCoupon(coupon)
        if (matchingRule != null && !couponApplied) {
            applyDiscount(calculateTotalWithoutDiscount(products), matchingRule)
            couponApplied = true
            textView.text = "Valid"
            textView.setTextColor(Color.GREEN)
            calculateTotalPrice(products)
        } else if (couponApplied) {
            Snackbar.make(requireView(), "Coupon already applied", Snackbar.LENGTH_SHORT).show()
        } else {
            textView.text = "Invalid"
            textView.setTextColor(Color.RED)
        }
    }

    private fun onAddProduct(item: Item) {
        val draftOrder = products.find { it.line_items?.get(0)?.title == item.title }
        if (draftOrder != null) {
            val lineItem = draftOrder.line_items?.get(0)
            if (lineItem != null) {
                val currentQuantity = lineItem.quantity ?: 0

                if (currentQuantity >= 5) {
                    Snackbar.make(requireView(), "You reached your limit!!.", Snackbar.LENGTH_SHORT).show()
                    return
                }

                val updatedDraftOrder = draftOrder.copy().apply {
                    line_items?.get(0)?.quantity = currentQuantity + 1
                }

                lifecycleScope.launch {
                    viewModel.updateDraftOrder(updatedDraftOrder.id.toString(), DraftOrderResponse(updatedDraftOrder))
                    viewModel.draftOrderResponse.collectLatest { response ->
                        if (response != null) {
                            calculateTotalPrice(products)
                            Log.i("ShoppingCardFragment", "Draft order updated: $response")
                        } else {
                            Log.e("ShoppingCardFragment", "Failed to update draft order")
                        }
                    }
                }
            }
        }
    }





    private fun onRemoveProduct(item: Item) {
        val draftOrder = products.find { it.line_items?.get(0)?.title == item.title }
        if (draftOrder != null) {
            val updatedDraftOrder = draftOrder.copy().apply {
                line_items?.get(0)?.quantity = line_items?.get(0)?.quantity?.minus(1)
            }
            lifecycleScope.launch {
                if (updatedDraftOrder.line_items?.get(0)?.quantity ?: 0 < 1) {
                    viewModel.deleteDraftOrder(updatedDraftOrder.id.toString())
                    viewModel.deleteDraftOrderList.collectLatest { response ->
                        if (response != null) {
                            products.remove(draftOrder)
                            calculateTotalPrice(products)
                            Log.i("ShoppingCardFragment", "Draft order deleted: $response")
                        } else {
                            Log.e("ShoppingCardFragment", "Failed to delete draft order")
                        }
                    }
                } else {
                    viewModel.updateDraftOrder(updatedDraftOrder.id.toString(), DraftOrderResponse(updatedDraftOrder))
                    viewModel.draftOrderResponse.collectLatest { response ->
                        if (response != null) {
                            calculateTotalPrice(products)
                            Log.i("ShoppingCardFragment", "Draft order updated: $response")
                        } else {
                            Log.e("ShoppingCardFragment", "Failed to update draft order")
                        }
                    }
                }
            }
        }
    }

    private fun applyDiscount(totalPrice: Double, priceRule: PriceRule) {
        val discountPercentage = priceRule.value.toDouble() / 100
        discountAmount = totalPrice * discountPercentage
        val convertedDiscountAmount = CurrencyConverter.convertToUSD(discountAmount)
        discountText.text =  "${"%.2f".format(convertedDiscountAmount)}"
        Log.i("discount", "applyDiscount: "+discountAmount)
    }

    private fun calculateTotalWithoutDiscount(items: List<DraftOrder>): Double {
        return items.sumOf { it.total_price?.toDouble() ?: 0.0 }
    }

    private fun calculateTotalPrice(items: List<DraftOrder>) {
        var totalPrice = calculateTotalWithoutDiscount(items)
        if (couponApplied) {
            totalPrice += discountAmount
        }
        val convertedTotalPrice = CurrencyConverter.convertToUSD(totalPrice)
        totalPriceTextView.text = CurrencyConverter.formatCurrency(convertedTotalPrice)
        //totalPriceTextView.text = "${"%.2f".format(convertedTotalPrice)}"
    }


}