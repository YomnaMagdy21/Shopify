package com.example.shopify.productDetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.MainCoroutineRule
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.productDetails.Product
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.productdetails.viewmodel.ProductDetailsViewModel
import com.example.shopify.utility.ApiState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.notNullValue

@RunWith(JUnit4::class)
class ProductDetailsViewModelTest {
    @get:Rule
    val myRule= InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule= MainCoroutineRule()

    lateinit var productDetailsViewModel: ProductDetailsViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit var product: Product
    lateinit var productModel: ProductModel


    @Before
    fun setUp(){
        repo= FakeShopifyRepository()
        productDetailsViewModel = ProductDetailsViewModel(repo)
         product = Product(null,null,null,null,1234,null,
            listOf(),null,null,null,null,null,"",null,"",null,null,null,false)
        productModel = ProductModel(product)

    }

    @Test
    fun getProductInfo_ID_ProductDetails()= runBlockingTest{

        //when
        productDetailsViewModel.getProductInfo(8032546776522)

        var data = productDetailsViewModel.productInfo.getOrAwaitValue {  }

        //Then

        while (data is ApiState.Loading) {
            delay(100)
            data = productDetailsViewModel.productInfo.getOrAwaitValue { }
        }

        // Then
        assertThat(data, `is`(instanceOf(ApiState.Success::class.java)))
        val successState = data as ApiState.Success<*>
        val result = successState.data as ProductModel
        assertEquals(result.product?.id, productModel.product?.id)

    }

    @Test
    fun getProductInfo_ID_ProductDetailsNotNull()=runBlockingTest{
        //when
        productDetailsViewModel.getProductInfo(8032546776522)

        var data = productDetailsViewModel.productInfo.getOrAwaitValue {  }

        //Then
        assertThat(data, `is`(notNullValue()))

    }
}