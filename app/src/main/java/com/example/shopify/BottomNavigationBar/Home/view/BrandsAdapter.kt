package com.example.shopify.BottomNavigationBar.Home.view

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
import com.example.shopify.model.Brands.SmartCollection

class BrandsAdapter(private val context: Context, var brands: List<SmartCollection>, var listener: OnBrandClickListener) : RecyclerView.Adapter<BrandsAdapter.ViewHolder>() {

    fun setBrandsList(brandsList: List<SmartCollection>) {
        this.brands = brandsList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return brands.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.home_brands_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(brands[position].image.src).into(holder.brandImg)
        holder.brandTitle.text = brands[position].title
        holder.card.setOnClickListener {
            listener.goToProducts(brands[position].id)

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandImg: ImageView = itemView.findViewById(R.id.iv_brand_home)
        val brandTitle : TextView = itemView.findViewById(R.id.tv_brand_title)
        val card: CardView = itemView.findViewById(R.id.brands_card)
    }
}
