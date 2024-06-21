package com.example.shopify.products.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.productDetails.Product
import com.example.shopify.utility.ApiState
import junit.framework.TestCase
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
class ProductsOfBrandViewModelTest{
    @get:Rule
    val myRule= InstantTaskExecutorRule()

    lateinit var productsOfBrandViewModel: ProductsOfBrandViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit  var product : List<Product>
    lateinit var collectProductsModel: CollectProductsModel


    @Before
    fun setUp(){
        repo= FakeShopifyRepository()
        productsOfBrandViewModel = ProductsOfBrandViewModel(repo)
        product = listOf(
            Product(null,null,null,null,24125,null,
            listOf(),null,"women",null,null,null,"",null,"product2",null,null,null,false)
        )

        collectProductsModel = CollectProductsModel(product)

    }


    @Test
    fun getProductsOfBrands_ID_ProductsOfBrandsNotNull() = runBlockingTest{
        //When
        productsOfBrandViewModel.getProductsOfBrands(24125)

        val apiState = productsOfBrandViewModel.accessProductsList.getOrAwaitValue {  } as ApiState.Success<*>

        var result = apiState.data as CollectProductsModel
        while (apiState.data is ApiState.Loading) {
            delay(100)
            result = apiState.data as CollectProductsModel
        }

        //Then
        assertThat(result, `is`(notNullValue()))
    }

    @Test
    fun getProductsOfBrands_ID_ProductsOfBrandsFirstItem() = runBlockingTest{
        //When
        productsOfBrandViewModel.getProductsOfBrands(24125)

        val apiState = productsOfBrandViewModel.accessProductsList.getOrAwaitValue {  } as ApiState.Success<*>

        var result = apiState.data as CollectProductsModel
        while (apiState.data is ApiState.Loading) {
            delay(100)
            result = apiState.data as CollectProductsModel
        }

        //Then
        TestCase.assertEquals(result.products[0].id, collectProductsModel.products[0].id)
    }

}