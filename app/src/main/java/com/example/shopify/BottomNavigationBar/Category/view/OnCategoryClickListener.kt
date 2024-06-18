package com.example.shopify.BottomNavigationBar.Category.view

import com.example.shopify.model.productDetails.Product

interface OnCategoryClickListener {
    fun onCategoryClick(id:Long)
    fun onFavBtnClick(favorite: Product)
    fun onClickToRemove(id: Long)
}