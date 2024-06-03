package com.example.shopify.BottomNavigationBar.Category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R

class CategoryProductsAdapter(private val context: Context) : RecyclerView.Adapter<CategoryProductsAdapter.ViewHolder>() {

    private val productImages = listOf(R.drawable.clothes1, R.drawable.clothes2, R.drawable.shoes2, R.drawable.clothes, R.drawable.clothes1, R.drawable.clothes2, R.drawable.shoes2, R.drawable.clothes)
    private val productNames = listOf("Product1", "Product2", "Product3", "Product4", "Product1", "Product2", "Product3", "Product4")
    private val productPrices = listOf("20.00", "50.00", "80.00", "60.00", "20.00", "50.00", "80.00", "60.00")

    override fun getItemCount(): Int {
        return productImages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.category_products_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.productImage.setImageResource(productImages[position])
        holder.productName.text = productNames[position]
        holder.productPrice.text = productPrices[position]
        holder.productCurrency.text = context.getString(R.string.egp)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.iv_product_category)
        val productName: TextView = itemView.findViewById(R.id.tv_product_name_category_card)
        val productPrice: TextView = itemView.findViewById(R.id.tv_product_price_category_card)
        val productCurrency: TextView = itemView.findViewById(R.id.tv_product_currency_category_card)
        val card: CardView = itemView.findViewById(R.id.products_card_category)
    }
}
