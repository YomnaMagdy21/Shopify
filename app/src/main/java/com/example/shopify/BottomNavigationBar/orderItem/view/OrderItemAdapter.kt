package com.example.shopify.BottomNavigationBar.orderItem.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopify.R
import com.example.shopify.model.PostOrders.LineItem
import com.example.shopify.model.PostOrders.NoteAttribute

class OrderItemAdapter (private val context: Context, private var lineItems: List<LineItem>/*,private val noteAttributes: List<NoteAttribute>*/
) : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.order_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lineItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lineItem = lineItems[position]
        holder.nameProduct.text = lineItem.name
        holder.priceProduct.text = lineItem.price + " EGP"
        holder.quantityProduct.text = lineItem.quantity.toString()



//
//        val noteAttribute = noteAttributes.getOrNull(position)
//
//        // Display images associated with this line item's note attributes
//        noteAttribute?.values?.forEach { imageUrl ->
//            Glide.with(context)
//                .load(imageUrl)
//                .placeholder(R.drawable.bag) // Placeholder image while loading
//                .error(R.drawable.bag) // Error image if Glide fails to load
//                .into(holder.imageProduct)
//
//        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameProduct: TextView = itemView.findViewById(R.id.tv_product_name_value)
        val priceProduct: TextView = itemView.findViewById(R.id.tv_product_price_value)
        val quantityProduct: TextView = itemView.findViewById(R.id.tv_product_quantity_value)
        val card: CardView = itemView.findViewById(R.id.card_order_item)
//        val imageProduct : ImageView = itemView.findViewById(R.id.iv_order_item)

    }
}
