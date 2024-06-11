package com.example.shopify.shoppingCard.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopify.R
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.draftModel.LineItem

class ShoppingCardAdapter(private var items: List<Item>,
                          private val onAddProduct: (Item) -> Unit,
                          private val onRemoveProduct: (Item) -> Unit) : RecyclerView.Adapter<ShoppingCardAdapter.ShoppingCardViewHolder>() {

    class ShoppingCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.myCardList)
        val imageView: ImageView = view.findViewById(R.id.cardListImage)
        val titleTextView: TextView = view.findViewById(R.id.titleOfProduct)
        val priceTextView: TextView = view.findViewById(R.id.price)
        val plusTextView: TextView = view.findViewById(R.id.textViewPlus)
        val numberOfItemsTextView: TextView = view.findViewById(R.id.textViewNumberOfItems)
        val reduceTextView: TextView = view.findViewById(R.id.textViewReduce)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardlistitem, parent, false)
        return ShoppingCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingCardViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.priceTextView.text = item.price
        holder.numberOfItemsTextView.text = item.numberOfItems.toString()
        //holder.imageView.setImageResource(item.imageResId)
        Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.imageView)

        holder.plusTextView.setOnClickListener {
            onAddProduct(item)
        }

        holder.reduceTextView.setOnClickListener {
            onRemoveProduct(item)
        }

    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}


data class Item(
    val title: String,
    val price: String,
    var numberOfItems: Int,
    //val imageResId: Int,
    val imageUrl: String
)