package com.example.shopify.newAddress.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shopify.R
import com.example.shopify.MyAddress.view.myAddressFragment
import com.example.shopify.MyAddress.viewModel.MyAddressFactory
import com.example.shopify.MyAddress.viewModel.MyAddressViewModel
import com.example.shopify.databinding.FragmentNewAddressBinding
import com.example.shopify.map.mapFragment
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.model.addressModel.Address
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.newAddress.viewModel.NewAddressFactory
import com.example.shopify.newAddress.viewModel.NewAddressViewModel
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.launch
import kotlin.math.log


class newAddress : Fragment() {

    private lateinit var viewModel : NewAddressViewModel
    lateinit var addButton : Button
    lateinit var phone : EditText
    lateinit var city :EditText
    lateinit var country : EditText
    lateinit var building : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initialize
        val remoteDataSource = ShopifyRemoteDataSourceImp.getInstance()
        val repository = ShopifyRepositoryImp.getInstance(remoteDataSource)
        val factory = NewAddressFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(NewAddressViewModel::class.java)

        city = view.findViewById(R.id.editTextCity)
        phone = view.findViewById(R.id.editTextPhone)
        country = view.findViewById(R.id.editTextCountry)
        building = view.findViewById(R.id.editTextBuilding)
        addButton = view.findViewById(R.id.confirmButton)

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userID", null)

        //add new address
        addButton.setOnClickListener{
            val address1 = building.text.toString()
            val city = city.text.toString()
            val country = country.text.toString()
            val phone = phone.text.toString()

            if (validateInputs(address1, city, country, phone)) {
                val address = Address(address1, city, country,phone, customer_id = userId?.toLong())
                Log.i("phone", "onViewCreated: "+address)
                if (userId != null) {
                    viewModel.addAddresses(userId.toLong(), AddNewAddress(address))
                }
            }
        }

        observeViewModel()

        //navigate back
        val back = view.findViewById<ImageView>(R.id.backImage)
        back.setOnClickListener {
            val newFragment = myAddressFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }

        //navigate to map fragment
        val mapFragmentText = view.findViewById<TextView>(R.id.textView9)
        mapFragmentText.setOnClickListener{
            val newFragment = mapFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addAddresses.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        // Show loading indicator
                    }
                    is ApiState.Success<*> -> {
                        // Address added successfully, navigate back
                        Log.i("neww", "observeViewModel: ")
                    }
                    is ApiState.Failure -> {
                        // Show error message
                    }
                }
            }
        }
    }

    private fun validateInputs(address1: String, city: String, country: String, phone: String): Boolean {
        var isValid = true

        if (address1.isEmpty() || address1.length < 5) {
            isValid = false
            building.error = "Address must be at least 5 characters"
        }
        if (city.isEmpty() || city.length < 2) {
            isValid = false
            this.city.error = "City must be at least 2 characters"
        }
        if (country.isEmpty() || country.length < 2) {
            isValid = false
            this.country.error = "Country must be at least 2 characters"
        }
        if (phone.isEmpty() || phone.length < 10) {
            isValid = false
            this.phone.error = "Phone number must be at least 7 characters"
        }

        return isValid
    }
}