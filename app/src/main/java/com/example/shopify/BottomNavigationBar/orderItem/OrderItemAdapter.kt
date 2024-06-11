package com.example.shopify.BottomNavigationBar.orderItem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R

class OrderItemAdapter ( private val context: Context,
private val names: List<String>,
private val prices: List<String>,
) : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.order_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameProduct.text = names[position]
        holder.priceProduct.text = prices[position]
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameProduct: TextView = itemView.findViewById(R.id.tv_product_name_value)
        val priceProduct: TextView = itemView.findViewById(R.id.tv_product_price_value)
        val card: CardView = itemView.findViewById(R.id.card_order_item)
    }
}
