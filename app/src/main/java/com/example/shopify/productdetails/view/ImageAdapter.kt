package com.example.shopify.productdetails.view


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.shopify.R
import com.example.shopify.databinding.FragmentProductDetailsBinding
import com.example.shopify.databinding.ViewpagerItemBinding
import com.example.shopify.model.productDetails.Image

class ImageAdapter(private val context: Context, private val imageUrls: List<String>) : PagerAdapter() {


    private lateinit var binding: ViewpagerItemBinding


    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        binding = ViewpagerItemBinding.inflate(inflater, container, false)

        Glide.with(context)
            .load(imageUrls[position])
            .into(binding.imageView)

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
