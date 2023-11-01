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
    //names of variables that are saved and loaded for subsequent activities
    const val PREFERENCE_NAME = "CountryAppPreference"
    const val COUNTRY_RESPONSE_DATA = "country_response_data"
    //this is delay for changing countries in next turn while player choose before
    const val NEXT_TURN_ANIM_DELAY = 800L
    //this is delay for number animation
    const val NUMBER_ANIM_DELAY = 1000L

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