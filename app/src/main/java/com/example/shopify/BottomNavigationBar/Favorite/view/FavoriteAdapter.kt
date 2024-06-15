package com.example.shopify.BottomNavigationBar.Favorite.view//package com.example.shopify.BottomNavigationBar.Favorite

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.model.ItemLine
import com.example.shopify.databinding.FavItemBinding
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.Draft_orders_list

class FavoriteAdapter(var context: Context, var listener: onFavoriteClickListener):
    ListAdapter<ItemLine, FavoriteAdapter.DayViewHolder>(DayDiffUtil()) {


    lateinit var binding: FavItemBinding


    class DayViewHolder(var binding: FavItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        binding = FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val current = getItem(position)

        holder.binding.productName.text = current.title
      //  holder.binding.productImg.setImageResource(current.draft_orders.get(0).)
        holder.binding.cardView.setOnClickListener {
            listener.onFavClick()
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