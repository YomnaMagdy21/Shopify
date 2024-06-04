package com.example.shopify.BottomNavigationBar.OrderList

import com.example.shopify.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class OrderListFragment : Fragment() {

    private lateinit var orderListAdapter: OrderListAdapter
    private lateinit var recyclerView: RecyclerView

    private val dates = listOf("2023-05-31", "2023-06-01", "2023-06-02", "2023-06-03", "2023-06-04", "2023-05-31", "2023-06-01", "2023-06-02", "2023-06-03", "2023-06-04")
    private val prices = listOf("200.00", "150.00", "100.00", "250.00", "300.00", "200.00", "150.00", "100.00", "250.00", "300.00")
    private val currency = "EGP"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_list, container, false)
        recyclerView = view.findViewById(R.id.rv_order_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        orderListAdapter = OrderListAdapter(requireContext(), dates, prices, currency)
        recyclerView.adapter = orderListAdapter

        return view
    }
}
