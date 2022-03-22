package com.kudu.shopapp.model

data class User(
    val id: String = "",
    val firstname: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0
)
