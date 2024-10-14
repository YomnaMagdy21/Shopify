package com.example.shopify.BottomNavigationBar.Me

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.example.shopify.BottomNavigationBar.Favorite.view.FavoriteFragment
import com.example.shopify.BottomNavigationBar.OrderList.view.OrderListFragment
import com.example.shopify.MainActivity
import com.example.shopify.R
import com.example.shopify.firebase.Firebase
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.setting.view.settingFragment
import com.example.shopify.signup.view.SignUpFragment
import com.example.shopify.utility.SharedPreference


class MeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        var settingIcon = view.findViewById<CardView>(R.id.cardViewSetting)
        settingIcon.setOnClickListener{
            val newFragment = settingFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }

        var orderListFragment = view.findViewById<CardView>(R.id.cardViewOrders)
        orderListFragment.setOnClickListener{
            val newFragment = OrderListFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }
        var wishListFragment = view.findViewById<CardView>(R.id.cardViewWishList)
        wishListFragment.setOnClickListener{
            val newFragment = FavoriteFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, newFragment)
                .addToBackStack(null)
                .commit()
        }

        var name = view.findViewById<TextView>(R.id.textViewCardList)
        var login = view.findViewById<TextView>(R.id.button)
        var lottie = view.findViewById<LottieAnimationView>(R.id.animationViewGuest)


        var guest = SharedPreference.getGuest(requireContext())
        if(guest == "yes"){

            settingIcon.visibility = View.GONE
            orderListFragment.visibility = View.GONE
            wishListFragment.visibility = View.GONE
            name.text = ""
            login.visibility = View.VISIBLE
            lottie.visibility =View.VISIBLE
            login.setOnClickListener {
                val intent = Intent(requireContext(), MainActivity::class.java)
           //     intent.putExtra("fragmentToLoad", "signInFragment")
                startActivity(intent)
               // (activity as? MainActivity)?.replaceFragment(SignInFragment())
                Firebase(requireContext()).saveLoginState(false)
            }
//                parentFragmentManager.beginTransaction()
//                .replace(R.id.home_fragment, SignInFragment())
//                .commit()
            //
            //           }
        } else {
            var email = SharedPreference.getUserEmail(requireContext())
           // var firstName = SharedPreference.getFirstName(requireContext(), email)
            name.text ="Welcome "
        }
    }
}