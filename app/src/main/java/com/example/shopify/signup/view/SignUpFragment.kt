package com.example.shopify.signup.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.BottomNavigationBar.Home.HomeFragment

import com.example.shopify.R
import com.example.shopify.databinding.FragmentSignInBinding
import com.example.shopify.databinding.FragmentSignUpBinding
import com.example.shopify.login.view.SignInFragment


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.login.setOnClickListener {


            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, SignInFragment() )
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.signup.setOnClickListener {
//
            val intent= Intent(requireContext(),BottomNavActivity::class.java)
            startActivity(intent)

        }
    }

}