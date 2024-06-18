package com.example.shopify.BottomNavigationBar.Category.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopify.R
import com.example.shopify.model.productDetails.Product
import com.example.shopify.utility.SharedPreference

class CategoryProductsAdapter(private val context: Context,
                              var listener: OnCategoryClickListener,
                              private var myProducts: List<Product>
                              /*private val onAddToCartClick: (Product) -> Unit*/) : RecyclerView.Adapter<CategoryProductsAdapter.ViewHolder>() {
    fun updateData(myNewProducts: List<Product>){
        this.myProducts = myNewProducts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return myProducts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.category_products_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product  = myProducts.get(position)
        holder.productName.text = product.title?.split("|")?.last()?.trim()
        holder.productPrice.text = product.variants?.get(0)?.price + " EGP"
        Glide.with(context).load(product.image?.src).into(holder.productImage)
        holder.card.setOnClickListener {
            listener.onCategoryClick(product.id?: 8663275405476)
        }
        /*holder.addToCartButton.setOnClickListener {
            onAddToCartClick(product)
        }*/
      var email = SharedPreference.getUserEmail(context)
//        product.variants?.get(0)?.id?.let { it1 ->
//            SharedPreference.saveFav(context,
//                it1,email,false)
//        }
        val fav = product.variants?.get(0)?.id?.let { it1 ->
            SharedPreference.getFav(context,
                it1,email
            )
        }
        if (fav==true) {
            holder.fav.setImageResource(R.drawable.favorite_24)
        } else {
            holder.fav.setImageResource(R.drawable.favorite_border_24)
        }

        holder.fav.setOnClickListener {
            var guest = SharedPreference.getGuest(context)
          //  var email = SharedPreference.getUserEmail(context)
            if(guest == "yes"){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Warning")
                builder.setMessage("You are guest, you can't add to favorite")
                builder.setPositiveButton("ok") { dialog, which ->

                }

                builder.show()
            }else {
                val isFav =
                    product.variants?.get(0)?.id?.let { it1 ->
                        SharedPreference.getFav(
                            context,
                            it1,
                            email
                        )
                    }
                if (isFav == true) {
                    product.variants?.get(0)?.id?.let { it1 ->
                        SharedPreference.saveFav(
                            context,
                            it1, email, false
                        )
                    }
                    // SharedPreference.saveFav(context, current.variants?.get(0)?.id!!,false)
                    // sharedPreferences.edit().putBoolean(current.id.toString(), false).apply()
                    holder.fav.setImageResource(R.drawable.favorite_border_24)
                    product.variants?.get(0)?.id?.let { it1 -> listener.onClickToRemove(it1) }
                    //  current.id?.let { it1 -> FavoriteFragment().deleteFav(it1) }
                } else {
                    product.variants?.get(0)?.id?.let { it1 ->
                        SharedPreference.saveFav(
                            context,
                            it1, email, true
                        )
                    }

                    //  sharedPreferences.edit().putBoolean(current.id.toString(), true).apply()
                    holder.fav.setImageResource(R.drawable.favorite_24)
                    listener.onFavBtnClick(product)
                }
            }

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.iv_product_category)
        val productName: TextView = itemView.findViewById(R.id.tv_product_name_category_card)
        val productPrice: TextView = itemView.findViewById(R.id.tv_product_price_category_card)
        val card: CardView = itemView.findViewById(R.id.products_card_category)
        val fav : ImageView = itemView.findViewById(R.id.fav)
    }
}
