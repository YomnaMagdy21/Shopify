package com.example.shopify.BottomNavigationBar.Category.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.productDetails.Product
import com.example.shopify.utility.ApiState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class CategoryViewModelTest{

    @get:Rule
    val myRule= InstantTaskExecutorRule()

    lateinit var categoryViewModel: CategoryViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit  var product : List<Product>
    lateinit var collectProductsModel: CollectProductsModel


    @Before
    fun setUp(){
        repo= FakeShopifyRepository()
        categoryViewModel = CategoryViewModel(repo)
        product = listOf(Product(null,null,null,null,24125,null,
            listOf(),null,"women",null,null,null,"",null,"product2",null,null,null,false))

        collectProductsModel = CollectProductsModel(product)

    }


    @Test
    fun getAllProducts_IDAndType_AllProductsNotNull() = runBlockingTest{
        //When
        categoryViewModel.getProducts(24125, "women")

        val apiState = categoryViewModel.accessAllProductList.getOrAwaitValue {  } as ApiState.Success<*>

        var result = apiState.data as CollectProductsModel
        while (apiState.data is ApiState.Loading) {
            delay(100)
            result = apiState.data as CollectProductsModel
        }

        //Then
        assertThat(result, `is`(notNullValue()))
    }


    @Test
    fun getAllProducts_IDAndType_AllProductsFirstItem() = runBlockingTest{
        //When
        categoryViewModel.getProducts(24125, "women")

        val apiState = categoryViewModel.accessAllProductList.getOrAwaitValue {  } as ApiState.Success<*>

        var result = apiState.data as CollectProductsModel

        while (apiState.data is ApiState.Loading) {
            delay(100)
            result = apiState.data as CollectProductsModel
        }

        //Then
        assertEquals(result.products[0].id,collectProductsModel.products[0].id)
    }
}