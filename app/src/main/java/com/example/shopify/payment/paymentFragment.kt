package com.example.shopify.payment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.shopify.R
import com.example.shopify.model.addressModel.Address
import com.example.shopify.MyAddress.view.myAddressFragment
import com.example.shopify.ShoppingCart.view.shoppingCardFragment
import com.example.shopify.utility.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONException
import org.json.JSONObject

class paymentFragment : Fragment() {

    private lateinit var address: Address
    lateinit var myCurrentAdreesText: TextView
    lateinit var totalPriceText : TextView

    //payment
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerId: String
    private lateinit var ephemeralKey: String
    private lateinit var clientSecret: String
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initalizting
        PaymentConfiguration.init(requireContext(), Constants.PUBLISH_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)


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

        // Retrieve the total price from the arguments
        arguments?.getDouble("total_price", 0.0)?.let {
            totalPrice = it
        }

        myCurrentAdreesText = view.findViewById(R.id.textView10)
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userID", null)

        arguments?.getSerializable("selected_address")?.let { addressObj ->
            if (addressObj is Address) {
                address = addressObj
            } else {
                Log.e("paymentFragment", "Unexpected type for selected_address")
                address = userId?.let { loadAddressFromPreferences(it) } ?: Address(
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
            address = userId?.let { loadAddressFromPreferences(it) } ?: Address(
                "",
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
        totalPriceText.text = totalPrice.toString()


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

        //pay
        val checkBoxOnline = view.findViewById<CheckBox>(R.id.checkBoxOnline)
        val checkBoxOffline = view.findViewById<CheckBox>(R.id.checkBoxCash)

        checkBoxOnline.setOnClickListener {
            if (checkBoxOnline.isChecked) {
                checkBoxOffline.isChecked = false
                paymentFlow()
            } else {
                Log.i("flow", "Online CheckBox is not checked.")
            }
        }

        checkBoxOffline.setOnClickListener {
            if (checkBoxOffline.isChecked) {
                checkBoxOnline.isChecked = false
                //
            } else {
                Log.i("flow", "Offline CheckBox is not checked.")
            }
        }

        creatStripCustomerId()
    }

    private fun loadAddressFromPreferences(userId: String): Address? {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("default_address_$userId", Context.MODE_PRIVATE)
        val addressJson = sharedPreferences.getString("address", null)
        return addressJson?.let { Gson().fromJson(it, Address::class.java) }
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Snackbar.make(requireView(), "Payment completed successfully!", Snackbar.LENGTH_SHORT).show()
                totalPriceText.text = " "
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
                    Toast.makeText(requireContext(),"Customer Id: "+customerId,Toast.LENGTH_SHORT).show()
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
                val str = (totalPrice * 100).toInt()
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