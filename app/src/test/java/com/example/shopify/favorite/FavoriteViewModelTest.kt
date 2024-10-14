package com.example.shopify.favorite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.MainCoroutineRule
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.productdetails.viewmodel.ProductDetailsViewModel
import com.example.shopify.utility.ApiState
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest

import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FavoriteViewModelTest {
    @get:Rule
    val myRule= InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule= MainCoroutineRule()

    lateinit var favoriteViewModel:FavoriteViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit var favDraftOrder:FavDraftOrder
    lateinit var favDraftOrderResponse: FavDraftOrderResponse
    @Before
    fun setUp() {
        repo = FakeShopifyRepository()
        favoriteViewModel = FavoriteViewModel(repo)
         favDraftOrder = FavDraftOrder()
         favDraftOrderResponse= FavDraftOrderResponse(favDraftOrder)

    }

    @Test
    fun getFavorite_FavDraftOrder_FavDraftOrderResponse()= runBlockingTest{
        //when
        favoriteViewModel.getFavorites(8032546776522)

        var fav = favoriteViewModel.fav.getOrAwaitValue {  }


        while (fav is ApiState.Loading) {
           delay(100)
            fav =  favoriteViewModel.fav.getOrAwaitValue {  }
        }

        // Then
        assertThat(fav, `is`(instanceOf(ApiState.Success::class.java)))
        val successState = fav as ApiState.Success<*>
        val result = successState.data as FavDraftOrderResponse
        assertEquals(result.draft_order, favDraftOrderResponse.draft_order)

    }

    @Test
    fun updateFavorite_IdAndFavDraftOrderResponse_LineItems()= runBlockingTest{
        //when
        favoriteViewModel.updateFavorite(8032546776522,favDraftOrderResponse)

        var fav = favoriteViewModel.fav.getOrAwaitValue {  }


        while (fav is ApiState.Loading) {
            delay(100)
            fav =  favoriteViewModel.fav.getOrAwaitValue {  }
        }

        // Then
        assertThat(fav, `is`(instanceOf(ApiState.Success::class.java)))
        val successState = fav as ApiState.Success<*>
        val result = successState.data as FavDraftOrderResponse
        assertEquals(result.draft_order?.line_items, favDraftOrderResponse.draft_order?.line_items)

    }

    @Test
    fun deleteFavorite_ID()= runBlockingTest{
        //when
        favoriteViewModel.deleteFavorite(8032546776523)

        var fav = favoriteViewModel.fav.getOrAwaitValue {  }


        while (fav is ApiState.Loading) {
            delay(100)
            fav =  favoriteViewModel.fav.getOrAwaitValue {  }
        }

        // Then
        assertThat(fav, `is`(instanceOf(ApiState.Success::class.java)))
        val successState = fav as ApiState.Success<*>
        val result = successState.data as FavDraftOrderResponse
        assertThat(result.draft_order?.id, `is`(nullValue()))

    }


}