package com.example.shopify.BottomNavigationBar.Category


import com.example.shopify.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.productdetails.view.ProductDetailsFragment

class CategoryFragment : Fragment() ,OnCategoryClickListener{

    private lateinit var tvAll: TextView
    private lateinit var tvWomen: TextView
    private lateinit var tvMen: TextView
    private lateinit var tvKids: TextView
    private lateinit var ivClothes: ImageView
    private lateinit var ivShoes: ImageView
    private lateinit var ivBags: ImageView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        tvAll = view.findViewById(R.id.tv_main_category_all)
        tvWomen = view.findViewById(R.id.tv_main_category_women)
        tvMen = view.findViewById(R.id.tv_main_category_men)
        tvKids = view.findViewById(R.id.tv_main_category_kids)
        ivClothes = view.findViewById(R.id.iv_sub_cat_clothes)
        ivShoes = view.findViewById(R.id.iv_sub_cat_shoes)
        ivBags = view.findViewById(R.id.iv_sub_cat_bags)

        recyclerView = view.findViewById(R.id.rv_products_in_category)
        adapter = CategoryProductsAdapter(requireContext(),this)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        // Click listeners for TextViews
        tvAll.setOnClickListener { selectCategory(tvAll) }
        tvWomen.setOnClickListener { selectCategory(tvWomen) }
        tvMen.setOnClickListener { selectCategory(tvMen) }
        tvKids.setOnClickListener { selectCategory(tvKids) }

        // Click listeners for ImageViews
        ivClothes.setOnClickListener { selectImageView(ivClothes) }
        ivShoes.setOnClickListener { selectImageView(ivShoes) }
        ivBags.setOnClickListener { selectImageView(ivBags) }

        // Default selections
        selectCategory(tvAll)
        selectImageView(ivClothes)

        return view
    }

    private fun selectCategory(selectedTextView: TextView) {
        val categories = listOf(tvAll, tvWomen, tvMen, tvKids)
        categories.forEach { textView ->
            textView.setBackgroundResource(R.drawable.rounded_unselected_text_view)
            textView.setTextColor(resources.getColor(R.color.black))
        }

        selectedTextView.setBackgroundResource(R.drawable.rounded_selected_text_view)
        selectedTextView.setTextColor(resources.getColor(R.color.white))
    }

    private fun selectImageView(selectedImageView: ImageView) {
        val imageViews = listOf(ivClothes, ivShoes, ivBags)
        imageViews.forEach { imageView ->
            imageView.setBackgroundResource(R.drawable.rounded_unselected_image_view_filter)
        }

        selectedImageView.setBackgroundResource(R.drawable.rounded_selected_image_view_filter)
    }

    override fun onCategoryClick() {
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, ProductDetailsFragment() )
        transaction.addToBackStack(null)
        transaction.commit()
    }
}