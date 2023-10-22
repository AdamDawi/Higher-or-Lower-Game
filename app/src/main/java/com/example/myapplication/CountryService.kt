package com.example.myapplication

import retrofit2.Call
import retrofit2.http.GET

interface CountryService
{
    @GET("v3.1/all")

    fun getCountry() : Call<List<CountryModel>>
}