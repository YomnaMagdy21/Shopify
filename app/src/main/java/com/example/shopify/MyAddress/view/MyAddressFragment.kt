package com.example.shopify.MyAddress.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.shopify.R
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.addressModel.AddressesModel
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.payment.paymentFragment
import com.example.shopify.MyAddress.viewModel.MyAddressFactory
import com.example.shopify.MyAddress.viewModel.MyAddressViewModel
import com.example.shopify.newAddress.view.newAddress
import com.example.shopify.setting.view.settingFragment
import com.example.shopify.utility.ApiState
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class myAddressFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAddressAdapter: MyAddressAdapter
    private lateinit var viewModel: MyAddressViewModel
    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val remoteDataSource = ShopifyRemoteDataSourceImp.getInstance()
        val repository = ShopifyRepositoryImp.getInstance(remoteDataSource)
        val factory = MyAddressFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(MyAddressViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.RVAddresses)
        recyclerView.layoutManager = LinearLayoutManager(context)

        lottieAnimationView = view.findViewById(R.id.animationView)
        //get user id and pass it
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userID", null)
        Log.i("userid", "onViewCreated: " + userId)



        myAddressAdapter = MyAddressAdapter(
            emptyList(),
            onItemClick = { address ->
                if (userId != null) {
                    saveAddressToPreferences(userId,address)
                }
                address.id?.let {
                    if (userId != null) {
                        viewModel.makeDefaultAddress(userId.toLong(), it)
                        Snackbar.make(
                            view,
                            "Default address updated successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                Log.i("default", "onViewCreated: " + address.id)
                //navigateToPaymentFragment(address)
            },
            onDeleteButtonClick = { address ->
                address.id?.let {
                    if (userId != null) {
                        viewModel.deleteAddress(userId.toLong(), it)
                        Snackbar.make(view, "Address deleted successfully", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
                Log.i("delete", "onViewCreated: " + address.id)
            }, onEditButtonClick = { address ->
                val bundle = Bundle().apply {
                    putSerializable("address", address)
                    putString("address1", address.address1)
                    putString("city", address.address2)
                    putString("country", address.city)
                    putString("phone", address.company)
                }
                val editAddressFragment = newAddress().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, editAddressFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )


        recyclerView.adapter = myAddressAdapter

        val addNewAddress = view.findViewById<Button>(R.id.AddAddressButton)
        addNewAddress.setOnClickListener {
            val newFragment = newAddress()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }

        val back = view.findViewById<ImageView>(R.id.backImage)
        back.setOnClickListener {
            val newFragment = settingFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }

        if (userId != null) {
            viewModel.getAllAddresses(userId.toLong())
        }

        lifecycleScope.launch {
            viewModel.accessAllAddressesList.collectLatest { apiState ->
                when (apiState) {
                    is ApiState.Success<*> -> {
                        var products = apiState.data as AddressesModel?
                        products?.let {
                            if (!it.addresses.isNullOrEmpty()) {
                                val defaultAddressId = userId?.let { it1 -> getDefaultAddressIdFromSharedPreferences(it1) }
                                myAddressAdapter.updateAddresses(it.addresses, defaultAddressId)
                                lottieAnimationView.visibility = View.GONE
                                Log.i("address", "onViewCreated: Gone")

                            } else {
                                Log.i("address", "onViewCreated: hiiiiiiii")
                                lottieAnimationView.visibility = View.VISIBLE
                                lottieAnimationView.playAnimation()
                            }
                        }
                    }

                    is ApiState.Failure -> {
                        Log.e("myAddressFragment", "Failed to fetch addresses: ${apiState.msg}")
                    }

                    is ApiState.Loading -> {
                    }

                    else -> {}
                }
            }
        }

    }

    private fun saveAddressToPreferences(userId: String, address: Address) {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("default_address_$userId", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("address", Gson().toJson(address))
            apply()
        }
    }

    private fun getDefaultAddressIdFromSharedPreferences(userId: String): Long? {
        val sharedPreferences =
            requireContext().getSharedPreferences("default_address_$userId", Context.MODE_PRIVATE)
        val defaultAddressJson = sharedPreferences.getString("address", null)
        return defaultAddressJson?.let {
            val defaultAddress = Gson().fromJson(it, Address::class.java)
            defaultAddress.id
        }
    }



    private fun navigateToPaymentFragment(address: Address) {
        val bundle = Bundle().apply {
            putSerializable("selected_address", address)
        }
        val paymentFragment = paymentFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, paymentFragment)
            .addToBackStack(null)
            .commit()
    }

}