package com.example.assignment5

data class NaverResult(
    val resultCode: String,
    val message: String,
    val response: Profile
)

data class Profile(
    val id: String,
    val email: String,
    val name: String
)
