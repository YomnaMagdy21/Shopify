package com.example.shopify.login.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.R

import com.example.shopify.databinding.FragmentSignInBinding
import com.example.shopify.firebase.Firebase
import com.example.shopify.model.Customer
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.createCustomerRequest
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.signup.view.SignUpFragment
import com.example.shopify.signup.viewmodel.SignUpViewModel
import com.example.shopify.signup.viewmodel.SignUpViewModelFactory
import com.example.shopify.utility.ApiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private var isPasswordVisible = false
    private val RC_SIGN_IN = 123

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val TAG = "ID"
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var signUpViewModelFactory: SignUpViewModelFactory


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
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        signUpViewModelFactory = SignUpViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        signUpViewModel = ViewModelProvider(this, signUpViewModelFactory).get(SignUpViewModel::class.java)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.login.setOnClickListener {
            var email = binding.emailEditText.text.toString()
            var password = binding.password.text.toString()
            if (email.isEmpty()) {
                binding.emailEditText.error = "Email cannot be empty"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.password.error = "Password cannot be empty"
                binding.password.requestFocus()
                return@setOnClickListener
            }


        Firebase(requireContext()).loginClient(requireContext(),email,password)
            val intent= Intent(requireContext(), BottomNavActivity::class.java)
            startActivity(intent)
        }
        binding.signup.setOnClickListener {
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,SignUpFragment() )
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.visibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.visibility.setImageResource(R.drawable.baseline_visibility_off_24)
            } else {
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.visibility.setImageResource(R.drawable.baseline_visibility_24)
            }
            binding.password.setSelection(binding.password.text.length)
        }

        binding.logo.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null && account.idToken != null) {
                    handleSignInResult(account.idToken!!)
                    Toast.makeText(context, "login with google successfully", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.e(TAG,
                        "Google sign-in failed: ID token is null"
                    )
                }
            } catch (e: ApiException) {
                Log.e(TAG,
                    "Google sign-in failed: " + e.statusCode
                )
            }
        }
    }
    private fun handleSignInResult(idToken: String) {
        val mAuth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null) {

                        user.sendEmailVerification().addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                Log.i(TAG, "Verification email sent to ${user.email}")

                                Firebase(requireContext()).checkIfUserExists(user.uid) { exists ->
                                    if (exists) {
                                        startActivity(Intent(context, BottomNavActivity::class.java))
                                        Toast.makeText(context, "Welcome back! Verification email sent.", Toast.LENGTH_LONG).show()
                                    } else {
                                        val customer = Customer(
                                            0, user.email, null, null, "", "", "", "", 0, null, null,
                                            true, null, null, null, null
                                        )
                                        Firebase(requireContext()).writeNewUser(customer)

                                        val client = createCustomerRequest(customer)
                                        signUpViewModel.registerCustomerInAPI(client)

                                        lifecycleScope.launch {
                                            signUpViewModel.register.collectLatest { result ->
                                                when (result) {
                                                    is ApiState.Success<*> -> {
                                                        val person = result.data as? createCustomerRequest
                                                        Log.i(TAG, "Customer ID: ${person?.customer?.id}")
                                                        startActivity(Intent(context, BottomNavActivity::class.java))
                                                        Toast.makeText(context, "User logged in with Google successfully. Verification email sent.", Toast.LENGTH_LONG).show()
                                                    }
                                                    else -> {

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "Failed to send verification email.", emailTask.exception)
                                Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Google sign-in failed", task.exception)
                }
            }
    }



}