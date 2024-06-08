package com.example.shopify.setting.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.MainActivity
import com.example.shopify.R
import com.example.shopify.databinding.FragmentSettingBinding
import com.example.shopify.databinding.FragmentSignUpBinding
import com.example.shopify.firebase.Firebase
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.setting.MyAddresses.view.myAddressFragment

class settingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardView1Adress = view.findViewById<CardView>(R.id.cardView1Adress)
        cardView1Adress.setOnClickListener {
            val newFragment = myAddressFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.logOutButton.setOnClickListener {
            Firebase(requireContext()).logout()
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_fragment, SignInFragment())
                .commit()
        }
    }

}