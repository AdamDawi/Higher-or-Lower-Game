package com.example.myapplication

import java.io.Serializable

data class Countries(
    val countries: List<CountryModel>
): Serializable

data class CountryModel(
    val name: CountryName,
    val population: String,
    val flags: Flag
): Serializable

data class CountryName(
    val common: String
)

data class Flag(
    val png: String
)