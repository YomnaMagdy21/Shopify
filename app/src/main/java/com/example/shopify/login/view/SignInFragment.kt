package com.example.shopify.login.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.BottomNavigationBar.Home.HomeFragment
import com.example.shopify.R
import com.example.shopify.databinding.FragmentFavoriteBinding
import com.example.shopify.databinding.FragmentSignInBinding
import com.example.shopify.signup.view.SignUpFragment


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.login.setOnClickListener {


            val intent= Intent(requireContext(), BottomNavActivity::class.java)
            startActivity(intent)
        }
        binding.signup.setOnClickListener {
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main,SignUpFragment() )
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

}