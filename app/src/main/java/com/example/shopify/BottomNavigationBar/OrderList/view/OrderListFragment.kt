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
import com.airbnb.lottie.LottieAnimationView
import com.example.shopify.BottomNavigationBar.OrderList.viewModel.OrderListViewModel
import com.example.shopify.BottomNavigationBar.OrderList.viewModel.OrderListViewModelFactory
import com.example.shopify.BottomNavigationBar.orderItem.view.OrderItemFragment
import com.example.shopify.Models.orderList.RetriveOrderModel
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.utility.ApiState
import com.example.shopify.utility.SharedPreference
import kotlinx.coroutines.launch

class OrderListFragment : Fragment(), OnOrderClickListener {

    private lateinit var orderListAdapter: OrderListAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var orderListViewModel: OrderListViewModel
    private lateinit var factory: OrderListViewModelFactory
    private val currency = "EGP"
    private lateinit var progressBar: ProgressBar
    lateinit var backImage : ImageView
    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_list, container, false)
        recyclerView = view.findViewById(R.id.rv_order_list)
        progressBar = view.findViewById(R.id.progressBar3)
        backImage = view.findViewById(R.id.iv_backRow)
        lottieAnimationView = view.findViewById(R.id.lottie_no_data2)

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


        viewLifecycleOwner.lifecycleScope.launch {
            orderListViewModel.accessOrderList.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        lottieAnimationView.visibility = View.GONE
                    }
                    is ApiState.Success<*> -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        lottieAnimationView.visibility = View.GONE

                        val orders = (it.data as List<Order>)
                        if (orders.size==0){
                            println("No orders available")
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.GONE
                            lottieAnimationView.visibility = View.VISIBLE
                        }else{
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            lottieAnimationView.visibility = View.GONE
                            orderListAdapter.updateData(orders)

                        }


                    }
                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        lottieAnimationView.visibility = View.VISIBLE

                    }
                }
            }
        }
        val email = SharedPreference.getUserEmail(requireContext())

        orderListViewModel.getSpecOrders(email)


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
