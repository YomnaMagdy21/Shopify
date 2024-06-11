package com.example.shopify.payment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.shopify.R
import com.example.shopify.model.addressModel.Address
import com.example.shopify.MyAddress.view.myAddressFragment
import com.example.shopify.shoppingCard.view.shoppingCardFragment
import com.google.gson.Gson

class paymentFragment : Fragment() {

    private lateinit var address: Address
    lateinit var myCurrentAdreesText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        arguments?.let {
            address = it.getSerializable("selected_address") as Address
        } ?: run {
            // If there are no arguments, load the default address from SharedPreferences
            address = loadAddressFromPreferences() ?: Address("", "", "", "", "", "", "")
        }

        myCurrentAdreesText.text = "${address.address1}, ${address.city}, ${address.country}, ${address.phone}"


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
    }

    private fun loadAddressFromPreferences(): Address? {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("default_address", Context.MODE_PRIVATE)
        val addressJson = sharedPreferences.getString("address", null)
        return addressJson?.let { Gson().fromJson(it, Address::class.java) }
    }
}