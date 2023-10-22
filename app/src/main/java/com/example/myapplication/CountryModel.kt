package com.example.myapplication

data class CountryModel(
    val name: CountryName,
    val population: String,
    val flags: Flag
)

data class CountryName(
    val common: String
)

data class Flag(
    val png: String
)