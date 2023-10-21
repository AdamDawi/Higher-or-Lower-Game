package com.example.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants{

    const val BASE_URL:String = "https://restcountries.com/"
    fun isNetworkAvailable(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //for android version 23 or newer
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
        {
            //checking network
            val network = connectivityManager.activeNetwork ?: return false
            //checking network capabilities
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            //checking different types of network
            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else //for older android versions
        {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }

    }
}