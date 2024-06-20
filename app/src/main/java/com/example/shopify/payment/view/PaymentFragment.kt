package com.example.shopify.payment.view

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
import com.example.shopify.BottomNavigationBar.Home.view.HomeFragment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.shopify.R
import com.example.shopify.model.addressModel.Address
import com.example.shopify.MyAddress.view.myAddressFragment
import com.example.shopify.ShoppingCart.model.ShoppingCardIClear
import com.example.shopify.ShoppingCart.view.shoppingCardFragment
import com.example.shopify.model.PostOrders.Customer
import com.example.shopify.model.PostOrders.LineItem
import com.example.shopify.model.PostOrders.NoteAttribute
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.payment.viewModel.PaymentViewModelFactory
import com.example.shopify.payment.viewModel.PaymentViewModel
import com.google.gson.Gson
import com.example.shopify.utility.Constants
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class paymentFragment : Fragment() {

    private lateinit var address: Address
    lateinit var myCurrentAdreesText: TextView
    lateinit var edit : TextView

    lateinit var orderButton: Button
    lateinit var paymentViewModel: PaymentViewModel
    lateinit var paymentViewModelFactory: PaymentViewModelFactory
    private lateinit var userEmail: String
    private lateinit var draftOrders: ArrayList<DraftOrder>

    lateinit var checkBoxOffline: CheckBox
    lateinit var checkBoxOnline: CheckBox
    private var paymentMethod: String = ""

    lateinit var totalPriceText : TextView

    //payment
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerId: String
    private lateinit var ephemeralKey: String
    private lateinit var clientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentViewModelFactory = PaymentViewModelFactory(ShopifyRepositoryImp.getInstance(ShopifyRemoteDataSourceImp.getInstance()))
        paymentViewModel = ViewModelProvider(requireActivity(), paymentViewModelFactory).get(PaymentViewModel::class.java)
        //initalizting
        PaymentConfiguration.init(requireContext(), Constants.PUBLISH_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        
        arguments?.let {
            draftOrders = it.getSerializable("products") as ArrayList<DraftOrder>
            paymentViewModel.totalPrice = it.getDouble("total_price")
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

        checkBoxOffline = view.findViewById(R.id.checkBoxCash)
        checkBoxOnline = view.findViewById(R.id.checkBoxOnline)

        myCurrentAdreesText = view.findViewById(R.id.textView10)
        edit = view.findViewById(R.id.textViewEdit)

        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userID", null)

        arguments?.getSerializable("selected_address")?.let { addressObj ->
            if (addressObj is Address) {
                address = addressObj
            } else {
                Log.e("paymentFragment", "Unexpected type for selected_address")
                address = userId?.let { loadAddressFromPreferences(it) }?: Address(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            }
        } ?: run {
            // If there are no arguments, load the default address from SharedPreferences
            address = userId?.let { loadAddressFromPreferences(it) }?: Address(
                "Add your Address Please",
                "",
                "",
                "",
                "",
                "",
                ""
            )
        }

        myCurrentAdreesText.text =
            "${address.address1}, ${address.address2}, ${address.city}, ${address.company}"

        totalPriceText = view.findViewById(R.id.textView3)
        totalPriceText.text = paymentViewModel.totalPrice.toString()


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
        edit.setOnClickListener {
            val myAddressFragment = myAddressFragment().apply {
                arguments = Bundle().apply {
                    putString("source", "payment")
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, myAddressFragment)
                .addToBackStack(null)
                .commit()
        }

        //pay
        checkBoxOnline.setOnClickListener {
            if (checkBoxOnline.isChecked) {
                checkBoxOffline.isChecked = false
                paymentFlow()
                paymentMethod = "Visa"
            } else {
                Log.i("flow", "Online CheckBox is not checked.")
            }
        }
        checkBoxOffline.setOnClickListener {
            if (checkBoxOffline.isChecked) {
                checkBoxOnline.isChecked = false
                paymentMethod = "Cash"
                //
            } else {
                Log.i("flow", "Offline CheckBox is not checked.")
            }
        }
        creatStripCustomerId()

        // listener of orderButton to create order
        orderButton.setOnClickListener {
            if (paymentMethod.isEmpty()) {
                Snackbar.make(view, "Please choose a payment method.", Snackbar.LENGTH_SHORT).show()
            } else if (paymentMethod == "Cash") {
                createOrder()
            } else {
                Snackbar.make(view, "Please complete the payment process.", Snackbar.LENGTH_SHORT).show()
            }
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
            total_price = paymentViewModel.totalPrice.toString(),
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

    private fun loadAddressFromPreferences(userId: String): Address? {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("default_address_$userId", Context.MODE_PRIVATE)

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

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Snackbar.make(requireView(), "Payment completed successfully!", Snackbar.LENGTH_SHORT).show()
                //totalPriceText.text = " "
                createOrder()
            }

            is PaymentSheetResult.Failed -> {
                Snackbar.make(requireView(), "Payment failed. Please try again.", Snackbar.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Canceled -> {
                Snackbar.make(requireView(), "Payment was canceled.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    //creat strip customer id
    fun creatStripCustomerId(){
    val request: StringRequest = object : StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    customerId = jsonObject.getString("id")
                    getEphericalKey(customerId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                //
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: HashMap<String, String> = HashMap<String, String>()
                header.put("Authorization", "Bearer ${Constants.SECRET_KEY}")
                return header
            }
        }

    val requestQueue = Volley.newRequestQueue(requireContext())
    requestQueue.add(request)
    }

    //get ephericalKey
    private fun getEphericalKey(id: String){
        val request: StringRequest = object : StringRequest(
            Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
            Response.Listener { response ->
                try{
                    val jsonObject = JSONObject(response)
                    ephemeralKey = jsonObject.getString("id")
                    //Toast.makeText(requireContext(),"Epherical Key: "+ephemeralKey,Toast.LENGTH_SHORT).show()
                    Log.i("keyys", "getEphericalKey: "+ephemeralKey)
                    getClientSecret(customerId,ephemeralKey)
                }catch (e : JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                //
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: HashMap<String, String> = HashMap<String,String>()
                header.put("Authorization" , "Bearer ${Constants.SECRET_KEY}")
                header.put("Stripe-Version" , "2020-08-27")
                return header
            }

            override fun getParams(): Map<String, String> {
                val param: HashMap<String, String> = HashMap<String,String>()
                param.put("customer" ,customerId)
                return param
            }
        }

        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(request)
    }

    //get client secrect key
    private fun getClientSecret(customerId: String, ephemeralKey: String) {
        val request: StringRequest = object : StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
            Response.Listener {response ->
                try{
                    val jsonObject = JSONObject(response)
                    clientSecret = jsonObject.getString("client_secret")
                    Log.i("keyys", "getClientSecret: $clientSecret")
                }catch (e : JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error->
                error.printStackTrace()
                Log.e("keyys", "getClientSecret: Error ${error.message}")
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: HashMap<String,String> = HashMap<String,String>()
                header.put("Authorization" , "Bearer ${Constants.SECRET_KEY}")
                return header
            }

            override fun getParams(): Map<String, String> {
                val param: HashMap<String,String> = HashMap<String,String>()
                param.put("customer" ,customerId)
                val str = (paymentViewModel.totalPrice * 100).toInt()
                param.put("amount", str.toString())
                param.put("currency" ,"EGP")
                param.put("automatic_payment_methods[enabled]" ,"true")
                return param
            }
        }

        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(request)
    }

    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(clientSecret, PaymentSheet.Configuration("SHOPIFY",
            PaymentSheet.CustomerConfiguration(customerId,ephemeralKey)))
    }


}