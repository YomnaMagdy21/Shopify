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
import com.example.shopify.R
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.addressModel.AddressesModel
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.payment.paymentFragment
import com.example.shopify.MyAddress.viewModel.MyAddressFactory
import com.example.shopify.MyAddress.viewModel.MyAddressViewModel
import com.example.shopify.newAddress.newAddress
import com.example.shopify.setting.view.settingFragment
import com.example.shopify.utility.ApiState
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class myAddressFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAddressAdapter: MyAddressAdapter
    private lateinit var viewModel: MyAddressViewModel

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


        myAddressAdapter = MyAddressAdapter(
            emptyList(),
            onItemClick = { address ->
                saveAddressToPreferences(address)
                navigateToPaymentFragment(address)
            },
            onDeleteButtonClick = { address ->
                address.id?.let { viewModel.deleteAddress(7670572875940, it) }
                Log.i("delete", "onViewCreated: "+address.id)
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


        val currentUser = FirebaseAuth.getInstance().currentUser
        //Log.i("address", "onViewCreated: $customerId")

        viewModel.getAllAddresses(7670572875940)

    lifecycleScope.launch {
        viewModel.accessAllAddressesList.collectLatest { apiState ->
            when (apiState) {
                is ApiState.Success<*> -> {
                    var products = apiState.data as AddressesModel?
                    products?.let {
                        if (it.addresses != null) {
                            myAddressAdapter.updateAddresses(it.addresses)
                        }else{
                            Log.i("address", "onViewCreated: hiiiiiiii")
                        }
                    }
                }
                is ApiState.Failure -> {
                    Log.e("myAddressFragment", "Failed to fetch addresses: ${apiState.msg}")
                }
                is ApiState.Loading -> {
                }
            }
        }
    }

}
    private fun saveAddressToPreferences(address: Address) {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("default_address", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("address", Gson().toJson(address))
            apply()
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