package com.example.shopify.CheckNetwork

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NetworkConectivityObserver (private val context: Context) : NetworkObservation {

    private val connectivityManger = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observeOnNetwork(): Flow<InternetStatus> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(InternetStatus.Available) }
                }
                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(InternetStatus.Lost) }
                }
                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(InternetStatus.UnAvailable) }
                }
            }
            connectivityManger.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManger.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()

    }
}