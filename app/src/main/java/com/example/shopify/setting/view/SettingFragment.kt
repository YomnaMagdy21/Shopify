package com.example.shopify.setting.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import com.example.shopify.MainActivity
import com.example.shopify.R
import com.example.shopify.databinding.FragmentSettingBinding
import com.example.shopify.firebase.Firebase
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.MyAddress.view.myAddressFragment
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
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.home_fragment, SignInFragment())
//                .commit()
            signOut()
        }

        binding.cardView2Currnucy.setOnClickListener{
            showCurrencyDialog()
        }

        binding.cardView3About.setOnClickListener{
            showAboutUsDialog()
        }

    }

    private fun showCurrencyDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_currency, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val option1Button = dialogView.findViewById<Button>(R.id.option1Button)
        val option2Button = dialogView.findViewById<Button>(R.id.option2Button)

        option1Button.setOnClickListener {
            // Handle option 1 (USD) selection
            // e.g., save the selected currency or perform any action
            dialogBuilder.dismiss()
        }

        option2Button.setOnClickListener {
            // Handle option 2 (EUR) selection
            // e.g., save the selected currency or perform any action
            dialogBuilder.dismiss()
        }
        dialogBuilder.show()
    }

    private fun showAboutUsDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.about_us_diaglog, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogBuilder.show()
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