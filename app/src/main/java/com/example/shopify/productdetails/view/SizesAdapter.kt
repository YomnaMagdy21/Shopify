package com.example.shopify.productdetails.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopify.BottomNavigationBar.Favorite.model.AllProductImages
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.BottomNavigationBar.Favorite.view.FavoriteAdapter
import com.example.shopify.BottomNavigationBar.Favorite.view.onFavoriteClickListener
import com.example.shopify.R
import com.example.shopify.databinding.FavItemBinding
import com.example.shopify.databinding.SizeItemBinding
import com.example.shopify.model.productDetails.Product
import com.example.shopify.setting.currency.CurrencyConverter
import com.example.shopify.utility.SharedPreference

class SizesAdapter(var context: Context, var listener: OnProductDetailsListener) :
    ListAdapter<String, SizesAdapter.DayViewHolder>(DayDiffUtil()) {

    private var selectedItem = 0


    class DayViewHolder(var binding: SizeItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = SizeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)

        holder.binding.size.text = current

        val backgroundColor = if (selectedItem == position) {
            R.color.gray
        } else {
            R.color.backgroundColor1
        }
        holder.binding.sizeCard.setCardBackgroundColor(
            ContextCompat.getColor(context, backgroundColor)
        )


        holder.binding.sizeCard.setOnClickListener {
            listener.onSizeClick(current)
            val previousSelected = selectedItem
            selectedItem = holder.adapterPosition

            if (previousSelected != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousSelected)
            }
            notifyItemChanged(selectedItem)
        }
    }


    }




class DayDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return newItem == oldItem
    }
}
