package com.example.myapplication

import org.json.JSONArray

data class ReceivedDataModel(
    val name: CountryName,
    val population: String
)

data class CountryName(val common: String,
                       val official: String)