package com.example.shopify.BottomNavigationBar.orderItem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R


class OrderItemFragment : Fragment() {

    private val names = listOf("Product1", "Product2", "Product3", "Product4", "Product5", "Product6")
    private val prices = listOf("20.00 EGP", "50.00 EGP", "80.00 EGP", "60.00 EGP", "20.00 EGP", "50.00 EGP")
    lateinit var  clientName : TextView
    lateinit var  address : TextView
    lateinit var  phoneNumber : TextView
    lateinit var  totalPrice : TextView
    lateinit var  paymentMethod : TextView


    private lateinit var orderItemAdapter: OrderItemAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_item, container, false)
        recyclerView = view.findViewById(R.id.rv_order_items)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        orderItemAdapter = OrderItemAdapter(requireContext(), names, prices)
        recyclerView.adapter = orderItemAdapter

        clientName = view.findViewById(R.id.client_name_value)
        address = view.findViewById(R.id.client_address_value)
        phoneNumber = view.findViewById(R.id.client_phone_value)
        totalPrice = view.findViewById(R.id.client_total_price_value)
        paymentMethod = view.findViewById(R.id.client_payment_method_value)


        clientName.text = "Yomna Maagdy"
        address.text = "Shatbi Alexandria"
        phoneNumber.text = "01550559172"
        totalPrice.text = "3500.00 EGP"
        paymentMethod.text = "Visa"



        return view
    }

}