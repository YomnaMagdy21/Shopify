package com.example.shopify.BottomNavigationBar.orderItem.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.orderItem.viewModel.OrderItemViewModel
import com.example.shopify.BottomNavigationBar.orderItem.viewModel.OrderItemViewModelFactory
import com.example.shopify.R
import com.example.shopify.model.PostOrders.Order


class OrderItemFragment : Fragment() {

    lateinit var  clientName : TextView
    lateinit var  address : TextView
    lateinit var  phoneNumber : TextView
    lateinit var  totalPrice : TextView
    lateinit var  paymentMethod : TextView

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

        // get data passed from OrderList
        val order = arguments?.getSerializable("order") as Order

        factory = OrderItemViewModelFactory(order)

        orderItemViewModel = ViewModelProvider(this, factory).get(OrderItemViewModel::class.java)


        // Bind data to the UI
        clientName.text = orderItemViewModel.customerName
        address.text = orderItemViewModel.address
        phoneNumber.text = orderItemViewModel.phoneNumber
        totalPrice.text = orderItemViewModel.totalPrice


        val imageUrls = order.note_attributes?.filter { it.name == "image" }?.map { it.value ?: "" } ?: emptyList()

        orderItemAdapter = OrderItemAdapter(requireContext(), orderItemViewModel.lineItems , imageUrls)
        print("imagggggggge : $imageUrls")
        recyclerView.adapter = orderItemAdapter



        return view
    }

}