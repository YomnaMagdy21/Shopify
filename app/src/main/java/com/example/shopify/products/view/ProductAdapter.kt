package com.example.shopify.products.view

import android.content.Context
import android.content.SharedPreferences
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.BottomNavigationBar.Favorite.view.FavoriteAdapter
import com.example.shopify.BottomNavigationBar.Favorite.view.FavoriteFragment
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModelFactory
import com.example.shopify.R
import com.example.shopify.databinding.FavItemBinding
import com.example.shopify.databinding.ProductItemBinding
import com.example.shopify.model.productDetails.Product
import com.example.shopify.utility.SharedPreference

class ProductAdapter(var context: Context, var productsOfBrand: List<Product>, var listener: OnProductClickListener): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("favPref", Context.MODE_PRIVATE)



    //    fun updateFavoriteState(productId: Long, isFavorite: Boolean) {
//        val editor = sharedPreferences.edit()
//        editor.putBoolean("fav_$productId", isFavorite)
//        editor.apply()
//        notifyDataSetChanged()
//    }
    fun setProductsBrandsList(productsOfBrand: List<Product>) {
        this.productsOfBrand = productsOfBrand
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return productsOfBrand.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.products_of_brand_card, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = productsOfBrand.getOrNull(position)

        current?.let {
            holder.productTitle.text = it.title

            val variants = current.variants
            if (variants != null && variants.isNotEmpty()) {
                holder.productPrice.text = variants[0].price + " EGP"
            } else {
                holder.productPrice.text = "Null" // Or any other suitable message
            }

            Glide.with(context).load(it.image?.src).into(holder.productImg)

//            val isFavorite = sharedPreferences.getBoolean("fav_${current.id}", false)
//            holder.fav.setImageResource(if (isFavorite) R.drawable.favorite_24 else R.drawable.favorite_border_24)
//
//            holder.fav.setOnClickListener{
//                listener.onFavBtnClick(current)
////                val fav = sharedPreferences.getBoolean("fav", false)
////                if(fav){
////                    holder.fav.setImageResource(R.drawable.favorite_24)
////                }
////                else{
////                    holder.fav.setImageResource(R.drawable.favorite_border_24)
////                }
//            }
//            if(current.isFav){
//                holder.fav.setImageResource(R.drawable.favorite_24)
//            }else{
//                holder.fav.setImageResource(R.drawable.favorite_border_24)
//            }

            val fav = current.variants?.get(0)?.id?.let { it1 ->
                SharedPreference.getFav(context,
                    it1
                )
            }
            if (fav==true) {
                holder.fav.setImageResource(R.drawable.favorite_24)
            } else {
                holder.fav.setImageResource(R.drawable.favorite_border_24)
            }

            holder.fav.setOnClickListener {
                val isFav =
                    current.variants?.get(0)?.id?.let { it1 -> SharedPreference.getFav(context, it1) }
                if (isFav == true) {
                    current.variants?.get(0)?.id?.let { it1 ->
                        SharedPreference.saveFav(context,
                            it1,false)
                    }
                   // SharedPreference.saveFav(context, current.variants?.get(0)?.id!!,false)
                   // sharedPreferences.edit().putBoolean(current.id.toString(), false).apply()
                    holder.fav.setImageResource(R.drawable.favorite_border_24)
                    current.variants?.get(0)?.id?.let { it1 -> listener.onClickToRemove(it1) }
                  //  current.id?.let { it1 -> FavoriteFragment().deleteFav(it1) }
                } else {
                    current.variants?.get(0)?.id?.let { it1 ->
                        SharedPreference.saveFav(context,
                            it1,true)
                    }

                    //  sharedPreferences.edit().putBoolean(current.id.toString(), true).apply()
                    holder.fav.setImageResource(R.drawable.favorite_24)
                    listener.onFavBtnClick(current)
                }

            }

        }


        var product  = productsOfBrand.get(position)

        holder.card.setOnClickListener {
            listener.goToDetails(product.id?: 8663275405476)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImg: ImageView = itemView.findViewById(R.id.iv_product_of_brand)
        val productTitle : TextView = itemView.findViewById(R.id.tv_product_title_of_brand)
        val productPrice : TextView = itemView.findViewById(R.id.tv_product_price_of_brand)

        val card: CardView = itemView.findViewById(R.id.products_of_brand_card)

        val fav : ImageView = itemView.findViewById(R.id.iv_add_to_fav)
    }


}
