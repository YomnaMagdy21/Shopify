package com.example.shopify.BottomNavigationBar.Favorite.view//package com.example.shopify.BottomNavigationBar.Favorite

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.BottomNavigationBar.Favorite.model.Items
import com.example.shopify.R
import com.example.shopify.databinding.FavItemBinding
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.Draft_orders_list
import com.example.shopify.model.productDetails.Product
import com.example.shopify.model.productDetails.ProductModel

class FavoriteAdapter(var context: Context, var listener: onFavoriteClickListener):
    ListAdapter<ItemLine, FavoriteAdapter.DayViewHolder>(DayDiffUtil()) {


    lateinit var binding: FavItemBinding
    lateinit var product : Product


    class DayViewHolder(var binding: FavItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)

        holder.binding.productName.text = current?.title

        product = Product(null,null,null,null,current.id,null,null,null,null,null,null,null,null,null,current.title,null,null,null,false)
        val sharedPreferences = context.getSharedPreferences("imgPref", Context.MODE_PRIVATE)
        val imgUrl = sharedPreferences.getString("img", null)

        Log.i("TAG", "onBindViewHolder: image ${imgUrl}")
//        if(current.id == product.id){
            Glide.with(context)
                .load(imgUrl)
                .placeholder(R.drawable.bag) // Placeholder image while loading
                .into(holder.binding.productImg)
       // }
//        if (!current?.sku.isNullOrEmpty()) {
//            // Check if the SKU is a URL or a resource identifier
//            Log.i("TAG", "onBindViewHolder: current.sku ${current?.sku}")
//            if (current?.sku?.startsWith("https://") == true) {
//                // Load image from URL
//                // You can use any image loading library like Picasso, Glide, Coil, etc.
//                // For example with Glide:
//                Glide.with(context)
//                    .load(current?.sku)
//                    .placeholder(R.drawable.bag2) // Placeholder image while loading
//                    .into(holder.binding.productImg)
//            } else {
//                // Assume SKU is a resource ID (example: R.drawable.image_name)
//                try {
//                    val resourceId = current?.sku?.toInt()
//                    if (resourceId != null) {
//                        holder.binding.productImg.setImageResource(resourceId)
//                    }
//                } catch (e: NumberFormatException) {
//                    // Handle invalid resource ID case
//                    holder.binding.productImg.setImageResource(R.drawable.bag2)
//                }
//            }
//        } else {
//            // Handle the case where SKU is null or empty
//            holder.binding.productImg.setImageResource(R.drawable.bag2)
//        }

        holder.binding.cardView.setOnClickListener {
            listener.onFavClick()
        }
        holder.binding.deleteImg.setOnClickListener {
            current?.id?.let { it1 -> listener.removeFavorite(it1) }
        }


    }

}

class DayDiffUtil : DiffUtil.ItemCallback<ItemLine>() {
    override fun areItemsTheSame(oldItem: ItemLine, newItem: ItemLine): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ItemLine, newItem: ItemLine): Boolean {
        return newItem == oldItem
    }

}

data class FavItem(var title:String)