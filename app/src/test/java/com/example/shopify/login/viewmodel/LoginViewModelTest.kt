package com.example.shopify.login.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.BottomNavigationBar.Favorite.viewmodel.FavoriteViewModel
import com.example.shopify.MainCoroutineRule
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.Customer
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.createCustomersResponse
import com.example.shopify.utility.ApiState
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginViewModelTest {
    @get:Rule
    val myRule= InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule= MainCoroutineRule()

    lateinit var loginViewModel: SignInViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit var customer: Customer
    lateinit var customersResponse:createCustomersResponse
    @Before
    fun setUp() {
        repo = FakeShopifyRepository()
        loginViewModel = SignInViewModel(repo)
         customer=
            Customer(null,null,null,null,"","","",null,null,null,null,true,null,null,null,null,null)

         customersResponse = createCustomersResponse(listOf(customer))
    }

    @Test
    fun getCustomerByEmail_Email_Customer()= runBlockingTest{
        //when
        loginViewModel.getCustomerByEmail("klm@gmail.com")

        var customer1 = loginViewModel.login.getOrAwaitValue {  }


        while (customer1 is ApiState.Loading) {
            delay(100)
            customer1 =  loginViewModel.login.getOrAwaitValue {  }
        }

        // Then
        assertThat(
            customer1,
            `is`(instanceOf(ApiState.Success::class.java))
        )
        val successState = customer1 as ApiState.Success<*>
        val result = successState.data as createCustomersResponse
        assertEquals(result.customers.get(0).email,customersResponse.customers.get(0).email)
    }
}