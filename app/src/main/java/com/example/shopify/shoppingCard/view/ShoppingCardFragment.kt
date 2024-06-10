package com.example.shopify.shoppingCard.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.payment.paymentFragment
import com.example.shopify.shoppingCard.view.model.ShoppingCardRepo
import com.example.shopify.shoppingCard.view.viewModel.PriceRuleViewModelFactory
import com.example.shopify.shoppingCard.view.viewModel.ShoppingCardViewModel


class shoppingCardFragment : Fragment() {

    private lateinit var viewModel: ShoppingCardViewModel
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

        val items = listOf(
            Item("Product 1", "10.99 EGP", 1, R.drawable.clothes2222),
            Item("Product 2", "20.99 EGP", 2, R.drawable.clothes2222),
            Item("Product 3", "30.99 EGP", 3, R.drawable.clothes2222)
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCardList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ShoppingCardAdapter(items)

        val checkOut = view.findViewById<Button>(R.id.checkOutButton)
        checkOut.setOnClickListener {
            val newFragment = paymentFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }
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