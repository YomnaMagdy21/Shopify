package com.example.shopify.signup.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shopify.BottomNavigationBar.BottomNavActivity
import com.example.shopify.BottomNavigationBar.Home.HomeFragment

import com.example.shopify.R
import com.example.shopify.databinding.FragmentSignInBinding
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
import com.google.firebase.auth.FirebaseAuth
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

        signUpViewModel = ViewModelProvider(this, signUpViewModelFactory).get(signUpViewModel::class.java)


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
        binding.signup.setOnClickListener {
           email=binding.email.text.toString()
            password=binding.password.text.toString()
            firstname=binding.firstname.text.toString()
            lastname=binding.secondname.text.toString()
            password_confirmation=binding.confirm.text.toString()
            Firebase().createCustomerAccount(email,password){ user, error ->
                if(user != null){

                    var customer = Customer(0,email,null,null,firstname,lastname,password,password_confirmation,0,null,null,true,null,null,null,null)
                    var client = createCustomerRequest(customer)
                    signUpViewModel.registerCustomerInAPI(client)
                    lifecycleScope.launch {

                        signUpViewModel.register.collectLatest {result ->
                            when(result){

                                is ApiState.Success<*> -> {
                                   var person = result.data as? createCustomerRequest
                                    Log.i(TAG, "onViewCreated: ${person?.customer?.id}")

                                }
                                else ->{

                                }
                            }
                        }
                    }
                }

            }


//
            val intent= Intent(requireContext(),BottomNavActivity::class.java)
            startActivity(intent)

        }




    }

}