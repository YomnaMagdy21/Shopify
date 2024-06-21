package com.example.shopify.signup.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.MainCoroutineRule
import com.example.shopify.getOrAwaitValue
import com.example.shopify.login.viewmodel.SignInViewModel
import com.example.shopify.model.Customer
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.createCustomerRequest
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
class SignUpViewModelTest {

    @get:Rule
    val myRule= InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule= MainCoroutineRule()

    lateinit var signupViewModel: SignUpViewModel
    lateinit var repo: FakeShopifyRepository
    lateinit var customer : Customer
    lateinit var customerRequest: createCustomerRequest
    lateinit var favDraftOrder: FavDraftOrder
    lateinit var favDraftOrderResponse: FavDraftOrderResponse


    @Before
    fun setUp() {
        repo = FakeShopifyRepository()
        signupViewModel = SignUpViewModel(repo)
        customer = Customer(null,null,null,null,"","","",null,null,null,null,true,null,null,null,null,null)
        customerRequest = createCustomerRequest(customer)
        favDraftOrder = FavDraftOrder()
        favDraftOrderResponse= FavDraftOrderResponse(favDraftOrder)


    }

    @Test
    fun registerCustomerInAPI_Customer()= runBlockingTest {
        //when
        signupViewModel.registerCustomerInAPI(customerRequest)

        var customer1 = signupViewModel.register.getOrAwaitValue {  }


        while (customer1 is ApiState.Loading) {
            delay(100)
            customer1 = signupViewModel.register.getOrAwaitValue {  }
        }

        // Then
        assertThat(customer1,`is`(instanceOf(ApiState.Success::class.java))
        )
        val successState = customer1 as ApiState.Success<*>
        val result = successState.data as createCustomerRequest
        assertEquals(result.customer.id,customerRequest.customer.id)

    }

    @Test
    fun createFavDraftOrders_FavDraftOrder()= runBlockingTest {
        //when
        signupViewModel.createFavDraftOrders(favDraftOrderResponse)

        var fav = signupViewModel.wishList.getOrAwaitValue {  }


        while (fav is ApiState.Loading) {
            delay(100)
            fav =  signupViewModel.wishList.getOrAwaitValue {  }
        }

        // Then
        assertThat(fav,`is`(instanceOf(ApiState.Success::class.java))
        )
        val successState = fav as ApiState.Success<*>
        val result = successState.data as FavDraftOrderResponse
        assertEquals(result.draft_order?.id,favDraftOrderResponse.draft_order?.id)

    }


}