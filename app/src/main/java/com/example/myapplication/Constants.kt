package com.example.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants{

    const val BASE_URL:String = "https://restcountries.com/"
    /*
    0 is upper country on layout
    1 is bottom country on layout
    */
    const val UP_COUNTRY:Int = 0
    const val DOWN_COUNTRY:Int = 1
    const val PREFERENCE_NAME = "WeatherAppPreference"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"

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