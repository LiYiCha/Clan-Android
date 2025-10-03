package com.yc.auth.data.source.remote.dto


data class LoginRequest(
    val username: String,
    val password: String,
    val code : String,
)
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

