package com.example.shopify.BottomNavigationBar.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopify.R

class BrandsAdapter(private val context: Context, private val brandImages: List<Int>,var listener: OnBrandClickListener) : RecyclerView.Adapter<BrandsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return brandImages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.home_brands_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageResource = brandImages[position]
        holder.image.setImageResource(imageResource)
        holder.card.setOnClickListener {
            (holder.itemView.context as? AppCompatActivity)?.let { activity ->
                listener.goToProducts()
            }

        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.iv_brand_home)
        val card: CardView = itemView.findViewById(R.id.brands_card)
    }
}
