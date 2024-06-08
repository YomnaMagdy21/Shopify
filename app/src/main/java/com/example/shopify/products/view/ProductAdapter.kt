package com.example.shopify.products.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.view.FavoriteAdapter
import com.example.shopify.databinding.FavItemBinding
import com.example.shopify.databinding.ProductItemBinding

class ProductAdapter (var context: Context, var listener: OnProductClickListener):
    ListAdapter<Favorite, ProductAdapter.DayViewHolder>(DayDiffUtil()) {


    lateinit var binding: ProductItemBinding


    class DayViewHolder(var binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)

        holder.binding.productName.text = current.name
        holder.binding.productImg.setImageResource(current.img)
        holder.binding.cardView.setOnClickListener {
            listener.goToDetails()
        }

    }
}

class DayDiffUtil : DiffUtil.ItemCallback<Favorite>() {
    override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return newItem == oldItem
    }

}