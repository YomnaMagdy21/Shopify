package com.example.shopify.BottomNavigationBar.Favorite.view//package com.example.shopify.BottomNavigationBar.Favorite

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.databinding.FavItemBinding

class FavoriteAdapter(var context: Context, var listener: onFavoriteClickListener):
    ListAdapter<Favorite, FavoriteAdapter.DayViewHolder>(DayDiffUtil()) {


    lateinit var binding: FavItemBinding


    class DayViewHolder(var binding: FavItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)

        holder.binding.productName.text = current.name
        holder.binding.productImg.setImageResource(current.img)
        holder.binding.cardView.setOnClickListener {
            listener.onFavClick()
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