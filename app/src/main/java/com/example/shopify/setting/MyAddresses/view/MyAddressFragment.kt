package com.example.shopify.setting.MyAddresses.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R
import com.example.shopify.setting.newAddress.view.newAddress
import com.example.shopify.setting.view.settingFragment

class myAddressFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAddressAdapter: MyAddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        myAddressAdapter = MyAddressAdapter()
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
    }
}