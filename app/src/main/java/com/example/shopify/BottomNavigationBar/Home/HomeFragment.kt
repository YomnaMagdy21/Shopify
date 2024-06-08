package com.example.shopify.BottomNavigationBar.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.shopify.R
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.shopify.products.view.ProductsFragment



class HomeFragment : Fragment() , OnBrandClickListener{

    private lateinit var viewPager: ViewPager2
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var brandsRecyclerView: RecyclerView


    private lateinit var brandsAdapter: BrandsAdapter


   private val images = listOf(
        R.drawable.ads2,
        R.drawable.ads3,
        R.drawable.ads4
   )


    private val brandImages = listOf(
        R.drawable.nike,
        R.drawable.puma,
        R.drawable.adiddas,
        R.drawable.ck2,
        R.drawable.nike,
        R.drawable.puma,
        R.drawable.adiddas,
        R.drawable.ck2
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        brandsRecyclerView = view.findViewById(R.id.rv_brands_in_home)

        setupRecyclerView()
        setupImageSlider()
        return view
    }
    private fun setupImageSlider() {
        imageSliderAdapter = ImageSliderAdapter(images)
        viewPager.adapter = imageSliderAdapter

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            var currentItem = viewPager.currentItem
            val itemCount = imageSliderAdapter.itemCount

            if (currentItem == itemCount - 1) {
                currentItem = 0
            } else {
                currentItem++
            }

            viewPager.setCurrentItem(currentItem, true)
            handler.postDelayed(runnable, 3000)
        }

        handler.postDelayed(runnable, 3000)
    }


    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 2) // 2 columns
        brandsRecyclerView.layoutManager = gridLayoutManager
        brandsAdapter = BrandsAdapter(requireContext(), brandImages,this)
        brandsRecyclerView.adapter = brandsAdapter
    }

    override fun goToProducts() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, ProductsFragment() )
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
