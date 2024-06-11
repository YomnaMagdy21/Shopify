package com.example.shopify.productdetails.view


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.shopify.databinding.ViewpagerItemBinding

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

        binding.imageView.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        binding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
