package com.example.shopify.products.view

import com.example.shopify.BottomNavigationBar.Favorite.model.Favorite
import com.example.shopify.model.productDetails.Product

interface OnProductClickListener {
    fun goToDetails(id:Long)
    fun onFavBtnClick(favorite: Product)
    fun onClickToRemove(id: Long)
}