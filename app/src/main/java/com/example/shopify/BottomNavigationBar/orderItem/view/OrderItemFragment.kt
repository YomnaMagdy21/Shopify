package com.example.shopify.BottomNavigationBar.orderItem.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.orderItem.viewModel.OrderItemViewModel
import com.example.shopify.BottomNavigationBar.orderItem.viewModel.OrderItemViewModelFactory
import com.example.shopify.R
import com.example.shopify.model.PostOrders.NoteAttribute
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.utility.SharedPreference


class OrderItemFragment : Fragment() {

    lateinit var  clientName : TextView
    lateinit var  address : TextView
    lateinit var  phoneNumber : TextView
    lateinit var  totalPrice : TextView
    lateinit var  paymentMethod : TextView
    lateinit var backImage : ImageView
    private lateinit var orderItemViewModel: OrderItemViewModel
    private lateinit var factory: OrderItemViewModelFactory

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


        clientName = view.findViewById(R.id.client_name_value)
        address = view.findViewById(R.id.client_address_value)
        phoneNumber = view.findViewById(R.id.client_phone_value)
        totalPrice = view.findViewById(R.id.client_total_price_value)
        paymentMethod = view.findViewById(R.id.client_payment_method_value)
        backImage = view.findViewById(R.id.iv_backRow)

        // get data passed from OrderList
        val order = arguments?.getSerializable("order") as Order

        factory = OrderItemViewModelFactory(order)

        orderItemViewModel = ViewModelProvider(this, factory).get(OrderItemViewModel::class.java)

        backImage.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val rightBack = view.findViewById<ImageView>(R.id.rightImage)
        rightBack.visibility = View.GONE

        var language = SharedPreference.getLanguage(requireContext())
        if(language == "ar"){
            backImage.visibility = View.GONE
            rightBack.visibility = View.VISIBLE
            rightBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        println("Order Items :-------------------")
        println("user name : ${orderItemViewModel.customerName}")
        println("address : ${orderItemViewModel.address}")
        println("phone Number : ${orderItemViewModel.phoneNumber}")
        println("total Price : ${orderItemViewModel.totalPrice}")
        println("payment Method : ${orderItemViewModel.paymentMethod}")





        // Bind data to the UI
        clientName.text = orderItemViewModel.customerName
        address.text = orderItemViewModel.address
        phoneNumber.text = orderItemViewModel.phoneNumber
        totalPrice.text = orderItemViewModel.totalPrice
        paymentMethod.text = orderItemViewModel.paymentMethod


       // val noteAttributes = order.note_attributes ?: emptyList()


        orderItemAdapter = OrderItemAdapter(requireContext(), orderItemViewModel.lineItems /*, noteAttributes*/)

        println("Order Items")

        println("ID: ${order.id}")
        println("Address1: ${order.shipping_address?.address1}")
        println("city: ${order.shipping_address?.address2}")
        println("country: ${order.shipping_address?.city}")
        println("phone: ${order.shipping_address?.company}")

        println("Created At: ${order.created_at}")
        println("Total Price: ${order.total_price}")
        println("first name: ${order.customer?.first_name}")
        println("last name: ${order.customer?.last_name}")
        println("Payment Method: ${order.tags}")
        println("Note Attributes:")
//        noteAttributes.forEach { noteAttribute ->
//            println("Name: ${noteAttribute.name}, Value: ${noteAttribute.values}")
//        }
        recyclerView.adapter = orderItemAdapter



        return view
    }

}