package com.example.shopify.model

import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.Models.orderList.RetriveOrderModel
import com.example.shopify.Models.products.CollectProductsModel
import com.example.shopify.model.Brands.BrandModel
import com.example.shopify.model.Brands.Image
import com.example.shopify.model.Brands.SmartCollection
import com.example.shopify.model.PostOrders.Order
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

    var smartCollections : List<SmartCollection> = listOf(SmartCollection("11" , "body", true, "handle" , 2425, Image("","",1,"",20), "", "", listOf(), "", "","","" ))

    var brandModel = BrandModel(smartCollections)

    val productCategory = listOf(Product(null,null,null,null,24125,null,
        listOf(),null,"women",null,null,null,"",null,"product2",null,null,null,false))

    val collectProductsModel = CollectProductsModel(productCategory)


    var order: List<Order> = listOf( Order(null , null , null , null , "nermeenzaitoon@gmail.com" , null , 24125))
    var retriveOrderModel = RetriveOrderModel(order)

    override suspend fun getBrands(): Flow<BrandModel?> {
            return flowOf(brandModel)

    }

    override fun createNewCustomer(customer: createCustomerRequest): Flow<createCustomerRequest?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProducts(): Flow<CollectProductsModel?> {
        TODO("Not yet implemented")
    }

    override fun getCustomerByEmail(email: String): Flow<createCustomersResponse?> {
        TODO("Not yet implemented")
    }

    override fun getCustomerById(customerId: Long): Flow<createCustomerRequest?> {
        TODO("Not yet implemented")
    }

    override fun getProductInfo(product_id: Long): Flow<ProductModel?> {
        return  flow {
            emit(productInfoData)
        }
    }

    override suspend fun getCollectionProducts(id: Long): Flow<CollectProductsModel?> {
        return flowOf(collectProductsModel)
    }

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

    override fun getFavDraftOrders(id: Long): Flow<FavDraftOrderResponse?> {
        return flow {
            emit(favDraftOrderResponse)
        }
    }

    override fun createFavDraftOrders(draftOrderResponse: FavDraftOrderResponse): Flow<FavDraftOrderResponse?> {
        return flow {
            emit(favDraftOrderResponse)
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
        return flowOf(retriveOrderModel)
    }

    override fun getSpecificOrder(id: Long): Flow<RetriveOrder?> {
        TODO("Not yet implemented")
    }


}