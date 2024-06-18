package com.example.shopify.BottomNavigationBar.OrderList.view

import com.example.shopify.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.OrderList.viewModel.OrderListViewModel
import com.example.shopify.BottomNavigationBar.OrderList.viewModel.OrderListViewModelFactory
import com.example.shopify.BottomNavigationBar.orderItem.view.OrderItemFragment
import com.example.shopify.Models.orderList.RetriveOrderModel
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.launch

class OrderListFragment : Fragment(), OnOrderClickListener {

    private lateinit var orderListAdapter: OrderListAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var orderListViewModel: OrderListViewModel
    private lateinit var factory: OrderListViewModelFactory
    private val currency = "EGP"
    private lateinit var progressBar: ProgressBar
    lateinit var backImage : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_list, container, false)
        recyclerView = view.findViewById(R.id.rv_order_list)
        progressBar = view.findViewById(R.id.progressBar3)
        backImage = view.findViewById(R.id.iv_backRow)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        factory = OrderListViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        orderListViewModel = ViewModelProvider(
            requireActivity(),
            factory
        ).get(OrderListViewModel::class.java)

        orderListAdapter = OrderListAdapter(requireContext(), emptyList(), currency, this)
        recyclerView.adapter = orderListAdapter

        backImage.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            orderListViewModel.accessOrderList.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                    is ApiState.Success<*> -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        val orders = (it.data as RetriveOrderModel).orders
                        if (orders.isNullOrEmpty()) {
                            println("No orders available")
                        } else {
                            println("Orders retrieved successfully")
                            orders.forEach { order ->
                                println("Order ID: ${order.id}, Created At: ${order.created_at}, Total Price: ${order.total_price} ")
                                println(" phone : ${order.shipping_address?.phone} , address : ${order.shipping_address?.address1}")

                                println("city: ${order.shipping_address?.address2}")
                                println("country: ${order.shipping_address?.city}")
                                println("phone: ${order.shipping_address?.company}")

                                println("Created At: ${order.created_at}")
                                println("Total Price: ${order.total_price}")
                                println("first name: ${order.customer?.first_name}")
                                println("last name: ${order.customer?.last_name}")
                                println("Payment Method: ${order.tags}")

                                println("Line Items:")
                                order.line_items?.forEachIndexed { index, item ->
                                    println("Item ${index + 1}: ${item.name}, Quantity: ${item.quantity}, Variant ID: ${item.variant_id}")
                                }


                                println("In Orders List-----------------------------------------------")
                                //  note attributes
                                Log.d("OrderListFragment", "Note Attributes:")
                                order.note_attributes?.forEach { noteAttribute ->
                                    Log.d("OrderListFragment", "Name: ${noteAttribute.name}, Value: ${noteAttribute.values}")
                                }
                            }
                        }
                        orderListAdapter.updateData(orders)
                    }
                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.GONE

                    }
                }
            }
        }

        orderListViewModel.getOrders()

        return view
    }

    override fun onOrderClick(order: Order) {
        val newFragment = OrderItemFragment()
        val bundle = Bundle().apply {
            putSerializable("order", order)
        }
        newFragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, newFragment)
            .addToBackStack(null)
            .commit()
    }
}
