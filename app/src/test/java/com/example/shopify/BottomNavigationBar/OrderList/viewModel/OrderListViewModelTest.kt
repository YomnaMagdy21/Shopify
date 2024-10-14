package com.example.shopify.BottomNavigationBar.OrderList.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.MainCoroutineRule
import com.example.shopify.Models.orderList.RetriveOrderModel
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.PostOrders.Order
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
class OrderListViewModelTest{
    @get:Rule
    val myRule= InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule= MainCoroutineRule()

    lateinit var orderListViewModel: OrderListViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit  var order: List<Order>
    lateinit var retriveOrderModel: RetriveOrderModel

    @Before
    fun setUp(){
        repo= FakeShopifyRepository()
        orderListViewModel = OrderListViewModel(repo)
        order = listOf( Order(null , null , null , null , "nermeenzaitoon@gmail.com" , null , 24125))
        retriveOrderModel = RetriveOrderModel(order)

    }

    @Test
    fun getSpecOrders_Email_SpecOrdersNotNull() = mainCoroutineRule.runBlockingTest{
        //When
        orderListViewModel.getSpecOrders("nermeenzaitoon@gmail.com")

        val apiState = orderListViewModel.accessOrderList.getOrAwaitValue {  } as ApiState.Success<*>

        var result = apiState.data as List<Order>
        while (apiState.data is ApiState.Loading) {
            delay(100)
            result = apiState.data as List<Order>
        }

        //Then
        assertThat(result, `is`(notNullValue()))
    }


    @Test
    fun getSpecOrders_Email_SpecOrdersFirstItem() = mainCoroutineRule.runBlockingTest{
        //When
        orderListViewModel.getSpecOrders("nermeenzaitoon@gmail.com")

        val apiState = orderListViewModel.accessOrderList.getOrAwaitValue {  } as ApiState.Success<*>

        var result = apiState.data as List<Order>
        while (apiState.data is ApiState.Loading) {
            delay(100)
            result = apiState.data as List<Order>
        }

        //Then
        TestCase.assertEquals(result[0].id, retriveOrderModel.orders.get(0).id)
    }




}