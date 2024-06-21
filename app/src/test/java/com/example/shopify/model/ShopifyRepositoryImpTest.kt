package com.example.shopify.model

import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.Models.orderList.RetriveOrderModel
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.Brands.Image
import com.example.shopify.model.Brands.SmartCollection
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.RetriveOrder.RetriveOrder
import com.example.shopify.model.productDetails.Product
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.network.ShopifyRemoteDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Test

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

    lateinit var productCategory  : List<Product>
    lateinit var collectProductsModel : CollectProductsModel

    lateinit var smartCollections : List<SmartCollection>
    lateinit var brandModel : BrandModel

    lateinit var orders : List<Order>
    lateinit var retriveOrderModel : RetriveOrderModel

    lateinit var order1 : Order
    lateinit var retriveOrder : RetriveOrder
    lateinit var postOrderModel: PostOrderModel


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

        productCategory = listOf(Product(null,null,null,null,24125,null,
            listOf(),null,"women",null,null,null,"",null,"product2",null,null,null,false))

        collectProductsModel = CollectProductsModel(productCategory)


        // barnds
        smartCollections = listOf(SmartCollection("11" , "body", true, "handle" , 2425, Image("","",1,"",20), "", "", listOf(), "", "","","" ))

        brandModel = BrandModel(smartCollections)

        // order list
        orders = listOf( Order(null , null , null , null , "nermeenzaitoon@gmail.com" , null , 24125))
        retriveOrderModel = RetriveOrderModel(orders)

        // post order
        order1 = Order(null , null , null , null , "nermeenzaitoon@gmail.com" , null , 24125)
        retriveOrder = RetriveOrder(order1)
        postOrderModel = PostOrderModel(order1)

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


    // get all products of category
    @Test
    fun getAllProductsOfCategory_IDAndType_Products()= runBlockingTest{
        //when
        val products = fakeShopifyRepository.getProducts(24125 , "Women" ).getOrAwaitValue {  }
        //then
        assertEquals(products!!.products[0].id,collectProductsModel.products[0].id)
    }


    // get all brands
    @Test
    fun getBrands_Brands() = runBlockingTest {

        val result = fakeShopifyRepository.getBrands().getOrAwaitValue {  }

        assertThat(result, `is`(notNullValue()))
        assertEquals(result!!.smart_collections[0].id,brandModel.smart_collections[0].id)

    }


    // get all products of brand
    @Test
    fun getProductsOfBrand_Id_Products()=runBlockingTest{
        // When
        val result = fakeShopifyRepository.getCollectionProducts(24125).getOrAwaitValue {  }

        // Then
        assertEquals(result!!.products[0].id, collectProductsModel.products[0].id)

    }

    // get all orders
    @Test
    fun getAllOrderList_Orders() = runBlockingTest {
        val result = fakeShopifyRepository.getOrderList().getOrAwaitValue {  }

        assertThat(result, notNullValue())
        assertEquals(result!!.orders[0].id, retriveOrderModel.orders[0].id)

    }

    // post order
    @Test
    fun createOrder_Order()= runBlocking{
        fakeShopifyRepository.createOrder(postOrderModel).collectLatest { result->

            assertThat(retriveOrder,`is`(result))
        }
    }

}