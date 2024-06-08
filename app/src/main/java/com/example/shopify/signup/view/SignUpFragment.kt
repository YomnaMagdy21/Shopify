package com.example.shopify.signup.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.R
import com.example.shopify.databinding.FragmentSignUpBinding
import com.example.shopify.firebase.Firebase
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.model.Customer
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.createCustomerRequest
import com.example.shopify.network.ShopifyRemoteDataSourceImp
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


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var email : String
    lateinit var password : String
    lateinit var password_confirmation : String
    lateinit var firstname:String
    lateinit var lastname:String
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var signUpViewModelFactory: SignUpViewModelFactory
    val TAG = "ID"
    private val RC_SIGN_IN = 123

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var isPasswordVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientID))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
//        val signInIntent = googleSignInClient.signInIntent
//        ActivityCompat.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
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
        firebaseAuth = FirebaseAuth.getInstance()
        binding.login.setOnClickListener {


            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, SignInFragment() )
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.visibility1.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.visibility1.setImageResource(R.drawable.baseline_visibility_off_24)
            } else {
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.visibility1.setImageResource(R.drawable.baseline_visibility_24)
            }
            binding.password.setSelection(binding.password.text.length)
        }
        binding.visibility2.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.confirm.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.visibility2.setImageResource(R.drawable.baseline_visibility_off_24)
            } else {
                binding.confirm.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.visibility2.setImageResource(R.drawable.baseline_visibility_24)
            }
            binding.password.setSelection(binding.password.text.length)
        }


        binding.signup.setOnClickListener {
            email = binding.email.text.toString()
            password = binding.password.text.toString()
            firstname = binding.firstname.text.toString()
            lastname = binding.secondname.text.toString()
            password_confirmation = binding.confirm.text.toString()


            if (firstname.isEmpty()) {
                binding.firstname.error = "First name cannot be empty"
                binding.firstname.requestFocus()
                return@setOnClickListener
            }
            if (lastname.isEmpty()) {
                binding.secondname.error = "Last name cannot be empty"
                binding.secondname.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.email.error = "Email cannot be empty"
                binding.email.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.password.error = "Password cannot be empty"
                binding.password.requestFocus()
                return@setOnClickListener
            }
            if (password_confirmation.isEmpty()) {
                binding.confirm.error = "Password confirmation cannot be empty"
                binding.confirm.requestFocus()
                return@setOnClickListener
            }
            if (password != password_confirmation) {
                binding.confirm.error = "Passwords do not match"
                binding.confirm.requestFocus()
                return@setOnClickListener
            }
            Firebase(requireContext()).createCustomerAccount(email, password) { user, error ->
                if (user != null) {

                    user.sendEmailVerification().addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            Log.i(TAG, "Verification email sent to ${user.email}")
                            Toast.makeText(context, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show()


                            val customer = Customer(
                                0, user.email, null, null, firstname, lastname, password, password_confirmation, 0, null, null,
                                true, null, null, null, null
                            )
                            val client = createCustomerRequest(customer)
                            Firebase(requireContext()).writeNewUser(customer)
                            signUpViewModel.registerCustomerInAPI(client)

                            lifecycleScope.launch {
                                signUpViewModel.register.collectLatest { result ->
                                    when (result) {
                                        is ApiState.Success<*> -> {
                                            val person = result.data as? createCustomerRequest
                                            Log.i(TAG, "Customer ID: ${person?.customer?.id}")
                                            startActivity(Intent(context, BottomNavActivity::class.java))
                                            Toast.makeText(context, "User registered successfully", Toast.LENGTH_LONG).show()
                                        }
                                        else -> {

                                        }
                                    }
                                }
                            }
                        } else {
                            Log.e(TAG, "Failed to send verification email.", emailTask.exception)
                            Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e(TAG, "Account creation failed: $error")
                    Toast.makeText(context, "Account creation failed: $error", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.google.setOnClickListener {
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