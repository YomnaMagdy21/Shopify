package com.example.shopify.payment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.shopify.R
import com.example.shopify.model.addressModel.Address
import com.example.shopify.MyAddress.view.myAddressFragment
import com.example.shopify.ShoppingCart.view.shoppingCardFragment
import com.example.shopify.model.PostOrders.Customer
import com.example.shopify.model.PostOrders.LineItem
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.google.gson.Gson

class paymentFragment : Fragment() {

    private lateinit var address: Address
    lateinit var myCurrentAdreesText : TextView
    lateinit var orderButton: Button
    lateinit var paymentViewModel: PaymentViewModel
    lateinit var paymentViewModelFactory: PaymentViewModelFactory
    private lateinit var userEmail: String
    private lateinit var draftOrders: ArrayList<DraftOrder>
    private var totalPrice: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            draftOrders = it.getSerializable("products") as ArrayList<DraftOrder>
            totalPrice = it.getDouble("total_price")
            userEmail = it.getString("email") ?: ""

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         myCurrentAdreesText = view.findViewById(R.id.textView10)
        orderButton = view.findViewById(R.id.placeOrderButton)
        paymentViewModelFactory = PaymentViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )
        paymentViewModel = ViewModelProvider(
            requireActivity(),
            paymentViewModelFactory
        ).get(PaymentViewModel::class.java)

        address = arguments?.getSerializable("selected_address") as? Address
            ?: loadAddressFromPreferences() ?: Address("", "", "", "", "", "", "")
        myCurrentAdreesText.text = "${address.address1}, ${address.address2}, ${address.city}, ${address.company}"


        //navigate to shopping card fragment
        val back = view.findViewById<ImageView>(R.id.backImage)
        back.setOnClickListener {
            val newFragment = shoppingCardFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }
        //navigate to address list fragment
        myCurrentAdreesText.setOnClickListener{
            val newFragment = myAddressFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }


        // listener of orderButton to create order
        orderButton.setOnClickListener {
            createOrder()
        }
    }

    private fun createOrder() {
        val customer = draftOrders[0].customer ?: return
        val lineItems = draftOrders.flatMap { it.line_items ?: emptyList() }


        val orderLineItems = lineItems.map {
            LineItem(
                variant_id = it.variant_id,
                quantity = it.quantity,
                name = it.title
            )
        }



        val order = Order(
            line_items = orderLineItems,
            shipping_address = address,
            financial_status = "paid",
            fulfillment_status = "unfulfilled",
            email = customer.email,
            id = null, // will be auto generatedd
            total_price = totalPrice.toString(),
            currency = "EGP",
            customer = Customer(
                id = customer.id,
                email = userEmail,
                created_at = customer.created_at,
                updated_at = customer.updated_at,
                first_name = "Nermeen",
                last_name = "Hamda",
                state = customer.state,
                note = customer.note as String?,
            )
        )

        println("Order Details:")
        println("ID: ${order.id}")
        println("Created At: ${order.created_at}")
        println("Total Price: ${order.total_price}")
        println("Line Items:")
        order.line_items?.forEachIndexed { index, item ->
            println("Item ${index + 1}: ${item.name}, Quantity: ${item.quantity}, Variant ID: ${item.variant_id} , userEmail : $userEmail ")
        }

        val postOrderModel = PostOrderModel(order)
        paymentViewModel.createOrder(postOrderModel)
    }

    private fun loadAddressFromPreferences(): Address? {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("default_address", Context.MODE_PRIVATE)
        val addressJson = sharedPreferences.getString("address", null)
        return addressJson?.let { Gson().fromJson(it, Address::class.java) }
    }
}