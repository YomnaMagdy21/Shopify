package com.example.shopify.shoppingCard.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.network.RetrofitHelper
import com.example.shopify.payment.paymentFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class shoppingCardFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            Item("Product 1", "10.99 EGP", 1, R.drawable.tshirt),
            Item("Product 2", "20.99 EGP", 2, R.drawable.tshirt),
            Item("Product 3", "30.99 EGP", 3, R.drawable.tshirt)
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

        getDiscountCodeCount()
    }

    private fun getDiscountCodeCount() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitHelper.apiService.getDiscountCodes()
                }
                Toast.makeText(context, "Discount Code Count: ${response}", Toast.LENGTH_LONG)
                    .show()
                Log.i("dis", "getDiscountCodeCount: " + response)
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.i("dis", "getDiscountCodeCount: "+"Error: ${e.message}")
            }
        }
    }
}