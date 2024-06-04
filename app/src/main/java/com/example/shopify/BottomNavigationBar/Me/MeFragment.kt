package com.example.shopify.BottomNavigationBar.Me

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.example.shopify.BottomNavigationBar.OrderList.OrderListFragment
import com.example.shopify.R
import com.example.shopify.setting.settingFragment


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
    }
}