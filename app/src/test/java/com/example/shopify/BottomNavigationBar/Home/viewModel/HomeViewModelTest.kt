package com.example.shopify.BottomNavigationBar.Home.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.MainCoroutineRule
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.Brands.Image
import com.example.shopify.model.Brands.SmartCollection
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.utility.ApiState
import junit.framework.TestCase
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
class HomeViewModelTest{

    @get:Rule
    val myRule= InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule= MainCoroutineRule()

    lateinit var homeViewModel: HomeViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit  var smartCollections : List<SmartCollection>
    lateinit var brandModel: BrandModel


    @Before
    fun setUp(){
        repo= FakeShopifyRepository()
        homeViewModel = HomeViewModel(repo)
        smartCollections = listOf(SmartCollection("11" , "body", true, "handle" , 2425, Image("","",1,"",20), "", "", listOf(), "", "","","" ))
        brandModel = BrandModel(smartCollections)

    }

    @Test
    fun getBrandsAndCheckNotNull() = runBlockingTest {
        //when
        homeViewModel.getBrands()
        val apiState = homeViewModel.accessBrandsList.getOrAwaitValue {  } as ApiState.Success<*>
        var result = apiState.data as BrandModel?


        //then
        assertThat(result, `is`(notNullValue()))
    }

    @Test
    fun getBrandsAndCheckTheSizeOfListIsEqualOne() = runBlockingTest {
        //when
        homeViewModel.getBrands()
        val apiState = homeViewModel.accessBrandsList.getOrAwaitValue {  } as ApiState.Success<*>
        var result = apiState.data as BrandModel?


        //then
        assertEquals(result?.smart_collections?.size, 1)
    }

    @Test
    fun getbrandsAndCheckTheResultAsExpectedOrNo()= runBlockingTest {
        //When
        homeViewModel.getBrands()   // return fake list od smartCollection
        val apiState = homeViewModel.accessBrandsList.getOrAwaitValue {  } as ApiState.Success<*>
        var result = apiState.data as BrandModel?


        while (apiState.data is ApiState.Loading) {
            delay(100)
            result = apiState.data as BrandModel?
        }
        //Then
        assertEquals(result?.smart_collections!![0].id,brandModel.smart_collections[0].id)

    }
}