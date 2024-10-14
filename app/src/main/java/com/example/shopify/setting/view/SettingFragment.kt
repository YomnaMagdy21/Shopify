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
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shopify.MainActivity
import com.example.shopify.R
import com.example.shopify.databinding.FragmentSettingBinding
import com.example.shopify.firebase.Firebase
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.MyAddress.view.myAddressFragment
import com.example.shopify.MyAddress.viewModel.MyAddressFactory
import com.example.shopify.MyAddress.viewModel.MyAddressViewModel
import com.example.shopify.ShoppingCart.viewModel.ShoppingCardViewModel
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.setting.currency.CurrencyApiClient
import com.example.shopify.setting.currency.CurrencyConverter
import com.example.shopify.setting.currency.CurrencyRemoteDataSourceImpl
import com.example.shopify.setting.currency.CurrencyRepository
import com.example.shopify.setting.viewModel.CurrencyViewModel
import com.example.shopify.setting.viewModel.CurrencyViewModelFactory
import com.example.shopify.utility.Constants
import com.example.shopify.utility.SharedPreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class settingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var viewModel: CurrencyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        val apiService = CurrencyApiClient.getApiService()
        val remoteDataSource = CurrencyRemoteDataSourceImpl(apiService)
        val repository = CurrencyRepository(remoteDataSource)
        val factory = CurrencyViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(CurrencyViewModel::class.java)

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
            val myAddressFragment = myAddressFragment().apply {
                arguments = Bundle().apply {
                    putString("source", "setting")
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, myAddressFragment)
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
            SharedPreference.saveUserEmail(requireContext(), "")

        }

        binding.cardView2Currnucy.setOnClickListener{
            showCurrencyDialog()
        }

        binding.cardView3About.setOnClickListener{
            showAboutUsDialog()
        }

        binding.cardViewLanguage.setOnClickListener {
            showLanguage()
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
            // Convert EGP to USD
            CurrencyConverter.setCurrencyCode("USD")
            convertCurrency("EGP", "USD", 1.0)
            dialogBuilder.dismiss()
        }

        option2Button.setOnClickListener {
            // Convert USD to EGP
            CurrencyConverter.setCurrencyCode("EGP")
            convertCurrency("USD", "EGP", 1.0)
            dialogBuilder.dismiss()
        }
        dialogBuilder.show()
    }

    private fun convertCurrency(from: String, to: String, amount: Double) {
        viewModel.convertCurrency(Constants.API_KEY, from, to, amount)
        lifecycleScope.launch {
            viewModel.currencyState.collectLatest { response ->
                response?.let {
                    if (it.isSuccessful) {
                        val rates = it.body()?.rates
                        val convertedAmount = rates?.get(to)?.rate_for_amount
                        showConvertedAmountDialog(convertedAmount)
                    } else {
                        // Handle error
                        showErrorDialog("Conversion failed. Please try again.")
                    }
                }
            }
        }
    }

    private fun showConvertedAmountDialog(amount: Double?) {
        //val message = "Converted Amount: ${amount ?: "N/A"}"
        val message = "converted Sccussfully"
        AlertDialog.Builder(requireContext())
            .setTitle("Currency Conversion")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
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

    fun showLanguage(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.language_dialog, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val option1Button = dialogView.findViewById<Button>(R.id.english)
        val option2Button = dialogView.findViewById<Button>(R.id.arabic)

        option1Button.setOnClickListener {
            SharedPreference.saveLanguage(requireContext(),"en")
            updateAppContext("en")
            dialogBuilder.dismiss()
        }

        option2Button.setOnClickListener {
            SharedPreference.saveLanguage(requireContext(),"ar")
            updateAppContext("ar")
            dialogBuilder.dismiss()
        }
        dialogBuilder.show()

    }

    private fun updateAppContext(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate(requireActivity())
    }



}