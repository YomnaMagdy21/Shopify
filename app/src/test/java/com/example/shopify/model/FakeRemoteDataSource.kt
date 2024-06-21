package com.example.shopify.model

import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.Models.orderList.RetriveOrderModel
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.ShoppingCart.model.PriceRule
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.Brands.Image
import com.example.shopify.model.Brands.SmartCollection
import com.example.shopify.model.PostOrders.Order
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.RetriveOrder.RetriveOrder
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.model.addressModel.AddressesModel
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.model.productDetails.Product
import com.example.shopify.model.productDetails.ProductModel
import com.example.shopify.network.ShopifyRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource : ShopifyRemoteDataSource {

    var customer = Customer(null,null,null,null,"","","",null,null,null,null,true,null,null,null,null,null)
   var customerRequest = createCustomerRequest(customer)
    var customerResponse = createCustomersResponse(listOf(customer))
    var product= Product(null,null,null,null,1234,null,
        listOf(),null,null,null,null,null,"",null,"",null,null,null,false)
    var productModel=ProductModel(product)

    // category & products
    var  products = listOf(Product(null,null,null,null,24125,null,
        listOf(),null,"women",null,null,null,"",null,"product2",null,null,null,false))

    var collectProductsModel = CollectProductsModel(products)


    // barnds
    var smartCollections : List<SmartCollection> = listOf(SmartCollection("11" , "body", true, "handle" , 2425, Image("","",1,"",20), "", "", listOf(), "", "","","" ))

    var brandModel = BrandModel(smartCollections)

    // order list
    var orders: List<Order> = listOf( Order(null , null , null , null , "nermeenzaitoon@gmail.com" , null , 24125))
    var retriveOrderModel = RetriveOrderModel(orders)

    // post order
    var order1= Order(null , null , null , null , "nermeenzaitoon@gmail.com" , null , 24125)
    var retriveOrder = RetriveOrder(order1)










    override suspend fun getBrands(): Flow<BrandModel?> {
        return flowOf(brandModel)
    }

    override fun createNewCustomer(customer: createCustomerRequest): Flow<createCustomerRequest?> {
        return flow { emit(customerRequest) }
    }


    override fun getCustomerByEmail(email: String): Flow<createCustomersResponse?> {
        return flow { emit(customerResponse) }
    }

    override fun getCustomerById(customerId: Long): Flow<createCustomerRequest?> {
        return flow { emit(customerRequest) }
    }

    override fun getProductInfo(product_id: Long): Flow<ProductModel?> {
       return flow { emit(productModel) }
    }

    override suspend fun getCollectionProducts(id: Long): Flow<CollectProductsModel?> {
        return flowOf(collectProductsModel)
    }

    // products of category
    override suspend fun getProducts(
        collectionId: Long?,
        productType: String?
    ): Flow<CollectProductsModel?> {
        return flowOf(collectProductsModel)
    }

    override suspend fun getAddresses(customerId: Long): Flow<AddressesModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun addAddress(
        customerId: Long,
        addresse: AddNewAddress
    ): Flow<AddressesModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun removeAddresses(customerId: Long, addressId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun makeAddressDefault(
        customerId: Long,
        addressId: Long
    ): Flow<AddressesModel?> {
        TODO("Not yet implemented")
    }

    override fun getFavDraftOrders(id: Long): Flow<FavDraftOrderResponse?> {
        return flow { emit(FavDraftOrderResponse()) }
    }

    override fun createFavDraftOrder(draftOrderResponse: FavDraftOrderResponse): Flow<FavDraftOrderResponse?> {
        return flow { emit(FavDraftOrderResponse()) }
    }

    override suspend fun editAddress(
        customerId: Long,
        addressId: Long,
        addresse: AddNewAddress
    ): Flow<AddressesModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(order: PostOrderModel): Flow<RetriveOrder?> {
        return flowOf(retriveOrder)
    }

    override suspend fun getOrderList(): Flow<RetriveOrderModel?> {
        return flowOf(retriveOrderModel)
    }

    override fun updateFavDraftOrder(
        id: Long,
        draftOrderResponse: FavDraftOrderResponse
    ): Flow<FavDraftOrderResponse?> {
        return flow { FavDraftOrderResponse() }
    }

    override fun deleteFavDraftOrder(id: Long): Flow<FavDraftOrderResponse?> {
        return flow { FavDraftOrderResponse() }
    }

    override suspend fun updateDraftOrder(
        id: String,
        draftOrder: DraftOrderResponse
    ): Flow<DraftOrderResponse?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDraftOrder(id: String): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getDraftOrders(): Flow<List<DraftOrder>> {
        TODO("Not yet implemented")
    }

    override suspend fun createDraftOrder(draftOrder: DraftOrderResponse): Flow<DraftOrderResponse?> {
        TODO("Not yet implemented")
    }

    override suspend fun getPriceRules(): Flow<List<PriceRule>> {
        TODO("Not yet implemented")
    }
}