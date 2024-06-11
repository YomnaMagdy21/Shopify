package com.example.shopify.setting.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.shopify.R
import com.example.shopify.databinding.FragmentSettingBinding
import com.example.shopify.firebase.Firebase
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.setting.MyAddresses.view.myAddressFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class settingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientID))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

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
            signOut()
        }

    }

    fun signOut() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity()) {

                }
        }
    }



}