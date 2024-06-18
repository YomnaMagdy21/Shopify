package com.example.shopify.payment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shopify.BottomNavigationBar.Home.view.HomeFragment
import com.example.shopify.MyAddress.view.myAddressFragment
import com.example.shopify.R
import com.example.shopify.ShoppingCart.model.ShoppingCardIClear
import com.example.shopify.ShoppingCart.view.shoppingCardFragment
import com.example.shopify.model.PostOrders.Customer
import com.example.shopify.model.PostOrders.LineItem
import com.example.shopify.model.PostOrders.NoteAttribute
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.utility.ApiState
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class paymentFragment : Fragment() {

    private lateinit var address: Address
    lateinit var myCurrentAdreesText: TextView
    lateinit var orderButton: Button
    lateinit var paymentViewModel: PaymentViewModel
    lateinit var paymentViewModelFactory: PaymentViewModelFactory
    private lateinit var userEmail: String
    private lateinit var draftOrders: ArrayList<DraftOrder>
    private var totalPrice: Double = 0.0

    lateinit var checkBoxCash: CheckBox
    lateinit var checkBoxOnline: CheckBox
    private var paymentMethod: String = ""

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

        checkBoxCash = view.findViewById(R.id.checkBoxCash)
        checkBoxOnline = view.findViewById(R.id.checkBoxOnline)




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
        myCurrentAdreesText.text =
            "${address.address1}, ${address.address2}, ${address.city}, ${address.company}"


        checkBoxCash.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxOnline.isChecked = false
                paymentMethod = "Cash"
            }
        }

        checkBoxOnline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxCash.isChecked = false
                paymentMethod = "Visa"
            }
        }

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
        myCurrentAdreesText.setOnClickListener {
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

//        lifecycleScope.launch {
//            paymentViewModel.accessOrder.collectLatest { state ->
//                when (state) {
//                    is ApiState.Success<*> -> {
//
//                    }
//
//                    is ApiState.Failure -> {
//                        // Handle error
//                    }
//
//                    else -> {
//                        // Handle loading
//                    }
//                }
//            }
//        }
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

        val noteAttributes = mutableListOf<NoteAttribute>()
        draftOrders.forEach { draftOrder ->
            draftOrder.note_attributes?.forEach { noteAttr ->
                noteAttributes.add(
                    NoteAttribute(
                        name = "image",
                        values = listOf(noteAttr.value ?: "")   // list of image of each draft order
                    )
                )
            }
        }
        // 3 draft orders -> 3 images


        val order = Order(
            line_items = orderLineItems,
            note_attributes = noteAttributes,
            shipping_address = Address(
                address1 = address.address1,
                city = address.address2,
                country = address.city,
                phone = address.company
            ),
            financial_status = "paid",
            fulfillment_status = "unfulfilled",
            email = customer.email,
            id = null,                                                     // will be auto-generated
            total_price = totalPrice.toString(),
            currency = "EGP",
            tags = paymentMethod,
            customer = Customer(
                id = customer.id,
                email = customer.email,
                created_at = customer.created_at,
                updated_at = customer.updated_at,
                first_name = customer.first_name,
                last_name = customer.last_name,
                state = customer.state,
                note = customer.note as String?
            ),
        )

        // Print note_attributes
        println("Note Attributes:")
        order.note_attributes?.forEach { noteAttribute ->
            println("Name: ${noteAttribute.name}, Value: ${noteAttribute.values}")
        }

        println("Order Details:")
        println("ID: ${order.id}")
        println("Address1: ${address.address1}")
        println("city: ${address.address2}")
        println("country: ${address.city}")
        println("phone: ${address.company}")

        println("Created At: ${order.created_at}")
        println("Total Price: ${order.total_price}")
        println("first name: ${order.customer?.first_name}")
        println("last name: ${order.customer?.last_name}")
        println("Payment Method: ${order.tags}")

        println("Line Items:")
        order.line_items?.forEachIndexed { index, item ->
            println("Item ${index + 1}: ${item.name}, Quantity: ${item.quantity}, Variant ID: ${item.variant_id} , userEmail : $userEmail ")
        }


        val postOrderModel = PostOrderModel(order)
        paymentViewModel.createOrder(postOrderModel)
        clearShoppingCart()
    }

    private fun clearShoppingCart() {
        val shoppingCardFragment = parentFragmentManager.findFragmentByTag("SHOPPING_CARD_FRAGMENT_TAG") as? ShoppingCardIClear
        shoppingCardFragment?.clearShoppingCart()
        orderCreatedPopup()
        navigateToHomeFragment()
        println("1111111111111111111111111111111111111Shopping cart is cleared.-------------------------------------------")

    }

    private fun loadAddressFromPreferences(): Address? {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("default_address", Context.MODE_PRIVATE)
        val addressJson = sharedPreferences.getString("address", null)
        return addressJson?.let { Gson().fromJson(it, Address::class.java) }
    }

    private fun orderCreatedPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("Order Created")
            .setMessage("Your order has been successfully created.")
            .setPositiveButton("OK") { _, _ ->
                val shoppingCardFragment =
                    parentFragmentManager.findFragmentById(R.id.frame_layout) as? ShoppingCardIClear
                shoppingCardFragment?.clearShoppingCart()
            }
            .show()
    }

    private fun navigateToHomeFragment() {
        val newFragment = HomeFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, newFragment)
            .commit()
    }
}