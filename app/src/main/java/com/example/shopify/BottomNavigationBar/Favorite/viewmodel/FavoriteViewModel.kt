package com.example.shopify.BottomNavigationBar.Favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrder
import com.example.shopify.BottomNavigationBar.Favorite.model.FavDraftOrderResponse
import com.example.shopify.model.ShopifyRepository
import com.example.shopify.model.createCustomerRequest
import com.example.shopify.model.draftModel.DraftOrder
import com.example.shopify.model.draftModel.DraftOrderResponse
import com.example.shopify.utility.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel (private val _repo: ShopifyRepository): ViewModel() {
    private var _fav: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val fav: StateFlow<ApiState> = _fav

    private var _wishList: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val wishList: StateFlow<ApiState> = _wishList

    private val _favDraftOrderList = MutableStateFlow<List<FavDraftOrder>?>(emptyList())
    val favDraftOrderList : StateFlow<List<FavDraftOrder>?> = _favDraftOrderList

    fun getFavorites(id:Long){
        viewModelScope.launch(Dispatchers.IO){
            _repo.getFavDraftOrders(id)
                .catch {err->
                    _fav.value= ApiState.Failure(err)
                }
                .collect{
                    _fav.value= ApiState.Success(it)
                }
        }
    }

    fun updateFavorite(id:Long,favDraftOrder:FavDraftOrderResponse){
        viewModelScope.launch(Dispatchers.IO){
            _repo.updateFavDraftOrder(id,favDraftOrder)
                .catch {err->
                    _fav.value= ApiState.Failure(err)
                }
                .collect{
                    _fav.value= ApiState.Success(it)
                }
        }
    }

//    fun createFavDraftOrders(draftOrder: FavDraftOrderResponse){
//        viewModelScope.launch(Dispatchers.IO){
//            _repo.createFavDraftOrders(draftOrder)
//                .catch {err->
//                    _wishList.value= ApiState.Failure(err)
//                }
//                .collect{
//                    _wishList.value= ApiState.Success(it)
//                }
//        }
//    }
}