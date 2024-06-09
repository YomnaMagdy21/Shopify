package com.example.shopify.shoppingCard.view

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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.network.RetrofitHelper
import com.example.shopify.payment.paymentFragment
import com.example.shopify.shoppingCard.view.model.ShoppingCardRepo
import com.example.shopify.shoppingCard.view.viewModel.PriceRuleViewModelFactory
import com.example.shopify.shoppingCard.view.viewModel.ShoppingCardViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class shoppingCardFragment : Fragment() {

    private lateinit var viewModel: ShoppingCardViewModel
    private lateinit var adapter: ShoppingCardAdapter
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
        adapter = ShoppingCardAdapter(emptyList())
        recyclerView.adapter = adapter

        //cards
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email.toString()
        viewModel.getDraftOrders(userEmail)

        lifecycleScope.launch {
            viewModel.draftOrderList.collectLatest { draftOrders ->
                val items = draftOrders?.map { draftOrder ->
                    Item(
                        title = draftOrder.line_items?.get(0)?.title ?: "No Name",
                        price = draftOrder.total_price ?: "0.0",
                        numberOfItems = draftOrder.line_items?.sumOf {
                            it.quantity ?: 0
                        } ?: 0,
                        imageResId = R.drawable.tshirt
                    )
                } ?: emptyList()
                adapter.updateItems(items)
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
        if (matchingRule != null) {
            textView.text = "Valid"
            textView.setTextColor(Color.GREEN)
        } else {
            textView.text = "Invalid"
            textView.setTextColor(Color.RED)
        }
    }

}