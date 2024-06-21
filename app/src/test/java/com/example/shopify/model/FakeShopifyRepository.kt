package com.example.shopify.model

import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.Models.orderList.RetriveOrderModel
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.PostOrders.PostOrderModel
import com.example.shopify.model.RetriveOrder.RetriveOrder
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.model.addressModel.AddressesModel
import com.example.shopify.model.productDetails.Product
import com.example.shopify.model.productDetails.ProductModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeShopifyRepository : ShopifyRepository{

    val product = Product(null,null,null,null,null,null,
        listOf(),null,null,null,null,null,"",null,"",null,null,null,false)

    var productInfoData = ProductModel(product)
    var favDraftOrder = FavDraftOrder()
    var favDraftOrderResponse=FavDraftOrderResponse(favDraftOrder)
    var customer=
        Customer(null,null,null,null,"","","",null,null,null,null,true,null,null,null,null,null)

    var customersResponse = createCustomersResponse(listOf(customer))
   var customer1 = Customer(null,null,null,null,"","","",null,null,null,null,true,null,null,null,null,null)
   var customerRequest = createCustomerRequest(customer1)

    var fakeRemoteDataSource=FakeRemoteDataSource()
    override suspend fun getBrands(): Flow<BrandModel?> {
        TODO("Not yet implemented")
    }

    override fun createNewCustomer(customer: createCustomerRequest): Flow<createCustomerRequest?> {
       return flow{fakeRemoteDataSource.createNewCustomer(customer)}
    }

    override suspend fun getAllProducts(): Flow<CollectProductsModel?> {
        TODO("Not yet implemented")
    }

    override fun getCustomerByEmail(email: String): Flow<createCustomersResponse?> {
        return flow {
            fakeRemoteDataSource.getCustomerByEmail(email)
        }
    }

    override fun getCustomerById(customerId: Long): Flow<createCustomerRequest?> {
        TODO("Not yet implemented")
    }

    override fun getProductInfo(product_id: Long): Flow<ProductModel?> {
        return  flow {
            fakeRemoteDataSource.getProductInfo(product_id)
        }
    }

    override suspend fun getCollectionProducts(id: Long): Flow<CollectProductsModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun getProducts(
        collectionId: Long?,
        productType: String?
    ): Flow<CollectProductsModel?> {
        TODO("Not yet implemented")
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

    override fun getFavDraftOrders(id: Long): Flow<FavDraftOrderResponse?> {
        return flow {
            fakeRemoteDataSource.getFavDraftOrders(id)
        }
    }

    override fun createFavDraftOrders(draftOrderResponse: FavDraftOrderResponse): Flow<FavDraftOrderResponse?> {
        return flow {
            fakeRemoteDataSource.createFavDraftOrder(draftOrderResponse)
        }
    }

    override fun updateFavDraftOrder(
        id: Long,
        draftOrderResponse: FavDraftOrderResponse
    ): Flow<FavDraftOrderResponse?> {
        return flow {
            emit(favDraftOrderResponse)
        }
    }

    override fun deleteFavDraftOrder(id: Long): Flow<FavDraftOrderResponse?> {
        return flow {
            emit(FavDraftOrderResponse(draft_order = null))
        }
    }

    override suspend fun makeAddressDefault(
        customerId: Long,
        addressId: Long
    ): Flow<AddressesModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun editAddress(
        customerId: Long,
        addressId: Long,
        addresse: AddNewAddress
    ): Flow<AddressesModel?> {
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(order: PostOrderModel): Flow<RetriveOrder?> {
        TODO("Not yet implemented")
    }

    override suspend fun getOrderList(): Flow<RetriveOrderModel?> {
        TODO("Not yet implemented")
    }

    override fun getSpecificOrder(id: Long): Flow<RetriveOrder?> {
        TODO("Not yet implemented")
    }


}