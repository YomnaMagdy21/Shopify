package com.example.shopify.model

import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.model.productDetails.Product
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.network.ShopifyRemoteDataSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

//@RunWith(RobolectricTestRunner::class)
class ShopifyRepositoryImpTest {

    lateinit var fakeShopifyRemoteDataSource: ShopifyRemoteDataSource
    lateinit var fakeShopifyRepository: ShopifyRepository
    lateinit var customer : Customer
    lateinit var customerRequest: createCustomerRequest
    lateinit var customerResponse : createCustomersResponse
    lateinit var product:Product
    lateinit var productModel: ProductModel
    lateinit var favDraftOrderResponse:FavDraftOrderResponse


    @Before
    fun setup(){
        fakeShopifyRemoteDataSource=FakeRemoteDataSource()
        fakeShopifyRepository= ShopifyRepositoryImp(fakeShopifyRemoteDataSource)
        customer = Customer(null,null,null,null,"","","",null,null,null,null,true,null,null,null,null,null)
        customerRequest = createCustomerRequest(customer)
        customerResponse = createCustomersResponse(listOf(customer))
         product =  Product(null,null,null,null,1234,null,
             listOf(),null,null,null,null,null,"",null,"",null,null,null,false)

        productModel=ProductModel(product)

         favDraftOrderResponse= FavDraftOrderResponse()




    }

    @Test
    fun createNewCustomer_Customer()= runBlocking{


        fakeShopifyRepository.createNewCustomer(customerRequest).collectLatest { result->

            assertThat(customerRequest,`is`(result))
        }
    }

    @Test
    fun getCustomerByEmail_Email_Customer()= runBlocking{


        fakeShopifyRepository.getCustomerByEmail("xyz@gmail.com").collectLatest { result->

            assertThat(customerResponse,`is`(result))
        }
    }

    @Test
    fun getCustomerByID_ID_Customer()= runBlocking{


        fakeShopifyRepository.getCustomerById(1234).collectLatest { result->

            assertThat(customerRequest,`is`(result))
        }
    }

    @Test
    fun getProductInfo_ID_ProductModel()= runBlocking{


        fakeShopifyRepository.getProductInfo(1234).collectLatest { result->

            assertThat(productModel,`is`(result))
        }
    }

    @Test
    fun getFavDraftOrder_ID_FavDraftOrder()= runBlocking{


        fakeShopifyRepository.getFavDraftOrders(5043762398564).collectLatest { result->

            assertThat(favDraftOrderResponse,`is`(result))
        }
    }
    @Test
    fun createFavDraftOrders_FavDraftOrder()=runBlocking{
        fakeShopifyRepository.createFavDraftOrders(favDraftOrderResponse).collectLatest { result->

            assertThat(favDraftOrderResponse,`is`(result))
        }
    }

    @Test
    fun updateFavDraftOrder_FavDraftOrder()= runBlocking {
        fakeShopifyRepository.updateFavDraftOrder(5043762398564,favDraftOrderResponse).collectLatest { result->

            assertThat(favDraftOrderResponse,`is`(result))
        }
    }

    @Test
    fun deleteFavDraftOrder_FavDraftOrder()= runBlocking {
        fakeShopifyRepository.deleteFavDraftOrder(5043762398564).collectLatest { result->

            assertThat(favDraftOrderResponse,`is`(result))
        }
    }


}