package com.example.shopify.login.view

import android.content.Context
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
import com.example.shopify.MainActivity
import com.example.shopify.R

import com.example.shopify.databinding.FragmentSignInBinding
import com.example.shopify.firebase.Firebase
import com.example.shopify.login.viewmodel.SignInViewModel
import com.example.shopify.login.viewmodel.SignInViewModelFactory
import com.example.shopify.model.Customer
import com.example.shopify.model.ShopifyRepositoryImp
import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.createCustomersResponse
import com.example.shopify.network.ShopifyRemoteDataSourceImp
import com.example.shopify.signup.view.SignUpFragment
import com.example.shopify.signup.viewmodel.SignUpViewModel
import com.example.shopify.signup.viewmodel.SignUpViewModelFactory
import com.example.shopify.utility.ApiState
import com.example.shopify.utility.SharedPreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private var isPasswordVisible = false
    private val RC_SIGN_IN = 123

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val TAG = "ID"
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var signUpViewModelFactory: SignUpViewModelFactory

    lateinit var signInViewModel: SignInViewModel
    lateinit var signInViewModelFactory: SignInViewModelFactory
    var firebaseAuth = FirebaseAuth.getInstance()



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

        signInViewModelFactory = SignInViewModelFactory(
            ShopifyRepositoryImp.getInstance(
                ShopifyRemoteDataSourceImp.getInstance()
            )
        )

        signInViewModel = ViewModelProvider(this, signInViewModelFactory).get(SignInViewModel::class.java)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.guest.setOnClickListener {
            SharedPreference.saveGuest(requireContext(),"yes")
            startActivity(Intent(context, BottomNavActivity::class.java))
            SharedPreference.saveUserEmail(requireContext(),"guest")
          //  Firebase(requireContext()).saveLoginState(true)
        }
        var email = binding.emailEditText.text.toString().trim()
        var password = binding.password.text.toString().trim()
        signInViewModel.getCustomerByEmail(email)

        binding.login.setOnClickListener {
            SharedPreference.saveGuest(requireContext(),"no")
            var email = binding.emailEditText.text.toString().trim()
            var password = binding.password.text.toString().trim()
          //  val firebaseUser = FirebaseAuth.getInstance().currentUser

            if (email.isEmpty()) {
                binding.emailEditText.error = "Email cannot be empty"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }
           else if (!isValidEmail(email)) {
                binding.emailEditText.error = "Invalid Email "
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }
          else  if (password.isEmpty()) {
                binding.password.error = "Password cannot be empty"
                binding.password.requestFocus()
                return@setOnClickListener
            }
            else {

                binding.progressBar.visibility = View.VISIBLE

                Firebase(requireContext()).loginClient(requireContext(), email, password) { isSuccess ->
                    binding.progressBar.visibility = View.GONE
                    if (!isSuccess) {
                        binding.password.error = "Incorrect password"
                        binding.password.requestFocus()
                    } else {
                        Snackbar.make(requireView(), "User logged in successfully", Snackbar.LENGTH_SHORT).show()
                        Firebase(requireContext()).saveLoginState(true)
                        SharedPreference.saveUserEmail(requireContext(), email)
                     //   collectCustomerDetails(email, password)
                    }
                }

                lifecycleScope.launch {
                    signInViewModel.login.collectLatest { result ->
                        when (result) {
                            is ApiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                Log.i(TAG, "Loading state")

                            }

                            is ApiState.Success<*> -> {
                                binding.progressBar.visibility = View.GONE
                                val person = result.data as? createCustomersResponse
                                signInViewModel.getCustomerByEmail(email)

                                if(person != null) {

                                    if (person.customers.isNotEmpty()) {

//                                        var firstName = person.customers.get(0).first_name
//                                        Log.i(
//                                            TAG,
//                                            "onViewCreated: first name ${person?.customers?.get(0)?.first_name}"
//                                        )
//                                        var email1 = person.customers.get(0).email
//                                        Log.i(TAG, "onViewCreated: email1$email1")
//                                        if (!firstName.isEmpty()) {
//
//                                            SharedPreference.saveFirstName(
//                                                requireContext(),
//                                                email,
//                                                firstName
//                                            )
//
//                                        }
//                                        var userEmail = person?.customers?.get(0)?.email
//                                        Log.i(TAG, "onViewCreated: userEmail ${userEmail}")
//                                        Log.i(
//                                            TAG,
//                                            "onViewCreated: userEmail ${person?.customers?.get(0)?.tags}"
//                                        )
//                                        if (userEmail != email) {
//                                            binding.emailEditText.error = "Incorrect Email "
//                                            binding.emailEditText.requestFocus()
//                                            return@collectLatest
//
//                                        } else if (password != person?.customers?.get(0)?.tags) {
//                                            binding.password.error = "Incorrect password "
//                                            binding.password.requestFocus()
//                                            return@collectLatest
//                                        } else {
//
//
//                                            startActivity(
//                                                Intent(
//                                                    context,
//                                                    BottomNavActivity::class.java
//                                                )
//                                            )
//                                            Firebase(requireContext()).saveLoginState(true)
//                                            SharedPreference.saveUserEmail(
//                                                requireContext(),
//                                                userEmail
//                                            )
//                                            SharedPreference.saveUserEmail(requireContext(), email)
//                                        }
                                        if (person != null) {

                                            Log.i(
                                                TAG,
                                                "Customer ID in login: ${person.customers?.get(0)?.id}"
                                            )
                                            var userID = person.customers?.get(0)?.id
                                            if (userID != null) {

                                                val sharedPreferences =
                                                    requireContext().getSharedPreferences(
                                                        "MyPrefs",
                                                        Context.MODE_PRIVATE
                                                    )
                                                val editor = sharedPreferences.edit()
                                                editor.putString("userID", userID.toString())
                                                editor.apply()

                                                Log.i(
                                                    TAG,
                                                    "Saved userID to SharedPreferences: $userID"
                                                )
                                            } else {
                                                Log.i(TAG, "UserID is null")
                                            }

                                        } else {
                                            Log.i(TAG, "Received null person")
                                        }
                                    }
                                }
                            }

                            is ApiState.Failure -> {
                                binding.progressBar.visibility = View.GONE
                                Log.e(TAG, "API call failed: ${result.msg}")
                                // Handle the failure state here
                            }

                            else -> {

                                binding.progressBar.visibility = View.GONE
                                Log.i(TAG, "Unexpected state: ${result.javaClass.simpleName}")
                            }
                        }
                    }
                }
            }
        }
        binding.signup.setOnClickListener {
        //    (activity as? MainActivity)?.replaceFragment(SignUpFragment())
            if (savedInstanceState == null) {
                val transaction =
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, SignUpFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
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
            binding.progressBar.visibility = View.VISIBLE
            SharedPreference.saveGuest(requireContext(),"no")
        }
    }
    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null && account.idToken != null) {
                    handleSignInResult(account.idToken!!)
                    Snackbar.make(requireView(), "login with google successfully", Snackbar.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG,
                        "Google sign-in failed: ID token is null"
                    )
                }
            } catch (e: ApiException) {
                Log.e(TAG,
                    "Google sign-in failed: " + e.statusCode
                )
            }finally {
                binding.progressBar.visibility = View.GONE
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
                        Firebase(requireContext()).checkIfEmailExists(user.email!!) { exists ->
                            if (exists) {

                                proceedToNextPage(user)
                                signInViewModel.getCustomerByEmail(user.email!!)

                                Firebase(requireContext()).saveLoginState(true)
                            } else {

                                user.sendEmailVerification().addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Log.i(TAG, "Verification email sent to ${user.email}")
                                        proceedToNextPage(user)
                                    } else {
                                        Log.e(TAG, "Failed to send verification email.", emailTask.exception)
                                        Snackbar.make(requireView(), "Failed to send verification email.", Snackbar.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Google sign-in failed", task.exception)
                }
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun proceedToNextPage(user: FirebaseUser) {
        user.email?.let { SharedPreference.saveUserEmail(requireContext(), it) }
        Firebase(requireContext()).checkIfUserExists(user.uid) { userExists ->
            if (userExists) {
                startActivity(Intent(context, BottomNavActivity::class.java))
                Snackbar.make(requireView(), "Welcome back!", Snackbar.LENGTH_SHORT).show()
            } else {
                val customer = Customer(
                    0, user.email, null, null, "", "", "", "", 0, null, null,
                    true, null, null, "",null, null
                )
                Firebase(requireContext()).writeNewUser(customer)

                val client = createCustomerRequest(customer)
                signUpViewModel.registerCustomerInAPI(client)

                lifecycleScope.launch {
                    signInViewModel.login.collectLatest { result ->
                        when (result) {
                            is ApiState.Loading ->{
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is ApiState.Success<*> -> {
                                val person = result.data as? createCustomerRequest
                                Log.i(TAG, "Customer ID: ${person?.customer?.id}")

                                startActivity(Intent(context, BottomNavActivity::class.java))
                                Snackbar.make(requireView(), "User logged in with Google successfully.", Snackbar.LENGTH_SHORT).show()
                            }
                            else -> {
                                // Handle other API states if needed
                            }
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }



}















//


//            val firebaseUser = FirebaseAuth.getInstance().currentUser
//
//            if (firebaseUser != null) {
//                Firebase(requireContext()).fetchUserData(firebaseUser, requireContext()) { firstName, email, password ->
//                    if (firstName != null && email != null && password != null) {
//                        var email1 = binding.emailEditText.text.toString().trim()
//                        var password1 = binding.password.text.toString().trim()
//                        if(email != email1){
//                            binding.emailEditText.error = "Incorrect Email "
//                            binding.emailEditText.requestFocus()
//                            return@fetchUserData
//                        }
//                        if(password1 != password){
//                            binding.password.error = "Incorrect Password"
//                            binding.password.requestFocus()
//                            return@fetchUserData
//
//                        }



//
//                        binding.progressBar.visibility = View.VISIBLE
//                        Firebase(requireContext()).loginClient(requireContext(), email, password) { isSuccess ->
//                            binding.progressBar.visibility = View.GONE
//                            if (!isSuccess) {
//                                binding.password.error = "Incorrect password"
//                                binding.password.requestFocus()
//                            }
//                            else{
//                                Snackbar.make(requireView(), "User logged in successfully", Snackbar.LENGTH_SHORT).show()
//                                Firebase(requireContext()).saveLoginState(true)
//                                SharedPreference.saveUserEmail(requireContext(), email)
//
//                            }
//                        }
//                        // Use the retrieved firstName and email
//                        Log.d("UserData", "First Name: $firstName, Email: $email")
//                        // Update UI or perform actions based on retrieved data
//                    } else {
//                        // Handle case where data could not be retrieved
//                        Log.e("UserData", "Error: Unable to fetch user data")
//                    }
//                }
//            }

//                firebaseAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            // Toast.makeText(context, "User logged in successfully", Toast.LENGTH_LONG).show()
//                            startActivity(Intent(context, BottomNavActivity::class.java))
//                            Firebase(requireContext()).saveLoginState(true)
//                            Snackbar.make(requireView(), "User logged in successfully", Snackbar.LENGTH_SHORT).show()
////                            Firebase(requireContext()).saveLoginState(true)
//                            SharedPreference.saveUserEmail(requireContext(), email)
//                         //   collectCustomerDetails(email, password)
//
//                        } else {
//                            val exception = task.exception
//                            if (exception is FirebaseAuthInvalidUserException) {
//                                // Handle non-existent email
//                                binding.emailEditText.error = "Email does not exist "
//                                binding.emailEditText.requestFocus()
//                                Toast.makeText(context, "Login Error: Email does not exist", Toast.LENGTH_LONG).show()
//                            } else {
//                                // Handle other errors
//                                binding.password.error = "Incorrect password"
//                                binding.password.requestFocus()
//                                Toast.makeText(context, "Login Error: " + exception!!.message, Toast.LENGTH_LONG).show()
//                            }
//
//                        }
//

