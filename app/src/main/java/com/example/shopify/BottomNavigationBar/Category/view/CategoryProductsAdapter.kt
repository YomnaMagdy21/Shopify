package com.example.shopify.BottomNavigationBar.Category.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopify.R
import com.example.shopify.databinding.GuestAlertBinding
import com.example.shopify.login.view.SignInFragment
import com.example.shopify.model.productDetails.Product

import com.example.shopify.utility.SharedPreference

import com.example.shopify.setting.currency.CurrencyConverter
 

class CategoryProductsAdapter(private val context: Context,
                              var listener: OnCategoryClickListener,
                              private var myProducts: List<Product>
                              /*private val onAddToCartClick: (Product) -> Unit*/) : RecyclerView.Adapter<CategoryProductsAdapter.ViewHolder>() {


         lateinit var binding: GuestAlertBinding
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
        binding = GuestAlertBinding.inflate(inflater)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product  = myProducts.get(position)
        holder.productName.text = product.title?.split("|")?.last()?.trim()
       // holder.productPrice.text = product.variants?.get(0)?.price
        Glide.with(context).load(product.image?.src).into(holder.productImage)
        holder.card.setOnClickListener {
            listener.onCategoryClick(product.id?: 8663275405476)
        }

        val convertedPrice = product.variants?.get(0)?.price?.let {
            CurrencyConverter.convertToUSD(
                it.toDouble() )
        }
        holder.productPrice.text = convertedPrice?.let { CurrencyConverter.formatCurrency(it) }
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
                showAlertDialog()
//                val builder = AlertDialog.Builder(context)
//                builder.setTitle("Warning")
//                builder.setMessage("You are guest, you can't add to favorite")
//                builder.setPositiveButton("ok") { dialog, which ->
//
//                }
//
//                builder.show()
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
                    val inflater = LayoutInflater.from(context)
                    val dialogView = inflater.inflate(R.layout.remove_fav, null)
                    dialogView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val alertDialog = AlertDialog.Builder(context)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create()

                    val yes: Button = dialogView.findViewById(R.id.yes)
                    val no: Button = dialogView.findViewById(R.id.no)

                    yes.setOnClickListener {
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
                        alertDialog.dismiss()
                    }

                    no.setOnClickListener {
                        alertDialog.dismiss()
                    }

                    alertDialog.show()

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

    fun showAlertDialog() {
//        val dialog= Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        binding = GuestAlertBinding.inflate(LayoutInflater.from(context))
//        dialog.setContentView(binding.root)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        binding.login.setOnClickListener {
//            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.home_fragment, SignInFragment())
//            transaction.addToBackStack(null)
//            transaction.commit()
//            dialog.dismiss()
//        }
//        binding.cancel.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.guest_alert, null)
        dialogView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val loginButton: Button = dialogView.findViewById(R.id.login)
        val cancelButton: Button = dialogView.findViewById(R.id.cancel)

        loginButton.setOnClickListener {
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.home_fragment, SignInFragment())
            transaction.addToBackStack(null)
            transaction.commit()
            alertDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}
