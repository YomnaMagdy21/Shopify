package com.example.shopify.cart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.MainCoroutineRule
import com.example.shopify.ShoppingCart.viewModel.ShoppingCardViewModel
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class cartTest {
    @get:Rule
    val myRule= InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule= MainCoroutineRule()

    lateinit var cartViewModel: ShoppingCardViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit var cartDraftOrder: DraftOrder
    lateinit var cartDraftOrderResponse: DraftOrderResponse


}